package com.webcab.elit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBPoints extends SQLiteOpenHelper {

    String LOG_TAG = "SQLite";
    public static final int DB_VERSION = 2;
    public static final String TABLE_NAME = "addr";

    //DB Points fields
    public static final String POINTS_TITLE = "title";
    public static final String POINTS_DESC = "desc";
    public static final String POINTS_STREET_NAME = "str";
    public static final String POINTS_STREET_ID = "strid";
    public static final String POINTS_HOUSE_NUMBER = "dom";
    public static final String POINTS_HOUSE_ID = "domid";
    public static final String POINTS_HOUSE_PORCH = "parad";
    public static final String POINTS_INFO = "prim";
    public static final String SERVER_TEMPLATE_ID = "server_template_id";

    public DBPoints(Context context) {
        // конструктор суперкласса
        super(context, "myDB", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "-- onCreate database --");
        // создаем таблицу с полями
        db.execSQL("create table " + TABLE_NAME + " ("
                + "id integer primary key autoincrement,"
                + SERVER_TEMPLATE_ID + " text, "
                + "title text,"
                + "desc text,"
                + "str text,"
                + "strid text,"
                + "dom text,"
                + "domid text,"
                + "parad text,"
                + "prim text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2 && oldVersion == 1) {
            String upgradeQuery = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + SERVER_TEMPLATE_ID + " TEXT";
            db.execSQL(upgradeQuery);
        }
    }
}
