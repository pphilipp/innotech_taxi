package com.webcab.elit;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Cabinet extends Activity {

	TextView t1, t2, t3, t4, t5, t6, t7, version;
	Button arch, mydata, rev;
	SharedPreferences mSettings;
	ImageButton i1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cabinet);

		
		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
		
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
		
		t5 = (TextView) findViewById(R.id.txt_arch);
		t5.setTypeface(font1);

		t6 = (TextView) findViewById(R.id.txt_my);
		t6.setTypeface(font1);
		t7 = (TextView) findViewById(R.id.txt_otz);
		t7.setTypeface(font1);

		version = (TextView) findViewById(R.id.txt_version);
		version.setTypeface(font1);

		Button about= (Button)findViewById(R.id.bt_about);
		about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent in = new Intent(Intent.ACTION_VIEW);
				// Try Google play
				in.setData(Uri.parse("market://details?id=com.webcab.elit"));
				if (!MyStartActivity(in)) {
					// Market (Google play) app seems not installed, let's try to
					// open a webbrowser
					in.setData(Uri
							.parse("https://play.google.com/store/apps/details?id=com.webcab.elit"));

				}
			}
		});

		Button logout = (Button) findViewById(R.id.logout);
		logout.setTypeface(font1);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = mSettings.edit();
				editor.clear();
				editor.commit();

				Intent intent = new Intent(Cabinet.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});

		(findViewById(R.id.btn_write_to_developers)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {//
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT,
						getResources().getString(R.string.str_write_to_developers_subject));
				intent.setData(Uri.parse("mailto:vladimir.kaminsky@innotech-ua.com"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		try {
			PackageInfo pack=getPackageManager().getPackageInfo(getPackageName(), 0);
			version.setText(pack.versionName);
		} catch (PackageManager.NameNotFoundException e) {
			// ""
			e.printStackTrace();
		}

		i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Cabinet.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i2 = (ImageButton) findViewById(R.id.it_2);
		i2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Cabinet.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i3 = (ImageButton) findViewById(R.id.it_3);
		i3.setPressed(true);
		i3.setClickable(false);
		ImageView iv3 = (ImageView) findViewById(R.id.it_3_img);
		iv3.setImageResource(R.drawable.cab_active);
		//t3.setTextColor(Color.rgb(0, 102, 255));
		t3.setTextColor(getResources().getColor(R.color.blue_WebCab));
		// float scale =
		// getBaseContext().getResources().getDisplayMetrics().density;
		// int px = Math.round(40 * scale);
		// iv3.getLayoutParams().width = px;
		// iv3.getLayoutParams().height = px;

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Cabinet.this, HomeScreen.class);
				intent.putExtra("showMap", true);
				startActivity(intent);
				finish();
			}
		});
		
//		TextView qwer = (TextView) findViewById(R.id.fr_poisk);
//		qwer.setText("fgdfgdfg");
		
		arch = (Button) findViewById(R.id.bt_arch);
		arch.setTypeface(font1);
		mydata = (Button) findViewById(R.id.bt_mydata);
        mydata.setTypeface(font1);
		rev = (Button) findViewById(R.id.bt_rev);
        rev.setTypeface(font1);
		
		arch.setOnClickListener(listener);
		mydata.setOnClickListener(listener);
		rev.setOnClickListener(listener);
	}
	
	OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v==arch) {
				Intent intent = new Intent(Cabinet.this, CabinetArch.class);
				startActivity(intent);
				//finish();
			} else if (v==mydata) {
				Intent intent = new Intent(Cabinet.this, CabinetMyData.class);
				startActivity(intent);
				//finish();
			} else if (v==rev) {
				Intent intent = new Intent(Cabinet.this, CompanyReview.class);
				startActivity(intent);
			//	finish();
			}
		}
	};

	private boolean MyStartActivity(Intent aIntent) {
		try {
			startActivity(aIntent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}
	
	@Override
	public void onBackPressed() {
		/*
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					Cabinet.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							Cabinet.super.onBackPressed();
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
		getMenuInflater().inflate(R.menu.cabinet, menu);
		return true;
	}
}
