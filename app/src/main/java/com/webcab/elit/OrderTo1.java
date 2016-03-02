package com.webcab.elit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.webcab.elit.adapters.AutoCompleteLoadding;
import com.webcab.elit.data.addr2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 18.08.2015.
 */
public class OrderTo1 extends FragmentActivity implements AddressFragment.onAddressActionListener {

    List<addr2> addresses;
    List<Fragment> fragmentList;

    Button bt_ok, bt_c, btnAdd;

    ContextThemeWrapper themedContext;
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_to1);

        fragmentList = new ArrayList<Fragment>();
        addresses = new ArrayList<addr2>();


        mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        if (mSettings.contains("longRoute")) {
            try {
                JSONArray addrJSON = new JSONArray(mSettings.getString("longRoute", ""));
                addresses = getAddresesFromJSON(addrJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (addr2 point : addresses) {
                Bundle args = new Bundle();
                Fragment f = new AddressFragment();
                args.putString("streetName", point.getSreetname());
                args.putString("houseNumber", point.getHousenumber());
                f.setArguments(args);
                fragmentList.add(f);
            }
        }

        if (savedInstanceState == null) {
            if (fragmentList.size() == 0)
                fragmentList.add(new AddressFragment());
            int i = 0;
            for (Fragment f : fragmentList) {
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, f, String.valueOf(i)).commit();
                i++;
            }
        }



        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            themedContext = new ContextThemeWrapper( OrderTo1.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
        }
        else {
            themedContext = new ContextThemeWrapper( OrderTo1.this, android.R.style.Theme_Light_NoTitleBar );
        }



        btnAdd = (Button) findViewById(R.id.but_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newAddressFragment = new AddressFragment();
                fragmentList.add(newAddressFragment);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, newAddressFragment, String.valueOf(fragmentList.size() - 1)).commit();
            }
        });

        bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (getAddressList(fragmentList)) {
                    JSONArray addressJSONArray = new JSONArray();
                    Log.d("Address_check", "there are " + addresses.size() + " addresses");
                    for (int i = 0; i < fragmentList.size(); i++) {
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
                    }

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("longRoute", addressJSONArray.toString());


                    editor.putString("StreetId2", addresses.get(addresses.size() - 1).getStreetid());
                    editor.putString("HouseId2", addresses.get(addresses.size() - 1).getHouseID());
                    editor.putString("tox", addresses.get(addresses.size() - 1).getGeox());
                    editor.putString("toy", addresses.get(addresses.size() - 1).getGeoy());
                    editor.putString("Street2", addresses.get(addresses.size() - 1).getSreetname());
                    editor.putString("Dom2", addresses.get(addresses.size() - 1).getHousenumber());

                    editor.commit();
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
        bt_c.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });



        setUpFonts();
    }

    private List<addr2> getAddresesFromJSON(JSONArray addrJSON) {
        List<addr2> addr = new ArrayList<addr2>();
        for (int i = 0; i < addrJSON.length(); i++) {
            JSONObject point;
            try {
                point = (JSONObject) addrJSON.get(i);
                JSONObject streetJSON = point.getJSONObject("street");
                JSONObject houseJSON = point.getJSONObject("house");
                addr.add(new addr2(streetJSON.optString("value"), streetJSON.optString("id"), houseJSON.optString("value"),
                        houseJSON.optString("geox"), houseJSON.optString("geoy")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return addr;
    }

    private boolean getAddressList(List<Fragment> fragmentList) {
        boolean result = false;
        String curStreetName;
        String curstreetID;
        String curHouseID;
        String curhouseNumber;
        String curGeoX;
        String curGeoY;
        for (Fragment f : fragmentList) {
            try {
                curStreetName = ((AutoCompleteLoadding) f.getView().findViewById(R.id.aut1)).getText().toString();
                curhouseNumber = ((AutoCompleteLoadding) f.getView().findViewById(R.id.aut2)).getText().toString();
                curstreetID = ((TextView) f.getView().findViewById(R.id.street_id)).getText().toString();
                curHouseID = ((TextView) f.getView().findViewById(R.id.houseID)).getText().toString();
                curGeoX = ((TextView) f.getView().findViewById(R.id.geox)).getText().toString();
                curGeoY = ((TextView) f.getView().findViewById(R.id.geoy)).getText().toString();
            } catch (NullPointerException e) {
                Log.d("route error", "error = " + e.getMessage());
                return result;
            }
            if (!curStreetName.equals("") && !curstreetID.equals("")
                    && !curhouseNumber.equals("")
                    && !curGeoX.equals("")
                    && !curGeoY.equals("")) {
                addr2 mAddress = new addr2(curStreetName, curstreetID, curhouseNumber, curGeoX, curGeoY);
                mAddress.setHouseID(curHouseID);
                addresses.add(mAddress);

            }
        }
        if (addresses.size() != fragmentList.size()) {
            return result;
        } else {
            result = true;
        }
        return result;
    }

    private void setUpFonts() {

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

        ((Button) findViewById(R.id.button3)).setTypeface(font);
        ((Button) findViewById(R.id.bt_cancel)).setTypeface(font);
        ((Button) findViewById(R.id.bt_ok)).setTypeface(font);
        ((Button) findViewById(R.id.but_add)).setTypeface(font);


    }

    @Override
    public void deleteAddress(int id) {
        if (fragmentList.size() > 1) {
            getSupportFragmentManager().beginTransaction().detach(fragmentList.get(id)).commit();
            fragmentList.remove(id);
            addresses.remove(id);
        }
    }

    @Override
    public void moveUp(int id) {
        /*if (fragmentList.size() > 1 && id > 0) {
            Fragment movingFragment = fragmentList.get(id);
            Fragment oldFragment = fragmentList.get(id - 1);
            for (Fragment f : fragmentList)
                getSupportFragmentManager().beginTransaction().remove(f).commit();
            fragmentList.add(id, oldFragment);
            fragmentList.add(id - 1, movingFragment);
            int i = 0;
            for (Fragment f : fragmentList) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, f, String.valueOf(i)).commit();
                i++;
            }
        }*/
    }

    @Override
    public void moveDown(int id) {

    }
}
