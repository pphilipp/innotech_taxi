package com.webcab.elit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.webcab.elit.adapters.AutoCompleteLoadding;
import com.webcab.elit.adapters.HomeAdapter;
import com.webcab.elit.adapters.StreetAdapter;
import com.webcab.elit.data.addr2;
import com.webcab.elit.data.house;
import com.webcab.elit.data.street;
import com.webcab.elit.net.ServiceConnection;

public class Order_from extends Activity implements TextWatcher {

    SharedPreferences mSettings;

    TextView t1, t2, t3, t4, tc, txt_coord;
    Button bt_ok, bt_c;
    EditText aut3, edit_d, aut4;
    AutoCompleteLoadding aut1, aut2;
    HomeAdapter hAdapter;
    boolean aut1_selected, aut2_selected;
    ScrollView scrollView;
    street selectStreet;
    house selectHouse;

    ContextThemeWrapper themedContext;
    //String[] data = { "Киев" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_from);


        mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        scrollView = (ScrollView) findViewById(R.id.scrollView1);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            themedContext = new ContextThemeWrapper( Order_from.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
        }
        else {
            themedContext = new ContextThemeWrapper( Order_from.this, android.R.style.Theme_Light_NoTitleBar );
        }

        Typeface font1 = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

        ((TextView) findViewById(R.id.txt_year)).setTypeface(font1);
        ((TextView) findViewById(R.id.TextView01)).setTypeface(font1);
        ((TextView) findViewById(R.id.TextView02)).setTypeface(font1);
        ((TextView) findViewById(R.id.TextView03)).setTypeface(font1);
        ((TextView) findViewById(R.id.TextView06)).setTypeface(font1);
        ((TextView) findViewById(R.id.txt_color)).setTypeface(font1);
        ((Button) findViewById(R.id.button3)).setTypeface(font1);


        ProgressBar pr_str = (ProgressBar) findViewById(R.id.pr_str);
        ProgressBar pr_dom = (ProgressBar) findViewById(R.id.pr_dom);

        aut1 = (AutoCompleteLoadding) findViewById(R.id.aut1);
        aut1.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
        aut1.setTypeface(font1);
        aut1.setLoadingIndicator(pr_str);
        StreetAdapter stAdapter = new StreetAdapter(this, R.layout.street_item, R.id.txt_year); //android.R.layout.select_dialog_item
        aut1.setAdapter(stAdapter);

        aut2 = (AutoCompleteLoadding) findViewById(R.id.aut2);
        aut2.setTypeface(font1);
        aut2.setLoadingIndicator(pr_dom);
        Log.d("minimum", "min - " + aut2.getThreshold());

        aut2.setThreshold(1);
        aut2.setEnabled(true);
        aut2.addTextChangedListener(this);





        aut1.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // ""
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {

                    street st;
                    st = (street) aut1.getAdapter().getItem(0);
                    if (st != null) {
                        aut1.setText(st.getName());
                        //aut1.setSelection(st.getName().length());

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("StreetId", st.getId());

                        editor.putString("Lat1", st.getGeoy());
                        editor.putString("Lon1", st.getGeox());
                        txt_coord.setText(st.getGeoy() + ", " + st.getGeox());

                        editor.putString("HouseId", "");

                        editor.putString("fromx", st.getGeox());
                        editor.putString("fromy", st.getGeoy());
                        Log.d("SHARED", "puted " + st.getId() + " to StreetId");
                        Log.d("SHARED", "puted " + st.getGeox() + " - " + st.getGeoy() + " to fromx and fromy");
                        editor.commit();
                        String streetId = mSettings.getString("StreetId", "");
                        Log.d("streetid", "streetId - " + streetId);

                        aut1.dismissDropDown();

                        hAdapter = new HomeAdapter(Order_from.this, R.layout.street_item, R.id.txt_year, streetId);
                        hAdapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if (aut2.getDropDownAnchor() != 0)
                                    selectHouse = hAdapter.getItem(0);
                            }
                        });
                        aut2.setAdapter(hAdapter);
                        aut2.setEnabled(true);
                        aut2.setText("");
                        aut2.requestFocus();
                        aut2_selected = false;
                    }


                    aut2.requestFocus();

                    return false;
                }
                return false;
            }
        });


        aut1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                aut1_selected = true;
                //Log.d("setOnItemClickListener" , "pos - " + arg2);
                Object obj = arg0.getItemAtPosition(arg2); //должно быть "com.webcab.elit.data.street"
                street st = (street) arg0.getItemAtPosition(arg2);
                if (st != null) {
                    Log.d("setOnItemClickListener", "type - " + obj.getClass().getName());
                    //Log.d("setOnItemClickListener" , "name - " + st.getName() + " id - " + st.getId());
                    aut1.setText(st.getName());
                    //aut1.setSelection(st.getName().length());

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("StreetId", st.getId());

                    editor.putString("Lat1", st.getGeoy());
                    editor.putString("Lon1", st.getGeox());
                    txt_coord.setText(st.getGeoy() + ", " + st.getGeox());

                    editor.putString("HouseId", "");

                    editor.putString("fromx", st.getGeox());
                    editor.putString("fromy", st.getGeoy());
                    Log.d("SHARED", "puted " + st.getId() + " to StreetId");
                    Log.d("SHARED", "puted " + st.getGeox() + " - " + st.getGeoy() + " to fromx and fromy");
                    editor.commit();

                    String streetId = mSettings.getString("StreetId", "");
                    Log.d("streetid", "streetId - " + streetId);
                    hAdapter = new HomeAdapter(Order_from.this, R.layout.street_item, R.id.txt_year, streetId);

                    hAdapter.registerDataSetObserver(new DataSetObserver() {
                        @Override
                        public void onChanged() {
                            super.onChanged();
                            if (aut2.getDropDownAnchor() != 0)
                                selectHouse = hAdapter.getItem(0);
                        }
                    });
                    aut2.setAdapter(hAdapter);
                    aut2.setEnabled(true);
                    aut2.setText("");
                    aut2.requestFocus();
                    aut2_selected = false;

                }

            }
        });

        aut1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String streetId = mSettings.getString("StreetId", "");

                    if (streetId != null && !streetId.equals("") && aut2.getText().toString().equals("")) {
                        Log.d("myLogs", "trying to setUp aut2");
                        hAdapter = new HomeAdapter(Order_from.this, R.layout.street_item, R.id.txt_year, streetId);
                        hAdapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if (aut2.getDropDownAnchor() != 0)
                                    selectHouse = hAdapter.getItem(0);
                            }
                        });
                        aut2.setAdapter(hAdapter);
                        aut2.setEnabled(true);
                        //aut2.setText("");
                        aut2.requestFocus();
                        aut2_selected = false;
                    }
                }
            }
        });


        aut2.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // ""
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    aut2_selected = true;
                    house st = null;
                    if (aut2.getAdapter() != null) {
                        st = (house) aut2.getAdapter().getItem(0);
                        Log.d("myLogs", "hAdapterCount1 = " + aut2.getAdapter().getCount());
                    }

                    if (st != null) {
                        aut2.setText(st.getValue());
                        //aut1.setSelection(st.getName().length());

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("HouseId", st.getId());

                        editor.putString("Lat1", st.getY());
                        editor.putString("Lon1", st.getX());
                        txt_coord.setText(st.getY() + ", " + st.getX());

                        editor.putString("fromx", st.getX());
                        editor.putString("fromy", st.getY());
                        Log.d("SHARED", "puted " + st.getX() + " - " + st.getY() + " to fromx and fromy");
                        Log.d("SHARED", "puted " + st.getId() + " to HouseId");
                        editor.commit();
                        aut2.dismissDropDown();
                        aut3.setText("");
                        aut3.setEnabled(true);
                        aut3.requestFocus();
                    } else {
                        String streetId = mSettings.getString("StreetId", "");
                        if (streetId != null && !streetId.equals(""))
                            Log.d("myLogs", "streetId1 = " + streetId);
                    }
                    aut3.requestFocus();

                    return false;
                }
                return false;
            }
        });


        aut2.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                aut2_selected = true;
                //Log.d("setOnItemClickListener" , "pos - " + arg2);
                Object obj = arg0.getItemAtPosition(arg2); //должно быть "com.webcab.elit.data.house"
                house st = (house) arg0.getItemAtPosition(arg2);
                Log.d("myLogs", "hAdapterCount2 = " + aut2.getAdapter().getCount());
                if (st != null) {
                    Log.d("setOnItemClickListener", "type - " + obj.getClass().getName());
                    //Log.d("setOnItemClickListener" , "name - " + st.getName() + " id - " + st.getId());
                    aut2.setText(st.getValue());
                    //aut1.setSelection(st.getName().length());

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("HouseId", st.getId());

                    editor.putString("Lat1", st.getY());
                    editor.putString("Lon1", st.getX());
                    txt_coord.setText(st.getY() + ", " + st.getX());

                    editor.putString("fromx", st.getX());
                    editor.putString("fromy", st.getY());
                    Log.d("SHARED", "puted " + st.getX() + " - " + st.getY() + " to fromx and fromy");
                    Log.d("SHARED", "puted " + st.getId() + " to HouseId");
                    editor.commit();
                }
                aut3.setText("");
                aut3.setEnabled(true);
                aut3.requestFocus();
            }
        });

        aut2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String streetId = mSettings.getString("StreetId", "");

                    if (streetId != null && !streetId.equals("")) {
                        Log.d("myLogs", "trying to setUp aut2");
                        hAdapter = new HomeAdapter(Order_from.this, R.layout.street_item, R.id.txt_year, streetId);
                        hAdapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if (aut2.getDropDownAnchor() != 0)
                                    selectHouse = hAdapter.getItem(0);
                            }
                        });
                        aut2.setAdapter(hAdapter);
                        aut2.setEnabled(true);
                        //aut2.setText("");
                        aut2_selected = false;
                    }
                }
            }
        });

//        ((LinearLayout) findViewById(R.id.mainLayout))
                scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(selectHouse != null && !aut2_selected){
                    aut2.setText(selectHouse.getValue());
                    aut2.dismissDropDown();
                }
                //TODO тестирование определение номера дома без выбора из списка
                return true;
            }
        });

        aut3 = (EditText) findViewById(R.id.aut3);
        aut4 = (EditText) findViewById(R.id.aut4);
        aut4.setTypeface(font1);
        aut3.setTypeface(font1);
        edit_d = (EditText) findViewById(R.id.edit_d);
        edit_d.setTypeface(font1);

        aut3.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // ""
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    //edit_d.setEnabled(true);
                    edit_d.setText("");
                    edit_d.requestFocus();

                    return false;
                }
                return false;
            }
        });


        String str = mSettings.getString("Street", "");
        String dom = mSettings.getString("Dom", "");
        String parad = mSettings.getString("Parad", "");
        String prim = mSettings.getString("Prim", "");

        if (mSettings.contains("Street")) {
            aut1.setText(str);
            Log.d("shared", "seted street - " + str);
            aut1_selected = true;
        } else
            aut1_selected = false;
        if (mSettings.contains("Dom")) {
            aut2.setText(dom);
            Log.d("shared", "seted Dom - " + dom);
            aut2_selected = true;
        } else
            aut2_selected = false;
        if (mSettings.contains("Parad")) {
            aut3.setText(parad);
            Log.d("shared", "seted parad - " + parad);
        }
        if (mSettings.contains("Prim")) {
            edit_d.setText(prim);
            Log.d("shared", "seted Prim - " + prim);
        }

        bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_ok.setTypeface(font1);
        bt_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ""

                //				Log.d("TEST", aut1.getText() + " " + aut2.getText() +
                //						" " + mSettings.getString("StreetId", "") + " " + mSettings.getString("HouseId", ""));
                if (selectHouse != null && !aut2_selected) {
                    aut2.setText(selectHouse.getValue());

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("HouseId", selectHouse.getId());

                    editor.putString("Lat1", selectHouse.getY());
                    editor.putString("Lon1", selectHouse.getX());
                    txt_coord.setText(selectHouse.getY() + ", " + selectHouse.getX());

                    editor.putString("fromx", selectHouse.getX());
                    editor.putString("fromy", selectHouse.getY());
                    Log.d("SHARED", "puted " + selectHouse.getX() + " - " + selectHouse.getY() + " to fromx and fromy");
                    Log.d("SHARED", "puted " + selectHouse.getId() + " to HouseId");
                    editor.commit();
                    aut2.dismissDropDown();
                    aut3.setText("");
                    aut3.setEnabled(true);
                }
                if (ifFormOk()) {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("Street", aut1.getText().toString());
                    Log.d("SHARED", "puted " + aut1.getText().toString() + " to Street");
                    editor.putString("Dom", aut2.getText().toString());
                    Log.d("SHARED", "puted " + aut2.getText().toString() + " to Dom");
                    editor.putString("Parad", aut3.getText().toString());
                    Log.d("SHARED", "puted " + aut3.getText().toString() + " to Parad");
                    editor.putString("Prim", edit_d.getText().toString());
                    Log.d("SHARED", "puted " + edit_d.getText().toString() + " to Prim");
                    editor.putString("flat", aut4.getText().toString());
                    Log.d("SHARED", "puted " + edit_d.getText().toString() + " to flat");
                    editor.commit();
                    Log.d("SHARED", "COMMITED");

                    Intent intent = new Intent(Order_from.this, HomeScreen.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                themedContext).create();
                        alertDialog
                                .setMessage("Вы должны заполнить поля со зведочками (*) из предложенных!");
                        alertDialog.setButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                });

                        alertDialog.show();
                    } catch (Exception e) {
                        Log.d(Constants.CONTENT_DIRECTORY,
                                "Show Dialog: " + e.getMessage());
                    }
                }
            }
        });

        bt_c = (Button) findViewById(R.id.bt_cancel);
        bt_c.setTypeface(font1);
        bt_c.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        Button map = (Button) findViewById(R.id.bt_map);
        map.setTypeface(font1);
        map.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ""
                Intent i = new Intent(Order_from.this, Map_Activity.class);
                i.putExtra("wh", "1");
                startActivity(i);
            }
        });

        txt_coord = (TextView) findViewById(R.id.txt_coord);

        if ((mSettings.contains("Lat1")) && (mSettings.contains("Lon1"))) {
            txt_coord.setText(mSettings.getString("Lat1", "") + ", " + mSettings.getString("Lon1", ""));
            aut3.requestFocus();
        } else {
            txt_coord.setText("");
        }

        setUoFocus();

    }

    private boolean ifFormOk() {

        Boolean condition =
                //not empty fields
                !aut1.getText().toString().equals("")
                        && !aut2.getText().toString().equals("")
                        && (!aut4.getText().toString().equals("") || !aut3.getText().toString().equals(""))
                //not empty IDs
                && !mSettings.getString("StreetId", "").equals("")
                && !mSettings.getString("HouseId", "").equals("");

        return condition;
    }

    @Override
    protected void onRestart() {
        // ""
        super.onRestart();
        Log.d("log activity", "onRestart homescreen");
        if ((mSettings.contains("Lat1")) && (mSettings.contains("Lon1"))) {
            txt_coord.setText(mSettings.getString("Lat1", "") + ", " + mSettings.getString("Lon1", ""));

            Thread th = new Thread(new Runnable() {

                @Override
                public void run() {
                    ServiceConnection sc = new ServiceConnection(
                            getApplicationContext());

                    final addr2 addr = sc.getAddressFromGeo(mSettings.getString("Lat1", ""), mSettings.getString("Lon1", ""));

                    if (addr != null) {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                edit_d.requestFocus();

                                aut1.setText(addr.getSreetname());
                                aut2.setText(addr.getHousenumber());
                                //aut3.requestFocus();
                            }
                        });

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("StreetId", addr.getStreetid());
                        editor.putString("HouseId", "1");
                        editor.commit();
                    }


                }
            });

            th.start();

        } else {
            txt_coord.setText("");
        }
        setUoFocus();
    }

    @Override
    protected void onResume() {
        // ""
        super.onResume();
        setUoFocus();


    }

    private void setUoFocus() {
        if (!aut1.getText().toString().equals("") && !aut2.getText().toString().equals("")) {
            Log.d("log activity", "in cond");
            aut1_selected = true;
            aut2_selected = false;
            aut1.dismissDropDown();
            aut2.dismissDropDown();

            aut2.clearFocus();
            aut3.clearFocus();
            aut4.clearFocus();
            aut1.setSelection(aut1.getText().toString().length());
            //aut1.requestFocus();
            //aut3.requestFocus();
        } else {
            Log.d("log activity", "out of cond");
        }
    }

    @Override
    protected void onStart() {
        // ""
        super.onStart();
        Log.d("log activity", "onStart homescreen");
        setUoFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_order_from, menu);
        return true;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // ""

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // ""

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // ""
    }

}
