package com.webcab.elit.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.webcab.elit.Log;
import com.webcab.elit.R;
import com.webcab.elit.data.route;
import com.webcab.elit.net.ServiceConnection;

import java.text.DecimalFormat;

/**
 * Created by Sergey on 30.09.2015.
 */
public class GetPriceAsync extends AsyncTask<Void, Void, route> {

    Context mContext;
    //start state TextView, text always is "Стоимость" must be GONE when price is called
    TextView price;
    //Price titles (cost and distance) must be visible once getPrice is called
    TextView titleTxt;
    //Price values must be visible once price and route are gotten from serve
    TextView valuesTxt;
    route r;
    SharedPreferences mSettings;
    ProgressBar mProgressBar;
    LinearLayout parentView;
    Button order;

    /**
     * AsyncTask which gets route from server, including price, distance route points. Method updates buttons state
     * before and after request to server. OnPreRequest it makes visible progressBar in price button and disables
     * order button. OnPostRequest it updates values of price and distance in price button, makes invisibe progressBar
     * and makes button order clickable
     * @param mContext - current ap context
     * @param parentView - parent view for bottom buttons (price and order)
     * @param mSettings - SharedPref of this app
     */
    public GetPriceAsync(Context mContext, LinearLayout parentView, SharedPreferences mSettings) {
        this.mContext = mContext;
        this.mSettings = mSettings;
        this.parentView = parentView;
        this.price = (TextView) parentView.findViewById(R.id.start_state_price_txt);
        this.titleTxt = (TextView) parentView.findViewById(R.id.price_titles_txt);
        this.valuesTxt = (TextView) parentView.findViewById(R.id.price_values_txt);
        this.mProgressBar = (ProgressBar) parentView.findViewById(R.id.progressBar);
        Typeface font1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");
        this.titleTxt.setTypeface(font1);
        this.valuesTxt.setTypeface(font1);
        this.order = (Button) parentView.findViewById(R.id.button_call);
    }

    public interface OnRouteGot{
        void saveRoute(route mRoute);
    }

    OnRouteGot thereIsRoute;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        price.setVisibility(View.GONE);
        titleTxt.setVisibility(View.VISIBLE);
        titleTxt.setTextSize(14);
        valuesTxt.setVisibility(View.GONE);
        /*price.setText(Html.fromHtml("Стоимость: " + "__" + " грн." + "<br>"
                + "Расстояние: " + "__" + " км"));*/
        mProgressBar.setVisibility(View.VISIBLE);
        titleTxt.setText(Html.fromHtml("Стоимость: " + "<br>"
                + "Расстояние: "));
        order.setEnabled(false);
    }

    @Override
    protected route doInBackground(Void... params) {

        ServiceConnection sc2 = new ServiceConnection(mContext);
        r = sc2.getRoute(mContext);

        return r;
    }

    @Override
    protected void onPostExecute(route route) {
        super.onPostExecute(route);

        if (r != null) {
            thereIsRoute = (OnRouteGot) mContext;
            thereIsRoute.saveRoute(r);
            Log.d("GET_PRICE", "r != null");
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("Price", r.getPrice() + "");
            editor.putString("Distance", r.getDistance() + "");
            editor.commit();


            if (r != null && ((r.getPrice() != null) && (r.getDistance()) != null) &&
                    (!r.getPrice().equals("")) && (!r.getDistance().equals(""))) {
                titleTxt.setTextSize(14);
                mProgressBar.setVisibility(View.GONE);
                valuesTxt.setVisibility(View.VISIBLE);
                //update text for price button
                DecimalFormat df = new DecimalFormat("###.#");
                float distance = Float.parseFloat(r.getDistance());
                titleTxt.setText((Html.fromHtml("Стоимость: " + "<br>"
                        + "Расстояние: ")));
                valuesTxt.setText(Html.fromHtml(r.getPrice() + " грн." + "<br>"
                        + df.format(distance) + " км"));
                //enable order button
                order.setEnabled(true);
            } else {
                valuesTxt.setVisibility(View.VISIBLE);
                valuesTxt.setTextSize(14);
                titleTxt.setText((Html.fromHtml("Стоимость: " + "<br>"
                        + "Расстояние: ")));
                valuesTxt.setText(Html.fromHtml("не определено" + "<br>"
                        + "не определено"));
            }
        }
    }
}
