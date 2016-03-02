package com.webcab.elit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.crashlytics.android.Crashlytics;
import com.webcab.elit.Utils.Utilits;
import com.webcab.elit.data.autos;
import com.webcab.elit.net.ServiceConnection;

import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    final String TAG = "SERVICE_TAG";
    //interval between requests to server for car info in seconds
    public static final int TIME_INTERVAL_BETWEEN_REQUESTS = 15;
    boolean created;
    NotificationManager nm;
    private SharedPreferences mSettingsOrder;
    private SharedPreferences.Editor mEditor;
    SharedPreferences mSettingsReg;

    private Intent intentCallBack = new Intent(OrderWaitNew.BROADCAST_ACTION);


    private final Handler handler = new Handler();
    Intent intent;

    private PendingIntent pi;

    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d(TAG, "onCreate");
        created = false;

    }


    public int onStartCommand(Intent intent, int flags, int startId) {

        long sec = 0;
        long millis = 0;
        String driverId = "";
        String orderId = "";
        String car = "";

        //pi = intent.getParcelableExtra(OrderWaitNew.PARAM_PINTENT);


        mSettingsReg = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        if (intent != null && intent.getExtras() != null) {
            Bundle b = intent.getExtras();
            sec = b.getLong("seconds");
            millis = b.getLong("millis");
            driverId = b.getString("driverId");
            orderId = b.getString("orderId");
            car = b.getString("car");

            //save current order
            Utilits.saveCurrentOrder(getApplicationContext(), orderId);

        }

        if (!created) {
            someTask(sec, millis, driverId, orderId, car);
            created = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        created = false;
        Log.d(TAG, "onDestroy");
        Log.d(TAG, "created " + created);
    }

    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    void someTask(final long seconds, final long millis, final String driverId, final String orderId, final String car) {

        final Context ctx = this;
        Log.d(TAG, "my task ___ orderID - " + orderId);



        final Notification notif = new Notification(R.drawable.noticication_ico,
                "Машина в пути.", System.currentTimeMillis());

        //Intent intent = new Intent(this, OrderWait.class);
        final Intent intent = new Intent(this, HomeScreen.class);
        intent.putExtra("methodName", "orderwait");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("carOn", car);
        intent.putExtra("orderId", orderId);
        intent.putExtra("driverId", driverId);
        intent.putExtra("millis", millis);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Taxi elit")
                /*.setContentText("Машина в пути к вам.")
                .setSound(Uri.parse("android.resource://"
                + this.getPackageName() + "/" + R.raw.arrived))*/
                .setSmallIcon(R.drawable.noticication_ico)
                .setContentIntent(pIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
        Notification mNotif = mBuilder.build();
        mNotif.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(1, mNotif);


        /*notif.setLatestEventInfo(this, "Taxi elit",
                "Машина в пути к вам.", pIntent);

        //notif.sound = Uri.parse("android.resource://com.myPackageName.org/" + R.raw.sound);

        notif.flags |= Notification.FLAG_ONGOING_EVENT;

        // отправляем
        nm.notify(1, notif);*/



        Thread whereIsCarThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "starting thread");

                Runnable send = new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast(intentCallBack);
                    }
                };


                ServiceConnection sc = new ServiceConnection(ctx);
                boolean carArrived = false;
                int errorCount = 0;

                do {
                    try {
                        TimeUnit.SECONDS.sleep(TIME_INTERVAL_BETWEEN_REQUESTS);
                    } catch (InterruptedException e) {
                        Log.d("error", e.getMessage());
                    }

                    Log.d(TAG, "sending request, driverID = " + driverId);
                    autos auto = sc.getCoordAuto(driverId);




                    //if error
                    if (auto == null) {
                        Log.d(TAG, "Car update => fail");
                        //server response error. Max 2 errors
                        if (++errorCount > 1) {
                            Log.d(TAG, "breaking thread");
                            //send notification to crashlytics
                            if (Crashlytics.getInstance() != null) {
                                Crashlytics.getInstance().core.setString("waitingCarError", "orderID = " + orderId);
                            }
                            Utilits.deleteOrderID(getApplicationContext(), orderId);
                            sendErrorNotification();
                            break;
                        }
                    } else {
                        Log.d(TAG, "Car update => " + auto.getCarbrand());
                        //put recieved data into intent
                        intentCallBack.putExtra(OrderWaitNew.ORDER_ID, orderId);
                        intentCallBack.putExtra(OrderWaitNew.CALL_SIGN_ID, driverId);
                        intentCallBack.putExtra(OrderWaitNew.CAR_GEO_X, auto.getGeox());
                        intentCallBack.putExtra(OrderWaitNew.CAR_GEO_Y, auto.getGeoy());
                        intentCallBack.putExtra(OrderWaitNew.CAR_COLOR,auto.getCarcolor());
                        intentCallBack.putExtra(OrderWaitNew.CAR_BRAND, auto.getCarbrand());
                        intentCallBack.putExtra(OrderWaitNew.CAR_STATE_NUMBER, auto.getStatenumber());
                        handler.post(send);
                    }

                    //car arrived
                    if (auto != null && auto.getStatusforweborder() == 1) {
                        //send notification set exit condition
                        Log.d(TAG, "Car arrived");
                        Utilits.deleteOrderID(getApplicationContext(), orderId);
                        sendNotif(car, driverId);
                        carArrived = true;
                    }


                }
                while (!carArrived);
                Log.d(TAG, "out of loop");
                stopSelf();
            }
        });
        whereIsCarThread.start();

    }

    private void sendErrorNotification() {

        //cancel previous notifications
        nm.cancel(1);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(true)
                .setContentTitle(this.getResources().getString(R.string.app_name))
                .setContentInfo(this.getResources().getString(R.string.connect_error))
                .setContentText(this.getResources().getString(R.string.call_operator))
                .setSound(Uri.parse("android.resource://"
                        //+ this.getPackageName() + "/" + R.raw.horn))
                        + this.getPackageName() + "/" + R.raw.arrived))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);

        Intent homeIntent = new Intent(this, HomeScreen.class);
        homeIntent.putExtra("methodName", "callDialog");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeScreen.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(homeIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        if (HomeScreen.appIsActive) {
            try {
                resultPendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        } else {
            //nm.notify(2, notif);
            mNotificationManager.notify(2, mBuilder.build());
        }
    }


    void sendNotif(String car, String driverId) {

        nm.cancel(1);

        Log.d(TAG, "sendNotif " + System.currentTimeMillis());


        Notification notif = new Notification(R.drawable.noticication_ico,
                "Машина ожидает вас .", System.currentTimeMillis());

        Intent intent = new Intent(this, HomeScreen.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.putExtra("methodName", "ordercarhere");
        intent.putExtra("carHere", car);
        intent.putExtra("driverId", driverId);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);




        /*notif.setLatestEventInfo(this, "Taxi elit",
                "Машина прибыла и ожидает вас.", pIntent);
        // ставим флаг, чтобы уведомление пропало после нажатия
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        //notif.flags |= Notification.FLAG_NO_CLEAR;

        //notif.sound = Uri.parse("android.resource://com.myPackageName.org/" + R.raw.sound);

        notif.defaults |= Notification.DEFAULT_SOUND;
        notif.defaults |= Notification.DEFAULT_VIBRATE;
        notif.defaults |= Notification.DEFAULT_LIGHTS;*/

        //construct notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.noticication_ico)
                        .setContentTitle("Taxi elit")
                        .setContentText("11Машина прибыла и ожидает вас.")
                        .setSound(Uri.parse("android.resource://"
                                //+ this.getPackageName() + "/" + R.raw.horn))
                                + this.getPackageName() + "/" + R.raw.arrived))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH);


        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeScreen.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(pIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.


        //notif.number = 2;

        // отправляем
        if (HomeScreen.appIsActive) {
            /*try {
                //pIntent.send();

            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }*/
            startActivity(intent);
        } else {
            //nm.notify(2, notif);
            mNotificationManager.notify(2, mBuilder.build());
        }
    }

}
