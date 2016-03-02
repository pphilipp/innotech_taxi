package com.webcab.elit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.format.Time;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.webcab.elit.Utils.GetPriceAsync;
import com.webcab.elit.Utils.Utilits;
import com.webcab.elit.data.addr;
import com.webcab.elit.data.addr2;
import com.webcab.elit.data.autos;
import com.webcab.elit.data.order;
import com.webcab.elit.data.route;
import com.webcab.elit.net.ServiceConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


//public class HomeScreen extends MapActivity {
public class HomeScreen extends FragmentActivity implements OnDateSetListener, OnTimeSetListener, GetPriceAsync.OnRouteGot {
    private static final String CITY = "Киев";
    private static final int MAX_POINTS_IN_ROUTE = 4;
    private static final String TAG = "HOME_SCREEN_TAG";
    //dispatcher phone number
    private static final String DISP_NUMBER = "0442488248";
    // Asyntask
    MyTaxiApplication aController;
    AsyncTask<Void, Void, Void> mRegisterTask;
    // public static SlidingDrawer slidingDrawer;
    static order or;
    static autos auto;
    static SharedPreferences mSettings, mDataSettings;
    static Activity activity_home;
    static Typeface font_Roboto;
    static Typeface arialBlack;
    static PopupWindow window;
    static Long tm;
    static String orderId;
    static TextView orderWait_timer, createOrder_timer, poisk;
    static CountDownTimer createOrder_ct1, createOrder_ct2, orderWait_ct3;
    static ImageView radar;
    static boolean start_timer = false, looper = true;
    //	ImageView img_f;
    //	ImageView img_t;
    static boolean driving = false;
    public GoogleMap mMap;
    ImageView snow, pos, pat, wifi, smoke, bag, dragMinus;
    Button fr, to, dop;
    TextView client, cl_name, cl_phone;
    Button sendOrder;
    //Button handle;
    Button handle1;
    LinearLayout handle;
    boolean f, t;
    ImageView pin;
    route r = null;
    boolean refresh = false;
    boolean refreshMapWithAroute = false;
    boolean isDatePick = false, isTimePick = false;
    byte car_i = 1;
    int DIALOG_TIME = 1;
    int DIALOG_DATE = 2;
    //MapController mc;
    LatLng p;
    boolean isMap = false;
    CharSequence[] tmepl;
    List<addr> rt;
    LinearLayout dopL;
    Typeface font1;
    LocationManager locMan;
    String provider;
    Calendar calendar = null;
    Intent sIntent;
    View sRoot;
    Bundle bundle;
    MarkerOptions mm;
    Marker marker;
    CameraUpdate cuf;
    private ImageButton c, c1, c2, c3;
    private ImageView ci, ci1, ci2, ci3;
    private ImageView f1;
    private ImageView f2;
    private TextView ct0, t1, t2, t3, t4, ct1, ct2, ct3, txt_goN, txt_goT,
            txt_from, txt_to, txt_dop;
    private ImageView iv4;
    private ImageView iv1;
    private ImageButton i1;
    private ImageButton i4;

    //extra addresses
    private LinearLayout ll_points1, ll_points2, ll_points_add;
    private Button bt_to1, bt_to2, bt_to_add;
    private TextView txt_to1, txt_to2, txt_to_add;
    private ImageView f21, f22, f2_add;

    private long selDate = 0;
    private long curDate = 0;

    private double myLon = 0;
    private double myLat = 0;

    Boolean showCountDown = false;


    static boolean appIsActive = false;

    ContextThemeWrapper themedContext;

    private static final Boolean prelimEnabled = true;


    private boolean changed = false;

    SlidingUpPanelLayout mSliding;
    private TextView price;

    //address buttons TAGS
    public static final int FROM = 0;
    public static final int TO_1 = 1;
    public static final int TO_2 = 2;
    public static final int TO_3 = 3;
    public static final int ADD = 4;

    //point id extras name
    public static final String POINT_ID = "pointID";


    private OnClickListener getTemplClickListener;
    private OnClickListener deleteClickListener;

    private TextView titleTxt;
    private TextView valuesTxt;


    SharedPreferences app_preferences;
    SharedPreferences.Editor mEditor;

    static void RemoveViewFromWindow(final View view) {
        if (activity_home != null) {
            activity_home.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        ViewGroup v = (ViewGroup) window.getContentView();
                        v.removeView(view);
                        window.setContentView(v);
                    } catch (Exception e) {
                        ////Mint.logException(e);
                    }
                }
            });
        }

    }

    public static void printLog(Context context) {
        try {
//            android.os.Process.myPid();
            Time now = new Time();
            now.setToNow();
            android.util.Log.d("Play LOG!!! ", now.monthDay + "." + now.month + "." + now.year + " " + now.hour + ":" + now.minute);
            File filename = new File(Environment.getExternalStorageDirectory().toString().concat(File.separator).concat("WebCab"));//context.getExternalFilesDir(null).getPath()
            filename.mkdirs();
            String command = "logcat -f " + filename.getPath().concat(File.separator).concat("my_app.log") + " -v time -d *:D";
            Runtime.getRuntime().exec(command);
            android.util.Log.d("Stop LOG!!!", "command: " + command);

        } catch (Exception e) {
            ////Mint.logException(e);
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen2);


        aController = (MyTaxiApplication) getApplicationContext();
        SharedPreferences mSettingsReg = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            themedContext = new ContextThemeWrapper(HomeScreen.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        } else {
            themedContext = new ContextThemeWrapper(HomeScreen.this, android.R.style.Theme_Light_NoTitleBar);
        }


        Log.d("log_activity", "onresume homescreen");
        activity_home = this;
        font_Roboto = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

        arialBlack = Typeface.createFromAsset(getAssets(),
                "fonts/arialBlack.ttf");
//        printLog(this);


        titleTxt = (TextView) findViewById(R.id.price_titles_txt);
        valuesTxt = (TextView) findViewById(R.id.price_values_txt);

        snow = (ImageView) findViewById(R.id.snow);
        pos = (ImageView) findViewById(R.id.pos);
        pat = (ImageView) findViewById(R.id.pat);
        wifi = (ImageView) findViewById(R.id.wifi);
        smoke = (ImageView) findViewById(R.id.smoke);
        bag = (ImageView) findViewById(R.id.bag);

        mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        mDataSettings = getSharedPreferences("mydata", Context.MODE_PRIVATE);

        try {
            Runtime.getRuntime().exec("logcat -d log.log");
        } catch (IOException e) {
            ////Mint.logException(e);
            e.printStackTrace();
        }

        if (!isOnline()) {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        themedContext).create();
                alertDialog
                        .setMessage("Отсутствует интернет соединение. Попробуйте позже.");
                alertDialog.setButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        });

                alertDialog.show();
            } catch (Exception e) {
                //Mint.logException(e);
                Log.d(Constants.CONTENT_DIRECTORY,
                        "Show Dialog: " + e.getMessage());
            }
        }

        font1 = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

        c = (ImageButton) findViewById(R.id.с0);
        ct0 = (TextView) findViewById(R.id.ct0);
        ct0.setTypeface(font1);
        ci = (ImageView) findViewById(R.id.it_0_class_img);
        c1 = (ImageButton) findViewById(R.id.с1);

        ct1 = (TextView) findViewById(R.id.ct1);
        ct1.setTypeface(font1);
        ci1 = (ImageView) findViewById(R.id.it_1_class_img);
        c2 = (ImageButton) findViewById(R.id.с2);
        ct2 = (TextView) findViewById(R.id.ct2);
        ct2.setTypeface(font1);
        ci2 = (ImageView) findViewById(R.id.it_2_class_img);
        c3 = (ImageButton) findViewById(R.id.с3);
        ct3 = (TextView) findViewById(R.id.ct3);
        ct3.setTypeface(font1);
        ci3 = (ImageView) findViewById(R.id.it_3_class_img);

        //      price = (Button) findViewById(R.id.button2); // стоимость
        price = (TextView) findViewById(R.id.start_state_price_txt); // стоимость
        price.setTypeface(font1);
        sendOrder = (Button) findViewById(R.id.button_call); // заказать
        sendOrder.setTypeface(font1);


        //delete ClickListener
        deleteClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Tag of clicked view
                int viewTag = (int) v.getTag();
                //store addresses from longRoute into List (if exists
                List<addr2> deleteAddressList = new ArrayList<>();
                JSONArray deleteAddressesJSON = null;
                if (mSettings.contains("longRoute") && !mSettings.getString("longRoute", "").equals("")) {
                    try {
                        deleteAddressesJSON = new JSONArray(mSettings.getString("longRoute", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (deleteAddressesJSON != null) {
                    for (int i = 0; i < deleteAddressesJSON.length(); i++) {
                        try {
                            addr2 curAddr = Utilits.getAddressFromJSON((JSONObject) deleteAddressesJSON.get(i));
                            deleteAddressList.add(curAddr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //prepare prefs for writing
                SharedPreferences.Editor editor = mSettings.edit();
                switch (viewTag) {
                    case FROM:
                        //delete address "from" from prefs
                        editor.putString("Street", "");
                        editor.putString("StreetId", "");
                        editor.putString("HouseId", "");
                        editor.putString("Dom", "");
                        editor.putString("Parad", "");
                        editor.putString("fromx", "");
                        editor.putString("fromy", "");
                        editor.putString("Prim", "");
                        editor.putString("flat", "");
                        f = false;
                        txt_from.setText(getResources().getString(R.string.address_car_to_come));
                        txt_from.setTextColor(getResources().getColor(R.color.grey_text));
                        break;
                    case TO_1:
                        //delete first address where. longRoute (if exists)
                        if (deleteAddressList != null && deleteAddressList.size() > 0) {
                            //there are more than one address "where"
                            deleteAddressList.remove(0);
                        }
                        if (deleteAddressList != null && deleteAddressList.size() > 0) {
                            //if there left addresses in longRoute, store first one in Street2
                            editor.putString("Street2", deleteAddressList.get(0).getSreetname());
                            editor.putString("StreetId2", deleteAddressList.get(0).getStreetid());
                            editor.putString("HouseId2", deleteAddressList.get(0).getHouseID());
                            editor.putString("Dom2", deleteAddressList.get(0).getHousenumber());
                            editor.putString("tox", deleteAddressList.get(0).getGeox());
                            editor.putString("toy", deleteAddressList.get(0).getGeoy());
                            putLongRouteIntoPrefs(deleteAddressList);
                        } else {
                            //there are no addresses now
                            //delete from street2
                            editor.putString("Street2", "");
                            editor.putString("StreetId2", "");
                            editor.putString("HouseId2", "");
                            editor.putString("Dom2", "");
                            editor.putString("tox", "");
                            editor.putString("toy", "");
                            editor.putString("longRoute", "");
                        }
                        txt_to.setText(getResources().getString(R.string.arrive_address));
                        txt_to.setTextColor(getResources().getColor(R.color.grey_text));
                        t = false;
                        break;
                    case TO_2:
                        //delete second address where. delete from longRoute (it definitely exists)
                        if (deleteAddressList != null) {
                            deleteAddressList.remove(1);
                            putLongRouteIntoPrefs(deleteAddressList);
                        }
                        txt_to1.setText(getResources().getString(R.string.arrive_address));
                        txt_to1.setTextColor(getResources().getColor(R.color.grey_text));
                        break;
                    case TO_3:
                        //delete third and last address where. delete from longRoute
                        if (deleteAddressList != null) {
                            deleteAddressList.remove(2);
                            putLongRouteIntoPrefs(deleteAddressList);
                        }
                        txt_to2.setText(getResources().getString(R.string.arrive_address));
                        txt_to2.setTextColor(getResources().getColor(R.color.grey_text));
                        break;
                }
                editor.commit();
                //update form
                upDateHomescreen();
            }
        };

        //get template clickListener
        getTemplClickListener = new OnClickListener() {

            @Override
            public void onClick(final View v) {
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(
                        themedContext);
                alt_bld.setTitle("Выберите шаблон для 'куда'");
                alt_bld.setSingleChoiceItems(tmepl, -1, new DialogInterface

                        .OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        //get templ data and store it in addr2

                        addr2 templateAddr = new addr2(rt.get(item).getStr(), rt.get(item).getStrid(), rt.get(item).getDom(), "", "");
                        templateAddr.setHouseID(rt.get(item).getDomid());
                        int viewTag = (int) v.getTag();

                        String addr = CITY + ", " + rt.get(item).getStr()
                                + ", " + rt.get(item).getDom();

                        if (!rt.get(item).getParad().equals("")) {
                            addr += ", п. " + rt.get(item).getParad();
                        }
                        SharedPreferences.Editor editor = mSettings.edit();
                        JSONArray templJSONArray = null;
                        if (mSettings.contains("longRoute") && !mSettings.getString("longRoute", "").equals("")) {
                            try {
                                templJSONArray = new JSONArray(mSettings.getString("longRoute", ""));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        switch (viewTag) {
                            case FROM:
                                txt_from.setText(addr.toString());
                                txt_from.setTextColor(getResources().getColor(R.color.blue_WebCab));
                                f1.setImageResource(R.drawable.delete_blue);
                                f1.setOnClickListener(deleteClickListener);

                                editor.putString("Street", rt.get(item).getStr());
                                editor.putString("StreetId", rt.get(item)
                                        .getStrid());
                                editor.putString("Parad", rt.get(item).getParad());
                                editor.putString("Dom", rt.get(item).getDom());
                                editor.putString("HouseId", rt.get(item).getDomid());
                                editor.putString("Prim", rt.get(item).getPrim());
                                editor.commit();
                                f = true;
                                break;
                            case TO_1:
                                txt_to.setText(addr.toString());
                                txt_to.setTextColor(getResources().getColor(R.color.blue_WebCab));
                                f2.setImageResource(R.drawable.delete_blue);
                                f2.setOnClickListener(deleteClickListener);
                                editor.putString("Street2", rt.get(item).getStr());
                                editor.putString("StreetId2", rt.get(item)
                                        .getStrid());
                                editor.putString("Dom2", rt.get(item).getDom());
                                editor.putString("", rt.get(item)
                                        .getDomid());
                                editor.commit();
                                t = true;
                                break;
                            case ADD:
                                List<addr2> addresses = new ArrayList<addr2>();
                                if (mSettings.contains("StreetId2") && !mSettings.getString("StreetId2", "").equals("")) {
                                    addr2 to1Address = new addr2(mSettings.getString("Street2", ""), mSettings.getString("StreetId2", ""),
                                            mSettings.getString("Dom2", ""), "", "");
                                    if (mSettings.contains("HouseId2")) {
                                        to1Address.setHouseID(mSettings.getString("HouseId2", ""));

                                    }
                                    addresses.add(to1Address);
                                }
                                if (templJSONArray == null) {
                                    //second address "where" must be added. Create JSON and store it in prefs
                                    addresses.add(templateAddr);
                                    templJSONArray = putLongRouteIntoPrefs(addresses);
                                    //show button for second address "where"
                                    ll_points1.setVisibility(View.VISIBLE);
                                    txt_to1.setText(addr.toString());
                                    txt_to1.setTextColor(getResources().getColor(R.color.blue_WebCab));
                                    f21.setImageResource(R.drawable.delete_blue);
                                    f21.setOnClickListener(deleteClickListener);
                                    editor.putString("longRoute", templJSONArray.toString());
                                    editor.commit();
                                } else {
                                    try {
                                        addresses.add(Utilits.getAddressFromJSON((JSONObject) templJSONArray.get(0)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    addresses.add(templateAddr);
                                    templJSONArray = putLongRouteIntoPrefs(addresses);
                                    ll_points2.setVisibility(View.VISIBLE);
                                    txt_to2.setText(addr.toString());
                                    txt_to2.setTextColor(getResources().getColor(R.color.blue_WebCab));
                                    f22.setImageResource(R.drawable.delete_blue);
                                    f22.setOnClickListener(deleteClickListener);
                                    editor.putString("longRoute", templJSONArray.toString());
                                    editor.commit();
                                }
                                t = true;
                                break;
                        }

                        //setUp Add Address button state
                        final Boolean firstAddressOk = (!txt_from.getText().equals(getResources().getString(R.string.address_car_to_come)));
                        Boolean secondAddressOk = (!txt_to.getText().equals(getResources().getString(R.string.arrive_address)));
                        ll_points_add.setVisibility((firstAddressOk && secondAddressOk) ? View.VISIBLE : View.GONE);
                        if (ll_points2.getVisibility() == View.VISIBLE) {
                            ll_points_add.setVisibility(View.GONE);
                        }
                        getPrice();
                        dialog.dismiss();

                    }
                });
                AlertDialog alert = alt_bld.create();
                alert.show();
            }
        };

        //define extra addresses view elements
        ll_points1 = (LinearLayout) findViewById(R.id.ll_points1);
        ll_points2 = (LinearLayout) findViewById(R.id.ll_points2);
        ll_points_add = (LinearLayout) findViewById(R.id.ll_points_add);
        bt_to1 = (Button) findViewById(R.id.bt_to1);
        bt_to2 = (Button) findViewById(R.id.bt_to2);
        bt_to_add = (Button) findViewById(R.id.bt_to_add);
        bt_to1.setTypeface(font1);
        bt_to2.setTypeface(font1);
        bt_to_add.setTypeface(font1);
        txt_to1 = (TextView) findViewById(R.id.txt_to1);
        txt_to2 = (TextView) findViewById(R.id.txt_to2);
        txt_to_add = (TextView) findViewById(R.id.txt_to_add);
        txt_to1.setTypeface(font1);
        txt_to2.setTypeface(font1);
        txt_to_add.setTypeface(font1);


        ifCar();


        t1 = (TextView) findViewById(R.id.txt_t1);
        t1.setTypeface(font1);
        t2 = (TextView) findViewById(R.id.txt_t2);
        t2.setTypeface(font1);
        t3 = (TextView) findViewById(R.id.txt_t3);
        t3.setTypeface(font1);
        t4 = (TextView) findViewById(R.id.txt_t4);
        t4.setTypeface(font1);

        txt_goN = (TextView) findViewById(R.id.txt_go_now);
        txt_goN.setTypeface(font1);
        txt_goT = (TextView) findViewById(R.id.txt_go_time);
        txt_goT.setTypeface(font1);

        txt_dop = (TextView) findViewById(R.id.txt_dop);
        txt_dop.setTypeface(font1);

        if (mSettings.contains("Time")) {
            if (!mSettings.getString("Time", "").equals("")) {
                //txt_goN.setTextColor(Color.rgb(21, 102, 255));
                txt_goN.setTextColor(getResources().getColor(R.color.blue_WebCab));
                txt_goN.setText(mSettings.getString("Time", ""));
                txt_goT.setText("Или уехать сейчас");
                txt_goT.setTextColor(Color.rgb(169, 169, 169));
            } else {
                txt_goN.setText("Уехать сейчас");
                //txt_goN.setTextColor(Color.rgb(21, 102, 255));
                txt_goN.setTextColor(getResources().getColor(R.color.blue_WebCab));

                txt_goT.setText("Или уехать в другое время");
                txt_goT.setTextColor(Color.rgb(169, 169, 169));
            }
        }
        txt_from = (TextView) findViewById(R.id.txt_from);

        if ((mSettings.contains("Street")) && (mSettings.contains("Dom"))
                && (mSettings.contains("Parad"))) {
            if ((!mSettings.getString("Street", "").equals(""))
                    && (!mSettings.getString("Dom", "").equals(""))) {
                String str = mSettings.getString("Street", "");
                String dom = mSettings.getString("Dom", "");
                String parad = mSettings.getString("Parad", "");
                String addr = /*"Киев, " + */str + ", " + dom;
                if (!parad.equals("")) {
                    addr += ", п. " + parad;
                }

                Log.d("shared", "geted street - " + str);
                Log.d("shared", "geted Dom - " + dom);
                Log.d("shared", "geted parad - " + parad);
                Log.d("shared", "addr - " + addr);
                txt_from.setText(addr.toString());
                txt_from.setTextColor(Color.rgb(76, 77, 79));

                f = true;
            }
        }


        txt_from.setTypeface(font1);
        txt_to = (TextView) findViewById(R.id.txt_to);

        if ((mSettings.contains("Street2")) && (mSettings.contains("Dom2"))) {
            if ((!mSettings.getString("Street2", "").equals(""))
                    && (!mSettings.getString("Dom2", "").equals(""))) {
                String str = mSettings.getString("Street2", "");
                String dom = mSettings.getString("Dom2", "");
                String parad = mSettings.getString("Parad", "");
                String addr = "Киев, " + str + ", " + dom;
                if (!parad.equals("")) {
                    addr += ", п. " + parad;
                }
                Log.d("shared", "geted street2 - " + str);
                Log.d("shared", "geted Dom2 - " + dom);
                Log.d("shared", "addr - " + addr);
                txt_to.setText(addr.toString());
                txt_to.setTextColor(Color.rgb(76, 77, 79));

                t = true;
            }
        }

        if (mSettings.contains("NasP")) {
            if (!mSettings.getString("NasP", "").equals("")) {
                txt_to.setText(mSettings.getString("NasP", ""));
                txt_to.setTextColor(Color.rgb(76, 77, 79));

                t = true;
            }
        }

        rt = new ArrayList<addr>();

        DBPoints dbHelper = new DBPoints(HomeScreen.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cr = db.query("addr", null, null, null, null, null, null);

        if (cr.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = cr.getColumnIndex("id");
            int titleColIndex = cr.getColumnIndex("title");
            int descColIndex = cr.getColumnIndex("desc");

            int strColIndex = cr.getColumnIndex("str");
            int stridColIndex = cr.getColumnIndex("strid");
            int domColIndex = cr.getColumnIndex("dom");
            int domidColIndex = cr.getColumnIndex("domid");
            int paradColIndex = cr.getColumnIndex("parad");
            int primColIndex = cr.getColumnIndex("prim");

            do {

                Log.d("MA",
                        "ID = " + cr.getInt(idColIndex) + ", name = "
                                + cr.getString(titleColIndex) + ", desc = "
                                + cr.getString(descColIndex));

                rt.add(new addr(cr.getInt(idColIndex), cr
                        .getString(titleColIndex), cr.getString(descColIndex),
                        cr.getString(strColIndex), cr.getString(stridColIndex),
                        cr.getString(domColIndex), cr.getString(domidColIndex),
                        cr.getString(paradColIndex), cr.getString(primColIndex)));

            } while (cr.moveToNext());
        } else {
            Log.d("MA", "0 rows");
        }

        cr.close();

        dbHelper.close();

        tmepl = new CharSequence[rt.size()];
        for (int i = 0; i < rt.size(); i++) {
            tmepl[i] = rt.get(i).getTitle() + " " + rt.get(i).getStr() + " "
                    + rt.get(i).getDom();
        }

        f1 = (ImageView) findViewById(R.id.f1);
        f2 = (ImageView) findViewById(R.id.f2);
        f21 = (ImageView) findViewById(R.id.f21);
        f22 = (ImageView) findViewById(R.id.f22);
        f2_add = (ImageView) findViewById(R.id.f2_add);

        f1.setTag(FROM);
        f2.setTag(TO_1);
        f21.setTag(TO_2);
        f22.setTag(TO_3);
        f2_add.setTag(ADD);


        Bundle extras = getIntent().getExtras();


        if (rt.size() > 0) {
            Log.d("exist", "rt.size - " + rt.size());
            //Bundle extras = getIntent().getExtras();

            if (extras != null) {
                Log.d("CAR", "here");
                if (extras.containsKey("templ_id")) {
                    int id = extras.getInt("templ_id");

                    setUpOrderForm(id);
                }

            }


        } else {
            setUpRightActionIcon();
        }

        //Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("routeId")) {

                clearForm();
                int id = extras.getInt("routeId");

                Log.d("hemo", "" + id);

                com.webcab.elit.data.rt rt = null;

                DBRoutes dbHelper2 = new DBRoutes(HomeScreen.this);
                // подключаемся к БД
                db = dbHelper2.getWritableDatabase();

                cr = db.query("route", null, "id=" + id, null, null, null,
                        null);

                // ставим позицию курсора на первую строку выборки
                // если в выборке нет строк, вернется false
                if (cr.moveToFirst()) {

                    // определяем номера столбцов по имени в выборке
                    int idColIndex = cr.getColumnIndex("id");
                    int titleColIndex = cr.getColumnIndex("title");
                    int descColIndex = cr.getColumnIndex("desc");

                    int strColIndex = cr.getColumnIndex("str");
                    int stridColIndex = cr.getColumnIndex("strid");
                    int domColIndex = cr.getColumnIndex("dom");
                    int domidColIndex = cr.getColumnIndex("domid");
                    int paradColIndex = cr.getColumnIndex("parad");
                    int primColIndex = cr.getColumnIndex("prim");
                    int autoColIndex = cr.getColumnIndex("auto");

                    int descColIndex2 = cr.getColumnIndex("desc2");
                    int strColIndex2 = cr.getColumnIndex("str2");
                    int stridColIndex2 = cr.getColumnIndex("strid2");
                    int domColIndex2 = cr.getColumnIndex("dom2");
                    int domidColIndex2 = cr.getColumnIndex("domid2");

                    do {

                        Log.d("MA", "ID = " + cr.getInt(idColIndex)
                                + ", name = " + cr.getString(titleColIndex)
                                + ", desc = " + cr.getString(descColIndex));

                        rt = new com.webcab.elit.data.rt(cr.getInt(idColIndex),
                                cr.getString(titleColIndex),
                                cr.getString(descColIndex),
                                cr.getString(strColIndex),
                                cr.getString(stridColIndex),
                                cr.getString(domColIndex),
                                cr.getString(domidColIndex),
                                cr.getString(paradColIndex),
                                cr.getString(primColIndex),
                                cr.getString(descColIndex2),
                                cr.getString(strColIndex2),
                                cr.getString(stridColIndex2),
                                cr.getString(domColIndex2),
                                cr.getString(domidColIndex2),
                                cr.getString(autoColIndex));

                    } while (cr.moveToNext());
                } else {
                    Log.d("MA", "0 rows");
                }

                cr.close();

                dbHelper.close();

                if (!f && !t && rt != null) {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("Street", rt.getStr());
                    editor.putString("StreetId", rt.getStrid());
                    editor.putString("Dom", rt.getDom());
                    editor.putString("HouseId", rt.getDomid());
                    editor.putString("Parad", rt.getParad());
                    editor.putString("Prim", rt.getPrim());
                    editor.putString("Car", rt.getAutoClass());


                    editor.putString("Street2", rt.getStr2());
                    editor.putString("StreetId2", rt.getStrid2());
                    editor.putString("Dom2", rt.getDom2());
                    editor.putString("HouseId2", rt.getDomid2());
                    editor.commit();


                    ifCar();

                    String addr = /*"Киев, " +*/ rt.getStr() + ", "
                            + rt.getDom();

                    String addr2 = /*"Киев, " +*/ rt.getStr2() + ", "
                            + rt.getDom2();

                    if (!rt.getParad().equals("")) {
                        addr += ", п. " + rt.getParad();
                    }

                    txt_from.setText(addr.toString());
                    txt_from.setTextColor(Color.rgb(76, 77, 79));
                    f1.setImageResource(R.drawable.delete_blue);
                    f1.setOnClickListener(deleteClickListener);

                    txt_to.setText(addr2.toString());
                    txt_to.setTextColor(Color.rgb(76, 77, 79));
                    f2.setImageResource(R.drawable.delete_blue);
                    f2.setOnClickListener(deleteClickListener);

                    t = true;
                    f = true;
                    getPrice();
                }
            }
        }


        txt_to.setTypeface(font1);


        i1 = (ImageButton) findViewById(R.id.it_1);
        i4 = (ImageButton) findViewById(R.id.it_4);
        iv4 = (ImageView) findViewById(R.id.it_4_img);
        i1.setPressed(true);
        iv1 = (ImageView) findViewById(R.id.it_1_img);
        i1.setBackgroundResource(R.drawable.top_bar_grad_active);
        i1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                /*if (!slidingDrawer.isOpened())
                    slidingDrawer.animateOpen();*/
                if (mSliding.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED)
                    h.sendEmptyMessage(EXPAND_SLIDER);
                iv1.setImageResource(R.drawable.car_active);
                t1.setTextColor(getResources().getColor(R.color.blue_WebCab));
                t4.setTextColor(Color.parseColor("#080000"));
                iv4.setImageResource(R.drawable.map_icon_inactive);
            }
        });

        iv1.setImageResource(R.drawable.car_active);
        t1.setTextColor(getResources().getColor(R.color.blue_WebCab));
        ImageButton i2 = (ImageButton) findViewById(R.id.it_2);
        i2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ""
                refresh = false;
                Intent intent = new Intent(HomeScreen.this, Templates.class);
                startActivity(intent);
                finish();
            }
        });

        ImageButton i3 = (ImageButton) findViewById(R.id.it_3);

        i3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                refresh = false;
                Intent intent = new Intent(HomeScreen.this, Cabinet.class);
                startActivity(intent);
                finish();
            }
        });

        iv4 = (ImageView) findViewById(R.id.it_4_img);
        iv4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ""
                refresh = false;
                /*Intent intent = new Intent(HomeScreen.this, Settings.class);
                startActivity(intent);
                finish();*/
                h.sendEmptyMessage(COLLAPSE_SLIDER);
                refresh = false;
            }
        });

        MapsInitializer.initialize(this);
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            try {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.4331, 30.5128), 8));
            } catch (Exception e) {
                //throw new RuntimeException(e);
                Log.d("CATCH_TAG", "Camera map error");
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }

        mSliding = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);


        handle1 = (Button) findViewById(R.id.handle1);
        handle = (LinearLayout) findViewById(R.id.handle);
        dragMinus = (ImageView) findViewById(R.id.drag_minus);

        //set open/close slider listener
        mSliding.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                dragMinus.setVisibility(View.GONE);
            }

            @Override
            public void onPanelCollapsed(View panel) {

                //Slider shows map
                dragMinus.setVisibility(View.VISIBLE);


                //setUp upper pannel
                iv4.setImageResource(R.drawable.map_icon_active);
                iv1.setImageResource(R.drawable.car_inactive);
                t4.setTextColor(getResources().getColor(R.color.blue_WebCab));
                t1.setTextColor(Color.parseColor("#080000"));
                i1.setBackgroundResource(R.drawable.top_bar_grad_inactive);
                i4.setBackgroundResource(R.drawable.top_bar_grad_active);

                if (r == null) {
                    getPrice();
                } /*else {
                    workWithMap();
                }*/
                workWithMap();
            }

            @Override
            public void onPanelExpanded(View panel) {
                //Slider shows buttons
                dragMinus.setVisibility(View.GONE);
                if (isMap) { // is map displayed
                    refresh = false;
                }
                iv4.setImageResource(R.drawable.map_icon_inactive);
                iv1.setImageResource(R.drawable.car_active);
                t4.setTextColor(Color.parseColor("#080000"));
                i1.setBackgroundResource(R.drawable.top_bar_grad_active);
                i4.setBackgroundResource(R.drawable.top_bar_grad_inactive);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });


        //slidingDrawer.animateClose();
        //h.sendEmptyMessage(EXPAND_SLIDER);


        Thread t12 = new Thread(new Runnable() {
            public void run() {

                ServiceConnection sc = new ServiceConnection(
                        HomeScreen.this);

                final List<autos> autos = sc.getCoordAll();

                runOnUiThread(new Runnable() {
                    public void run() {
                        handle1.setText("Cейчас на линии: "
                                + Html.fromHtml("<font color=green>"
                                + autos.size() + "</font> "));
                    }
                });
            }
        });
        t12.start();

        ci.setImageResource(R.drawable.cars);
        ci1.setImageResource(R.drawable.y_car);
        ci2.setImageResource(R.drawable.gr_car);
        ci3.setImageResource(R.drawable.bl_car);
        if (mSettings.contains("Car") && !mSettings.getString("Car", "").equals("")) {
            int autoClass = Integer.parseInt(mSettings.getString("Car", ""));
            setCarId(autoClass);
        } else {
            setCarId(1);
        }


        c.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (car_i != 0) {
                    setCarId(0);
                    getPrice();
                }
            }
        });

        c1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (car_i != 1) {
                    setCarId(1);

                    getPrice();
                }
            }
        });

        c2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (car_i != 2) {
                    setCarId(2);
                    getPrice();
                }
            }
        });

        c3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (car_i != 3) {
                    setCarId(3);
                    getPrice();
                }
            }
        });


        fr = (Button) findViewById(R.id.bt_from);
        fr.setTag(FROM);
        fr.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                refresh = false;
                Intent intent = new Intent(HomeScreen.this, Order_from.class);
                startActivity(intent);
                //finish();
            }
        });

        to = (Button) findViewById(R.id.bt_to);
        to.setTag(TO_1);
        to.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                refresh = false;
                if ((mSettings.contains("NasP"))
                        && (!mSettings.getString("NasP", "").equals(""))) {
                    Intent intent = new Intent(HomeScreen.this, ZaGorod.class);
                    startActivity(intent);
                    //	finish();
                } else {
                    //Intent intent = new Intent(HomeScreen.this, Order_to.class);
                    //Intent intent = new Intent(HomeScreen.this, OrderTo1.class);
                    /*Intent intent = new Intent(HomeScreen.this, Order_To3.class);
                    startActivity(intent);*/
                    //	finish();

                    Intent intent = new Intent(HomeScreen.this, AddAddress.class);
                    intent.putExtra(POINT_ID, ((int) v.getTag()));
                    startActivity(intent);
                }
            }
        });


        OnClickListener addressClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh = false;
                int orderType = (int) v.getTag();
                Intent intent;
                if (orderType == FROM) {
                    intent = new Intent(HomeScreen.this, Order_from.class);
                } else {
                    intent = new Intent(HomeScreen.this, AddAddress.class);
                    intent.putExtra(POINT_ID, ((int) v.getTag()));
                }
                startActivity(intent);
            }
        };

        bt_to1.setTag(TO_2);
        bt_to2.setTag(TO_3);
        bt_to_add.setTag(ADD);
        bt_to1.setOnClickListener(addressClickListener);
        bt_to2.setOnClickListener(addressClickListener);
        bt_to_add.setOnClickListener(addressClickListener);

        dop = (Button) findViewById(R.id.bt_dop);
        dop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                refresh = false;
                Intent intent = new Intent(HomeScreen.this, Order_dop.class);
                startActivity(intent);
                //	finish();
            }
        });

        dopL = (LinearLayout) findViewById(R.id.dop_layout);

        //get cost button and set clickListener to update price onClick
        LinearLayout mCostLayout = (LinearLayout) findViewById(R.id.cost_info_btn);
        mCostLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrice();
            }
        });


        sendOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (mDataSettings.getString("Name", "").equals("")) {
                    AlertDialog dialog = new AlertDialog.Builder(themedContext).setTitle("Внимание")
                            .setMessage("Укажите своё имя в Кабинете")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(HomeScreen.this, CabinetMyData.class));
                                }
                            }).create();
                    dialog.show();
                } else {
                    if (f && t) {
                        long preorderTime = mSettings.getLong("preorderTime", 0);
                        //final long currentTime = Calendar.getInstance().getTimeInMillis();
                        final long currentTime = System.currentTimeMillis();
                        final long orderTime = mSettings.getLong("timeToServer", currentTime);
                        if (orderTime - currentTime > preorderTime) {
                            //preliminary order
                            (new SendPrelimOrder()).execute(orderTime);
                        } else {
                            refresh = false;

                            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            nm.cancelAll();
                            driving = false;
                            stopService(new Intent(HomeScreen.this, WaitingCarService.class));
                            //mMap.clear();
                            //	new Handler().postDelayed(new Runnable() {

                            //		@Override
                            //		public void run() {
                            drawMeAndStartWaitActivity();
                            //			}
                            //		}, 1000);
                            //finish();                        }
                        }
                    } else {
                        // SERVICE SERVICE SERVICE SERVICE
                        // test test test


                        //					Intent intent = new Intent(HomeScreen.this, MyService.class);
                        //					intent.putExtra("seconds", 50l);
                        //					intent.putExtra("driverId", "4867");
                        //
                        //					SharedPreferences.Editor editor = mSettings.edit();
                        //					editor.putString("DriverId", "4867");
                        //					editor.commit();
                        //
                        //					startService(intent);


                        // test test test
                        // SERVICE SERVICE SERVICE SERVICE
                        try {
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    themedContext).create();

                            alertDialog
                                    .setMessage("Вы должны сначала заполнить данные заказа.");
                            alertDialog.setButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                        }
                                    });

                            alertDialog.show();
                        } catch (Exception e) {
                            //Mint.logException(e);
                            Log.d(Constants.CONTENT_DIRECTORY,
                                    "Show Dialog: " + e.getMessage());
                        }
                    }
                }
            }
        });

        //STOP

        //		img_f = (ImageView) findViewById(R.id.img_from);
        //		img_t = (ImageView) findViewById(R.id.img_to);
        //
        //		registerForContextMenu(img_f);
        //		registerForContextMenu(img_t);


        client = (TextView) findViewById(R.id.txt_client);
        client.setTypeface(font1);
        cl_name = (TextView) findViewById(R.id.txt_cl_name);
        cl_name.setTypeface(font1);
//		cl_phone = (TextView) findViewById(R.id.txt_cl_phone);
//		cl_phone.setTypeface(font1);

        if (!mSettings.getString("Name", "").equals("") ||
                !mSettings.getString("uPhone", "").equals("")) {

            cl_name.setText("ЕДУ НЕ Я");
            cl_name.setTextColor(Color.rgb(169, 169, 169));
            String clientName = mSettings.getString("Name", "").equals("")
                    ? mDataSettings.getString("Name", "")
                    : mSettings.getString("Name", "");
            String clientPhone = mSettings.getString("uPhone", "").equals("")
                    ? mSettings.getString("Phone", "")
                    : mSettings.getString("uPhone", "");
            client.setText("ЕДЕТ, " + clientName + " " + clientPhone);
            //client.setTextColor(Color.rgb(21, 102, 255));
            client.setTextColor(getResources().getColor(R.color.blue_WebCab));
//			cl_phone.setText(mSettings.getString("uPhone", ""));
        } else {
            cl_name.setText("ЕДУ Я, " + mSettings.getString("Phone", ""));
            //cl_name.setTextColor(Color.rgb(21, 102, 255));
            cl_name.setTextColor(getResources().getColor(R.color.blue_WebCab));
            client.setText("Едет другой пассажир");
            client.setTextColor(Color.rgb(169, 169, 169));
//            cl_phone.setText(mSettings.getString("Phone",""));
        }

        Button cl = (Button) findViewById(R.id.bt_client);
        cl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ClientInfo.class);
                startActivity(intent);
                // HomeScreen.	finish();
            }
        });

        Button clear = (Button) findViewById(R.id.but_clear);
        clear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                refreshMapWithAroute = false;
                try {
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            themedContext).create();
                    alertDialog.setMessage(themedContext.getResources().getString(R.string.sure_to_clear_form));
                    alertDialog.setButton(themedContext.getResources().getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    clearForm();
                                }
                            });
                    alertDialog.setCancelable(false);
                    alertDialog.setButton2("Нет", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();
                } catch (Exception e) {
                    //Mint.logException(e);
                    Log.d(Constants.CONTENT_DIRECTORY,
                            "Show Dialog: " + e.getMessage());
                }

            }
        });

        getPrice();

        //edit preliminary order
        if (extras != null && extras.containsKey("isPreorder")
                && extras.getBoolean("isPreorder")) {

            clearForm();
            setUpFormByPreliminaryOrder(extras.getString("orderJSON"));


        }


        setUpRightActionIcon();

        //setUp map screen on map button pressed
        if (getIntent().hasExtra("showMap") && getIntent().getBooleanExtra("showMap", false)) {
            getIntent().removeExtra("show map");
            h.sendEmptyMessage(COLLAPSE_SLIDER);
        } else {
            h.sendEmptyMessage(EXPAND_SLIDER);
        }

        //add here push event
       /* if (getIntent().hasExtra("push") && getIntent().getBooleanExtra("push", false)) {
            Log.d(TAG, "payLoad (HomeScreen) = ok, let us try");
            Intent mIntent = getIntent();
            h.sendEmptyMessage(COLLAPSE_SLIDER);
            if (or == null) {
                Log.d(TAG, "time = " + mIntent.getLongExtra("tm", 0));
                //(new getOrderInfo(mIntent)).execute();
                or = new order(Integer.valueOf(mIntent.getStringExtra("orderId")), "66666",
                        Integer.valueOf(mIntent.getStringExtra("driverId")), "", "",
                        "", "", "", "",
                        "", mIntent.getLongExtra("tm", 0), "phone");
            } else {
                Log.d(TAG, "here1");

            }
            //orderWait(mIntent);
            //startActivity(new Intent(HomeScreen.this, OrderWaitNew.class));
        } else {
            Log.d(TAG, "payLoad (HomeScreen) = no payLoad");
        }*/

        final SharedPreferences mSettingsOrder = getSharedPreferences(Utilits.Constants.ORDER_SETTINGS_NAME, Context.MODE_PRIVATE);
        if (mSettingsOrder.contains(Utilits.Constants.CURRENT_ORDER_LIST)
                && !mSettingsOrder.getString(Utilits.Constants.CURRENT_ORDER_LIST, "").equals("[]")
                && mSettingsOrder.contains(Utilits.Constants.ORDER_TYPE)) {


            switch (mSettingsOrder.getInt(Utilits.Constants.ORDER_TYPE, Utilits.Constants.NO_CURRENT_ORDERS)) {
                case Utilits.Constants.SIMPLE_CURRENT_ORDER:
                    //Build dialog with current orders
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(themedContext);
                    mBuilder.setTitle(R.string.attention)
                            .setMessage(R.string.you_have_orders)
                            .setPositiveButton(R.string.open_order, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO choose order to open
                                    mSettingsOrder.edit().putInt(Utilits.Constants.ORDER_TYPE, Utilits.Constants.NO_CURRENT_ORDERS);
                                    Intent orderWaitIntent = new Intent(HomeScreen.this, OrderWaitNew.class);
                                    orderWaitIntent.putExtra("orderIDs", mSettingsOrder.getString("currentOrders", ""));
                                    if (getIntent() != null && getIntent().hasExtra("driverId")) {
                                        orderWaitIntent.putExtra("callSignID", getIntent().getStringExtra("driverId"));
                                        Log.d(TAG, "callSignID = " + getIntent().getStringExtra("driverId"));
                                    } else {
                                        Log.d(TAG, "callSignID = no ID");
                                    }
                                    startActivity(orderWaitIntent);
                                }
                            })
                            .setNegativeButton(R.string.main_menu, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mSettingsOrder.edit().putInt(Utilits.Constants.ORDER_TYPE, Utilits.Constants.NO_CURRENT_ORDERS);
                                    clearForm();
                                    Intent mIntent = new Intent(HomeScreen.this, HomeScreen.class);
                                    mIntent.putExtra("methodName", "");
                                    startActivity(mIntent);

                                }
                            });
                    mBuilder.create().show();
                    break;
                case Utilits.Constants.NOTIFICATION_CURRENT_ORDER:
                    mSettingsOrder.edit().putInt(Utilits.Constants.ORDER_TYPE, Utilits.Constants.NO_CURRENT_ORDERS);
                    Intent orderWaitIntent = new Intent(HomeScreen.this, OrderWaitNew.class);
                    orderWaitIntent.putExtra("orderIDs", mSettingsOrder.getString("currentOrders", ""));
                    startActivity(orderWaitIntent);
                    break;
                case Utilits.Constants.NO_CURRENT_ORDERS:
                    break;
            }





        }

        if (getIntent() != null
                && getIntent().hasExtra("methodName")) {
            if (getIntent().getStringExtra("methodName").equals("ordercarhere")) {
                Log.d(TAG, "car here");
                orderCarHere(getIntent());
            }
            if (getIntent().getStringExtra("methodName").equals("orderwait")) {
                Log.d(TAG, "car wait");
                orderWait(getIntent());
            }

        } else {
            Log.d(TAG, "car here error");
        }


    }

    private class SendPrelimOrder extends AsyncTask<Long, Void, Void> {

        private Dialog  waitDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            sendOrder.setEnabled(false);
            waitDialog = new Dialog(themedContext);
            waitDialog.setContentView(R.layout.wait_dialog);
            waitDialog.setTitle(R.string.wait);
            waitDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (waitDialog != null) {
                waitDialog.dismiss();
            }
            sendOrder.setEnabled(true);
        }

        @Override
        protected Void doInBackground(Long... params) {
            String fromStr = mSettings.getString("Street", "") + ", " + mSettings.getString("Dom", "");
            String price = mSettings.getString("Price", "");
            String toStr = mSettings.getString("Street2", "") + ", " + mSettings.getString("Dom2", "");
            String flat = mSettings.getString("flat", "0");
            long orderTime = params[0];
            ServiceConnection serviceConnection = new ServiceConnection(HomeScreen.this);
            final JSONObject response = serviceConnection.createPrelimOrder(HomeScreen.this, fromStr, price, toStr, orderTime, flat);
            if (response != null && response.optBoolean("isadvanced")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(themedContext);
                        confirmDialog.setTitle(R.string.prelim_order_confirm_title);
                        confirmDialog.setMessage(response.optString("message"));
                        confirmDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        confirmDialog.create().show();
                        clearForm();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(themedContext);
                        confirmDialog.setTitle(R.string.error);
                        confirmDialog.setMessage(R.string.prelim_error_message);
                        confirmDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        confirmDialog.create().show();
                        clearForm();
                    }
                });
            }
            return null;
        }
    }

    private void workWithMap() {

        int zoom = 11;

        String coordinates[] = {"50.3", "30.50"};
        pin = (ImageView) findViewById(R.id.pin);
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);

        p = new LatLng(lat, lng);

        final ArrayList<LatLng> lg = new ArrayList<LatLng>();

        mMap.clear();

        refresh = false;

        if (mSettings.contains("DriverId") && !mSettings.getString("DriverId", "").equals("")) {

            Log.d("MAP_TAG", "there is driver");

            Thread t = new Thread(new Runnable() {
                public void run() {

                    ServiceConnection sc = new ServiceConnection(
                            HomeScreen.this);
                    Log.d("DriverId", "3");

                    refresh = true;

                    while (refresh) {

                        Log.d("DriverId", "4");

                        //для тестов
                        auto = sc.getCoordAuto(mSettings.getString("DriverId", ""));
                        //final autos auto = new autos(1, "2010", "6", "1", "101010", "10343", "АА 4702 НН", "Мазда 6", "Черный",  "Бирак Ярослав Федорович", "30.5207", "50.4533");
                        //mapView.setClickable(true); // разблокируем

                        Log.d("DriverId", "5");

                        if (auto != null && auto.getStatusforweborder() == 0) {

                            Log.d("DriverId", "6");

                            runOnUiThread(new Runnable() {
                                public void run() {

                                    //--------------------------------------

                                    mMap.clear();
                                    if (auto != null && auto.getGeox() != null && auto.getGeoy() != null
                                            && !auto.getGeox().equals("") && !auto.getGeoy().equals("")) {
                                        mMap.addMarker(new MarkerOptions().position(
                                                new LatLng(Double.parseDouble(auto.getGeoy()),
                                                        Double.parseDouble(auto.getGeox())))
                                                .title(auto.getDrivename()).snippet(auto.getCarbrand()
                                                        + ", " + auto.getCarcolor() + " ("
                                                        + auto.getStatenumber() + ")").icon(BitmapDescriptorFactory.fromResource(R.drawable.mm_or)));
                                    }
                                    //--------------------------------------
                                }
                            });
                        } else if (auto != null && auto.getStatusforweborder() == 1) {

                            refresh = false;

                            SharedPreferences.Editor edit = mSettings.edit();

                            edit.putString("DriverId", "");

                            edit.commit();

                        }
                        if (locMan != null && provider != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Location loc = locMan.getLastKnownLocation(provider);
                                    if (loc != null)
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("Я"));

                                }
                            });
                        }
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            //Mint.logException(e);
                            // ""
                            Log.d("error", e.getMessage());
                        }

                        // refresh = false; //just 1 iteration

                    }
                }
            });
            t.start();


        } else {


            Thread t132 = new Thread(new Runnable() {
                public void run() {


                    final ArrayList<LatLng> lg = new ArrayList<LatLng>();
                    Log.d("MAP_TAG", "t = " + t + ", f = " + f);
                    if (f && t) {
                        //Log.d("MAP_TAG", "r = " + r.getDistance());
                        if (r != null) {
                            for (int y = 0; y < r.getRouteY().size(); y++) {
                                if ((r.getRouteX() != null) && (r.getRouteY() != null)) {
                                    lg.add(new LatLng(Double.parseDouble(r
                                            .getRouteY().get(y)), Double
                                            .parseDouble(r.getRouteX().get(y))));
                                    p = lg.get(0);
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lg.get(0), 17), 2000, null);

                                }
                            });

                        }
                    }

                    ServiceConnection sc = new ServiceConnection(
                            HomeScreen.this);

                    //scale mMap camera to fit route

                    if (lg != null && lg.size() > 1) {
                        Log.d("MAP_TAG", "lg.size = " + lg.size());
                        LatLngBounds.Builder b = new LatLngBounds.Builder();
                        for (LatLng point : lg) {
                            b.include(point);
                        }
                        LatLngBounds bounds = b.build();
                        //Change the padding as per needed
                        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 35);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMap.animateCamera(cu);

                            }
                        });

                    }

                    refresh = true;
                    Log.d("MAP_TAG", "refresh = " + refresh);
                    while (refresh) {


                        final List<autos> autos = sc.getCoordAll();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                mMap.clear();
                                //getPrice();
                            }
                        });

                        if (f && t) {
                            if (r != null) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                if (lg != null && lg.size() > 1 && new MarkerOptions().position(lg.get(0)) != null) {
                                                    mMap.addMarker(new MarkerOptions().position(lg.get(0)).title("Точка A").icon(BitmapDescriptorFactory
                                                            .fromResource(R.drawable.flag_red)));

                                                    Log.d("MAP_TAG", "lg.size = " + lg.size());
                                                    mMap.addMarker(new MarkerOptions().position(lg.get(lg.size() - 1)).title("Точка Б").icon(BitmapDescriptorFactory
                                                            .fromResource(R.drawable.flag_g)));

                                                    mMap.addPolyline(new PolylineOptions().addAll(lg).color(Color.BLUE));
                                                }
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }

                        for (int i = 0; i < autos.size(); i++) {


                            String cl = autos.get(i).getAutotariffclassid();
                            if (cl.equals("4")) {
                                cl = "без класса";
                            } else if (cl.equals("2")) {
                                cl = "Стандарт класс";
                            } else if (cl.equals("1")) {
                                cl = "Бизнесс класс";
                            } else if (cl.equals("5")) {
                                cl = "Премиум класс";
                            }

                            final int j = i;
                            final String cl2 = cl;

                            if (autos.get(i).getStatus().equals("0")) {

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (autos.get(j).getGeoy() != null && autos.get(j).getGeox() != null
                                                && !autos.get(j).getGeox().equals("") && !autos.get(j).getGeoy().equals("")) {
                                            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(autos
                                                    .get(j).getGeoy()), Double.parseDouble(autos
                                                    .get(j).getGeox()))).title(cl2).snippet(autos.get(j)
                                                    .getCarbrand()
                                                    + ", "
                                                    + autos.get(j)
                                                    .getCarcolor()
                                                    + ", "
                                                    + autos.get(j).getYear()
                                                    + " г.").icon(BitmapDescriptorFactory.fromResource(R.drawable.auto_green_24)));
                                        }
                                    }
                                });

                            } else {

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (autos.get(j).getGeoy() != null && autos.get(j).getGeox() != null
                                                && !autos.get(j).getGeox().equals("") && !autos.get(j).getGeoy().equals("")) {
                                            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(autos
                                                    .get(j).getGeoy()), Double.parseDouble(autos
                                                    .get(j).getGeox()))).title(cl2).snippet(autos.get(j)
                                                    .getCarbrand()
                                                    + ", "
                                                    + autos.get(j)
                                                    .getCarcolor()
                                                    + ", "
                                                    + autos.get(j).getYear()
                                                    + " г.").icon(BitmapDescriptorFactory.fromResource(R.drawable.auto_red_24)));
                                        }
                                    }
                                });
                            }


                        }


                        runOnUiThread(new Runnable() {
                            public void run() {
                                handle1.setText("Сейчас на линии "
                                        + Html.fromHtml("<font color=green>"
                                        + autos.size() + "</font> "));


                            }
                        });
                        if (locMan != null && provider != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Location loc = locMan.getLastKnownLocation(provider);
                                        /*if (loc != null)
                                            mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("Я"));*/

                                }
                            });
                        }
//                            else Toast.makeText(HomeScreen.this, "Идёт поиск...", Toast.LENGTH_SHORT).show();

                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            //Mint.logException(e);
                            // ""
                            Log.d("error", e.getMessage());
                        }

                        // refresh = false; //just 1 iteration

                    }
                }
            });
            t132.start();

        }


        //--------------------------------------------------------------------------
        locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria crit = new Criteria();
        provider = locMan.getBestProvider(crit, true);
        if (locMan.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListner);
            android.util.Log.i("Provider", "GPS");
        }
        if (locMan.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, lListner);
            android.util.Log.i("Provider", "NETWORK");
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!changed) {
                    if (provider != null) {
                        Location loc = locMan.getLastKnownLocation(provider);
                        if (loc == null)
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(HomeScreen.this, "Не удалось определить координаты", Toast.LENGTH_LONG).show();
                                }
                            });
                        else
                            makeCoords(new String[]{loc.getLatitude() + "", loc.getLongitude() + ""});
                        locMan.removeUpdates(lListner);
                    }
//                        else {
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(p));
//                            mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 1500, null);
//                        }
                }
            }
        }, 5000L);


        isMap = true; // map displayed

    }


    private void setUpRightActionIcon() {

        //setUp default state
        f1.setImageResource(mSettings.getString("StreetId", "").equals("") ? R.drawable.fold_inactive1 : R.drawable.delete_blue);
        f2.setImageResource(mSettings.getString("StreetId2", "").equals("") ? R.drawable.fold_inactive1 : R.drawable.delete_blue);
        //f2.setImageResource(mSettings.getString("longRoute", "").equals("") ? R.drawable.fold_inactive1 : R.drawable.delete_blue);
        JSONArray subOrderJSON = null;
        if (mSettings.contains("longRoute") && !mSettings.getString("longRoute", "").equals("")) {
            try {
                subOrderJSON = new JSONArray(mSettings.getString("longRoute", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int routeLength = 1;
        if (subOrderJSON != null) {
            f21.setImageResource(subOrderJSON.length() < 1 ? R.drawable.fold_inactive1 : R.drawable.delete_blue);
            f22.setImageResource(subOrderJSON.length() < 2 ? R.drawable.fold_inactive1 : R.drawable.delete_blue);
            f21.setOnClickListener(deleteClickListener);
            f22.setOnClickListener(deleteClickListener);
            routeLength += subOrderJSON.length();
        } else {
            f21.setImageResource(R.drawable.fold_inactive1);
            f22.setImageResource(R.drawable.fold_inactive1);
        }


        f1.setOnClickListener(mSettings.getString("StreetId", "").equals("") ? getTemplClickListener : deleteClickListener);
        f2.setOnClickListener(mSettings.getString("StreetId2", "").equals("") ? getTemplClickListener : deleteClickListener);
        f2_add.setOnClickListener(getTemplClickListener);

        //setUp Add Address button state
        final Boolean firstAddressOk = (!txt_from.getText().equals(getResources().getString(R.string.address_car_to_come)));
        Boolean secondAddressOk = (!txt_to.getText().equals(getResources().getString(R.string.arrive_address)));
        ll_points_add.setVisibility((firstAddressOk && secondAddressOk && routeLength < MAX_POINTS_IN_ROUTE)
                ? View.VISIBLE : View.GONE);

    }

    /**
     * Stores JSONArray of addresses into longRoute
     *
     * @param addresses - list of addr2 addresses "where"
     * @return - JSONArray
     */
    private JSONArray putLongRouteIntoPrefs(List<addr2> addresses) {

        JSONArray addressJSONArray = new JSONArray();
        for (int i = 0; i < addresses.size(); i++) {
            JSONObject point = new JSONObject();
            JSONObject street = new JSONObject();
            JSONObject house = new JSONObject();
            try {
                street.put("value", addresses.get(i).getSreetname());
                street.put("id", addresses.get(i).getStreetid());
                street.put("geox", addresses.get(i).getGeox());
                street.put("geoy", addresses.get(i).getGeoy());

                house.put("value", addresses.get(i).getHousenumber());
                house.put("id", addresses.get(i).getHouseID());
                house.put("geox", addresses.get(i).getGeox());
                house.put("geoy", addresses.get(i).getGeoy());

                point.put("street", street);
                point.put("house", house);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("JSON error", "error = " + e.getMessage());
            }
            addressJSONArray.put(point);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("longRoute", addressJSONArray.toString());
            editor.commit();
        }
        Log.d("HomeScreen", "JSON = " + addressJSONArray);
        return addressJSONArray;
    }


    private void setUpFormByPreliminaryOrder(String orderJSON) {

        //TODO extra addresses buttons
        JSONObject order;
        try {
            order = new JSONObject(orderJSON);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        //read and save all data to SharedPrefs
        Editor editor = mSettings.edit();

        editor.putBoolean("prelim", true);
        //reading all addresses
        addr2 addressFrom = null;
        addr2 addressTo = null;
        List<addr2> middleAddresses = new ArrayList<>();
        JSONArray addressJSON = order.optJSONArray("addresses");
        if (addressJSON != null) {
            try {
                addressFrom = Utilits.getAddressFromJSON((JSONObject) addressJSON.get(0));
                for (int i = 1; i < addressJSON.length(); i++) {
                    middleAddresses.add(Utilits.getAddressFromJSON((JSONObject) addressJSON.get(i)));
                }
                addressTo = Utilits.getAddressFromJSON((JSONObject) addressJSON.get(addressJSON.length() - 1));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("PARSE", "AddressJSON parsing error = " + e.getMessage());
            }
        }

        //set preorder time for server
        long preTime = order.optLong("time") * 1000;
        editor.putLong("timeToServer", preTime);
        //set preorder timeLimit
        editor.putLong("preorderTime", (new ServiceConnection(HomeScreen.this)).getPreorderTime());

        //set orderID
        editor.putString("OrderId", order.optString("id"));

        //set carClass
        editor.putString("Car", order.optString("classAvto"));

        //put all address data into settings

        if (addressFrom != null) {
            editor.putString("Street", addressFrom.getSreetname());
            editor.putString("StreetId", addressFrom.getStreetid());
            editor.putString("HouseId", addressFrom.getHousenumber());
            //set dom equle to house (i can not see difference
            editor.putString("Dom", addressFrom.getHousenumber());
            //editor.putString("Parad", "");
            editor.putString("fromx", addressFrom.getGeox());
            editor.putString("fromy", addressFrom.getGeoy());
            //editor.putString("Prim", "");
        }

        if (addressTo != null) {
            editor.putString("Street2", addressTo.getSreetname());
            editor.putString("StreetId2", addressTo.getStreetid());
            editor.putString("HouseId2", addressTo.getHousenumber());
            editor.putString("Dom2", addressTo.getHousenumber());
            editor.putString("tox", addressTo.getGeox());
            editor.putString("toy", addressTo.getGeoy());
        }

        //if there are middle points in route
        if (middleAddresses != null && middleAddresses.size() > 0) {
            JSONArray addressJSONArray = new JSONArray();
            for (addr2 address : middleAddresses) {
                JSONObject point = new JSONObject();
                JSONObject street = new JSONObject();
                JSONObject house = new JSONObject();
                try {
                    street.put("value", address.getSreetname());
                    street.put("id", address.getStreetid());
                    street.put("geox", address.getGeox());
                    street.put("geoy", address.getGeoy());

                    house.put("value", address.getHousenumber());
                    house.put("id", address.getHouseID());
                    house.put("geox", address.getGeox());
                    house.put("geoy", address.getGeoy());

                    point.put("street", street);
                    point.put("house", house);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JSON error", "error = " + e.getMessage());
                }
                addressJSONArray.put(point);
            }
            editor.putString("longRoute", addressJSONArray.toString());
        }


        //setUp additional features
        String additional = order.optString("services");
        editor.putBoolean("Snow", additional.contains("3"));
        editor.putBoolean("Pos", additional.contains("15"));
        editor.putBoolean("Pat", additional.contains("34"));
        editor.putBoolean("Wifi", additional.contains("33"));
        editor.putBoolean("Smoke", additional.contains("35"));
        editor.putBoolean("Bag", additional.contains("11"));

        editor.commit();
        //show order data on form
        //from
        if (!mSettings.getString("Street", "").equals("")) {
            txt_from.setText(mSettings.getString("Street", "") + ", " + mSettings.getString("HouseId", ""));
            txt_from.setTextColor(getResources().getColor(R.color.blue_WebCab));
            f1.setImageResource(R.drawable.fold_active);
            f = true;
        }

        //to finish
        if (!mSettings.getString("Street2", "").equals("")) {
            txt_to.setText(mSettings.getString("Street2", "") + ", " + mSettings.getString("HouseId2", ""));
            txt_to.setTextColor(getResources().getColor(R.color.blue_WebCab));
            f2.setImageResource(R.drawable.fold_active);
            t = true;
        }

        //long route
        if (mSettings.contains("longRoute") && !mSettings.equals("")) {
            try {
                JSONArray addresses = new JSONArray(mSettings.getString("longRoute", ""));

                if (addresses.length() > 1) {
                    for (int i = 1; i < addresses.length(); i++) {
                        String currentAddress = "";
                        JSONObject point = addresses.getJSONObject(i);
                        JSONObject street = point.getJSONObject("street");
                        JSONObject house = point.getJSONObject("house");
                        currentAddress += street.getString("value") + ", " + house.getString("value");
                        switch (i) {
                            case 1:
                                ll_points1.setVisibility(View.VISIBLE);
                                txt_to1.setText(currentAddress);
                                ll_points_add.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                ll_points2.setVisibility(View.VISIBLE);
                                txt_to2.setText(currentAddress);
                                ll_points_add.setVisibility(View.GONE);
                                break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //time
        if (mSettings.getLong("timeToServer", 0) != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yy EEEE");
            String time = sdf.format(mSettings.getLong("timeToServer", 0));
            txt_goN.setText(time);
            txt_goT.setText("Или уехать сейчас");
            editor.putString("Time", time);
            editor.commit();
        } else {
            txt_goN.setText("Уехать сейчас");
            //txt_goN.setTextColor(Color.rgb(21, 102, 255));
            txt_goN.setTextColor(getResources().getColor(R.color.blue_WebCab));
            txt_goT.setText("Или уехать в другое время");
            txt_goT.setTextColor(Color.rgb(169, 169, 169));
        }


        //additional services
        txt_dop.setVisibility((mSettings.getBoolean("Snow", false))
                || (mSettings.getBoolean("Pos", false))
                || (mSettings.getBoolean("Pat", false))
                || (mSettings.getBoolean("Wifi", false))
                || (mSettings.getBoolean("Smoke", false))
                || (mSettings.getBoolean("Bag", false)) ? View.GONE : View.VISIBLE);
        snow.setVisibility(mSettings.getBoolean("Snow", false) ? View.VISIBLE : View.GONE);
        pos.setVisibility(mSettings.getBoolean("Pos", false) ? View.VISIBLE : View.GONE);
        pat.setVisibility(mSettings.getBoolean("Pat", false) ? View.VISIBLE : View.GONE);
        wifi.setVisibility(mSettings.getBoolean("Wifi", false) ? View.VISIBLE : View.GONE);
        smoke.setVisibility(mSettings.getBoolean("Smoke", false) ? View.VISIBLE : View.GONE);
        bag.setVisibility(mSettings.getBoolean("Bag", false) ? View.VISIBLE : View.GONE);

        getPrice();

    }

    private void setUpOrderForm(int id) {

        addr ad = null;

        DBPoints dbHelper = new DBPoints(HomeScreen.this);
        SQLiteDatabase db;

        dbHelper = new DBPoints(HomeScreen.this);
        // подключаемся к БД
        db = dbHelper.getWritableDatabase();

        Cursor cr = db.query("addr", null, "id=" + id, null, null, null,
                null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (cr.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = cr.getColumnIndex("id");
            int titleColIndex = cr.getColumnIndex("title");
            int descColIndex = cr.getColumnIndex("desc");

            int strColIndex = cr.getColumnIndex("str");
            int stridColIndex = cr.getColumnIndex("strid");
            int domColIndex = cr.getColumnIndex("dom");
            int domidColIndex = cr.getColumnIndex("domid");
            int paradColIndex = cr.getColumnIndex("parad");
            int primColIndex = cr.getColumnIndex("prim");

            do {

                Log.d("MA", "ID = " + cr.getInt(idColIndex)
                        + ", name = " + cr.getString(titleColIndex)
                        + ", desc = " + cr.getString(descColIndex));

                ad = new addr(cr.getInt(idColIndex),
                        cr.getString(titleColIndex),
                        cr.getString(descColIndex),
                        cr.getString(strColIndex),
                        cr.getString(stridColIndex),
                        cr.getString(domColIndex),
                        cr.getString(domidColIndex),
                        cr.getString(paradColIndex),
                        cr.getString(primColIndex));

            } while (cr.moveToNext());
        } else {
            Log.d("MA", "0 rows");
        }

        cr.close();

        dbHelper.close();

        if (!f) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("Street", ad.getStr());
            editor.putString("StreetId", ad.getStrid());
            editor.putString("Dom", ad.getDom());
            editor.putString("HouseId", ad.getDomid());
            editor.putString("Parad", ad.getParad());
            editor.putString("Prim", ad.getPrim());
            editor.commit();

            String addr = /*"Киев, " +*/ ad.getStr() + ", "
                    + ad.getDom();

            if (!ad.getParad().equals("")) {
                addr += ", п. " + ad.getParad();
            }

            txt_from.setText(addr.toString());
            txt_from.setTextColor(Color.rgb(76, 77, 79));

            f = true;

            getPrice();
        } else if (!t) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("Street2", ad.getStr());
            editor.putString("StreetId2", ad.getStrid());
            editor.putString("Dom2", ad.getDom());
            editor.putString("HouseId2", ad.getDomid());
            editor.commit();

            String addr = /*"Киев, " +*/ ad.getStr() + ", "
                    + ad.getDom();

            txt_to.setText(addr.toString());
            txt_to.setTextColor(Color.rgb(76, 77, 79));

            t = true;

            getPrice();
        }
    }

    private void ifCar() {
        if (mSettings.contains("Car")) {
            Log.d("CAR ", "car - " + mSettings.getString("Car", ""));
            if (!mSettings.getString("Car", "").equals("")) {
                String car = mSettings.getString("Car", "");
                if (car.equals("0")) {
                    setCarId(0);
                } else if (car.equals("1")) {
                    setCarId(1);
                } else if (car.equals("2")) {
                    setCarId(2);
                } else if (car.equals("3")) {
                    setCarId(3);
                }
            }
        }
    }

    private void setCarId(int i) {

        SharedPreferences.Editor editor = mSettings.edit();
        Log.d("CAR ", "Before car check");
        switch (i) {
            case 0:
                c.setBackgroundResource(R.drawable.item_active);
                c1.setBackgroundResource(R.drawable.item_1);
                c2.setBackgroundResource(R.drawable.item_1);
                c3.setBackgroundResource(R.drawable.item_1);

                /*ci.setImageResource(R.drawable.cars);
                ci1.setImageResource(R.drawable.y_car_inactiv);
                ci2.setImageResource(R.drawable.gr_car_inactiv);
                ci3.setImageResource(R.drawable.bl_car_inactiv);*/


                if (Build.VERSION.SDK_INT > 15) {
                    ci.setAlpha(1f);
                    ci1.setAlpha(0.5f);
                    ci2.setAlpha(0.5f);
                    ci3.setAlpha(0.5f);
                } else {
                    ci.setAlpha(255);
                    ci1.setAlpha(140);
                    ci2.setAlpha(140);
                    ci3.setAlpha(140);
                }


                ct0.setTextColor(getResources().getColor(R.color.blue_WebCab));
                ct1.setTextColor(Color.rgb(169, 169, 169));
                ct2.setTextColor(Color.rgb(169, 169, 169));
                ct3.setTextColor(Color.rgb(169, 169, 169));

                editor.putString("Car", "0");
                editor.commit();

                car_i = 0;

                break;
            case 1:
                c.setBackgroundResource(R.drawable.item_1);
                c1.setBackgroundResource(R.drawable.item_active);
                c2.setBackgroundResource(R.drawable.item_1);
                c3.setBackgroundResource(R.drawable.item_1);

                /*ci.setImageResource(R.drawable.cars_inactiv);
                ci1.setImageResource(R.drawable.y_car);
                ci2.setImageResource(R.drawable.gr_car_inactiv);
                ci3.setImageResource(R.drawable.bl_car_inactiv);*/

                if (Build.VERSION.SDK_INT > 15) {
                    ci.setAlpha(0.5f);
                    ci1.setAlpha(1f);
                    ci2.setAlpha(0.5f);
                    ci3.setAlpha(0.5f);
                } else {
                    ci.setAlpha(140);
                    ci1.setAlpha(255);
                    ci2.setAlpha(140);
                    ci3.setAlpha(140);
                }

                ct0.setTextColor(Color.rgb(169, 169, 169));
                ct1.setTextColor(getResources().getColor(R.color.blue_WebCab));
                ct2.setTextColor(Color.rgb(169, 169, 169));
                ct3.setTextColor(Color.rgb(169, 169, 169));

                editor.putString("Car", "1");
                editor.commit();

                car_i = 1;

                break;
            case 2:
                c.setBackgroundResource(R.drawable.item_1);
                c1.setBackgroundResource(R.drawable.item_1);
                c2.setBackgroundResource(R.drawable.item_active);
                c3.setBackgroundResource(R.drawable.item_1);

                /*ci.setImageResource(R.drawable.cars_inactiv);
                ci1.setImageResource(R.drawable.y_car_inactiv);
                ci2.setImageResource(R.drawable.gr_car);
                ci3.setImageResource(R.drawable.bl_car_inactiv);*/

                if (Build.VERSION.SDK_INT > 15) {
                    ci.setAlpha(0.5f);
                    ci1.setAlpha(0.5f);
                    ci2.setAlpha(1f);
                    ci3.setAlpha(0.5f);
                } else {
                    ci.setAlpha(140);
                    ci1.setAlpha(140);
                    ci2.setAlpha(255);
                    ci3.setAlpha(140);
                }

                ct0.setTextColor(Color.rgb(169, 169, 169));
                ct1.setTextColor(Color.rgb(169, 169, 169));
                ct2.setTextColor(getResources().getColor(R.color.blue_WebCab));
                ct3.setTextColor(Color.rgb(169, 169, 169));

                editor.putString("Car", "2");
                editor.commit();

                car_i = 2;
                Log.d("CAR ", "Car check");
                break;

            case 3:
                c.setBackgroundResource(R.drawable.item_1);
                c1.setBackgroundResource(R.drawable.item_1);
                c2.setBackgroundResource(R.drawable.item_1);
                c3.setBackgroundResource(R.drawable.item_active);

                /*ci.setImageResource(R.drawable.cars_inactiv);
                ci1.setImageResource(R.drawable.y_car_inactiv);
                ci2.setImageResource(R.drawable.gr_car_inactiv);
                ci3.setImageResource(R.drawable.bl_car);*/

                if (Build.VERSION.SDK_INT > 15) {
                    ci.setAlpha(0.5f);
                    ci1.setAlpha(0.5f);
                    ci2.setAlpha(0.5f);
                    ci3.setAlpha(1f);
                } else {
                    ci.setAlpha(140);
                    ci1.setAlpha(140);
                    ci2.setAlpha(140);
                    ci3.setAlpha(255);
                }

                ct0.setTextColor(Color.rgb(169, 169, 169));
                ct1.setTextColor(Color.rgb(169, 169, 169));
                ct2.setTextColor(Color.rgb(169, 169, 169));
                ct3.setTextColor(getResources().getColor(R.color.blue_WebCab));

                editor.putString("Car", "3");
                editor.commit();

                car_i = 3;

                break;
        }

    }

	/*public static  void DrawFlagForPosition(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title("Точка A").icon(BitmapDescriptorFactory
	              .fromResource(R.drawable.flag_b)));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
	}*/

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        // Create new adapter
//		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
//				HomeScreen.this, R.layout.item_addr_templ, R.id.txt_title);
//		adapter.add("new items 2");
//		adapter.add("new items .3..");
//		adapter.add("new items ..6.");
//		adapter.add("new items .2..");
//		adapter.add("new items 1...");
//		adapter.add("new items ...5");
//
//		// Use the new adapter
//		AlertDialog alert = (AlertDialog) dialog;
//		alert.getListView().setAdapter(adapter);
    }

    private void getPrice() {

        if (f && t) {

            Log.d("GET_PRICE", "f && t = true");

            LinearLayout buttonPanel = (LinearLayout) findViewById(R.id.bottop_panel);
            GetPriceAsync mGetPrice = new GetPriceAsync(HomeScreen.this, buttonPanel, mSettings);
            mGetPrice.execute();


        } else {
            price.setVisibility(View.VISIBLE);
            titleTxt.setVisibility(View.GONE);
            valuesTxt.setVisibility(View.GONE);
            price.setText("СТОИМОСТЬ");
            price.setTextSize(20);
        }
    }

    @Override
    public void onBackPressed() {

        //if (slidingDrawer != null && slidingDrawer.isOpened() && !showCountDown) {
        if (mSliding != null && mSliding.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED && !showCountDown) {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        themedContext).create();
                alertDialog.setMessage("Вы действительно хотите выйти?");
                alertDialog.setButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
//                            HomeScreen.super.onBackPressed();
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                System.exit(0);
                            }
                        });

                alertDialog.setButton2("Нет", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
            } catch (Exception e) {
                //Mint.logException(e);
                Log.d(Constants.CONTENT_DIRECTORY,
                        "Show Dialog: " + e.getMessage());
            }
        } else if (!showCountDown) {
            //slidingDrawer.animateOpen();
            h.sendEmptyMessage(EXPAND_SLIDER);
        }


    }

    //    public boolean isOnline() {
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
//            return true;
//        }
//        return false;
//    }
//
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onPause() {
        /*if (window != null)
            window.dismiss();
        Editor edit = mSettings.edit();
        //if (mSettings.contains("Car")){
        edit.putString("Car", String.valueOf(car_i));
        edit.putBoolean("showWindow", showCountDown);
        edit.commit();*/
        //	}
        super.onPause();
        refresh = false;
        appIsActive = false;
        Log.d("log_activity", "onpause homescreen");

    }

    @Override
    protected void onStop() {
        // ""
        super.onStop();
        refresh = false;
        refreshMapWithAroute = false;
        Log.d("log_activity", "onstop homescreen");

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null
                && getIntent().hasExtra("methodName")) {
            if (getIntent().getStringExtra("methodName").equals("ordercarhere")) {
                Log.d(TAG, "car here");
                orderCarHere(getIntent());
            }
            if (getIntent().getStringExtra("methodName").equals("orderwait")) {
                Log.d(TAG, "car wait");
                orderWait(getIntent());
            }

        } else {
            Log.d(TAG, "car here error");
        }

        upDateHomescreen();

        Log.d("log_activity", "onstart homescreen");

        appIsActive = true;


        if (getIntent() != null
                && getIntent().hasExtra("methodName")) {
            Log.d(TAG, "method name = " + getIntent().getStringExtra("methodName"));
        }



        getPrice();
        Log.d("log_activity", "onresume homescreen");
    }

    private void upDateHomescreen() {
        if (!mSettings.getString("Name", "").equals("") ||
                !mSettings.getString("uPhone", "").equals("")) {

            cl_name.setText("ЕДУ НЕ Я");
            cl_name.setTextColor(Color.rgb(169, 169, 169));
            String clientName = mSettings.getString("Name", "").equals("")
                    ? mDataSettings.getString("Name", "")
                    : mSettings.getString("Name", "");
            String clientPhone = mSettings.getString("uPhone", "").equals("")
                    ? mSettings.getString("Phone", "")
                    : mSettings.getString("uPhone", "");
            client.setText("ЕДЕТ, " + clientName + " " + clientPhone);
            //client.setTextColor(Color.rgb(21, 102, 255));
            client.setTextColor(getResources().getColor(R.color.blue_WebCab));
//			cl_phone.setText(mSettings.getString("uPhone", ""));
        } else {
            cl_name.setText("ЕДУ Я, " + mSettings.getString("Phone", ""));
            //cl_name.setTextColor(Color.rgb(21, 102, 255));
            cl_name.setTextColor(getResources().getColor(R.color.blue_WebCab));
            client.setText("Едет другой пассажир");
            client.setTextColor(Color.rgb(169, 169, 169));
//            cl_phone.setText(mSettings.getString("Phone",""));
        }

        if ((mSettings.contains("Street")) && (mSettings.contains("Dom"))
                && (mSettings.contains("Parad"))) {
            if ((!mSettings.getString("Street", "").equals(""))
                    && (!mSettings.getString("Dom", "").equals(""))) {
                String str = mSettings.getString("Street", "");
                String dom = mSettings.getString("Dom", "");
                String parad = mSettings.getString("Parad", "");
                String addr = CITY + ", " + str + ", " + dom;
                if (!parad.equals("")) {
                    addr += ", п. " + parad;
                }

                Log.d("shared", "geted street - " + str);
                Log.d("shared", "geted Dom - " + dom);
                Log.d("shared", "geted parad - " + parad);
                Log.d("shared", "addr - " + addr);
                txt_from.setText(addr.toString());
                txt_from.setTextColor(getResources().getColor(R.color.blue_WebCab));
                f1.setImageResource(R.drawable.delete_blue);

                f = true;
            }
        }

        if ((mSettings.contains("Street2")) && (mSettings.contains("Dom2"))) {
            if ((!mSettings.getString("Street2", "").equals(""))
                    && (!mSettings.getString("Dom2", "").equals(""))) {
                String str = mSettings.getString("Street2", "");
                String dom = mSettings.getString("Dom2", "");
                String parad = mSettings.getString("Parad", "");
                //String addr = "Киев, " + str + ", " + dom;
                String addr = CITY + ", " + str + ", " + dom;
                /*if (!parad.equals("")) {
                    addr += ", п. " + parad;
                }*/
                Log.d("shared", "geted street2 - " + str);
                Log.d("shared", "geted Dom2 - " + dom);
                Log.d("shared", "addr - " + addr);
                txt_to.setText(addr.toString());
                txt_to.setTextColor(getResources().getColor(R.color.blue_WebCab));
                f2.setImageResource(R.drawable.delete_blue);
                t = true;
            }
        }

        if (mSettings.contains("longRoute")) {
            Log.d("JSON error", "long route");
            try {
                JSONArray addressJSONArray = new JSONArray(mSettings.getString("longRoute", ""));
                for (int i = 0; i < addressJSONArray.length(); i++)
                    Log.d("JSON error", "addressJSON = " + addressJSONArray.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("JSON error", "error = " + e.getMessage());
            }
        } else {
            Log.d("JSON error", "no long route");
        }


        if (mSettings.contains("longRoute")) {
            if (!mSettings.getString("longRoute", "").equals("")) {
                JSONArray longRouteJSON = null;

                t = true;

                //show extra addresses
                String whereTo = "";
                String whereTo1 = "";
                String whereTo2 = "";
                addr2 lastAddress = null;
                addr2 lastAddress1 = null;
                addr2 lastAddress2 = null;
                try {
                    longRouteJSON = new JSONArray(mSettings.getString("longRoute", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (longRouteJSON.length()) {
                    case 1:
                        ll_points1.setVisibility(View.GONE);
                        ll_points2.setVisibility(View.GONE);
                        ll_points_add.setVisibility(View.VISIBLE);
                        try {
                            lastAddress = Utilits.getAddressFromJSON(longRouteJSON.getJSONObject(longRouteJSON.length() - 1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        whereTo = CITY + ", " + lastAddress.getSreetname() + ", " + lastAddress.getHousenumber();
                        txt_to.setText(whereTo);
                        txt_to.setTextColor(getResources().getColor(R.color.blue_WebCab));
                        //f2.setImageResource(R.drawable.fold_active);
                        break;
                    case 2:
                        ll_points1.setVisibility(View.VISIBLE);
                        ll_points2.setVisibility(View.GONE);
                        ll_points_add.setVisibility(View.VISIBLE);

                        try {
                            lastAddress1 = Utilits.getAddressFromJSON(longRouteJSON.getJSONObject(longRouteJSON.length() - 1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        whereTo1 = CITY + ", " + lastAddress1.getSreetname() + ", " + lastAddress1.getHousenumber();
                        txt_to1.setText(whereTo1);
                        txt_to1.setTextColor(getResources().getColor(R.color.blue_WebCab));
                        //f21.setImageResource(R.drawable.fold_active);
                        break;
                    case 3:
                        ll_points1.setVisibility(View.VISIBLE);
                        ll_points2.setVisibility(View.VISIBLE);
                        ll_points_add.setVisibility(View.GONE);
                        try {
                            lastAddress1 = Utilits.getAddressFromJSON(longRouteJSON.getJSONObject(longRouteJSON.length() - 2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            lastAddress2 = Utilits.getAddressFromJSON(longRouteJSON.getJSONObject(longRouteJSON.length() - 1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        whereTo1 = CITY + ", " + lastAddress1.getSreetname() + ", " + lastAddress1.getHousenumber();
                        whereTo2 = CITY + ", " + lastAddress2.getSreetname() + ", " + lastAddress2.getHousenumber();
                        txt_to1.setText(whereTo1);
                        txt_to1.setTextColor(getResources().getColor(R.color.blue_WebCab));
                        //f21.setImageResource(R.drawable.fold_active);
                        txt_to2.setText(whereTo2);
                        txt_to2.setTextColor(getResources().getColor(R.color.blue_WebCab));
                        //f22.setImageResource(R.drawable.fold_active);
                        break;
                }
            } else {
                ll_points1.setVisibility(View.GONE);
                ll_points2.setVisibility(View.GONE);
                if (txt_from.getText().equals("Адрес подачи автомобиля") ||
                        txt_to.getText().equals("Адрес прибытия автомобиля")) {
                    ll_points_add.setVisibility(View.GONE);
                } else {
                    ll_points_add.setVisibility(View.VISIBLE);
                }
            }
        } else {
            ll_points1.setVisibility(View.GONE);
            ll_points2.setVisibility(View.GONE);
            if (txt_from.getText().equals("Адрес подачи автомобиля") ||
                    txt_to.getText().equals("Адрес прибытия автомобиля")) {
                ll_points_add.setVisibility(View.GONE);
            } else {
                ll_points_add.setVisibility(View.VISIBLE);
            }
        }

        //setUp address buttons
        setUpRightActionIcon();

        getPrice();

        if ((mSettings.getBoolean("Snow", false))
                || (mSettings.getBoolean("Pos", false))
                || (mSettings.getBoolean("Pat", false))
                || (mSettings.getBoolean("Wifi", false))
                || (mSettings.getBoolean("Smoke", false))
                || (mSettings.getBoolean("Bag", false))) {
            //TODO отображение дополнительных услуг в виде значков
            txt_dop.setVisibility(View.GONE);
        } else {
            txt_dop.setVisibility(View.VISIBLE);
        }

        if (mSettings.getBoolean("Snow", false)) {
            snow.setVisibility(View.VISIBLE);
        } else {
            snow.setVisibility(View.GONE);
        }

        if (mSettings.getBoolean("Pos", false)) {
            pos.setVisibility(View.VISIBLE);
        } else {
            pos.setVisibility(View.GONE);
        }

        if (mSettings.getBoolean("Pat", false)) {
            pat.setVisibility(View.VISIBLE);
        } else {
            pat.setVisibility(View.GONE);
        }

        if (mSettings.getBoolean("Wifi", false)) {
            wifi.setVisibility(View.VISIBLE);
        } else {
            wifi.setVisibility(View.GONE);
        }

        if (mSettings.getBoolean("Smoke", false)) {
            smoke.setVisibility(View.VISIBLE);
        } else {
            smoke.setVisibility(View.GONE);
        }

        if (mSettings.getBoolean("Bag", false)) {
            bag.setVisibility(View.VISIBLE);
        } else {
            bag.setVisibility(View.GONE);
        }


    }

    @Override
    protected void onStart() {
        // ""
        super.onStart();
        //mapView.setEnabled(false);
        //slidingDrawer.animateClose();

    }

    public void drawRouteIfExist(ArrayList<LatLng> l) {

        Log.v("ABC", "size - " + l.size() + "first point - " + l.get(0).latitude + " " + l.get(0).longitude);

        mMap.addMarker(new MarkerOptions().position(l.get(0)).title("Точка A").icon(BitmapDescriptorFactory
                .fromResource(R.drawable.flag_red)));

        mMap.addMarker(new MarkerOptions().position(l.get(l.size() - 1)).title("Точка Б").icon(BitmapDescriptorFactory
                .fromResource(R.drawable.flag_g)));

        mMap.addPolyline(new PolylineOptions().addAll(l).color(Color.BLUE));

//		mMap.moveCamera(CameraUpdateFactory.newLatLng());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l.get(0), 17), 2000, null);
    }

    public void drawMeAndStartWaitActivity() {
        if (r != null) {
            //LatLng l =new LatLng(Double.parseDouble(r.getRouteX().get(0)),Double.parseDouble(r.getRouteY().get(0)));
            LatLng l = new LatLng(50.5, 30.5);
            mMap.addMarker(new MarkerOptions().position(l).title("Точка A").icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.flag_red)));
//			mMap.moveCamera();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(l, 7f, 0, 0)));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 14f));

            //zoom map to see all route
            /*var markers = //some array;
                    var bounds = new google.maps.LatLngBounds();
            for(i=0;i<markers.length;i++) {
                bounds.extend(markers[i].getPosition());
            }

            map.fitBounds(bounds);*/
        }
        Intent intent = new Intent();
        intent.putExtra("methodName", "CreateOrder");
        createOrder(intent);
        /*new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(HomeScreen.this,
						CreateOrder.class);
				//				finish();
				createOrder(intent);
				//startActivity(intent);
			}
		}, 1000);*/
        /*mMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				// ""
				Intent intent = new Intent(HomeScreen.this,
						CreateOrder.class);
				startActivity(intent);
				mMap.setOnMapLoadedCallback(null);
			}
		});*/

    }

    public void time() {
        showDialog(DIALOG_TIME);
    }

    public void date(View view) {

        if (prelimEnabled) {
            showDialog(DIALOG_DATE);
        } else {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        themedContext).create();
                alertDialog.setTitle("Тестовый режим!");
                alertDialog
                        .setMessage("Вы можете сделать заказ на ближайшее время. " +
                                "Для оформления предварительного заказа свяжитесь с" +
                                " диспетчером по телефону: 044-248-8-248 050-248-8-248" +
                                " 097-248-8-248 063-248-8-248");
                alertDialog.setButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } catch (Exception e) {
                Log.d(Constants.CONTENT_DIRECTORY,
                        "Show Dialog: " + e.getMessage());
            }
        }


    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            Log.d("DATE_LOG", "time = " + mSettings.getLong("timeToServer", 0));
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            if (mSettings.getLong("timeToServer", 0) != 0)
                c.setTimeInMillis(mSettings.getLong("timeToServer", 0));
            final TimePickerDialog tpd = new TimePickerDialog(themedContext, this,
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
            tpd.setButton(DialogInterface.BUTTON_NEGATIVE, "ОТМЕНА", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == DialogInterface.BUTTON_NEGATIVE) {
                        isTimePick = false;
                        tpd.updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                        dialogInterface.dismiss();

                    }

                }
            });
            tpd.setButton(DialogInterface.BUTTON_POSITIVE, themedContext.getResources().getString(R.string.ready), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == DialogInterface.BUTTON_POSITIVE) {
                        isTimePick = true;

                        Field mTimePickerField = null;
                        try {
                            mTimePickerField = tpd.getClass().getDeclaredField("mTimePicker");
                            mTimePickerField.setAccessible(true);
                            TimePicker mTimePickerInstance = (TimePicker) mTimePickerField.get(tpd);
                            setTime(mTimePickerInstance, mTimePickerInstance.getCurrentHour(),
                                    mTimePickerInstance.getCurrentMinute());
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return tpd;
        } else if (id == DIALOG_DATE) {
            final Calendar c = Calendar.getInstance();

            if (mSettings.getLong("timeToServer", 0) != 0)
                c.setTimeInMillis(mSettings.getLong("timeToServer", 0));
            else
                c.setTimeInMillis(System.currentTimeMillis());
            Log.d("DATE_CHECK", "day = " + c.get(Calendar.DAY_OF_MONTH));
            final DatePickerDialog dtpd = new DatePickerDialog(themedContext, this,
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));
            dtpd.setButton(DialogInterface.BUTTON_NEGATIVE, "ОТМЕНА", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == DialogInterface.BUTTON_NEGATIVE) {
                        isDatePick = false;
                        dtpd.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH));
                        dialogInterface.dismiss();
                    }

                }
            });
            dtpd.setButton(DialogInterface.BUTTON_POSITIVE, "ГОТОВО", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == DialogInterface.BUTTON_POSITIVE) {
                        isDatePick = true;
                        Field mDatePickerField = null;
                        try {
                            mDatePickerField = dtpd.getClass().getDeclaredField("mDatePicker");
                            mDatePickerField.setAccessible(true);
                            DatePicker mDatePickerInstance = (DatePicker) mDatePickerField.get(dtpd);
                            setDate(mDatePickerInstance, mDatePickerInstance.getYear(), mDatePickerInstance.getMonth(),
                                    mDatePickerInstance.getDayOfMonth());
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            return dtpd;
        }

        return super.onCreateDialog(id);
    }

    public void setTime(TimePicker view, int hourOfDay, int minute) {
        if (isTimePick) {
            int selTime = (hourOfDay * 60) + minute;
            Calendar c = Calendar.getInstance();
            int curTime = (c.get(Calendar.HOUR_OF_DAY) * 60)
                    + c.get(Calendar.MINUTE);
            Log.d("time", "min - " + selTime + ". min_cur - " + curTime);
            if (selTime < curTime && curDate == selDate) {
                try {
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            themedContext).create();
                    alertDialog
                            .setMessage("Вы ввели прошедшое время, повторите пожалуйста ввод!");
                    alertDialog.setButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    time();
                                }
                            });
                    alertDialog.setButton2("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.show();
                } catch (Exception e) {
                    //Mint.logException(e);
                    Log.d(Constants.CONTENT_DIRECTORY,
                            "Show Dialog: " + e.getMessage());
                }
            } else {
                Log.d("time", "good");

                SharedPreferences mSettings = getSharedPreferences(
                        "mysettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSettings.edit();
                long preorderTime = mSettings.getLong("preorderTime", 0);
                long currentTime = Calendar.getInstance().getTimeInMillis();
                SimpleDateFormat sdf;
                if (calendar != null) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    sdf = new SimpleDateFormat("HH:mm  dd.MM.yy EEEE");
                } else {
                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    sdf = new SimpleDateFormat("HH:mm");
                }
                if (calendar.getTimeInMillis() - currentTime > preorderTime) {

                    String time = sdf.format(calendar.getTime());
                    editor.putString("Time", time);
                    editor.putLong("timeToServer", calendar.getTimeInMillis());
                    editor.commit();

                    txt_goN.setText(time);
                    txt_goT.setText("Или уехать сейчас");
                } else {

                    editor.putString("Time", "");
                    editor.putLong("timeToServer", 0);
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    editor.commit();
                    txt_goN.setText("Уехать сейчас");
                    txt_goT.setText("Или уехать в другое время");
                }
            }
        }
        getPrice();
    }

    public void setDate(final DatePicker view, int year, int monthOfYear,
                        int dayOfMont) {
        Log.d("DATE_LOG", "date was set");
        if (isDatePick) {
            String date = "";
            //selDate = (year * 365 * 12) + (monthOfYear * 12) + dayOfMont;
            selDate = (year * 365) + (monthOfYear * 31) + dayOfMont;
            Calendar c = Calendar.getInstance();
            curDate = (c.get(Calendar.YEAR) * 365)
                    + (c.get(Calendar.MONTH) * 31)
                    + c.get(Calendar.DAY_OF_MONTH);
            if (selDate < curDate) {
                try {
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            themedContext).create();
                    alertDialog.setMessage("Вы ввели прошедшую дату, повторите пожалуйста ввод!");
                    alertDialog.setButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    date(view);
                                }
                            });
                    alertDialog.setButton2("Cancel",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // ""
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } catch (Exception e) {
                    //Mint.logException(e);
                    Log.d(Constants.CONTENT_DIRECTORY,
                            "Show Dialog: " + e.getMessage());
                }
            } else {
                Log.d("date", "good");
                if (selDate != curDate) {
//                        date =  + "." + monthOfYear + "." + year;
                    calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMont);
                    calendar.setFirstDayOfWeek(Calendar.MONDAY);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy EEEE");
                    date = sdf.format(calendar.getTime());
                } else {
                    date = "";
                    calendar = null;
                }
                Log.d("DATE_CHECK", "date = " + date);
                time();
            }
        }

    }

    @Override
    public void onDateSet(final DatePicker view, int year, int monthOfYear,
                          int dayOfMont) {

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    }

    private String callSignIdFromPush;

    public void orderWait(Intent intent) {

        //setIntent(null);
        sIntent = intent;

        callSignIdFromPush = sIntent.getStringExtra("driverId");

        Log.d(TAG, "running wait");

        activity_home.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (window != null && window.isShowing()) {
                    window.dismiss();
                }
                clearForm();
                findViewById(R.id.include1).setVisibility(View.VISIBLE);
                h.sendEmptyMessage(EXPAND_SLIDER);
            }
        });

        orderId = sIntent.getStringExtra("orderId");
        Log.d(TAG, "orderId = " + orderId + ", saved = " + Utilits.saveCurrentOrder(activity_home, orderId));
        SharedPreferences mSettingsOrder = getSharedPreferences("order", Context.MODE_PRIVATE);
        Intent orderWaitIntent = new Intent(activity_home, OrderWaitNew.class);
        orderWaitIntent.putExtra("orderIDs", mSettingsOrder.getString("currentOrders", ""));
        if (sIntent.hasExtra("driverId")) {
            orderWaitIntent.putExtra("callSignID", sIntent.getStringExtra("driverId"));
            Log.d(TAG, "callSignID = " + sIntent.getStringExtra("driverId"));
        } else {
            Log.d(TAG, "callSignID = no ID");
        }
        if (sIntent.hasExtra("needConfirm") && sIntent.getBooleanExtra("needConfirm", false)) {
            //confirm order and then start searching car
            (new ConfirmOrder(HomeScreen.this, orderId, sIntent.getStringExtra("driverId"), orderWaitIntent)).execute();
        }
        startActivity(orderWaitIntent);
    }

    private class ConfirmOrder extends AsyncTask<Void, Void, Boolean> {

        Context mContext;
        String orderID;
        Intent mIntent;
        String callsignid;

        public ConfirmOrder(Context mContext, String orderID, String callsignid, Intent mIntent) {
            this.mContext = mContext;
            this.orderID = orderID;
            this.mIntent = mIntent;
            this.callsignid = callsignid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;
            ServiceConnection sc = new ServiceConnection(mContext);
            result = sc.confirmPreOrder(orderID, callsignid);
            return result;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            Log.d(TAG, "success = " + success);
            if (success) {
                startActivity(mIntent);
            }
        }
    }

    public final static int COLLAPSE_SLIDER = 10;
    public final static int EXPAND_SLIDER = 20;

    //Drawer handler open/close
    Handler h = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == COLLAPSE_SLIDER) {
                mSliding.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                dragMinus.setVisibility(View.VISIBLE);
                iv4.setImageResource(R.drawable.map_icon_active);
                iv1.setImageResource(R.drawable.car_inactive);
                t4.setTextColor(getResources().getColor(R.color.blue_WebCab));
                t1.setTextColor(Color.parseColor("#080000"));
                i1.setBackgroundResource(R.drawable.top_bar_grad_inactive);
                i4.setBackgroundResource(R.drawable.top_bar_grad_active);
                isMap = true;
                workWithMap();
            }

            if (msg.what == EXPAND_SLIDER) {
                mSliding.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                dragMinus.setVisibility(View.GONE);
                iv4.setImageResource(R.drawable.map_icon_inactive);
                iv1.setImageResource(R.drawable.car_active);
                t4.setTextColor(Color.parseColor("#080000"));
                i1.setBackgroundResource(R.drawable.top_bar_grad_active);
                i4.setBackgroundResource(R.drawable.top_bar_grad_inactive);
                isMap = false;
            }

        }

        ;
    };

    private void playDefaultNotificationSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }

    public void createOrder(Intent intent) {
        sIntent = intent;
        activity_home.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //slidingDrawer.animateClose();

                findViewById(R.id.include1).setVisibility(View.GONE);

                h.sendEmptyMessage(COLLAPSE_SLIDER);


                if (window != null && window.isShowing()) {
                    window.dismiss();
                }
                LayoutInflater inflater = (LayoutInflater) activity_home.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View root = inflater.inflate(R.layout.order, null);
                root.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
                root.measure(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                window = new PopupWindow(root, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                // window.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                // window.setHeight(WindowManager.LayoutParams.MATCH_PARENT);

                //window.setContentView(root);

                window.showAtLocation(activity_home.findViewById(R.id.it_1), Gravity.CENTER, 0, 0);
                createOrder_timer = (TextView) root.findViewById(R.id.txt_timer);
                createOrder_timer.setTypeface(arialBlack);
                final FrameLayout frp = (FrameLayout) root.findViewById(R.id.fr_poisk);
                ((TextView) root.findViewById(R.id.txt_poisk)).setTypeface(font_Roboto);
                final LinearLayout ll = (LinearLayout) root.findViewById(R.id.ll_oder);
                final Button bt_ok = (Button) root.findViewById(R.id.bt_podtv);
                final Button bt_c = (Button) root.findViewById(R.id.bt_cancel);
                root.findViewById(R.id.ll_bot).setVisibility(View.GONE);

                bt_c.setTypeface(font_Roboto);
                bt_ok.setTypeface(font_Roboto);
                String steps = "1";


                String time = "39";
                /*if (handle1 != null && handle1.getText().equals("Cейчас на линии: "
                        + Html.fromHtml("<font color=green>"
                        + 0 + "</font> "))) {
                    Log.d("FINAL_TEST", "bla-bla-bla");
                    final SharedPreferences.Editor temp = mSettings.edit();
                    Thread getTimeThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String time = (new ServiceConnection(HomeScreen.this)).getTimeAutoSearch();
                            Log.d("FINAL_TEST", "search time = " + time);
                            temp.putString("autoSearchTime", time);
                            temp.commit();
                        }
                    });
                    getTimeThread.start();
                }*/

                /*final SharedPreferences.Editor temp = mSettings.edit();
                Thread getTimeThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String time = (new ServiceConnection(HomeScreen.this)).getTimeAutoSearch();
                        Log.d("FINAL_TEST", "search time = " + time);
                        temp.putString("autoSearchTime", time);
                        temp.commit();
                    }
                });
                getTimeThread.start();*/


                if (mSettings != null && mSettings.contains("autoSearchTime")) {
                    time = mSettings.getString("autoSearchTime", "39");
                }
                int autoSearchMilliSeconds;
                try {
                    autoSearchMilliSeconds = Integer.parseInt(time);
                } catch (Exception e) {
                    Log.d("HomeScreen", "autoSearchMilliSeconds error = " + e.getMessage());
                    autoSearchMilliSeconds = 39;
                }

                autoSearchMilliSeconds *= 1000;
                Log.d("FINAL_TEST", "search time from settings = " + autoSearchMilliSeconds);

                createOrder_ct1 = new CountDownTimer(autoSearchMilliSeconds, 1000) { // get seconds here =>

                    //int radar_level = 0;


                    public void onTick(long millisUntilFinished) {

                        showCountDown = true;
                        long s = millisUntilFinished / 1000;
                        if (Long.toString(s).length() == 1) {
                            createOrder_timer.setText("0:0" + s);
                        } else {
                            createOrder_timer.setText("0:" + s);
                        }

                    }

                    public void onFinish() {
                        //!!!
                        showCountDown = false;
                        Log.d("FINAL_TEST", "Timer: I`m finished");
                        playDefaultNotificationSound();
                        vibrate();
                        or = null;
                        try {
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    themedContext).create();
                            alertDialog.setMessage("В данный момент машин нет. Попробуйте изменить параметры заказа.");
                            alertDialog.setButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                            findViewById(R.id.include1).setVisibility(View.VISIBLE);
                                            h.sendEmptyMessage(EXPAND_SLIDER);
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    window.dismiss();
                                                    showCountDown = false;
                                                }
                                            });
                                        }
                                    });
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        } catch (Exception e) {
                            //Mint.logException(e);
                            Log.d(Constants.CONTENT_DIRECTORY,
                                    "Show Dialog: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                };
                createOrder_ct1.start();

                final String phone = mSettings.getString("Phone", "");
                //final String numb = phone.subSequence(3, phone.length()).toString();
                final String fromStr = mSettings.getString("Street", "") + ", " + mSettings.getString("Dom", "");
                final String price = mSettings.getString("Price", "");
                final String toStr = mSettings.getString("Street2", "") + ", " + mSettings.getString("Dom2", "");

                Thread t = new Thread(new Runnable() {
                    public void run() {

                        Log.d("createorder", "sending");


                        //для тестов
                        ServiceConnection sc2 = new ServiceConnection(activity_home);
                        or = sc2.createOrder(activity_home, fromStr, price, toStr);
                        //	auto= sc2.getCoordAuto(mSettings.getString("DriverId", ""));
						/*final order or = new order(6768829, "101010", "10343",
						"132123215", "Ауди 100", "Бежевый", "test test test", "30.5",
						"50.5");*/
                        //final order or=new order(6768829, "101010", 10343, "АА 4702 НН", "Мазда 6", "Черный", "Бирак Ярослав Федорович", "30.5207", "50.4533", "42", 123l, "0637562424");

                        if (or != null && !or.getDrivename().equals("null") && !or.getAutodriverid().equals("")) {

                            createOrder_ct1.cancel();
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putString("OrderId", "" + or.getOrderid());
                            editor.putString("DriverId", or.getAutodriverid());
                            editor.commit();

                            Log.d("myLogs", "order details: driverName = " + or.getDrivename());

                            activity_home.runOnUiThread(new Runnable() {

                                public void run() {
                                    RemoveViewFromWindow(radar);
                                    ProgressBar mBar = (ProgressBar) root.findViewById(R.id.progressBar2);
                                    RemoveViewFromWindow(mBar);
                                    MediaPlayer mediaPlayer = MediaPlayer.create(HomeScreen.this, R.raw.arrived);
                                    mediaPlayer.start();
                                    vibrate();
                                    root.findViewById(R.id.ll_bot).setVisibility(View.VISIBLE);
                                    //((ViewGroup)radar.getParent()).removeView(radar);
                                    //radar.setImageDrawable(null);
                                    bt_ok.setText("ПОДТВЕРДИТЬ");

                                    bt_ok.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            if (mSettings.contains("OrderId")) {
                                                Thread t = new Thread(new Runnable() {
                                                    public void run() {
                                                        createOrder_ct1.cancel();
                                                        createOrder_ct2.cancel();
                                                        //для тестов
                                                        ServiceConnection sc = new ServiceConnection(activity_home);
                                                        tm = sc.confirmOrder(mSettings.getString("OrderId", ""));
                                                        if (tm > 0)
                                                        //if (true)
                                                        {
                                                            runOnUiThread(new Runnable() {

                                                                @Override
                                                                public void run() {
                                                                    window.dismiss();
                                                                    showCountDown = false;
                                                                }
                                                            });
                                                            Intent openMainActivity = new Intent(activity_home, HomeScreen.class);
                                                            //openMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            //			Intent.fla
                                                            openMainActivity.putExtra("methodName", "orderwait");
                                                            //openMainActivity.putExtra("tm", 100000l);
                                                            openMainActivity.putExtra("tm", tm);
                                                            openMainActivity.putExtra("driverId", mSettings.getString("DriverId", ""));
                                                            openMainActivity.putExtra("orderId", mSettings.getString("OrderId", ""));
                                                            if (or != null) {
                                                                openMainActivity.putExtra("car", or.getCarcolor() + " " + or.getCarbrand() + ", " +
                                                                        or.getStatenumber() + ", " + or.getDrivename());
                                                            }
                                                            orderWait(openMainActivity);
                                                            //startActivity(openMainActivity);

                                                            //Intent intent = new Intent(CreateOrder.this, OrderWait.class);
                                                            //Intent intent = new Intent(CreateOrder.this, HomeScreen.class);
                                                            //для тестов
															/*intent.putExtra("tm", 100000l);
															intent.putExtra("driverId", mSettings.getString("DriverId", ""));
															intent.putExtra("
							                                ", mSettings.getString("OrderId", ""));
															if (or!=null) {
																intent.putExtra("car", or.getCarcolor() + " " + or.getCarbrand() + ", " +
																		or.getStatenumber() + ", " + or.getDrivename());
															}*/

															/*intent.putExtra("methodName", "orderwait");
															intent.putExtra("tm", tm);
															intent.putExtra("driverId", mSettings.getString("DriverId", ""));
															intent.putExtra("orderId", mSettings.getString("OrderId", ""));
															if (or!=null) {
																intent.putExtra("car", or.getCarcolor() + " " + or.getCarbrand() + ", " +
																		or.getStatenumber() + ", " + or.getDrivename());
															}*/

                                                            //																startActivity(intent);
                                                            //finish();
                                                        } else {
                                                            try {
                                                                AlertDialog alertDialog = new AlertDialog.Builder(
                                                                        themedContext).create();
                                                                alertDialog
                                                                        .setMessage("Error");
                                                                alertDialog.setButton("OK",
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog,
                                                                                                int which) {
                                                                                runOnUiThread(new Runnable() {

                                                                                    @Override
                                                                                    public void run() {
                                                                                        window.dismiss();
                                                                                        showCountDown = false;
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                alertDialog.setCancelable(false);
                                                                alertDialog.show();
                                                            } catch (Exception e) {
                                                                //Mint.logException(e);
                                                                Log.d(Constants.CONTENT_DIRECTORY,
                                                                        "Show Dialog: " + e.getMessage());
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                });
                                                t.start();
                                            }
                                        }
                                    });


                                    bt_c.setText("ОТМЕНА");
                                    bt_c.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            if (mSettings.contains("OrderId")) {
                                                Thread t = new Thread(new Runnable() {
                                                    public void run() {
                                                        if (createOrder_ct1 != null) {
                                                            createOrder_ct1.cancel();
                                                        }
                                                        if (createOrder_ct2 != null) {
                                                            createOrder_ct2.cancel();
                                                        }
                                                        ServiceConnection sc = new ServiceConnection(activity_home);
                                                        sc.cancelOrder(mSettings.getString("OrderId", ""));

                                                        SharedPreferences.Editor editor = mSettings.edit();
                                                        editor.putString("OrderId", "");
                                                        editor.putString("DriverId", "");
                                                        editor.commit();
                                                        runOnUiThread(new Runnable() {

                                                            @Override
                                                            public void run() {
                                                                window.dismiss();
                                                                findViewById(R.id.include1).setVisibility(View.VISIBLE);
                                                                showCountDown = false;
                                                                h.sendEmptyMessage(EXPAND_SLIDER);
                                                            }
                                                        });
														/*Intent intent = new Intent(CreateOrder.this, HomeScreen.class);
														startActivity(intent);
														finish();*/
                                                    }
                                                });
                                                t.start();

                                            }
                                        }
                                    });

                                    ll.removeView(frp);
                                    ll.removeView(createOrder_timer);
                                    ll.setBackgroundColor(Color.TRANSPARENT);
                                    LayoutInflater factory = LayoutInflater
                                            .from(activity_home);
                                    View myView = factory.inflate(R.layout.order_data,
                                            null);

                                    ll.addView(myView);
                                    //	TextView str = (TextView) findViewById(R.id.txt_addr);
                                    TextView time = (TextView) root.findViewById(R.id.txt_time);
                                    time.setTypeface(font1);
                                    //TextView price = (TextView) findViewById(R.id.txt_price);
                                    TextView model = (TextView) root.findViewById(R.id.txt_model);
                                    model.setTypeface(font1);
                                    TextView num = (TextView) root.findViewById(R.id.txt_num);
                                    num.setTypeface(font1);
                                    TextView color = (TextView) root.findViewById(R.id.txt_color);
                                    color.setTypeface(font1);

                                    if (mSettings.contains("Time")) {
                                        if (!mSettings.getString("Time", "").equals("")) {

                                            time.setText(mSettings
                                                    .getString("Time", ""));
                                        } else
                                            time.setText(or.getTime() / 60 + "");
                                    } else {
                                        if (or.getTime() < 0) {

                                            //attention!!!
                                            runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    Log.d("FINAL_TEST", "TIME error, time = " + or.getTime());
                                                    window.dismiss();
                                                    findViewById(R.id.include1).setVisibility(View.VISIBLE);
                                                    showCountDown = false;
                                                    //attention
                                                    //clearForm();
                                                }
                                            });
                                            return;
                                        }
                                        time.setText(or.getTime() / 60 + "");
                                    }
                                    model.setText(or.getCarbrand());
                                    color.setText(or.getCarcolor());
                                    num.setText(or.getStatenumber());

                                    try {
                                        ((LinearLayout) root.findViewById(R.id.ll_order_data)).addView(createOrder_timer);
                                    } catch (Exception e) {
                                        //Mint.logException(e);
                                        e.printStackTrace();
                                    }
                                    if (createOrder_ct2 != null)
                                        createOrder_ct2.cancel();
                                    createOrder_ct2 = new CountDownTimer(30000, 1000) {

                                        public void onTick(long millisUntilFinished) {
                                            long s = millisUntilFinished / 1000;
                                            if (Long.toString(s).length() == 1) {
                                                createOrder_timer.setText("0:0" + s);
                                            } else {
                                                createOrder_timer.setText("0:" + s);
                                            }

                                        }

                                        public void onFinish() {
                                            createOrder_ct1.cancel();
                                            Thread t12 = new Thread(new Runnable() {
                                                public void run() {
                                                    ServiceConnection sc = new ServiceConnection(activity_home);
                                                    sc.cancelOrder(mSettings.getString("OrderId", ""));
                                                }
                                            });
                                            t12.start();
                                            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            nm.cancelAll();

                                            SharedPreferences.Editor editor = mSettings.edit();
                                            editor.putString("OrderId", "");
                                            editor.putString("DriverId", "");
                                            editor.commit();

                                            try {
                                                AlertDialog alertDialog = new AlertDialog.Builder(
                                                        themedContext).create();
                                                alertDialog
                                                        .setMessage("Время на принятие решения у вас закончилось. Сделайте заказ еще раз.");
                                                alertDialog
                                                        .setButton(
                                                                "OK",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(
                                                                            DialogInterface dialog,
                                                                            int which) {

																/*Intent intent = new Intent(
																		CreateOrder.this,
																		HomeScreen.class);
																startActivity(intent);
																finish();*/
                                                                        runOnUiThread(new Runnable() {

                                                                            @Override
                                                                            public void run() {
                                                                                window.dismiss();
                                                                                showCountDown = false;
                                                                                findViewById(R.id.include1).setVisibility(View.VISIBLE);

                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                alertDialog.setCancelable(false);
                                                alertDialog.show();
                                            } catch (Exception e) {
                                                //Mint.logException(e);
                                                Log.d(Constants.CONTENT_DIRECTORY,
                                                        "Show Dialog: "
                                                                + e.getMessage());
                                                e.printStackTrace();
                                            }
                                        }
                                    }.start();
                                }
                            });
                        } else {

                            //createOrder_ct1.onFinish();
                        }
                    }
                });
                t.start();

            }
        });


    }

    private void vibrate() {
        Vibrator v = (Vibrator) HomeScreen.this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }


    public void createPrelimOrder(Intent intent) {
        //TODO функция инициализирующая отправку предварительного заказа на сервер
        sIntent = intent;
        final String phone = mSettings.getString("Phone", "");
        //final String numb = phone.subSequence(3, phone.length()).toString();
        final String fromStr = mSettings.getString("Street", "") + ", " + mSettings.getString("Dom", "");
        final String price = mSettings.getString("Price", "");
        final String toStr = mSettings.getString("Street2", "") + ", " + mSettings.getString("Dom2", "");
        final long dateTime = mSettings.getLong("timeToServer", 0);
        final String flat = mSettings.getString("flat", "0");


        new Thread(new Runnable() {
            public void run() {

                Log.d("createPrelimeOrder", "sending");


                //для тестов
                ServiceConnection sc2 = new ServiceConnection(activity_home);
                sc2.createPrelimOrder(activity_home, fromStr, price, toStr, dateTime, flat);
            }
        }).start();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);


        String method = intent.getStringExtra("methodName");
        if (method != null && method.equals("callDialog")) {
            callDispatcherDialog();
        }
        if (method != null && method.equals("orderwait")) {
            Log.d(TAG, "waiting car");
            orderWait(intent);
        } else if (method != null && method.equals("ordercarhere")) {
            orderCarHere(intent);
        }
    }

    private class getOrderInfo extends AsyncTask<Void, Void, autos> {

        private Intent mIntent;

        public getOrderInfo(Intent mIntent) {
            this.mIntent = mIntent;
        }

        @Override
        protected autos doInBackground(Void... params) {

            final ServiceConnection sc = new ServiceConnection(HomeScreen.this);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    Log.d(TAG, "driverID = " + mIntent.getStringExtra("driverId"));
                    auto = sc.getCoordAuto(mIntent.getStringExtra("driverId"));
                    //Log.d(TAG, "driverID = " + mIntent.getStringExtra("driverId") + ", callid = " + auto.getCallsignid());
                }
            });
            t.start();



            return auto;
        }

        @Override
        protected void onPostExecute(autos currentAuto) {
            super.onPostExecute(currentAuto);
            //hardcode!?
            or = new order(Integer.valueOf(mIntent.getStringExtra("orderId")), "66666",
                    Integer.valueOf(currentAuto.getCallsignid()), currentAuto.getStatenumber(), currentAuto.getCarbrand(),
                    currentAuto.getCarcolor(), currentAuto.getDrivename(), currentAuto.getGeox(), currentAuto.getGeoy(),
                    mIntent.getStringExtra("driverId"), mIntent.getLongExtra("tm", 0), "phone");
            auto = currentAuto;
            orderWait(mIntent);
            Log.d(TAG, "onPostExecute");
        }
    }


    private void callDispatcherDialog() {

        if (window != null && window.isShowing()) {
            window.dismiss();
        }

        if (orderWait_ct3 != null) {
            orderWait_ct3.cancel();
        }
        findViewById(R.id.include1).setVisibility(View.VISIBLE);
        h.sendEmptyMessage(EXPAND_SLIDER);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                themedContext);
        dialogBuilder.setTitle(R.string.attention)
                .setMessage(R.string.call_operator_question)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call dispatcher
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + DISP_NUMBER));
                        startActivity(callIntent);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearForm();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        final Dialog callDialog = dialogBuilder.create();

        new Handler().postDelayed(new Runnable() {

            public void run() {
                callDialog.show();
            }

        }, 100L);
    }

    private class CarHereSetUp extends AsyncTask<Void, Void, String> {

        Context mContext;

        public CarHereSetUp(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected String doInBackground(Void... params) {
            ServiceConnection sc2 = new ServiceConnection(mContext);
            String message = sc2.getMessageArrivedAuto();
            auto = sc2.getCoordAuto(bundle.getString("driverId"));
            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            try {
                JSONObject result = new JSONObject(message);
                final String mes = result.optString("Message");
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (auto != null) {
                            ((TextView) sRoot.findViewById(R.id.txt_color)).setText(auto.getCarcolor() != null ? auto.getCarcolor() : "");
                            ((TextView) sRoot.findViewById(R.id.txt_color)).setTypeface(font_Roboto);
                            ((TextView) sRoot.findViewById(R.id.txt_car_brand)).setText(auto.getCarbrand());
                            ((TextView) sRoot.findViewById(R.id.txt_car_brand)).setTypeface(font_Roboto);
                            ((TextView) sRoot.findViewById(R.id.txt_num)).setText(auto.getStatenumber());
                            ((TextView) sRoot.findViewById(R.id.txt_num)).setTypeface(font_Roboto);
                            ((TextView) sRoot.findViewById(R.id.txt_year)).setText(auto.getYear() + "p.");
                            ((TextView) sRoot.findViewById(R.id.txt_year)).setTypeface(font_Roboto);
                            ((TextView) sRoot.findViewById(R.id.go_out)).setTypeface(font_Roboto);

                            String te = "";
                            switch (Integer.parseInt(auto.getStatus())) {
                                case 1:
                                    te = "Бизнес класс";
                                    break;
                                case 2:
                                    te = "Стандарт класс";
                                    break;
                                case 5:
                                    te = "Премиум класс";
                                    break;
                                case 6:
                                    te = "Минивен";
                                    break;
                                default:
                                    te = "Без класса";
                                    break;
                            }
                            ((TextView) sRoot.findViewById(R.id.txt_status)).setText(te);
                        }
                        sRoot.findViewById(R.id.bt_ok).setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                window.dismiss();
                                showCountDown = false;
                                findViewById(R.id.include1).setVisibility(View.VISIBLE);
                                h.sendEmptyMessage(EXPAND_SLIDER);
                                driving = false;
                                SharedPreferences orderSettings = getSharedPreferences("order", Context.MODE_PRIVATE);
                                Editor mEdit = orderSettings.edit();
                                mEdit.clear().commit();
                                setIntent(null);
                                stopService(new Intent(HomeScreen.this, WaitingCarService.class));
                                //startActivity(new Intent(HomeScreen.this, HomeScreen.class));
                            }
                        });
                    }
                });

            } catch (JSONException e) {
                Log.d("error", e.getMessage());
            }
        }
    }

    private void orderCarHere(Intent intent) {
        sIntent = intent;
        bundle = sIntent.getExtras();
        activity_home.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (window != null && window.isShowing()) {
                    window.dismiss();
                }
                driving = false;
                if (orderWait_ct3 != null)
                    orderWait_ct3.cancel();



                LayoutInflater inflater = (LayoutInflater) activity_home.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                sRoot = inflater.inflate(R.layout.order_car_here, null);
                sRoot.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
                sRoot.measure(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                window = new PopupWindow(sRoot, WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                //window.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                //window.setHeight(WindowManager.LayoutParams.MATCH_PARENT);


                // window.setContentView(sRoot);


                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        window.showAtLocation(activity_home.findViewById(R.id.it_1), Gravity.CENTER, 0, 0);
                        findViewById(R.id.include1).setVisibility(View.GONE);
                        h.sendEmptyMessage(COLLAPSE_SLIDER);
                    }

                }, 100L);

                //Button bt_ok = ;


                ((sRoot.findViewById(R.id.but_call))).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            if (!or.getPhone().equals(""))
                                callIntent.setData(Uri.parse("tel:" + or.getPhone()));
                            else
                                //for test
                                callIntent.setData(Uri.parse("tel:" + 111));
                            startActivity(callIntent);
                        } catch (Exception e) {
                            //Mint.logException(e);
                            Log.d("CALL_CHECK", "call error = " + e.getMessage());
                            Toast.makeText(HomeScreen.this, "Нету номера", Toast.LENGTH_LONG).show();
                        }
                    }
                });


                if (bundle == null) {
                    Log.d("OrderWait", "0");
                } else {  //после приезда машины

                    final SharedPreferences mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("driverId", bundle.getString("driverId"));
                    editor.commit();

                    (new CarHereSetUp(HomeScreen.this)).execute();
                }
            }
        });
    }

    void drawMarkerOnDriveCar() {
        driving = true;
        new Thread(new Runnable() {

            @Override
            public void run() {
                ServiceConnection sc = new ServiceConnection(
                        HomeScreen.this);
                if (or != null) {
                    auto = sc.getCoordAuto(or.getCallsignid() + "");

                } else {
                    auto = sc.getCoordAuto(callSignIdFromPush + "");

                }
                or.setCarbrand(auto.getCarbrand());
                or.setCarcolor(auto.getCarcolor());
                or.setGeox(auto.getGeox());
                or.setGeoy(auto.getGeoy());

            }
        }).start();



        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setBackgroundResource(R.drawable.gray_without_corners);
        TextView tv = new TextView(this);
        tv.setText((or != null) ? or.getCarbrand() : "new car");
        //TableLayout.LayoutParams l=new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        //l.gravity=Gravity.CENTER;
        view.addView(tv);

        LinearLayout view2 = new LinearLayout(this);
        tv = new TextView(this);
        tv.setText((or != null) ? "   " + or.getCarcolor() + "   " : "new car");
        view2.addView(tv);
        tv = new TextView(this);
        tv.setText((or != null) ? or.getStatenumber() + "   " : "new car");
        view2.addView(tv);

        view.addView(view2);
        //view.setDrawingCacheEnabled(true);
        //view.buildDrawingCache();
        mm = new MarkerOptions();
        mm.icon(BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view)));

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (driving) {
                    ServiceConnection sc = new ServiceConnection(
                            HomeScreen.this);
                    if (or != null) {
                        auto = sc.getCoordAuto(or.getCallsignid() + "");
                    } else {
                        auto = sc.getCoordAuto(callSignIdFromPush + "");
                    }
                    try {
                        if (auto.getGeox() != null && auto.getGeoy() != null
                                && !auto.getGeox().equals("") && !auto.getGeoy().equals("")) {
                            mm.position(new LatLng(Double.parseDouble(auto.getGeox()), Double.parseDouble(auto.getGeoy())));
                            cuf = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(auto.getGeox()), Double.parseDouble(auto.getGeoy())));
                        }
                    } catch (Exception e) {
                        // //Mint.logException(e);
                        try {
                            if (or.getGeox() != null && or.getGeoy() != null
                                    && !or.getGeox().equals("") && !or.getGeoy().equals("")) {
                                mm.position(new LatLng(Double.parseDouble(or.getGeox()), Double.parseDouble(or.getGeoy())));
                                cuf = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(or.getGeoy()), Double.parseDouble(or.getGeox())));
                            }
                        } catch (Exception k) {
                            // //Mint.logException(e);
                        }
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mMap == null) {
                                mMap = ((SupportMapFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.map)).getMap();
                                try {
                                    MapsInitializer.initialize(getApplicationContext());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (marker != null) {
                                marker.remove();
                            }
                            try {
                                marker = mMap.addMarker(mm);
                                mMap.animateCamera(cuf);
                            } catch (Exception e) {
                                //Mint.logException(e);
                                Log.e("tag-tag-tag mm = ", mm + "");
                                Log.e("tag-tag-tag mm = ", mMap + "");
                                e.printStackTrace();
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000l);
                    } catch (InterruptedException e) {
                        //Mint.logException(e);
                        // ""
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            v.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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

    private void makeCoords(final String[] coordinates) {
        if (!f && !t) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final double lat = Double.parseDouble(coordinates[0]);
                    final double lng = Double.parseDouble(coordinates[1]);

                    p = new LatLng(lat, lng);
                    if (p != null) {
                        mMap.addMarker(new MarkerOptions().position(p).title("Я"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p, 14), 1500, null);
                    }
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(p));
                }

            });
        }
    }

    public void clearForm() {

        Log.d("DELETE_PREFS", "deleted");
        txt_from.setText("Адрес подачи автомобиля");
        txt_to.setText("Адрес прибытия автомобиля");

        txt_from.setTextColor(Color.rgb(169, 169, 169));
        txt_to.setTextColor(Color.rgb(169, 169, 169));

        txt_goN.setText("Уехать сейчас");
        txt_goT.setText("Или уехать в другое время");

        ((TextView) findViewById(R.id.price_titles_txt)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.price_values_txt)).setVisibility(View.GONE);
        price.setVisibility(View.VISIBLE);

        Editor editor = mSettings.edit();

        editor.putString("Street", "");
        editor.putString("StreetId", "");
        editor.putString("HouseId", "");
        editor.putString("Dom", "");
        editor.putString("Parad", "");
        editor.putString("fromx", "");
        editor.putString("fromy", "");
        editor.putString("Prim", "");
        editor.putString("flat", "");

        editor.putString("Street2", "");
        editor.putString("StreetId2", "");
        editor.putString("HouseId2", "");
        editor.putString("Dom2", "");
        editor.putString("tox", "");
        editor.putString("toy", "");

        editor.putString("longRoute", "");

        txt_dop.setVisibility(View.VISIBLE);
        editor.putBoolean("Snow", false);
        editor.putBoolean("Pos", false);
        editor.putBoolean("Pat", false);
        editor.putBoolean("Wifi", false);
        editor.putBoolean("Smoke", false);
        editor.putBoolean("Bag", false);
        snow.setVisibility(View.GONE);
        pos.setVisibility(View.GONE);
        pat.setVisibility(View.GONE);
        wifi.setVisibility(View.GONE);
        smoke.setVisibility(View.GONE);
        bag.setVisibility(View.GONE);

        editor.putString("Time", "");
        editor.putLong("timeToServer", 0);
        editor.putString("OrderId", "");
        editor.putBoolean("prelim", false);

        editor.putString("DriverId", "");

        //editor.clear();

        editor.commit();

        t = false;
        f = false;

        isTimePick = false;
        isDatePick = false;

        selDate = 0;
        curDate = 0;
        //calendar = null;
        if (calendar != null)
            calendar.clear();

        f1.setImageResource(R.drawable.fold_inactive1);
        f2.setImageResource(R.drawable.fold_inactive1);

        ll_points1.setVisibility(View.GONE);
        ll_points2.setVisibility(View.GONE);

        getPrice();
    }

    @Override
    public void saveRoute(route mRoute) {
        r = mRoute;
        if (isMap) {
            workWithMap();
        }
    }


    LocationListener lListner = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // ""

        }

        @Override
        public void onProviderEnabled(String provider) {
            // ""

        }

        @Override
        public void onProviderDisabled(String provider) {
            // ""

        }

        @Override
        public void onLocationChanged(Location location) {
            changed = true;
            myLat = location.getLatitude();
            myLon = location.getLongitude();
            makeCoords(new String[]{location.getLatitude() + "", location.getLongitude() + ""});
            locMan.removeUpdates(lListner);
        }
    };


}

