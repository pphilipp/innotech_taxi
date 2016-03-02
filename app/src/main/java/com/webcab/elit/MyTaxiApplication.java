package com.webcab.elit;

import android.app.Application;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

public class MyTaxiApplication extends Application {
	
	private UncaughtExceptionHandler defaultUEH;

	private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			
			String mess = thread.getName().toString() + stack2string(ex);
			
			DBCrashes dbCrashes = new DBCrashes(getApplicationContext());
			SQLiteDatabase db = dbCrashes.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			
			try {
				
				cv.put("title", ex.getClass().toString());
				cv.put("error", mess);
				
				long rowID = db.insert("crash", null, cv);
				Log.d("mainActivity", "row inserted, ID = " + rowID);
				
//				Cursor cr = db.query("crash", null, null, null, null, null, null);
//				
//				try {
//					if (cr.moveToFirst()) {
//
//						int idColIndex = cr.getColumnIndex("id");
//						int titleColIndex = cr.getColumnIndex("title");
//						int errorColIndex = cr.getColumnIndex("error");
//						int sentColIndex = cr.getColumnIndex("sent");
//						
//						do {
//							Log.d("MA", "ID = " + cr.getInt(idColIndex) + ", name = "
//											+ cr.getString(titleColIndex)
//											+ ", error = "
//											+ cr.getString(errorColIndex) + ", sent = "
//											+ cr.getInt(sentColIndex));
//						} while (cr.moveToNext());
//					} else {
//						Log.d("MA", "0 rows");
//					}
//				} catch (Exception e) {
//					Log.d("error", e.getMessage());
//				} finally {
//					cr.close();
//				}
				
			} catch (Exception e) {
                //Mint.logException(e);
				Log.d("error", e.getMessage());
			} finally {
				dbCrashes.close();
			}
			
//			File sdCardRoot = Environment.getExternalStorageDirectory();
//			File yourDir = new File(sdCardRoot, "taxi");
//			yourDir.mkdirs();
//			
//			createFile(mess);
			
			// re-throw critical exception further to the os (important)
			defaultUEH.uncaughtException(thread, ex);
		}
	};
	
//	public void createFile(String str){
//        FileWriter fWriter;
//        try{
//        	
//        	Calendar c = Calendar.getInstance();
//			String mf = "crash-" + c.get(Calendar.DATE) + "."
//					+ c.get(Calendar.MONTH) + "." + c.get(Calendar.YEAR) + "-"
//					+ c.get(Calendar.HOUR_OF_DAY) + "." + c.get(Calendar.MINUTE)
//					+ "." + c.get(Calendar.SECOND)+".txt";
//			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/taxi/"+mf;
//        	
//			Log.d("filename", path);
//			
//             fWriter = new FileWriter(path);
//             fWriter.write(str);
//             fWriter.flush();
//             fWriter.close();
//         }catch(Exception e){
//                  Log.d("error", e.getMessage());
//         }
//    }
	
	public String stack2string(Throwable e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);

			e.printStackTrace(pw);
			return "\r\n" + sw.toString() + "";
		} catch (Exception e2) {
            //Mint.logException(e2);
			return "bad stack2string";
		}
	}

	
	
	public MyTaxiApplication() {
		defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

		// setup handler for uncaught exception
		Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
	}


}
