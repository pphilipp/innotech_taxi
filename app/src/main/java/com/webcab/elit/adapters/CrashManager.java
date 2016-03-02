package com.webcab.elit.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.webcab.elit.DBCrashes;
import com.webcab.elit.Log;
import com.webcab.elit.data.crash;
import com.webcab.elit.net.ServiceConnection;

public class CrashManager implements Runnable {
	
	Context ctx;

	public CrashManager(Context ctx) {
		super();
		this.ctx = ctx;
	}

	@Override
	public void run() {
		
		ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!mWifi.isConnected()) {
			Log.d("CrashManager", "is not wifi. Exit.");
		    return;
		}
		
		Log.d("CrashManager", "start");
		
		SharedPreferences mSettings;
		mSettings = ctx.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
		final String phone = mSettings.getString("Phone", "");
		
		List<crash> l_cr = new ArrayList<crash>();
		
		DBCrashes dbCrashes = new DBCrashes(ctx);
		SQLiteDatabase db = dbCrashes.getWritableDatabase();
		
		
		
		try {
			
			Cursor cr = db.query("crash", null, null, null, null, null, null);
			
			try {
				if (cr.moveToFirst()) {

					int idColIndex = cr.getColumnIndex("id");
					int titleColIndex = cr.getColumnIndex("title");
					int errorColIndex = cr.getColumnIndex("error");
					int sentColIndex = cr.getColumnIndex("sent");
					
					do {
//						Log.d("MA", "ID = " + cr.getInt(idColIndex) + ", name = "
//										+ cr.getString(titleColIndex)
//										+ ", error = "
//										+ cr.getString(errorColIndex) + ", sent = "
//										+ cr.getInt(sentColIndex));
						
						l_cr.add(new crash(cr.getInt(idColIndex), cr.getString(titleColIndex),
								cr.getString(errorColIndex), cr.getInt(sentColIndex)));
						
					} while (cr.moveToNext());
				} else {
					Log.d("MA", "0 rows");
				}
			} catch (Exception e) {
				Log.e("crashM", e.getMessage());
			} finally {
				cr.close();
			}
			
			Log.d("crashM", "count - "+cr.getCount());
			
			for (int i=0; i<l_cr.size(); i++) {
				if (l_cr.get(i).getSent() == 0) {
					ServiceConnection sc = new ServiceConnection(ctx);
					String res = sc.sendError(phone, l_cr.get(i).getError(), l_cr.get(i).getTitle());
					
					if (res.equals("success")) {
						
						int row = db.delete("crash", "id='" + l_cr.get(i).getId()+ "'", null);
						Log.d("CrashManager", "id - " + l_cr.get(i).getId() + " - row deleted - " + row);
					}
				}
			}
			
		} catch (Exception e) {
			Log.d("CrashManager","");
            e.printStackTrace();
        } finally {
			dbCrashes.close();
		}
		
		Log.d("CrashManager", "stop");
	}

}
