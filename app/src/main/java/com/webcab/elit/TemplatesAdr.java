package com.webcab.elit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.webcab.elit.adapters.TemplAddrAdapter;
import com.webcab.elit.data.addr;

import java.util.ArrayList;
import java.util.List;

public class TemplatesAdr extends Activity {

	TextView t1, t2, t3, t4, t5, t6;
	DBPoints dbHelper;

	ListView lv;
	List<addr> rt;
	boolean desc;
	ImageButton i1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.templates);

		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

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
		((Button) findViewById(R.id.but_add1)).setTypeface(font1);

		i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(TemplatesAdr.this, HomeScreen.class);
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
				Intent intent = new Intent(TemplatesAdr.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(TemplatesAdr.this, Settings.class);
				startActivity(intent);
				finish();
			}
		});

        TextView addr = (TextView) findViewById(R.id.txt_addr);
        addr.setBackgroundResource(R.drawable.top_bar_grad_active);

		TextView mAll = (TextView) findViewById(R.id.txt_all);
		mAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TemplatesAdr.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});

        TextView tmepl = (TextView) findViewById(R.id.txt_route);
		tmepl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TemplatesAdr.this, Templates_2.class);
				startActivity(intent);
			//	finish();
			}
		});

		Button add = (Button) findViewById(R.id.but_add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TemplatesAdr.this,
						TemplAddAddr.class);
				startActivity(intent);
				//finish();
			}
		});

		lv = (ListView) findViewById(R.id.listView1);

		rt = getTempl(false);

		TemplAddrAdapter adapter = new TemplAddrAdapter(TemplatesAdr.this, rt);

		lv.setAdapter(adapter);
	}

	private List<addr> getTempl(boolean b) {
		List<addr> rt = new ArrayList<addr>();

		dbHelper = new DBPoints(TemplatesAdr.this);
		// подключаемся к БД
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		Cursor c;
		if (b) {
			c = db.query("addr", null, null, null, null, null, "title DESC");
		} else {
			c = db.query("addr", null, null, null, null, null, "title");
		}

		// ставим позицию курсора на первую строку выборки
		// если в выборке нет строк, вернется false
		if (c.moveToFirst()) {

			// определяем номера столбцов по имени в выборке
			int idColIndex = c.getColumnIndex("id");
			int titleColIndex = c.getColumnIndex("title");
			int descColIndex = c.getColumnIndex("desc");

			int strColIndex = c.getColumnIndex("str");
			int stridColIndex = c.getColumnIndex("strid");
			int domColIndex = c.getColumnIndex("dom");
			int domidColIndex = c.getColumnIndex("domid");
			int paradColIndex = c.getColumnIndex("parad");
			int primColIndex = c.getColumnIndex("prim");

			do {

				Log.d("MA",
						"ID = " + c.getInt(idColIndex) + ", name = "
								+ c.getString(titleColIndex) + ", desc = "
								+ c.getString(descColIndex));

				rt.add(new addr(c.getInt(idColIndex), c
						.getString(titleColIndex), c.getString(descColIndex), c
						.getString(strColIndex), c.getString(stridColIndex), c
						.getString(domColIndex), c.getString(domidColIndex), c
						.getString(paradColIndex), c.getString(primColIndex)));

			} while (c.moveToNext());
		} else {
			Log.d("MA", "0 rows");
		}

		c.close();

		dbHelper.close();

		return rt;
	}
	
	@Override
	public void onBackPressed() {
		/*
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					TemplatesAdr.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							TemplatesAdr.super.onBackPressed();
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

			TemplAddrAdapter adapter = new TemplAddrAdapter(TemplatesAdr.this, rt);

			lv.setAdapter(adapter);
		} else {
			item.setTitle("Сортировать ..Я-А");
			desc = true;
			rt = getTempl(false);

			TemplAddrAdapter adapter = new TemplAddrAdapter(TemplatesAdr.this, rt);

			lv.setAdapter(adapter);
		}

		return super.onOptionsItemSelected(item);
	}
}
