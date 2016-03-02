package com.webcab.elit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.webcab.elit.adapters.TemplAdapter;
import com.webcab.elit.data.templ;

import java.util.ArrayList;
import java.util.List;

public class Templates_2 extends Activity {

	TextView t1, t2, t3, t4, t5, t6;
	DBRoutes dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.templates_2);

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

		ImageButton i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Templates_2.this, HomeScreen.class);
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
				Intent intent = new Intent(Templates_2.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Templates_2.this, Settings.class);
				startActivity(intent);
				finish();
			}
		});

		TextView all = (TextView) findViewById(R.id.txt_all);
		all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Templates_2.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});

		TextView adr = (TextView) findViewById(R.id.txt_addr);
		adr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Templates_2.this, TemplatesAdr.class);
				startActivity(intent);
				finish();
			}
		});

		TextView routes = (TextView) findViewById(R.id.txt_route);
		routes.setBackgroundResource(R.drawable.top_bar_grad_active);


		Button add = (Button) findViewById(R.id.but_add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Templates_2.this, TemplAddRoute.class);
				startActivity(intent);
				//	finish();
			}
		});

		ListView lv = (ListView) findViewById(R.id.listView1);

		List<templ> rt = new ArrayList<templ>();

		dbHelper = new DBRoutes(Templates_2.this);
		// подключаемся к БД
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor c = db.query("route", null, null, null, null, null, null);

		// ставим позицию курсора на первую строку выборки
		// если в выборке нет строк, вернется false
		if (c.moveToFirst()) {

			// определяем номера столбцов по имени в выборке
			int idColIndex = c.getColumnIndex("id");
			int titleColIndex = c.getColumnIndex("title");
			int descColIndex = c.getColumnIndex("desc");

			int descColIndex2 = c.getColumnIndex("desc2");

			do {

				Log.d("MA",
						"ID = " + c.getInt(idColIndex) + ", name = "
								+ c.getString(titleColIndex) + ", desc = "
								+ c.getString(descColIndex));

				rt.add(new templ(c.getInt(idColIndex), c.getString(titleColIndex), c.getString(descColIndex) + " -> " + c.getString(descColIndex2), false));

			} while (c.moveToNext());
		} else {
			Log.d("MA", "0 rows");
		}

		c.close();

		dbHelper.close();

		List<templ> tmpl = new ArrayList<templ>();

		TemplAdapter adapter = new TemplAdapter(Templates_2.this, rt);
		lv.setAdapter(adapter);
	}
	/*	
	@Override
	public void onBackPressed() {

		try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					Templates_2.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							Templates_2.super.onBackPressed();
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
		}
	}
	 */
	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		getMenuInflater().inflate(R.menu.templates, menu);
	//		return true;
	//	}
}
