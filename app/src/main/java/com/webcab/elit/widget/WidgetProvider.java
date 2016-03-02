package com.webcab.elit.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.webcab.elit.Progress;
import com.webcab.elit.R;
import com.webcab.elit.Utils.Constants;

/**
 * Created by Sergey on 13.01.2016.
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        /*if (intent.getAction().equalsIgnoreCase(ACTION_ON_CLICK)) {
            int itemPos = intent.getIntExtra(ITEM_POSITION, -1);
            if (itemPos != -1) {
                Toast.makeText(context, "Clicked on item " + itemPos,
                        Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    /**
     * Updates widget UI and clickListeners
     * @param context
     * @param appWidgetManager
     * @param i - appWidgetID
     */
    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int i) {

        upDateHeader(context, appWidgetManager, i);
        upDateList(context, appWidgetManager, i);
        setListClick(context, appWidgetManager, i);

    }


    /**
     * Sets widget title and assigns click listeners to header elements. App icon and widget title click
     * leads to empty Homescreen, folder click leads to new route template screen
     * @param context
     * @param appWidgetManager
     * @param i - appWidgetID
     */
    private void upDateHeader(Context context, AppWidgetManager appWidgetManager, int i) {

        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(),
                R.layout.taxi_widget);

        mRemoteViews.setTextViewText(R.id.header_title, context.getResources().getString(R.string.app_name));

        //Empty HomeScreen intent and PendingIntent
        Intent homeScreenIntent = new Intent(context, Progress.class);
        homeScreenIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[] { i });
        homeScreenIntent.putExtra("Logged", true);
        PendingIntent homeScreenPIntent = PendingIntent.getActivity(context,
                0, homeScreenIntent, 0);

        //New route template intent and PendingIntent
        Intent templateIntent = new Intent(context, Progress.class);
        templateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[] { i });
        templateIntent.putExtra("Logged", true);
        templateIntent.putExtra(Constants.FORWARD, Constants.NEW_ROUTE_TEMPLATE);
        PendingIntent templatePIntent = PendingIntent.getActivity(context,
                1, templateIntent, 0);
        //setUp pIntents for views
        mRemoteViews.setOnClickPendingIntent(R.id.header_title, homeScreenPIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.widget_icon, homeScreenPIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.current_order, homeScreenPIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.new_template_img, templatePIntent);

        appWidgetManager.updateAppWidget(i, mRemoteViews);
    }

    /**
     * Updates widget`s templates list with stored in BD routes
     * @param context
     * @param appWidgetManager
     * @param i - appWidgetID
     */
    private void upDateList(Context context, AppWidgetManager appWidgetManager, int i) {

        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(),
                R.layout.taxi_widget);

        Intent adapter = new Intent(context, WidgetFactoryService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i);
        Uri data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME));
        adapter.setData(data);
        mRemoteViews.setRemoteAdapter(i, R.id.routes_list, adapter);
        appWidgetManager.updateAppWidget(i, mRemoteViews);
        appWidgetManager.notifyAppWidgetViewDataChanged(i, R.id.routes_list);
    }

    final String ACTION_ON_CLICK = "click_action";
    final static String ITEM_POSITION = "item_position";

    /**
     * Sends user to app Homescreen with preloaded data in case of route templates
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    private void setListClick(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(),
                R.layout.taxi_widget);
        Intent listClickIntent = new Intent(context, Progress.class);
        listClickIntent.setAction(ACTION_ON_CLICK);
        PendingIntent listClickPIntent = PendingIntent.getActivity(context, 0,
                listClickIntent, 0);
        mRemoteViews.setPendingIntentTemplate(R.id.routes_list, listClickPIntent);
        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
    }
}
