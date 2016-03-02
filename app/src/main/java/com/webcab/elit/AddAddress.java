package com.webcab.elit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.webcab.elit.Utils.Utilits;
import com.webcab.elit.adapters.AutoCompleteLoadding;
import com.webcab.elit.adapters.HomeAdapter;
import com.webcab.elit.adapters.StreetAdapter;
import com.webcab.elit.data.addr2;
import com.webcab.elit.data.house;
import com.webcab.elit.data.street;
import com.webcab.elit.net.ServiceConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 24.10.2015.
 */
public class AddAddress extends Activity {

    private static final String TAG = "AddAddress_check";
    SharedPreferences mSettings;

    AutoCompleteLoadding aut1, aut2;
    Button bt_ok, bt_c;
    ContextThemeWrapper themedContext;

    //current point values
    private String currentStreetName;
    private String currentStreetID;
    private String currentHouseID;
    private String currentHouseNumber;
    private String currentGeoX;
    private String currentGeoY;
    private addr2 currentAddr;
    private JSONObject currentAddressJSON;
    private List<addr2> currentAddressList;
    private JSONArray addressJSONArray;

    

    private int orderId = -1;

    private HomeAdapter hAdapter;
    private house selectHouse;
    private ProgressBar pr_dom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_to_universal);

        //assign preferences
        mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        //define custom contexttheme for alert dialogs
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            themedContext = new ContextThemeWrapper( AddAddress.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
        }
        else {
            themedContext = new ContextThemeWrapper( AddAddress.this, android.R.style.Theme_Light_NoTitleBar );
        }

        //set up font
        Typeface font1 = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

        ((TextView) findViewById(R.id.street_txt)).setTypeface(font1);
        ((TextView) findViewById(R.id.house_number_txt)).setTypeface(font1);
        ((Button) findViewById(R.id.button3)).setTypeface(font1);


        ProgressBar pr_str = (ProgressBar) findViewById(R.id.pr_str);
        pr_dom = (ProgressBar) findViewById(R.id.pr_dom);

        aut1 = (AutoCompleteLoadding) findViewById(R.id.aut1);
        aut1.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
        aut1.setTypeface(font1);

        aut2 = (AutoCompleteLoadding) findViewById(R.id.aut2);
        aut2.setTypeface(font1);

        aut1.setLoadingIndicator(pr_str);
        StreetAdapter stAdapter = new StreetAdapter(this, R.layout.street_item, R.id.txt_year); //android.R.layout.select_dialog_item
        aut1.setAdapter(stAdapter);


        aut2.setLoadingIndicator(pr_dom);
        aut2.setThreshold(1);
        aut2.setEnabled(true);



        //setUp Autocompletes

        aut1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                street st = (street) parent.getItemAtPosition(position);
                saveStreet(st);
            }
        });

        aut1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    street st = (street) aut1.getAdapter().getItem(0);
                    saveStreet(st);
                }
                return false;
            }
        });

        aut1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && currentStreetID == null || (currentStreetID != null && currentStreetID.equals(""))) {
                    street st = (street) aut1.getAdapter().getItem(0);
                    saveStreet(st);
                }
                if (hasFocus && currentStreetID != null) {
                    currentStreetID = "";
                }
            }
        });

        aut2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                house st = (house) parent.getItemAtPosition(position);
                saveHouse(st);
            }
        });

        aut2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    house st = (house) aut2.getAdapter().getItem(0);
                    saveHouse(st);
                }
                return false;
            }
        });

        aut2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && currentHouseID == null || (currentHouseID != null && currentHouseID.equals(""))) {
                    house st = (house) aut2.getAdapter().getItem(0);
                    saveHouse(st);
                }
                if (hasFocus && currentHouseID != null) {
                    currentStreetID = "";
                }
            }
        });

        //get all points in List
        currentAddressList = new ArrayList<>();
        if (mSettings.contains("Street2") && mSettings.contains("StreetId2")
                && mSettings.contains("Dom2") && mSettings.contains("tox")
                && mSettings.contains("toy") && mSettings.contains("HouseId2")
                && !mSettings.getString("Street2", "").equals("")) {
            addr2 mAddr = new addr2(mSettings.getString("Street2", ""), mSettings.getString("StreetId2", ""),
                    mSettings.getString("Dom2", ""), mSettings.getString("tox", ""), mSettings.getString("toy", ""));
            mAddr.setHouseID(mSettings.getString("HouseId2", ""));
            currentAddressList.add(mAddr);
        }
        if (mSettings.contains("longRoute") && !mSettings.getString("longRoute", "").equals("")) {
            try {
                addressJSONArray = new JSONArray(mSettings.getString("longRoute", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 1; i < addressJSONArray.length(); i++) {
                try {
                    addr2 mAddr = Utilits.getAddressFromJSON((JSONObject) addressJSONArray.get(i));
                    currentAddressList.add(mAddr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "list.size = " + currentAddressList.size());



        //check Intent and get pointID
        if (AddAddress.this.getIntent() != null && AddAddress.this.getIntent().hasExtra(HomeScreen.POINT_ID)) {
            orderId = AddAddress.this.getIntent().getIntExtra(HomeScreen.POINT_ID, -1);
            
            //define current point and get address for it if exist to edit it
            //store values of address in currentValues

            switch (orderId) {
                case HomeScreen.TO_1:
                    //two points route address TO must be taken from Street2
                    if (mSettings.contains("Street2") && mSettings.contains("StreetId2")
                            && mSettings.contains("Dom2") && mSettings.contains("tox")
                            && mSettings.contains("toy") && mSettings.contains("HouseId2")) {
                        currentAddr = new addr2(mSettings.getString("Street2", ""), mSettings.getString("StreetId2", ""),
                                mSettings.getString("Dom2", ""), mSettings.getString("tox", ""), mSettings.getString("toy", ""));
                        currentAddr.setHouseID(mSettings.getString("HouseId2", ""));
                    }
                    break;
                case HomeScreen.TO_2:
                    //three points route address is second in longRoute
                    if (mSettings.contains("longRoute") && !mSettings.getString("longRoute", "").equals("")) {
                        try {
                            addressJSONArray = new JSONArray(mSettings.getString("longRoute", ""));
                            currentAddressJSON = (JSONObject) addressJSONArray.get(1);
                            currentAddr = Utilits.getAddressFromJSON(currentAddressJSON);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON reading error");
                        }
                    }
                    break;
                case HomeScreen.TO_3:
                    //four points route address is third and last in longRoute
                    if (mSettings.contains("longRoute") && !mSettings.getString("longRoute", "").equals("")) {
                        try {
                            addressJSONArray = new JSONArray(mSettings.getString("longRoute", ""));
                            currentAddressJSON = (JSONObject) addressJSONArray.get(2);
                            currentAddr = Utilits.getAddressFromJSON(currentAddressJSON);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON reading error");
                        }
                    }
                    break;
                case HomeScreen.ADD:
                    break;
                case -1:
                    finish();
                    break;
            }
        }

        //setUp focus and text of Autocompletes
        if (currentAddr != null) {
            aut1.setAdapter(null);
            aut1.setText(currentAddr.getSreetname());
            aut1.setAdapter(stAdapter);
            aut2.setAdapter(null);
            aut2.setText(currentAddr.getHousenumber());
            aut2.setAdapter(stAdapter);
            aut1.requestFocus();
            aut1.setSelection(currentAddr.getSreetname().length());
        }


        bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_ok.setTypeface(font1);
        final int finalNewOrderID = -2;
        bt_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (aut2.getAdapter() != null && currentAddr != null && currentAddr.getHouseID().equals("")) {
                    Log.d(TAG, "check");
                    house st = (house) aut2.getAdapter().getItem(0);
                    saveHouse(st);
                } else {
                    Log.d(TAG, "check1");
                }

                if (currentAddr != null
                        && !currentAddr.getStreetid().equals("") && !currentAddr.getHouseID().equals("")) {
                    //store address in corresponding preference
                    //if it is new address and there is no any addresses "TO" or first address is edited
                    if (orderId == HomeScreen.ADD && currentAddressList.size() == 0) {
                        //add address to Street2
                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("StreetId2", currentAddr.getStreetid());
                        editor.putString("HouseId2", currentAddr.getHouseID());
                        editor.putString("tox", currentAddr.getGeox());
                        editor.putString("toy", currentAddr.getGeoy());
                        editor.putString("Street2", currentAddr.getSreetname());
                        editor.putString("Dom2", currentAddr.getHousenumber());
                        editor.commit();
                    }
                    //new address, but there are already address "TO" in list
                    if (orderId == HomeScreen.ADD && currentAddressList.size() > 0) {
                        addressJSONArray = new JSONArray();
                        currentAddressList.add(currentAddr);
                        Log.d(TAG, "added another address. list.size = " + currentAddressList.size());
                        putLongRouteIntoPrefs(currentAddressList);
                    }
                    //edit addresses
                    //first address edit
                    if (orderId == HomeScreen.TO_1) {
                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("StreetId2", currentAddr.getStreetid());
                        editor.putString("HouseId2", currentAddr.getHouseID());
                        editor.putString("tox", currentAddr.getGeox());
                        editor.putString("toy", currentAddr.getGeoy());
                        editor.putString("Street2", currentAddr.getSreetname());
                        editor.putString("Dom2", currentAddr.getHousenumber());
                        editor.commit();
                        if (mSettings.contains("longRoute") && !mSettings.getString("longRoute", "").equals("")) {
                            currentAddressList.remove(0);
                            currentAddressList.add(0, currentAddr);
                            putLongRouteIntoPrefs(currentAddressList);
                        }
                    }
                    //second address is edited
                    if (orderId == HomeScreen.TO_2) {
                        currentAddressList.remove(1);
                        currentAddressList.add(1, currentAddr);
                        putLongRouteIntoPrefs(currentAddressList);
                    }
                    //third address is edited
                    if (orderId == HomeScreen.TO_3) {
                        currentAddressList.remove(2);
                        currentAddressList.add(2, currentAddr);
                        Log.d(TAG, "Current Street = " + currentAddr.getSreetname());
                        putLongRouteIntoPrefs(currentAddressList);
                    }
                    //clear current values
                    currentStreetName = "";
                    currentStreetID = "";
                    currentHouseID = "";
                    currentHouseNumber = "";
                    currentGeoX = "";
                    currentGeoY = "";
                    currentAddr = null;
                    currentAddressJSON = null;
                    addressJSONArray = null;
                    currentAddressList.clear();
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
                        Log.d(SyncStateContract.Constants.CONTENT_DIRECTORY,
                                "Show Dialog: " + e.getMessage());
                    }
                }
            }
        });

        bt_c = (Button) findViewById(R.id.bt_cancel);
        bt_c.setTypeface(font1);
        bt_c.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        Button map = (Button) findViewById(R.id.bt_map);
        map.setTypeface(font1);
        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // ""
                Intent i = new Intent(AddAddress.this, Map_Activity.class);
                i.putExtra("wh", "1");
                startActivity(i);
            }
        });

    }

    private void saveHouse(house st) {
        if (st != null) {
            //aut2_selected = true;
            aut2.setText(st.getValue());
            currentHouseNumber = st.getValue();
            currentHouseID = st.getId();
            currentGeoX = st.getX();
            currentGeoY = st.getY();

            aut2.dismissDropDown();
            pr_dom.setVisibility(View.GONE);

            currentAddr = new addr2(currentStreetName, currentStreetID, currentHouseNumber, currentGeoX, currentGeoY);
            currentAddr.setHouseID(currentHouseID);

            aut2.setBackgroundResource(R.drawable.correct_address);
        }
    }

    /**
     * Store street data to current vars
     * @param st
     */
    private void saveStreet(street st) {
        if (st != null) {
            currentStreetName = st.getName();
            aut1.setText(currentStreetName);
            currentStreetID = st.getId();
            currentGeoX = st.getGeox();
            currentGeoY = st.getGeoy();

            aut1.dismissDropDown();

            HomeAdapter hAdapter = new HomeAdapter(AddAddress.this, R.layout.street_item, R.id.txt_year, currentStreetID);
            hAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    if (aut2.getDropDownAnchor() != 0) {
                        //selectHouse = hAdapter.getItem(0);
                    }
                }
            });
            aut2.setAdapter(hAdapter);
            hAdapter.notifyDataSetChanged();
            aut2.setEnabled(true);
            aut2.setThreshold(1);
            aut2.setLoadingIndicator(pr_dom);
            //aut2.showDropDown();
            aut2.setText("");
            //aut2_selected = false;
            aut1.setBackgroundResource(R.drawable.correct_address);
            aut2.requestFocus();
        }
    }

    /**
     * Stores JSONArray of addresses into longRoute
     * @param addresses - list of addr2 addresses "where"
     */
    private JSONArray putLongRouteIntoPrefs(List<addr2> addresses) {

        addressJSONArray = new JSONArray();
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
        Log.d(TAG, "JSON = " + addressJSONArray);
        return addressJSONArray;
    }

    @Override
    protected void onRestart() {
        // ""
        super.onRestart();
        Log.d("log activity", "onRestart homescreen");
        if ((mSettings.contains("Lat1")) && (mSettings.contains("Lon1"))) {

            Thread th = new Thread(new Runnable() {

                @Override
                public void run() {
                    ServiceConnection sc = new ServiceConnection(
                            getApplicationContext());

                    currentAddr = sc.getAddressFromGeo(mSettings.getString("Lat1", ""), mSettings.getString("Lon1", ""));
                    currentAddr.setHouseID("1");

                    if (currentAddr != null) {

                        runOnUiThread(new Runnable() {
                            public void run() {

                                aut1.setText(currentAddr.getSreetname());
                                aut1.setSelection(currentAddr.getSreetname().length());
                                aut1.clearFocus();
                                aut2.setText(currentAddr.getHousenumber());
                                aut2.setSelection(currentAddr.getHousenumber().length());
                                aut2.clearFocus();
                            }
                        });

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("StreetId", currentAddr.getStreetid());
                        editor.putString("HouseId", currentAddr.getHouseID());
                        editor.commit();
                    }


                }
            });

            th.start();

        }
    }

private class checkHouseNumber extends AsyncTask<Void, Void, List<house>> {

    private addr2 checkedAddress;
    private ServiceConnection sc;
    private Context mContext;
    private String checkedHouseID;

    public checkHouseNumber(Context mContext, addr2 checkedAddress, String checkedHouseID) {
        this.checkedAddress = checkedAddress;
        this.mContext = mContext;
        this.checkedHouseID = checkedHouseID;
        this.sc = new ServiceConnection(this.mContext);
    }

    @Override
    protected List<house> doInBackground(Void... params) {
        return sc.getStreetHouse(checkedAddress.getStreetid(), checkedHouseID);
    }

    @Override
    protected void onPostExecute(List<house> result) {
        super.onPostExecute(result);
        if (result.size() != 0) {
            //save results
        } else {
            //error message
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(themedContext);
            mBuilder.setTitle(R.string.attention);
            mBuilder.setMessage(R.string.wrong_house_number);
        }
    }
}
}
