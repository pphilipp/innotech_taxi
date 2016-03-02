package com.webcab.elit.data;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.webcab.elit.HomeScreen;
import com.webcab.elit.R;
import com.webcab.elit.net.ServiceConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 02.08.2015.
 */
public class GetFullOrderInfo extends AsyncTask {

    Context mContext;
    order_s ord_s;
    Intent mIntent;
    Dialog  waitDialog;
    ContextThemeWrapper themedContext;

    public GetFullOrderInfo(Context mContext, order_s ord_s) {
        this.mContext = mContext;
        this.ord_s = ord_s;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        mIntent = getPreorderIntent(ord_s);
        return null;
    }

    @Override
    protected void onPreExecute() {
        Log.d("PRELIM_CHECK1", "starting");
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            themedContext = new ContextThemeWrapper( mContext, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
        }
        else {
            themedContext = new ContextThemeWrapper( mContext, android.R.style.Theme_Light_NoTitleBar );
        }
        waitDialog = new Dialog(themedContext);
        waitDialog.setContentView(R.layout.wait_dialog);
        waitDialog.setTitle(R.string.wait);
        waitDialog.show();


        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        waitDialog.dismiss();
        mContext.startActivity(mIntent);
        ((Activity)mContext).finish();
        super.onPostExecute(o);
    }


    /**
     * Method gets all data for preliminary order and stores it in
     * intent extras
     * @param ord_s - current preliminary order
     * @return intent with extras
     */
    private Intent getPreorderIntent(final order_s ord_s) {

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

        final Intent intent;
        intent = new Intent(mContext, HomeScreen.class);

        intent.putExtra("isPreorder", true);
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
                            Log.d("CHECK_ADDR", "response house from web error = " + e.getMessage());
                        }
                    }
                    try {
                        orderJSON.put("addresses", address);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("orderJSON", orderJSON.toString());
            Log.d("ARCHIV", "orderJSON = " + orderJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //get here all order details and send as String JSON to HomeScreen activity
        return intent;
    }
}
