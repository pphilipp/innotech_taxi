package com.webcab.elit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBRoutes extends SQLiteOpenHelper {

    String LOG_TAG = "SQLite";
    public static final int DB_VERSION = 2;
    public static final String TABLE_NAME = "route";

    //DB Routes fields
    public static final String ROUTES_TITLE = "title";
    public static final String ROUTES_DESC = "desc";
    public static final String ROUTES_STREET_NAME = "str";
    public static final String ROUTES_STREET_ID = "strid";
    public static final String ROUTES_HOUSE_NUMBER = "dom";
    public static final String ROUTES_HOUSE_ID = "domid";
    public static final String ROUTES_PORCH = "parad";
    public static final String ROUTES_FLAT = "flat";
    public static final String ROUTES_INFO = "prim";
    public static final String ROUTES_AUTO = "auto";
    public static final String ROUTES_DESC2 = "desc2";
    public static final String ROUTES_STREET_2_NAME = "str2";
    public static final String ROUTES_STREET_2_ID = "strid2";
    public static final String ROUTES_HOUSE_2_NUMBER = "dom2";
    public static final String ROUTES_HOUSE_2_ID = "domid2";
    public static final String SERVER_TEMPLATE_ID = "server_template_id";

    public DBRoutes(Context context) {
        // конструктор суперкласса
        super(context, "myDBRoutes", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "-- onCreate database --");
        // создаем таблицу с полями
        db.execSQL("create table route ("
                + "id integer primary key autoincrement,"
                + SERVER_TEMPLATE_ID + " text, "
                + "title text,"
                + "desc text,"
                + "str text,"
                + "strid text,"
                + "dom text,"
                + "domid text,"
                + "parad text,"
                + ROUTES_FLAT + " text,"
                + "prim text,"
                + "auto text,"
                + "desc2 text,"
                + "str2 text,"
                + "strid2 text,"
                + "dom2 text,"
                + "domid2 text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2 && oldVersion == 1) {
            String upgradeQuery = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + SERVER_TEMPLATE_ID + " TEXT";
            db.execSQL(upgradeQuery);
        }
        if (newVersion == 3 && oldVersion < 3) {
            String upgradeQuery = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + SERVER_TEMPLATE_ID + " TEXT"
                    + ", ADD COLUMN " + ROUTES_FLAT + " TEXT";
            db.execSQL(upgradeQuery);
        }
    }
}
