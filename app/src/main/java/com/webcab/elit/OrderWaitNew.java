package com.webcab.elit;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.webcab.elit.Utils.Utilits;
import com.webcab.elit.data.autos;
import com.webcab.elit.data.order_s;
import com.webcab.elit.net.ServiceConnection;
import com.webcab.elit.widget.WidgetProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 30.11.2015.
 */
public class OrderWaitNew extends FragmentActivity {

    //messages for handler
    private static final int GET_CURRENT_ORDER_INFO = 100;
    private static final int GET_CURRENT_AUTO = 200;
    private static final int LAUNCH_SERVICE = 300;
    private static final String TAG = "ORDER_WAIT_TAG";

    public static final String BROADCAST_ACTION = "com.cartracking.broadcasttest.displayevent";

    //intent fields
    public static final String ORDER_ID = "orderID";
    public static final String CALL_SIGN_ID = "callSignID";
    public static final String CAR_GEO_X = "geox";
    public static final String CAR_GEO_Y = "geoy";
    public static final String CAR_BRAND = "carBrand";
    public static final String CAR_STATE_NUMBER = "stateNumber";
    public static final String CAR_COLOR = "carColor";

    private GoogleMap mMap;
    private LatLng fromCoords;


    private Typeface arialBlack;
    private Typeface font_Roboto;
    
    private TextView orderWait_timer;
    private CountDownTimer orderWait_ct3;


    private int orderID;
    private order_s currentOrder;
    private Intent serviceIntent;
    private TextView poisk;
    private Marker marker;

    private JSONObject fullOrderDescJSON;
    private ArrayList<Integer> ordersIDList;

    private ContextThemeWrapper themedContext;

    private ProgressBar waitBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_wait_new);

        //Context for alert dialogs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            themedContext = new ContextThemeWrapper(OrderWaitNew.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        } else {
            themedContext = new ContextThemeWrapper(OrderWaitNew.this, android.R.style.Theme_Light_NoTitleBar);
        }


        //define views
        arialBlack = Typeface.createFromAsset(getAssets(),
                "fonts/arialBlack.ttf");
        font_Roboto = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

        orderWait_timer = (TextView) findViewById(R.id.txt_timer);
        orderWait_timer.setTypeface(arialBlack);
        poisk = (TextView) findViewById(R.id.txt_poisk);

        waitBar = (ProgressBar) findViewById(R.id.progressBar);

        Button bt_c = (Button) findViewById(R.id.bt_cancel);
        bt_c.setTypeface(font_Roboto);

        Button bt_back = (Button) findViewById(R.id.bt_back_btn);
        bt_back.setTypeface(font_Roboto);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderWaitNew.this, HomeScreen.class));
                //finish();
            }
        });

        //cancel order
        bt_c.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            themedContext).create();
                    alertDialog.setMessage(OrderWaitNew.this.getResources().getString(R.string.confirm_order_cancel));
                    alertDialog.setButton(OrderWaitNew.this.getResources().getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Thread t = new Thread(new Runnable() {
                                        public void run() {

                                            //send cancel message to server
                                            ServiceConnection sc = new ServiceConnection(
                                                    OrderWaitNew.this);
                                            sc.cancelOrder(String.valueOf(getCurrentID()));

                                            //remove notification
                                            NotificationManager nm = (NotificationManager) OrderWaitNew.this.getSystemService(NOTIFICATION_SERVICE);
                                            nm.cancelAll();
                                            //switch off timer
                                            if (orderWait_ct3 != null) {
                                                orderWait_ct3.cancel();
                                            }
                                            //delete this order from list of currents
                                            Utilits.deleteOrderID(OrderWaitNew.this, String.valueOf(getCurrentID()));
                                            //stop tracking service
                                            stopService(new Intent(OrderWaitNew.this, WaitingCarService.class));
                                            //hide order in widget
                                            hideCurrentOrderInWidget();
                                            //quit this screen
                                            startActivity(new Intent(OrderWaitNew.this, HomeScreen.class));
                                            //finish();
                                        }
                                    });
                                    t.start();
                                }
                            });

                    alertDialog.setButton2(OrderWaitNew.this.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();
                } catch (Exception e) {
                    //Mint.logException(e);
                    Log.d(SyncStateContract.Constants.CONTENT_DIRECTORY,
                            "Show Dialog: " + e.getMessage());
                }

            }
        });

        if (getIntent().hasExtra("orderIDs")) {
            //orderIDs is a JSONArray [{"orderID": 12345}, {...}] with the list of current orders IDs
            Log.d(TAG, "orderJSON = " + getIntent().getStringExtra("orderIDs"));
            //convert JSONArray into List<Integer> of IDs
            try {
                JSONArray ordersJSONArray = new JSONArray(getIntent().getStringExtra("orderIDs"));
                ordersIDList = new ArrayList<>();
                for (int i = 0; i < ordersJSONArray.length(); i++) {
                    JSONObject orderJSON = ordersJSONArray.getJSONObject(i);
                    ordersIDList.add(orderJSON.optInt("orderID"));
                }
                //get orderID of order of current interest. This order state will shown in this view
                orderID = getCurrentID();
                //show progress bar while waiting info from server
                waitBar.setVisibility(View.VISIBLE);
                //fetch current order info from server
                getOrder();
            } catch (JSONException e) {
                Log.d(TAG, "Error in orderList JSONArray = " + e.getMessage());
                e.printStackTrace();
            }


        } else {
            //quite activity if there is no IDs
            finish();
        }
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
     * creates auto and order
     */
    private void getOrder() {
        mHandler.sendEmptyMessage(GET_CURRENT_ORDER_INFO);
    }

    /**
     * Start tracking service with this intent
     * @param mContext
     * @param secondsToArrive - how much time left before car arrives
     * @param orderUnixTime - order unix time
     * @param callsignID - carID
     * @param orderId - order ID
     * @param car - car info
     * @return - intent with extras
     */
    private Intent createServiceIntent(Context mContext, long secondsToArrive, long orderUnixTime,
                                       String callsignID, String orderId, String car) {

        //be aware that service runs only one car tracking currently. sending multiple request will
        //cause data mess. Must be redone for multiple current orders
        //Intent serviceIntent = new Intent(mContext, MyService.class);
        Intent serviceIntent = new Intent(mContext, WaitingCarService.class);
        serviceIntent.putExtra("seconds", secondsToArrive);
        serviceIntent.putExtra("millis", orderUnixTime);
        serviceIntent.putExtra("driverId", callsignID);
        serviceIntent.putExtra("orderId",orderId);
        serviceIntent.putExtra("car", car);
        //startService(serviceIntent);
        return serviceIntent;
    }

    /**
     * Tracking service callback receiver
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMapUI(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //register tracking service callback receiver
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister tracking service callback receiver
        unregisterReceiver(broadcastReceiver);
    }

    CameraUpdate cu = null;

    private void updateMapUI(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            //hide progressBar
            if (waitBar != null) {
                waitBar.setVisibility(View.GONE);
            }
            //get data from intent
            String geoX = intent.getStringExtra(CAR_GEO_X);
            String geoY = intent.getStringExtra(CAR_GEO_Y);


            //get geoInfo for address from and show address from on map

            if (fromCoords == null) {
                if (fullOrderDescJSON != null && !fullOrderDescJSON.equals("")) {
                    try {
                        JSONArray addressesArray = fullOrderDescJSON.getJSONArray("addresses");
                        if (addressesArray != null && addressesArray.length() > 0) {
                            JSONObject addressFrom = addressesArray.getJSONObject(0);
                            JSONObject streetJSON = addressFrom.getJSONObject("street");
                            fromCoords = new LatLng(streetJSON.getDouble("geoy"), streetJSON.getDouble("geox"));
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "JSON error = " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }


            //show markers on map

            if (mMap == null) {
                mMap = ((SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map)).getMap();
                try {
                    MapsInitializer.initialize(getApplicationContext());
                } catch (Exception e) {
                    //may cause errors
                    throw new RuntimeException(e);
                }
            }

            //clear map before use
            if (mMap != null) {
                mMap.clear();
            }

            final ArrayList<LatLng> lg = new ArrayList<LatLng>();
            lg.add(fromCoords);



            final MarkerOptions carMarker = new MarkerOptions();
            final MarkerOptions addressMarker = new MarkerOptions();


            //set up car marker view
            //TODO make more beautiful
            LinearLayout view = new LinearLayout(this);
            view.setOrientation(LinearLayout.VERTICAL);
            view.setBackgroundResource(R.drawable.gray_without_corners);
            view.setPadding(10, 10, 10, 10);
            TextView tv = new TextView(this);
            tv.setText(intent.getStringExtra(CAR_BRAND));
            view.addView(tv);

            LinearLayout view2 = new LinearLayout(this);
            tv = new TextView(this);
            tv.setText(intent.getStringExtra(CAR_COLOR));
            view2.addView(tv);
            tv = new TextView(this);
            tv.setText(intent.getStringExtra(CAR_STATE_NUMBER));
            view2.addView(tv);
            view.addView(view2);

            //car marker
            //carMarker.icon(BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view)));
            carMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mm_or));



            addressMarker.position(fromCoords);
            if (geoX != null && geoY != null
                    && !geoX.equals("") && !geoY.equals("")) {
                LatLng currentCarCoords = new LatLng(Double.parseDouble(geoY), Double.parseDouble(geoX));
                lg.add(currentCarCoords);
                carMarker.position(currentCarCoords);

                //zoom camera to match all markers
                LatLngBounds.Builder b = new LatLngBounds.Builder();
                if (lg != null && lg.size() > 1) {
                    for (LatLng point : lg) {
                        if (point != null) {
                            b.include(point);
                        }
                    }
                }
                LatLngBounds bounds = b.build();
                cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
            }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (marker != null) {
                        marker.remove();
                    }
                    try {
                        marker = mMap.addMarker(carMarker);
                        marker = mMap.addMarker(addressMarker);
                        marker.showInfoWindow();
                        mMap.animateCamera(cu);
                    } catch (Exception e) {
                        Log.e(TAG, "Map error = " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Makes custom marker icon for car marker
     * @param v
     * @return
     */
    private Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            v.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GET_CURRENT_ORDER_INFO:
                    (new GetOrder(OrderWaitNew.this)).execute(orderID);
                    break;
                case GET_CURRENT_AUTO:
                    (new GetCurrentAuto(OrderWaitNew.this)).execute(currentOrder.getCallsignid());
                    break;
                case LAUNCH_SERVICE:
                    try {
                        stopService(new Intent(OrderWaitNew.this, WaitingCarService.class));
                    } catch (Exception e) {
                        Log.d(TAG, "problens closing previous service");
                    }
                    Log.d(TAG, "Starting service");
                    startService(serviceIntent);
                    setUpFrom();
                    break;
            }
        }
    };

    private void setUpFrom() {

        final long timeTillCar = currentOrder.getWhenDate() - System.currentTimeMillis() / 1000;
        //setUp timer
        orderWait_ct3 = new CountDownTimer(timeTillCar * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long s = millisUntilFinished / 1000;

                if (s >= 60) {
                    long q = s / 60;
                    long w = s - (60 * q);
                    if (Long.toString(w).length() == 1) {
                        orderWait_timer.setText(q + ":0" + w);
                    } else {
                        orderWait_timer.setText(q + ":" + w);
                    }
                } else if (s < 60) {
                    if (Long.toString(s).length() == 1) {
                        orderWait_timer.setText("0:0" + s);
                    } else {
                        orderWait_timer.setText("0:" + s);
                    }
                }
            }

            StringBuilder textBuilder;

            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                Boolean looper = true;
                                int secs = 0, mins;
                                secs = -1;
                                mins = 0;
                                if (timeTillCar < 0) {
                                    mins = Math.abs((int) (timeTillCar / 60));
                                    secs = (int) (60 + timeTillCar + mins * 60);
                                    Log.d(TAG, "min = " + mins + ", sec = " + secs);
                                }

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        poisk.setText("Автомобиль опаздывает");
                                        poisk.setTypeface(font_Roboto);
                                        poisk.setTextColor(Color.RED);
                                    }
                                });
                                while (looper) {
                                    try {
                                        secs++;
                                        if (secs == 60) {
                                            mins++;
                                            secs = 0;
                                        }
                                        textBuilder = new StringBuilder("");
                                        textBuilder.append(mins).append(":");
                                        if (secs < 10) {
                                            textBuilder.append("0");
                                        }
                                        textBuilder.append(secs);
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                orderWait_timer.setText(textBuilder);
                                                orderWait_timer.setTextColor(Color.RED);
                                            }
                                        });
                                        Thread.sleep(1000l);
                                    } catch (InterruptedException e) {
                                        //Mint.logException(e);
                                        e.printStackTrace();
                                    }

                                }

                                findViewById(R.id.include1).setVisibility(View.VISIBLE);
                            }
                        }).start();
                    }
                });

            }
        };
        orderWait_ct3.start();
    }

    /**
     * Method returns current order of interest ID
     * must be redone for multiple current orders
     * @return - current order ID
     */
    public int getCurrentID() {
        if (ordersIDList != null && ordersIDList.size() > 0) {
            //replace this getter for multiple orders
            return ordersIDList.get(ordersIDList.size() - 1);
        }
        return -1;
    }

    /**
     * Async class fetching assigned to current order carInfo
     */
    private class GetCurrentAuto extends AsyncTask<Integer, Void, autos>{

        private Context mContext;

        public GetCurrentAuto(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected autos doInBackground(Integer... params) {
            ServiceConnection sc = new ServiceConnection(mContext);
            return sc.getCoordAuto(String.valueOf(params[0]));
        }

        @Override
        protected void onPostExecute(autos mAuto) {
            super.onPostExecute(mAuto);
            if (mAuto != null) {
                long timerValue = currentOrder.getWhenDate() - System.currentTimeMillis() / 1000;
                String carInfo = "" + mAuto.getCarbrand() + mAuto.getCarcolor() + mAuto.getStatenumber();
                serviceIntent = createServiceIntent(mContext, timerValue, currentOrder.getWhenDate(),
                        String.valueOf(currentOrder.getCallsignid()), String.valueOf(currentOrder.getOrderID()), carInfo);
                mHandler.sendEmptyMessage(LAUNCH_SERVICE);
            } else {
                Intent errorIntent = new Intent(getApplicationContext(), HomeScreen.class);
                errorIntent.putExtra("methodName", "callDialog");
                startActivity(errorIntent);
            }
        }
    }

    /**
     * Async class Fetches full order info
     */
    private class GetOrder extends AsyncTask<Integer, Void, order_s> {

        private Context mContext;
        SharedPreferences mSettings;

        public GetOrder(Context mContext) {
            this.mContext = mContext;
            this.mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        }

        @Override
        protected order_s doInBackground(Integer... params) {

            ServiceConnection sc = new ServiceConnection(mContext);

            //get phone
            final String phone = mSettings.getString("Phone", "");

            List<order_s> allOrders = sc.loadHistory(phone);
            int orderID = params[0];
            for (order_s order : allOrders) {
                if (order.getOrderID() == orderID) {
                    fullOrderDescJSON = fullOrderInfo(mContext, order);
                    if (getIntent() != null && getIntent().hasExtra("callSignID")) {
                        Log.d(TAG, "In waiting callsignID = " + getIntent().getStringExtra("callSignID"));
                        order.setCallsignid(Integer.parseInt(getIntent().getStringExtra("callSignID")));
                    } else {
                        Log.d(TAG, "In waiting no callSignID");
                    }
                    return order;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(order_s mOrder) {
            super.onPostExecute(mOrder);
            if (mOrder != null) {
                currentOrder = mOrder;
                //Log.d(TAG, "callId = " + currentOrder.getCallsignid());
                mHandler.sendEmptyMessage(GET_CURRENT_AUTO);
            }
        }
    }


    /**
     * Method returns order info as JSON
     * @param mContext
     * @param ord_s
     * @return current order JSON
     */
    private JSONObject fullOrderInfo(Context mContext, final order_s ord_s) {

        //result JSON example:
        /*
        * orderJSON =
        * {"id":2083,
        * "time":1438704360,
        * "services":[15,34,33],
        * "classAvto":2,
        * "addresses":[{"street":
        *       {"value":"ТЕСТ ОФІС","id":"51483","geox":"30.49487","geoy":"50.41685"},
        *       "house":{"value":"1000","id":"657778","geox":"30.49487","geoy":"50.41685"}},
        *       {"street":{"value":"Авдєєнка генерала вул.","id":"51140","geox":"30.39537","geoy":"50.43567"},
        *       "house":{"value":"3","id":"587297","geox":"30.39449","geoy":"50.43512"}}]}
        * */


        final JSONObject orderJSON = new JSONObject();
        try {
            orderJSON.put("id", ord_s.getOrderID());
            orderJSON.put("time", ord_s.getWhenDate());
            JSONArray additionalServ = new JSONArray();
            ArrayList<Integer> mServices = ord_s.getAdditionalServices();
            for (int serv : mServices) {
                additionalServ.put(serv);
            }
            orderJSON.put("services", additionalServ);
            orderJSON.put("classAvto", ord_s.getCarClass());
            final ServiceConnection sc = new ServiceConnection(mContext);

            //array for addresses
            JSONArray address = new JSONArray();
            //maybe later will need to receive array of addresses. For now its only from and to
            // points consists of street and house JSON objects
            List<String> addressesList = new ArrayList<>();
            addressesList.add(ord_s.getFrom());
            String addrTo[] = ord_s.getTo().split("->");
            for (int i = 0; i < addrTo.length; i++)
                addressesList.add(addrTo[i]);
            //addressesList.add(ord_s.getTo());
            for (String fullAddress : addressesList) {
                String[] splitAddr = fullAddress.split(",");
                final String curStreet = splitAddr[0];
                final String curHouse = splitAddr[1].trim();
                try {
                    JSONObject point = new JSONObject();
                    //may be errors as method returns String of JSONArray nevertheless with
                    //only one element
                    JSONObject street = (JSONObject) (new JSONArray(sc.getStreetCustom(curStreet))).get(0);
                    // {"value":"ТЕСТ ОФІС","id":"51483","geox":"30.49487","geoy":"50.41685"}
                    int streetID = street.optInt("id");
                    JSONObject house = new JSONObject(sc.getHouseCustom(streetID, curHouse));
                    // {}
                    point.put("street", street);
                    point.put("house", house);
                    address.put(point);
                } catch (JSONException e) {
                    e.printStackTrace();
                    android.util.Log.d("CHECK_ADDR", "response house from web error = " + e.getMessage());
                }
            }
            try {
                orderJSON.put("addresses", address);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "orderJSON = " + orderJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //get here all order details and send as String JSON to HomeScreen activity
        return orderJSON;
    }
}
