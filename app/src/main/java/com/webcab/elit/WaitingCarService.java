package com.webcab.elit;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.RemoteViews;

import com.crashlytics.android.Crashlytics;
import com.webcab.elit.Utils.Utilits;
import com.webcab.elit.data.autos;
import com.webcab.elit.net.ServiceConnection;
import com.webcab.elit.widget.WidgetProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Sergey on 09.12.2015.
 */
public class WaitingCarService extends IntentService {

    //types of notification
    public static final int WAITING_FOR_CAR = 100;
    public static final int CAR_IS_HERE = 200;
    public static final int ERROR_MESSAGE = 300;

    private static final String TAG = "WAIT_CAR_SERVICE";
    private static final int MAX_ERRORS = 4;
    List<String> driverIds = new ArrayList<>();
    //callBack intent
    private Intent   intentCallBack = new Intent(OrderWaitNew.BROADCAST_ACTION);
    public static final int TIME_INTERVAL_BETWEEN_REQUESTS = 15;

    private SharedPreferences mSettingsOrder;

    private String currentOrderID;

    private long whenCarArrives;

    private final Handler handler = new Handler();

    public WaitingCarService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        mSettingsOrder = getSharedPreferences(Utilits.Constants.ORDER_SETTINGS_NAME, Context.MODE_PRIVATE);

        Intent waitingIntent = new Intent(getApplicationContext(), HomeScreen.class);
        waitingIntent.putExtra("methodName", "orderwait");
        waitingIntent.putExtra("orderId", currentOrderID);
        sendNotification(WAITING_FOR_CAR, waitingIntent);

        if (intent != null && intent.getExtras() != null) {

            //get orderIDs list
            String driverId = intent.getStringExtra("driverId");
            //get orderID
            currentOrderID = intent.getStringExtra("orderId");
            //save current order
            Utilits.saveCurrentOrder(getApplicationContext(), currentOrderID);
            if (!driverId.equals("") && !driverIds.contains(driverId)) {
                //check if this order is currently under monitoring
                //if not add ID to list start monitoring
                driverIds.add(driverId);
                //start car monitoring
                monitorCar(driverId);
            }
            whenCarArrives = intent.getLongExtra("seconds", 0);

        } else {
            //TODO handle empty intent
        }
    }

    private void monitorCar(final String driverId) {

        showCurrentOrderInWidget();

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

                ServiceConnection sc = new ServiceConnection(getApplicationContext());
                boolean carArrived = false;
                int errorCount = 0;

                do {
                    Log.d(TAG, "sending request, driverID = " + driverId);
                    autos auto = sc.getCoordAuto(String.valueOf(driverId));

                    //if error
                    if (auto == null || (auto != null && auto.getGeox().equals(""))) {
                        Log.d(TAG, "Car update => fail");
                        //server response error. Max 2 errors
                        if (++errorCount > MAX_ERRORS - 1) {
                            Log.d(TAG, "breaking thread");
                            //send notification to crashlytics
                            if (Fabric.isInitialized() && Crashlytics.getInstance() != null) {
                                Crashlytics.getInstance().core.setString("waitingCarError", "orderID = " + currentOrderID);
                            }
                            Utilits.deleteOrderID(getApplicationContext(), currentOrderID);
                            //sendErrorNotification();
                            Intent errorIntent = new Intent(getApplicationContext(), HomeScreen.class);
                            errorIntent.putExtra("methodName", "callDialog");
                            hideCurrentOrderInWidget();
                            sendNotification(ERROR_MESSAGE, errorIntent);
                            break;
                        }
                    } else {
                        Log.d(TAG, "Car update => " + auto.getCarbrand());
                        //put recieved data into intent
                        intentCallBack.putExtra(OrderWaitNew.ORDER_ID, currentOrderID);
                        intentCallBack.putExtra(OrderWaitNew.CALL_SIGN_ID, driverId);
                        intentCallBack.putExtra(OrderWaitNew.CAR_GEO_X, auto.getGeox());
                        intentCallBack.putExtra(OrderWaitNew.CAR_GEO_Y, auto.getGeoy());
                        intentCallBack.putExtra(OrderWaitNew.CAR_COLOR, auto.getCarcolor());
                        intentCallBack.putExtra(OrderWaitNew.CAR_BRAND, auto.getCarbrand());
                        intentCallBack.putExtra(OrderWaitNew.CAR_STATE_NUMBER, auto.getStatenumber());
                        updateWidget(auto);
                        handler.post(send);
                    }

                    //car arrived
                    if (auto != null && auto.getStatusforweborder() == 1) {
                        //send notification set exit condition
                        Log.d(TAG, "Car arrived");
                        Utilits.deleteOrderID(getApplicationContext(), currentOrderID);
                        Intent carArrivedIntent = new Intent(getApplicationContext(), HomeScreen.class);
                        carArrivedIntent.putExtra("methodName", "ordercarhere");
                        carArrivedIntent.putExtra("orderId", currentOrderID);
                        carArrivedIntent.putExtra("driverId", String.valueOf(driverId));
                        mSettingsOrder.edit().putInt(Utilits.Constants.ORDER_TYPE, Utilits.Constants.CAR_ARRIVED);
                        hideCurrentOrderInWidget();
                        sendNotification(CAR_IS_HERE, carArrivedIntent);
                        carArrived = true;
                    }

                    try {
                        TimeUnit.SECONDS.sleep(TIME_INTERVAL_BETWEEN_REQUESTS);
                    } catch (InterruptedException e) {
                        Log.d("error", e.getMessage());
                    }
                }
                while (!carArrived);
                Log.d(TAG, "out of loop");
                stopSelf();
            }
        });
        whereIsCarThread.start();
    }

    private void showCurrentOrderInWidget() {

        Log.d("WIDGET_TAG", "show car details");
        ComponentName thisWidget = new ComponentName(this, WidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.taxi_widget);

        view.setViewVisibility(R.id.current_order, View.VISIBLE);
        view.setViewVisibility(R.id.routes_list, View.GONE);

        manager.updateAppWidget(thisWidget, view);
    }


    private void hideCurrentOrderInWidget() {
        Log.d("WIDGET_TAG", "hide car details");
        ComponentName thisWidget = new ComponentName(this, WidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.taxi_widget);

        view.setViewVisibility(R.id.current_order, View.GONE);
        view.setViewVisibility(R.id.routes_list, View.VISIBLE);

        manager.updateAppWidget(thisWidget, view);
    }

    /**
     * Updates widget info showing there current order info
     * @param auto
     */
    private void updateWidget(autos auto) {
        ComponentName thisWidget = new ComponentName(this, WidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.taxi_widget);

        view.setTextViewText(R.id.txt_model, auto.getCarbrand());
        view.setTextViewText(R.id.txt_color, auto.getCarcolor());
        view.setTextViewText(R.id.txt_num, auto.getStatenumber());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = "-- : --";
        if (whenCarArrives != 0) {
            time = sdf.format(whenCarArrives * 1000);
        }
        view.setTextViewText(R.id.txt_time, time);



        manager.updateAppWidget(thisWidget, view);
    }


    private void sendNotification(int notificationType, Intent mIntent) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeScreen.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(mIntent);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setContentTitle(getResources().getString(R.string.app_name_for_notif))
                .setSmallIcon(R.drawable.noticication_ico)
                .setContentIntent(contentIntent);

        switch (notificationType) {
            case WAITING_FOR_CAR:
                mBuilder.setContentText(getResources().getString(R.string.car_on_route));
                notification = mBuilder.build();
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                break;
            case CAR_IS_HERE:
                mBuilder.setSound(Uri.parse("android.resource://"
                        + getApplicationContext().getPackageName() + "/" + R.raw.arrived_double))
                        .setContentText(getResources().getString(R.string.car_arrived))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH);
                notification = mBuilder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                Log.d(TAG, "Intent extra = " + mIntent.getStringExtra("methodName"));
                break;
            case ERROR_MESSAGE:
                mBuilder
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentText(getResources().getString(R.string.push_error))
                        .setPriority(Notification.PRIORITY_MAX);
                notification = mBuilder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                break;
        }
        notificationManager.cancel(1001);

        //Log.d(TAG, "intent extras = " + mIntent.getStringExtra("methodName"));

        notificationManager.notify(1001, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
