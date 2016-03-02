package com.webcab.elit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBCrashes extends SQLiteOpenHelper {
	
	String LOG_TAG = "SQLite_Crashes";

    public DBCrashes(Context context) {
      // конструктор суперкласса
      super(context, "myDBcrash", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      Log.d(LOG_TAG, "-- onCreate database --");
      // создаем таблицу с полями
      db.execSQL("create table crash ("
          + "id integer primary key autoincrement," 
          + "title text,"
          + "error text,"
          + "sent integer DEFAULT 0"+ ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
  }
