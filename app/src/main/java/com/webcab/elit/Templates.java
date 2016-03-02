package com.webcab.elit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.webcab.elit.adapters.CustomComparator;
import com.webcab.elit.adapters.TemplAdapter;
import com.webcab.elit.data.TemplatesSyncHelper;
import com.webcab.elit.data.templ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Templates extends Activity {

	TextView t1, t2, t3, t4, t5, t6;
	static DBPoints dbHelper;
	
	ListView lv;
	List<templ> rt;
	boolean desc;
	ImageButton i1;
	static Context mContext;
	TemplatesSyncHelper mSyncHelper;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.templates_all);

		mContext = Templates.this;



		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

		((Button) findViewById(R.id.but_add1)).setTypeface(font1);

		t1 = (TextView) findViewById(R.id.txt_t1);
		t1.setTypeface(font1);
		t2 = (TextView) findViewById(R.id.txt_t2);
		t2.setTypeface(font1);
		t3 = (TextView) findViewById(R.id.txt_t3);
		t3.setTypeface(font1);
		t4 = (TextView) findViewById(R.id.txt_t4);
		t4.setTypeface(font1);

		t5 = (TextView) findViewById(R.id.txt_addr);
		t5.setTypeface(font1);
		t6 = (TextView) findViewById(R.id.txt_route);
		t6.setTypeface(font1);
		TextView t7 = (TextView) findViewById(R.id.txt_all);
		t7.setTypeface(font1);

		i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Templates.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i2 = (ImageButton) findViewById(R.id.it_2);
		i2.setPressed(true);
		i2.setClickable(false);
		ImageView iv2 = (ImageView) findViewById(R.id.it_2_img);
		iv2.setImageResource(R.drawable.fold_active);
		//t2.setTextColor(Color.rgb(0, 102, 255));
		t2.setTextColor(getResources().getColor(R.color.blue_WebCab));
		// float scale =
		// getBaseContext().getResources().getDisplayMetrics().density;
		// int px = Math.round(40 * scale);
		// iv2.getLayoutParams().width = px;
		// iv2.getLayoutParams().height = px;

		ImageButton i3 = (ImageButton) findViewById(R.id.it_3);
		i3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Templates.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Templates.this, Settings.class);
				startActivity(intent);
				finish();
			}
		});

		TextView adr = (TextView) findViewById(R.id.txt_addr);
		adr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Templates.this, TemplatesAdr.class);
				startActivity(intent);
				finish();
			}
		});

		TextView tmepl = (TextView) findViewById(R.id.txt_route);
		tmepl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Templates.this, Templates_2.class);
				startActivity(intent);
				//finish();
			}
		});

		TextView all = (TextView) findViewById(R.id.txt_all);
        all.setBackgroundResource(R.drawable.top_bar_grad_active);

		lv = (ListView) findViewById(R.id.listView1);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// ""
				Log.d("sd", ""+arg2);
			}
		});

		rt = getTempl(false);


		final TemplAdapter adapter = new TemplAdapter(Templates.this, rt);

		mSyncHelper = new TemplatesSyncHelper(mContext, TemplatesSyncHelper.SYNC_FROM_SERVER,
				adapter);
		mSyncHelper.execute();

		lv.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSyncHelper.cancel(true);

	}

	public static List<templ>  getTempl(boolean b) {
		List<templ> rt = new ArrayList<templ>();

		dbHelper = new DBPoints(mContext);
		// подключаемся к БД
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = null;
		
		try {
			c = db.query("addr", null, null, null, null, null, null);

			// ставим позицию курсора на первую строку выборки
			// если в выборке нет строк, вернется false
			if (c.moveToFirst()) {

				int count = 0;
				// определяем номера столбцов по имени в выборке
				int idColIndex = c.getColumnIndex("id");
				int titleColIndex = c.getColumnIndex("title");
				int descColIndex = c.getColumnIndex("desc");

				do {

					Log.d("MA",
							"ID = " + c.getInt(idColIndex) + ", name = "
									+ c.getString(titleColIndex) + ", desc = "
									+ c.getString(descColIndex));

					
					rt.add(new templ(c.getInt(idColIndex), c.getString(titleColIndex), c.getString(descColIndex), true));
					rt.get(count).setServerTemplID(c.getString(c.getColumnIndex(dbHelper.SERVER_TEMPLATE_ID)));
					count++;
				} while (c.moveToNext());
			} else {
				Log.d("MA", "0 rows");
			}
		} catch (Exception e) {
			Log.d("error", e.getMessage());
		} finally {
			c.close();
			db.close();
		}
		
		
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

					Log.d("MA",
							"ID = " + c2.getInt(idcolIndex)
									+ ", servID = " + c2.getString(c2.getColumnIndex(dbHelper2.SERVER_TEMPLATE_ID))
									+ ", name = " + c2.getString(titleColIndex) + ", desc = "
									+ c2.getString(descColIndex));
					
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
			
		
		
		
		dbHelper.close();
		
		if (b) {
			Collections.sort(rt, new CustomComparator());
			Collections.reverse(rt);
		} else {
			Collections.sort(rt, new CustomComparator());
		}

		for (templ tm : rt) {
			Log.d("TemplatesSYNC", "title = " + tm.getTitle() + ", servID = " + tm.getServerTemplID());
		}
		
		return rt;
	}
	
	@Override
	public void onBackPressed() {
		
		/*try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					Templates.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							Templates.super.onBackPressed();
						}
					});

			alertDialog.setButton2("Нет", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			alertDialog.show();
		} catch (Exception e) {
			Log.d(Constants.CONTENT_DIRECTORY,
					"Show Dialog: " + e.getMessage());
		}*/
		i1.performClick();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add("Сортировать ..Я-А");
		desc = true;
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (desc) {
			item.setTitle("Сортировать А-Я..");
			desc = false;
			rt = getTempl(true);

			TemplAdapter adapter = new TemplAdapter(Templates.this, rt);

			lv.setAdapter(adapter);
		} else {
			item.setTitle("Сортировать ..Я-А");
			desc = true;
			rt = getTempl(false);

			TemplAdapter adapter = new TemplAdapter(Templates.this, rt);
			
			lv.setAdapter(adapter);
		}
		

		return super.onOptionsItemSelected(item);
	}
}
