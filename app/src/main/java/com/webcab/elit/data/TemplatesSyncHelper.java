package com.webcab.elit.data;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.webcab.elit.DBPoints;
import com.webcab.elit.DBRoutes;
import com.webcab.elit.Templates;
import com.webcab.elit.adapters.TemplAdapter;
import com.webcab.elit.net.ServiceConnection;
import com.webcab.elit.widget.WidgetProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 05.10.2015.
 */
public class TemplatesSyncHelper extends AsyncTask<Void, Void, Void>{

    //possible actions for sync
    public static final int SYNC_FROM_SERVER = 1;
    public static final int ADD = 2;
    public static final int UPDATE = 3;
    public static final int DELETE = 4;

    //Address or Route
    public static final int ADDRESS = 0;
    public static final int ROUTE = 1;

    //Address template JSON fields
    private static final String ADDRESS_ID = "id";
    private static final String ADDRESS_NAME = "nameTemplate";
    private static final String ADDRESS_CITY = "cityTemplate";
    private static final String ADDRESS_STREET = "streetName";
    //this is not a mistake
    private static final String ADDRESS_HOUSE_NUMBER = "streetNumber";
    private static final String ADDRESS_PORCH = "streetHouse";
    private static final String ADDRESS_INFO = "templateInfo";
    private static final String ADDRESS_TYPE = "typeTemplate";

    //Route template JSON field
    private static final String ROUTE_ID = "id";
    private static final String ROUTE_NAME = "nameTemplate";
    private static final String ROUTE_CITY = "cityTemplate";
    private static final String ROUTE_STREET_NAME = "streetName";
    //this is not a mistake
    private static final String ROUTE_HOUSE_NUMBER = "streetNumber";
    //entrance
    private static final String ROUTE_PORCH = "streetHouse";
    private static final String ROUTE_INFO = "templateInfo";
    //"1" for route
    private static final String ROUTE_TYPE = "typeTemplate";
    private static final String ROUTE_AUTO_CLASS = "classAvto";
    //addresses where (JSONArray)
    private static final String ROUTE_SUBORDER = "add";
    private static final String ROUTE_STREET_2_NAME = "streetName2";
    private static final String ROUTE_HOUSE_2_NUMBER = "streetNumber2";
    private static final String ROUTE_HOUSE_2_PORCH = "streetHouse2";

    private static final String LOG_TAG = "TemplatesSYNC";

    //server functions
    private static final String GET_ADDRESSES = "loadTemplateOnlyAddress";
    private static final String GET_ROUTES = "loadTemplateOnlyRoute";
    private static final String DELETE_TEMPLATE = "deleteTemplate";
    private static final String SAVE_ADDRESS_TEMPLATE = "saveTemplateAddress";
    private static final String EDIT_ADDRESS_TEMPLATE = "editSaveTemplateAddress";
    private static final String SAVE_ROUTE_TEMPLATE = "saveTemplateRoutes";
    private static final String EDIT_ROUTE_TEMPLATE = "editSaveTemplateRoutes";

    private static final String DEFAULT_PORCH = "1";

    //server responses
    private static final String SUCCESS = "success";

    private Context mContext;
    //what to do with template (sync, update, delete, add)
    private int action;

    private ServiceConnection mConnection;
    private String clientPhone;
    private List<addr> addressesFromServer;
    private List<rt> routesFromServer;
    private TemplAdapter mAdapter;
    private int templateID;
    private ContentValues addressContentValues;
    private ContentValues routeContentValues;

    /**
     * Overrides local template DB with templates from server
     * @param mContext - app context
     * @param action - action to be performed (SYNC_FROM_SERVER is used for this constructor)
     * @param mAdapter - listView adapter
     */
    public TemplatesSyncHelper(Context mContext, int action, TemplAdapter mAdapter) {
        this.mContext = mContext;
        this.action = action;
        //get phone number from prefs
        SharedPreferences mSettings = this.mContext.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        this.clientPhone = mSettings.getString("Phone", "");
        this.mAdapter = mAdapter;
        this.mConnection = new ServiceConnection(this.mContext);
    }

    /**
     * Adds template to server
     * @param mContext - app context
     * @param action - action to be performed (ADD/UPDATE is used for this constructor)
     * @param templateContentValues - template data
     * @param kindOfTemplate - address or route
     */
    public TemplatesSyncHelper(Context mContext, int action, ContentValues templateContentValues, int kindOfTemplate) {
        this.mContext = mContext;
        this.action = action;
        //get phone number from prefs
        SharedPreferences mSettings = this.mContext.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        this.clientPhone = mSettings.getString("Phone", "");
        switch (kindOfTemplate) {
            case ADDRESS:
                this.addressContentValues = templateContentValues;
                break;
            case ROUTE:
                this.routeContentValues = templateContentValues;
                break;
        }
        this.mConnection = new ServiceConnection(this.mContext);
    }

    /**
     * Deletes template from server
     * @param mContext - app context
     * @param action - action to be performed (DELETE is used for this constructor)
     * @param id - server_id of template to be deleted
     * @param mAdapter - listView adapter
     */
    public TemplatesSyncHelper(Context mContext, int action, int id, TemplAdapter mAdapter) {
        this.mContext = mContext;
        this.action = action;
        //get phone number from prefs
        SharedPreferences mSettings = this.mContext.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        this.clientPhone = mSettings.getString("Phone", "");
        this.mAdapter = mAdapter;
        this.templateID = id;
        this.mConnection = new ServiceConnection(this.mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(LOG_TAG, "Starting sync");
    }

    @Override
    protected Void doInBackground(Void... params) {
        switch (action) {
            case SYNC_FROM_SERVER:
                //get templates from server and override local ones
                addressesFromServer = getAddressesTemplatesFromServer();
                routesFromServer = getRouteTemplatesFromServer();
                break;
            case UPDATE:
                if (addressContentValues != null) {
                    editAddressTemplateToServer(addressContentValues);
                }
                if (routeContentValues != null) {
                    addOrEditRouteToServer(routeContentValues);
                }
                break;
            case DELETE:
                if (templateID != -1 && deleteTemplateFromServer(templateID)) {
                    Log.d(LOG_TAG, "template with ID = " + templateID + " was deleted");
                }
                break;
            case ADD:
                if (addressContentValues != null) {
                    addAddressTemplateToServer(addressContentValues);
                }
                if (routeContentValues != null) {
                    addOrEditRouteToServer(routeContentValues);
                }
                break;
            default:
                Log.d(LOG_TAG, "not supported action");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (action == SYNC_FROM_SERVER) {
            if (addressesFromServer != null && clearAddressesTable()) {
                saveAddressTemplatesFromServerToDB(addressesFromServer);
            }
            if (routesFromServer != null && clearRoutesTable()) {
                saveRoutesTemplatesFromServerToDB(routesFromServer);
            }
        }
        if (mAdapter != null) {
            Intent intent = new Intent(mContext, WidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            int[] ids = AppWidgetManager.getInstance(mContext).getAppWidgetIds(new ComponentName(mContext, WidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            mContext.sendBroadcast(intent);
            synchronized (mAdapter) {
                mAdapter.upDateList(Templates.getTempl(false));
            }
        }
        Log.d(LOG_TAG, "Ended sync");
    }

    private Boolean addOrEditRouteToServer(ContentValues routeCV) {
        String url = "templateAll.php";
        String serverID = "";
        String nameTemplate = "";
        String cityTemplateAddress = "";
        String streetTemplate = "";
        String buildingTemplate = "";
        String homeTemplate = "";
        String infoTemplate = "";
        String classAvto = "";
        String to = "";
        String phone = "";
        try {
            phone = URLEncoder.encode(clientPhone, "UTF-8");
            serverID = routeCV.containsKey("serverID") ? URLEncoder.encode(routeCV.getAsString("serverID"), "UTF-8") : "";
            nameTemplate = URLEncoder.encode(routeCV.getAsString("title"), "UTF-8");
            cityTemplateAddress = URLEncoder.encode(routeCV.getAsString("city"), "UTF-8");
            streetTemplate = URLEncoder.encode(routeCV.getAsString("str"), "UTF-8");
            buildingTemplate = URLEncoder.encode(routeCV.getAsString("dom"), "UTF-8");
            infoTemplate = URLEncoder.encode(routeCV.getAsString("prim"), "UTF-8");
            homeTemplate = URLEncoder.encode(routeCV.getAsString("parad"), "UTF-8");
            classAvto = URLEncoder.encode(routeCV.getAsString("auto"), "UTF-8");
            to = URLEncoder.encode(routeCV.getAsString("str2") + ", " + routeCV.getAsString("dom2"), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "URLEncode error = " + e.getMessage());
        }
        String urlParameters = "";
        if (serverID.equals("")) {
            urlParameters =
                    "func=" + SAVE_ROUTE_TEMPLATE
                            + "&phone=" + phone
                            + "&nameTemplate=" + nameTemplate
                            + "&cityTemplateAddress=" + cityTemplateAddress
                            + "&streetTemplate=" + streetTemplate
                            + "&buildingTemplate=" + buildingTemplate
                            + "&infoTemplate=" + infoTemplate
                            + "&homeTemplate=" + homeTemplate
                            + "&classAvto=" + classAvto
                            + "&to=" + to;
        } else {
            urlParameters =
                    "func=" + EDIT_ROUTE_TEMPLATE
                            + "&phone=" + phone
                            + "&id=" + serverID
                            + "&nameTemplate=" + nameTemplate
                            + "&cityTemplateAddress=" + cityTemplateAddress
                            + "&streetTemplate=" + streetTemplate
                            + "&buildingTemplate=" + buildingTemplate
                            + "&infoTemplate=" + infoTemplate
                            + "&homeTemplate=" + homeTemplate
                            + "&classAvto=" + classAvto
                            + "&to=" + to;
        }
        String response = mConnection.postRequest(url, urlParameters);
        Log.d(LOG_TAG, "add route response = " + response);
        return checkResult(response);
    }

    /**
     * Edit address template on server
     * @param addressCV - address data
     * @return - true if success
     */
    private Boolean editAddressTemplateToServer(ContentValues addressCV){
        String url = "templateAll.php";
        String phoneNumber = "";
        String templateID = "";
        String templateName = "";
        String templateCity = "";
        String templateStreet = "";
        String templateHouseNumber = "";
        String templateInfo = "";
        //парадное
        String templatePorch = "";

        try {
            templateID = URLEncoder.encode(addressCV.getAsString("serverID"), "UTF-8");
            phoneNumber = URLEncoder.encode(clientPhone, "UTF-8");
            templateName = URLEncoder.encode(addressCV.getAsString("title"), "UTF-8");
            templateCity = URLEncoder.encode(addressCV.getAsString("city"), "UTF-8");
            templateStreet = URLEncoder.encode(addressCV.getAsString("str"), "UTF-8");
            templateHouseNumber = URLEncoder.encode(addressCV.getAsString("dom"), "UTF-8");
            templateInfo = URLEncoder.encode(addressCV.getAsString("prim"), "UTF-8");
            templatePorch = URLEncoder.encode(addressCV.getAsString("parad"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "URLEncode error = " + e.getMessage());
        }
        String urlParameters = "func=" + EDIT_ADDRESS_TEMPLATE
                + "&id=" + templateID
                + "&phone=" + phoneNumber
                + "&nameTemplate=" + templateName
                + "&cityTemplateAddress=" + templateCity
                + "&streetTemplate=" + templateStreet
                + "&buildingTemplate=" + templateHouseNumber
                + "&homeTemplate=" + templatePorch
                + "&infoTemplate=" + templateInfo;

        Log.d(LOG_TAG, "edit response = " + urlParameters);
        String response = mConnection.postRequest(url, urlParameters);
        Log.d(LOG_TAG, "edit response = " + response);

        return checkResult(response);
    }

    /**
     * Adds address template to server
     * @param addressCV - address info
     * @return - true if success
     */
    private Boolean addAddressTemplateToServer(ContentValues addressCV){
        String url = "templateAll.php";
        String phoneNumber = "";
        String templateName = "";
        String templateCity = "";
        String templateStreet = "";
        String templateHouseNumber = "";
        String templateInfo = "";
        //парадное
        String templatePorch = "";

        try {
            phoneNumber = URLEncoder.encode(clientPhone, "UTF-8");
            templateName = URLEncoder.encode(addressCV.getAsString("title"), "UTF-8");
            templateCity = URLEncoder.encode(addressCV.getAsString("city"), "UTF-8");
            templateStreet = URLEncoder.encode(addressCV.getAsString("str"), "UTF-8");
            templateHouseNumber = URLEncoder.encode(addressCV.getAsString("dom"), "UTF-8");
            templateInfo = URLEncoder.encode(addressCV.getAsString("prim"), "UTF-8");
            templatePorch = URLEncoder.encode(addressCV.getAsString("parad"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "URLEncode error = " + e.getMessage());
        }
        String urlParameters = "func=" + SAVE_ADDRESS_TEMPLATE
                + "&phone=" + phoneNumber
                + "&nameTemplate=" + templateName
                + "&cityTemplateAddress=" + templateCity
                + "&streetTemplate=" + templateStreet
                + "&buildingTemplate=" + templateHouseNumber
                + "&homeTemplate=" + templatePorch
                + "&infoTemplate=" + templateInfo;

        String response = mConnection.postRequest(url, urlParameters);

        return checkResult(response);
    }

    /**
     * Deletes template from server
     * @param id - serverID of template (can be recieved in getAddressTemplatesFromServer() or getRouteTemplatesFromServer())
     * @return - true if success
     */
    private Boolean deleteTemplateFromServer(int id) {
        String url = "templateAll.php";
        String phoneNumber = "";
        String templateID = "";
        try {
            phoneNumber = URLEncoder.encode(clientPhone, "UTF-8");
            templateID = URLEncoder.encode(String.valueOf(id), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "URLEncode error = " + e.getMessage());
        }
        String urlParameters = "func=" + DELETE_TEMPLATE + "&id=" + templateID + "&phone=" + phoneNumber;
        String result = mConnection.postRequest(url, urlParameters);

        return checkResult(result);
    }

    /**
     * Check server response
     * @param result - server response String
     * @return - true if success
     */
    private Boolean checkResult(String result) {
        if (result == null || result.equals("")) {
            return false;
        } else {
            try {
                JSONObject resJSON = new JSONObject(result);
                if (resJSON != null && resJSON.optString("message").equals(SUCCESS)) {
                    return true;
                } else {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "DELETE JSON error = " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Gets all address templates from server
     * @return - List<addr>
     */
    private List<addr> getAddressesTemplatesFromServer(){
        //prepare parameteres for post request
        String url = "templateAll.php";
        String phoneNumber = "";
        try {
            phoneNumber = URLEncoder.encode(clientPhone, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("LOG_TAG", "URLEncode error = " + e.getMessage());
        }
        String urlParameters = "func=" + GET_ADDRESSES + "&phone=" + phoneNumber;
        //send post request and get JSON as the result
        String result = mConnection.postRequest(url, urlParameters);
        List<addr> addressesFromServer = new ArrayList<>();
        try {
            JSONArray addressesJSON = new JSONArray(result);
            //go through all templates and store them in List
            for (int i = 0; i < addressesJSON.length(); i++) {
                JSONObject currentAddressJSON = addressesJSON.getJSONObject(i);
                String desc = currentAddressJSON.getString(ADDRESS_CITY) + ", " + currentAddressJSON.getString(ADDRESS_STREET)
                        + ", " + currentAddressJSON.getString(ADDRESS_HOUSE_NUMBER);
                //get street and house ID from server
                JSONObject street =
                        (JSONObject) (new JSONArray(mConnection.getStreetCustom(currentAddressJSON.getString(ADDRESS_STREET)))).get(0);
                int streetID = street.optInt("id");
                JSONObject house = new JSONObject(mConnection.getHouseCustom(streetID, currentAddressJSON.getString(ADDRESS_HOUSE_NUMBER)));
                int houseID = house.optInt("id");
                //add point to List
                addressesFromServer.add(new addr(Integer.parseInt(currentAddressJSON.getString(ADDRESS_ID)),
                        currentAddressJSON.getString(ADDRESS_NAME),
                        desc, currentAddressJSON.getString(ADDRESS_STREET),
                        String.valueOf(streetID),
                        currentAddressJSON.getString(ADDRESS_HOUSE_NUMBER),
                        String.valueOf(houseID),
                        currentAddressJSON.getString(ADDRESS_PORCH),
                        currentAddressJSON.getString(ADDRESS_INFO)));
                addressesFromServer.get(i).setServerTemplateID(currentAddressJSON.getString(ADDRESS_ID));

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TemplateHelper", "JSON error = " + e.getMessage());
        }
        return addressesFromServer;
    }

    /**
     * Gets all route templates from server
     * @return - List<rt>
     */
    private List<rt> getRouteTemplatesFromServer(){
        //prepare parameters for post request
        String url = "templateAll.php";
        String phoneNumber = "";
        try {
            phoneNumber = URLEncoder.encode(clientPhone, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "URLEncode error = " + e.getMessage());
        }
        String urlParameters = "func=" + GET_ROUTES + "&phone=" + phoneNumber;
        //send post request
        String result = mConnection.postRequest(url, urlParameters);
        Log.d(LOG_TAG, "routes result = " + result.toString());
        List<rt> routesFromServer = new ArrayList<>();
        try {
            JSONArray routesJSON = new JSONArray(result);
            for (int i = 0; i < routesJSON.length(); i++) {
                //get current route as JSON
                JSONObject currentRouteJSON = routesJSON.getJSONObject(i);
                //get City
                String city = currentRouteJSON.optString("cityTemplate");
                //get street info as JSON
                JSONObject street =
                        (JSONObject) (new JSONArray(mConnection.getStreetCustom(currentRouteJSON.getString(ROUTE_STREET_NAME)))).get(0);
                //get corresponding streetID
                int streetID = street.optInt("id");
                //get house JSON
                JSONObject house = new JSONObject(mConnection.getHouseCustom(streetID, currentRouteJSON.getString(ROUTE_HOUSE_NUMBER)));
                //get houseID
                int houseID = house.optInt("id");
                //get suborders (address where, for sure will be only one)
                JSONObject addressWhereJSON = (JSONObject) currentRouteJSON.getJSONArray(ROUTE_SUBORDER).get(0);
                //same operations with streetID and houseID as for the first address
                JSONObject street2 =
                        (JSONObject) (new JSONArray(mConnection.getStreetCustom(addressWhereJSON.getString(ROUTE_STREET_2_NAME)))).get(0);
                int streetID2 = street2.optInt("id");
                JSONObject house2 = new JSONObject(mConnection.getHouseCustom(streetID2, addressWhereJSON.getString(ROUTE_HOUSE_2_NUMBER)));
                int houseID2 = house2.optInt("id");
                //add route to routeList
                rt currentRoute = new rt(Integer.parseInt(currentRouteJSON.getString(ROUTE_ID)),
                        currentRouteJSON.getString(ROUTE_NAME),
                        "", currentRouteJSON.getString(ROUTE_STREET_NAME),
                        String.valueOf(streetID), currentRouteJSON.getString(ROUTE_HOUSE_NUMBER),
                        String.valueOf(houseID),
                        currentRouteJSON.getString(ROUTE_PORCH),
                        currentRouteJSON.getString(ROUTE_INFO),
                        "", addressWhereJSON.getString(ROUTE_STREET_2_NAME),
                        String.valueOf(streetID2),
                        addressWhereJSON.getString(ROUTE_HOUSE_2_NUMBER),
                        String.valueOf(houseID2),
                        currentRouteJSON.getString(ROUTE_AUTO_CLASS));
                currentRoute.setCity(city);
                currentRoute.setServerTemplateID(currentRouteJSON.getString(ROUTE_ID));
                routesFromServer.add(currentRoute);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TemplateHelper", "JSON error = " + e.getMessage());
        }
        return routesFromServer;
    }

    /**
     * Stores route templates from server to local DB 
     * @param routeTemplates - list of routes to be stored
     * @return - true if success
     */
    private Boolean saveRoutesTemplatesFromServerToDB(List<rt> routeTemplates) {
        Log.d(LOG_TAG, "Start saving routes into DB");
        DBRoutes routesHelper = new DBRoutes(mContext);
        SQLiteDatabase routesDB = routesHelper.getWritableDatabase();
        //go through all templates from server and save them in DB
        int count = 0;
        for (rt currentTemplate : routeTemplates) {
            ContentValues cv = new ContentValues();
            cv.put(routesHelper.SERVER_TEMPLATE_ID, currentTemplate.getServerTemplateID());
            Log.d(LOG_TAG, "serverID onSave = " + currentTemplate.getServerTemplateID());
            cv.put(routesHelper.ROUTES_TITLE, currentTemplate.getTitle());
            String desc = (currentTemplate.getDesc().equals("")
                    ? currentTemplate.getCity() + ", " + currentTemplate.getStr() + ", " + currentTemplate.getDom()
                    : currentTemplate.getDesc());
            cv.put(routesHelper.ROUTES_DESC, desc);
            cv.put(routesHelper.ROUTES_STREET_NAME, currentTemplate.getStr());
            cv.put(routesHelper.ROUTES_STREET_ID, currentTemplate.getStrid());
            cv.put(routesHelper.ROUTES_HOUSE_NUMBER, currentTemplate.getDom());
            cv.put(routesHelper.ROUTES_HOUSE_ID, currentTemplate.getDomid());
            //TODO Ihor must add this field on his side. Now it will be default value = 1
            String porch = (currentTemplate.getParad().equals("") ? DEFAULT_PORCH : currentTemplate.getParad());
            cv.put(routesHelper.ROUTES_PORCH, porch);
            cv.put(routesHelper.ROUTES_INFO, currentTemplate.getPrim());
            cv.put(routesHelper.ROUTES_AUTO, currentTemplate.getAutoClass());
            String desc2 = (currentTemplate.getDesc2().equals("")
                    ? currentTemplate.getCity() + ", " + currentTemplate.getStr2() + ", " + currentTemplate.getDom2()
                    : currentTemplate.getDesc2());
            cv.put(routesHelper.ROUTES_DESC2, desc2);
            cv.put(routesHelper.ROUTES_STREET_2_NAME, currentTemplate.getStr2());
            cv.put(routesHelper.ROUTES_STREET_2_ID, currentTemplate.getStrid2());
            cv.put(routesHelper.ROUTES_HOUSE_2_NUMBER, currentTemplate.getDom2());
            cv.put(routesHelper.ROUTES_HOUSE_2_ID, currentTemplate.getDomid2());
            long rowID = routesDB.insert(routesHelper.TABLE_NAME, null, cv);
            if (rowID != -1) {
                count++;
            }
            if (count == routeTemplates.size()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Stores address templates from server to local DB
     * @param addressTemplates - list of addresses to be saved in DB
     * @return - true if success
     */
    private Boolean saveAddressTemplatesFromServerToDB(List<addr> addressTemplates){
        Log.d(LOG_TAG, "Start saving addresses");
        DBPoints pointsHelper = new DBPoints(mContext);
        SQLiteDatabase pointsDB = pointsHelper.getWritableDatabase();
        //go through all templates from server and save them in DB
        int count = 0;
        for (addr currentTemplate : addressTemplates) {
            ContentValues cv = new ContentValues();
            cv.put(pointsHelper.SERVER_TEMPLATE_ID, currentTemplate.getServerTemplateID());
            cv.put(pointsHelper.POINTS_TITLE, currentTemplate.getTitle());
            cv.put(pointsHelper.POINTS_DESC, currentTemplate.getDesc());
            cv.put(pointsHelper.POINTS_STREET_NAME, currentTemplate.getStr());
            cv.put(pointsHelper.POINTS_STREET_ID, currentTemplate.getStrid());
            cv.put(pointsHelper.POINTS_HOUSE_NUMBER, currentTemplate.getDom());
            cv.put(pointsHelper.POINTS_HOUSE_ID, currentTemplate.getDomid());
            cv.put(pointsHelper.POINTS_INFO, currentTemplate.getPrim());
            cv.put(pointsHelper.POINTS_HOUSE_PORCH, currentTemplate.getParad());
            long rowID = pointsDB.insert(pointsHelper.TABLE_NAME, null, cv);
            if (rowID != -1) {
                count++;
            }
        }
        pointsDB.close();
        pointsHelper.close();
        if (count == addressTemplates.size()) {
            return true;
        }
        return false;
    }

    

    /**
     * Deletes all rows in DBpoints
     * @return
     */
    private Boolean clearAddressesTable(){
        Log.d(LOG_TAG, "Start clearing address DB");
        DBPoints pointsHelper = new DBPoints(mContext);
        SQLiteDatabase pointsDB = pointsHelper.getWritableDatabase();
        Cursor pointsCursor = pointsDB.rawQuery("SELECT id FROM " + pointsHelper.TABLE_NAME, null);
        if (pointsCursor.getCount() > 0) {
            //clear table
            Log.d(LOG_TAG, "Base check, points count = " + pointsCursor.getCount());
            pointsDB.execSQL("DELETE FROM " + pointsHelper.TABLE_NAME);
            return true;
        }
        pointsCursor.close();
        pointsDB.close();
        pointsHelper.close();
        return true;
    }
    
    

    /**
     * Deletes all rows in DBRoutes
     * @return
     */
    private Boolean clearRoutesTable(){
        Log.d(LOG_TAG, "Start clearing routes DB");
        DBRoutes routesHelper = new DBRoutes(mContext);
        SQLiteDatabase routesDB = routesHelper.getWritableDatabase();
        Cursor routesCursor = routesDB.rawQuery("SELECT id FROM " + routesHelper.TABLE_NAME, null);
        if (routesCursor.getCount() > 0) {
            //clear table
            Log.d(LOG_TAG, "Base check, routes count = " + routesCursor.getCount());
            routesDB.execSQL("DELETE FROM " + routesHelper.TABLE_NAME);
            return true;
        }
        routesCursor.close();
        routesDB.close();
        routesHelper.close();
        return true;
    }

    /**
     * 1. У Игоря из шаблонов не загружается в форму заказа парадная
     * 2. У Игоря в шаблонах маршрута НЕТ парадной
     */

}
