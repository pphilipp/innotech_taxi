package com.webcab.elit.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.webcab.elit.DBRoutes;
import com.webcab.elit.Log;
import com.webcab.elit.R;
import com.webcab.elit.data.templ;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 13.01.2016.
 */
public class TaxiWidgetListFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Intent mIntent;
    private List<templ> routes;

    public TaxiWidgetListFactory(Context mContext, Intent intent) {
        this.mContext = mContext;
        this.mIntent = intent;
        this.routes = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        this.routes = getTemples();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        mRemoteViews.setTextViewText(R.id.list_item_title, routes.get(position).getTitle());

        mRemoteViews.setTextViewText(R.id.route_tv, routes.get(position).getDesc());

        Intent clickIntent = new Intent();
        clickIntent.putExtra(WidgetProvider.ITEM_POSITION, position);
        clickIntent.putExtra("routeId", routes.get(position).getId());
        clickIntent.putExtra("Logged", true);
        mRemoteViews.setOnClickFillInIntent(R.id.item_layout, clickIntent);
        return mRemoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    private List<templ> getTemples() {

        List<templ> rt = new ArrayList<templ>();

        DBRoutes dbHelper2 = new DBRoutes(mContext);
        // подключаемся к БД

        SQLiteDatabase db1 = dbHelper2.getReadableDatabase();

        Cursor c2 = null;

        try {
            c2 = db1.query("route", null, null, null, null, null, null);

            // ставим позицию курсора на первую строку выборки
            // если в выборке нет строк, вернется false
            if (c2.moveToFirst()) {

                // определяем номера столбцов по имени в выборке
                int idcolIndex = c2.getColumnIndex("id");
                int titleColIndex = c2.getColumnIndex("title");
                int descColIndex = c2.getColumnIndex("desc");

                int descColIndex2 = c2.getColumnIndex("desc2");

                do {

                    templ currentTemple = new templ(c2.getInt(idcolIndex), c2.getString(titleColIndex),
                            c2.getString(descColIndex) + " -> " + c2.getString(descColIndex2), false);
                    currentTemple.setServerTemplID(c2.getString(c2.getColumnIndex(dbHelper2.SERVER_TEMPLATE_ID)));
                    rt.add(currentTemple);
                } while (c2.moveToNext());
            } else {
                Log.d("MA", "0 rows");
            }
        } catch (Exception e) {
            Log.d("error", e.getMessage());
        } finally {
            c2.close();
            db1.close();
        }



        return rt;
    }
}
