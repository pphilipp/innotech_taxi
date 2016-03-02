package com.webcab.elit.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.webcab.elit.HomeScreen;
import com.webcab.elit.R;
import com.webcab.elit.Utils.Utilits;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sergey on 19.11.2015.
 */
public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1000;
    private static final String TAG = "GCM_INTENT_SERVICE";
    NotificationManager mNotificationManager;

    //push fields
    private String message = "";
    private String gcmType = "";
    private String payLoad = "";

    //list of push types
    private static final int PRELIM_ORDER_PUSH = 1;
    private static final int PRELIM_ORDER_NO_CAR_PUSH = 2;
    private static final int CAR_ARRIVED_PUSH = 3;
    private static final int NOT_DEFINED_PUSH = 404;

    //gcmTypes
    private static final String PRELIMINARY = "PrelimOrderCarFound";
    private static final String PRELIMINARY_NO_CAR = "PrelimOrderCarNotFound";
    private static final String CAR_ARRIVED = "PrelimOrderDriverArrived";

    private Intent preOrderIntent;

    public GCMIntentService() {
        super(GCMIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        Log.d(TAG, "incoming GCM");

        if (!extras.isEmpty() && extras.containsKey("message")
                && extras.containsKey("gcmType") && extras.containsKey("payLoad")) {



            message = extras.getString("message");
            gcmType = extras.getString("gcmType");
            payLoad = extras.getString("payLoad");

            Log.d(TAG, "incoming GCM = " + payLoad);

            switch (getPushType(extras)) {
                case PRELIM_ORDER_PUSH:
                    if (pushNotEmpty()) {

                        preOrderIntent = new Intent(this, HomeScreen.class);
                        try {
                            JSONObject payLoadJSON = new JSONObject(payLoad);
                            String orderID = payLoadJSON.optString("orderID");
                            String callSignID = payLoadJSON.optString("driverID");
                            Utilits.saveCurrentOrder(getApplicationContext(), orderID);

                            preOrderIntent.putExtra("payLoad", payLoad);
                            preOrderIntent.putExtra("driverId", callSignID);
                            preOrderIntent.putExtra("orderId", orderID);
                            preOrderIntent.putExtra("methodName", "orderwait");
                            preOrderIntent.putExtra("needConfirm", true);

                            sendNotification(message, preOrderIntent);

                        } catch (JSONException e) {
                            Log.d(TAG, "Push JSON error = " + e.getMessage());
                            e.printStackTrace();
                            sendNotification("error", null);
                        }
                    } else {
                        //TODO behavior when push fields are corrupted
                        sendNotification("error", null);
                    }
                    break;
                case PRELIM_ORDER_NO_CAR_PUSH:
                    //TODO no car found
                    break;
                case CAR_ARRIVED_PUSH:

                    try {
                        JSONObject payLoadJSON = new JSONObject(payLoad);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*Utilits.deleteOrderID(getApplicationContext(), currentOrderID);
                    Intent carArrivedIntent = new Intent(getApplicationContext(), HomeScreen.class);
                    carArrivedIntent.putExtra("methodName", "ordercarhere");
                    carArrivedIntent.putExtra("orderId", currentOrderID);
                    carArrivedIntent.putExtra("driverId", String.valueOf(driverId));
                    mSettingsOrder.edit().putInt(Utilits.Constants.ORDER_TYPE, Utilits.Constants.CAR_ARRIVED);*/

                    break;
            }



        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }



    private int getPushType(Bundle extras) {
        int type = NOT_DEFINED_PUSH;

        if (extras.containsKey("gcmType") && !extras.getString("gcmType").equals("")) {

            //check possible push types
            if (extras.getString("gcmType").equals(PRELIMINARY)) {
                return PRELIM_ORDER_PUSH;
            }

            if (extras.getString("gcmType").equals(PRELIMINARY_NO_CAR)) {
                return PRELIM_ORDER_NO_CAR_PUSH;
            }
        }
        return type;
    }

    /**
     * check if all fields are not empty
     * @return
     */
    private boolean pushNotEmpty() {
        return (message != null && gcmType != null && payLoad != null);
    }

    private void sendNotification(String msg, Intent mIntent) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                mIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.noticication_ico)
                        .setContentTitle(getResources().getString(R.string.app_name_for_notif))
                        .setContentText(msg)
                        .setSound(Uri.parse("android.resource://"
                                + getPackageName() + "/" + R.raw.arrived))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


}
