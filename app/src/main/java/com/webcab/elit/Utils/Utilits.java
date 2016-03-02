package com.webcab.elit.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.webcab.elit.Log;
import com.webcab.elit.data.addr2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sergey on 19.08.2015.
 */
public class Utilits {

    public class Constants {
        //settings
        public static final String ORDER_SETTINGS_NAME = "order";
        public static final String ORDER_TYPE = "orderType";
        public static final String CURRENT_ORDER_LIST = "currentOrders";
        //order types
        public static final int SIMPLE_CURRENT_ORDER = 100;
        public static final int NOTIFICATION_CURRENT_ORDER = 200;
        public static final int NO_CURRENT_ORDERS = 0;
        public static final int CAR_ARRIVED = 300;
    }

    private static final String TAG = "UTILS_TAG";

    public static addr2 getAddressFromJSON(JSONObject addressJSON) {
        Log.d("PARSE", "incoming JSON = " + addressJSON);
        String streetName = "";
        String streetid = "";
        String housenumber = "";
        //taking house geos
        String geox = "";
        String geoy = "";
        JSONObject street = addressJSON.optJSONObject("street");
        streetName = street.optString("value");
        streetid = street.optString("id");
        JSONObject house = addressJSON.optJSONObject("house");
        housenumber = house.optString("value");
        geox = house.optString("geox");
        geoy = house.optString("geoy");
        return new addr2(streetName, streetid, housenumber, geox, geoy);
    }

    public static void saveDeviceInfo(Context mContext) {
        SharedPreferences mSettings = mContext.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSettings.edit();
        if (!mSettings.contains("os_version") || mSettings.getInt("os_version", -1) < 0) {
            //get and save in preferences ANDROID VERSION_CODE
            mEditor.putInt("os_version", Build.VERSION.SDK_INT);
            Log.d("DEVICE_INFO", "os_version = " + Build.VERSION.SDK_INT);
        }
        if (!mSettings.contains("device_name") || mSettings.getString("device_name", "").equals("")) {
            //get and save in preferences device name
            mEditor.putString("device_name", Build.BRAND + ", " + Build.MODEL);
            Log.d("DEVICE_INFO", "device_name = " + Build.BRAND + ", " + Build.MODEL);
        }
        if (!mSettings.contains("app_version") || mSettings.getInt("app_version", -1) < 0) {
            //get and save in preferences app version
            try {
                PackageInfo pack = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                mEditor.putInt("app_version", pack.versionCode);
                Log.d("DEVICE_INFO", "app_version = " + pack.versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                mEditor.putInt("app_version", -1);
                Log.d("DEVICE_INFO", "app_version error = " + e.getMessage());
            }
        }
        mEditor.commit();
    }

    public static Boolean saveCurrentOrder(Context mContext, String orderId) {
        if (orderId != null && !orderId.equals("")) {
            //save current order
            SharedPreferences mSettingsOrder = mContext.getSharedPreferences("order", Context.MODE_PRIVATE);
            try {
                JSONArray ordersJSONArray;
                if (mSettingsOrder.contains("currentOrders")
                        && !mSettingsOrder.getString("currentOrders", "").equals("")
                        && !mSettingsOrder.getString("currentOrders", "").equals("[{}]")) {
                    ordersJSONArray = new JSONArray(mSettingsOrder.getString("currentOrders", ""));
                    //check if this order is already stored
                    for (int i = 0; i < ordersJSONArray.length(); i++) {
                        JSONObject currentOrder = ordersJSONArray.getJSONObject(i);
                        if (orderId.equals(currentOrder.getString("orderID"))) {
                            mSettingsOrder.edit().putInt(Utilits.Constants.ORDER_TYPE, Constants.SIMPLE_CURRENT_ORDER).commit();
                            return true;
                        }
                    }
                } else {
                    ordersJSONArray = new JSONArray();
                }
                ordersJSONArray.put((new JSONObject()).put("orderID", orderId));
                SharedPreferences.Editor mEditor = mSettingsOrder.edit();
                mEditor.putString("currentOrders", ordersJSONArray.toString());
                mEditor.putInt(Utilits.Constants.ORDER_TYPE, Constants.SIMPLE_CURRENT_ORDER);
                mEditor.commit();
                return true;
            } catch (JSONException e) {
                Log.d(TAG, "saving orderJSON error = " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static Boolean deleteOrderID(Context mContext, String orderID) {
        SharedPreferences mSettingsOrder = mContext.getSharedPreferences("order", Context.MODE_PRIVATE);
        if (mSettingsOrder.contains("currentOrders")
                && !mSettingsOrder.getString("currentOrders", "").equals("")) {
            try {
                JSONArray ordersJSONArray = new JSONArray(mSettingsOrder.getString("currentOrders", ""));
                JSONArray tempArray = new JSONArray();
                for (int i = 0; i < ordersJSONArray.length(); i++) {
                    String currentID = ordersJSONArray.getJSONObject(i).getString("orderID");
                    if (!orderID.equals(currentID)) {
                        tempArray.put((new JSONObject()).put("orderID", currentID));
                    }
                }
                mSettingsOrder.edit().putString("currentOrders", tempArray.toString()).commit();
            } catch (JSONException e) {
                Log.d(TAG, "Delete orderID error = " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
        return false;
    }




}
