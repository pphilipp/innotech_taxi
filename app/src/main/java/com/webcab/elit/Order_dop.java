package com.webcab.elit;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Order_dop extends Activity {

	SharedPreferences mSettings;
	
	TextView /*t1, t2, t3, t4,*/ txt_snow, txt_pos, txt_pat, txt_wifi, txt_smoke, txt_bag;
	Button bt_ok, bt_c;
	LinearLayout ll_snow, ll_pos, ll_pat, ll_wifi, ll_smoke, ll_bag;
	
	ImageView imgb_snow, imgb_pos, imgb_pat, imgb_wifi, imgb_smoke, imgb_bag/*, img_snow, img_pos, img_pat, img_wifi, img_smoke, img_bag*/;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_dop);

		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

        ((Button) findViewById(R.id.button3)).setTypeface(font1);

		ll_snow = (LinearLayout) findViewById(R.id.ll_snow);
		ll_pos = (LinearLayout) findViewById(R.id.ll_pos);
		ll_pat = (LinearLayout) findViewById(R.id.ll_pat);
		ll_wifi = (LinearLayout) findViewById(R.id.ll_wifi);
		ll_smoke = (LinearLayout) findViewById(R.id.ll_smoke);
		ll_bag = (LinearLayout) findViewById(R.id.ll_bag);


		bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_ok.setTypeface(font1);
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			/*	Intent intent = new Intent(Order_dop.this, HomeScreen.class);
				startActivity(intent);*/
				finish();
			}
		});
		

		
		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
		
		OnClickListener onC = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = mSettings.edit();
				if (v.equals(imgb_snow)) {
					if (mSettings.getBoolean("Snow", false)) {
						//v.setBackgroundResource(R.drawable.tumb);
						v.setBackgroundResource(R.drawable.sn1);
						//img_snow.setPressed(false);
						ll_snow.setPressed(false);
						editor.putBoolean("Snow", false);
						editor.commit();
					} else {
						//v.setBackgroundResource(R.drawable.tumb_a);
						v.setBackgroundResource(R.drawable.sn1_a);
						//img_snow.setPressed(true);
						ll_snow.setPressed(true);
						editor.putBoolean("Snow", true);
						editor.commit();
					}
				} else if (v.equals(imgb_pos)) {
					if (mSettings.getBoolean("Pos", false)) {
						//v.setBackgroundResource(R.drawable.tumb);
						v.setBackgroundResource(R.drawable.mastercard);
						//img_pos.setPressed(false);
						ll_pos.setPressed(false);
						editor.putBoolean("Pos", false);
						editor.commit();
					} else {
						//v.setBackgroundResource(R.drawable.tumb_a);
						v.setBackgroundResource(R.drawable.mastercard_a);
						//img_pos.setPressed(true);
						ll_pos.setPressed(true);
						editor.putBoolean("Pos", true);
						editor.commit();
					}
				} else if (v.equals(imgb_pat)) {
					if (mSettings.getBoolean("Pat", false)) {
						//v.setBackgroundResource(R.drawable.tumb);
						v.setBackgroundResource(R.drawable.teddy_bear);
						//img_pat.setPressed(false);
						ll_pat.setPressed(false);
						editor.putBoolean("Pat", false);
						editor.commit();
					} else {
						//v.setBackgroundResource(R.drawable.tumb_a);
						v.setBackgroundResource(R.drawable.teddy_bear_a);
						//img_pat.setPressed(true);
						ll_pat.setPressed(true);
						editor.putBoolean("Pat", true);
						editor.commit();
					}
				} else if (v.equals(imgb_wifi)) {
					if (mSettings.getBoolean("Wifi", false)) {
						//v.setBackgroundResource(R.drawable.tumb);
						v.setBackgroundResource(R.drawable.wifi_n);
						//img_wifi.setPressed(false);
						ll_wifi.setPressed(false);
						editor.putBoolean("Wifi", false);
						editor.commit();
					} else {
						//v.setBackgroundResource(R.drawable.tumb_a);
						v.setBackgroundResource(R.drawable.wifi_a);
						//img_wifi.setPressed(true);
						ll_wifi.setPressed(true);
						editor.putBoolean("Wifi", true);
						editor.commit();
					}
				} else if (v.equals(imgb_smoke)) {
					if (mSettings.getBoolean("Smoke", false)) {
						//v.setBackgroundResource(R.drawable.tumb);
						v.setBackgroundResource(R.drawable.nuclear_power_plant);
						//img_smoke.setPressed(false);
						ll_smoke.setPressed(false);
						editor.putBoolean("Smoke", false);
						editor.commit();
					} else {
						//v.setBackgroundResource(R.drawable.tumb_a);
						v.setBackgroundResource(R.drawable.nuclear_power_plant_a);
						//img_smoke.setPressed(true);
						ll_smoke.setPressed(true);
						editor.putBoolean("Smoke", true);
						editor.commit();
					}
				} else if (v.equals(imgb_bag)) {
					if (mSettings.getBoolean("Bag", false)) {
						//v.setBackgroundResource(R.drawable.tumb);
						v.setBackgroundResource(R.drawable.treasure_chest1);
						//img_bag.setPressed(false);
						ll_bag.setPressed(false);
						editor.putBoolean("Bag", false);
						editor.commit();
					} else {
						//v.setBackgroundResource(R.drawable.tumb_a);
						v.setBackgroundResource(R.drawable.treasure_chest1_a);
						//img_bag.setPressed(true);
						ll_bag.setPressed(true);
						editor.putBoolean("Bag", true);
						editor.commit();
					}
				}
			}
		};
		
		txt_snow = (TextView) findViewById(R.id.txt_snow);
		txt_snow.setTypeface(font1);
		//img_snow = (ImageView) findViewById(R.id.img_snow);
		imgb_snow = (ImageView) findViewById(R.id.imgb_snow);
		if (mSettings.getBoolean("Snow", false)) {
			imgb_snow.setBackgroundResource(R.drawable.sn1_a);
			//img_snow.setPressed(true);
			ll_snow.setPressed(true);
		}
		imgb_snow.setOnClickListener(onC);
		
		
		txt_pos = (TextView) findViewById(R.id.txt_pos);
		txt_pos.setTypeface(font1);
		//img_pos = (ImageView) findViewById(R.id.img_pos);
		imgb_pos = (ImageView) findViewById(R.id.imgb_pos);
		if (mSettings.getBoolean("Pos", false)) {
			imgb_pos.setBackgroundResource(R.drawable.mastercard_a);
			//img_pos.setPressed(true);
			ll_pos.setPressed(true);
		}
		imgb_pos.setOnClickListener(onC);
		
		
		txt_pat = (TextView) findViewById(R.id.txt_pat);
		txt_pat.setTypeface(font1);
		//img_pat = (ImageView) findViewById(R.id.img_pat);
		imgb_pat = (ImageView) findViewById(R.id.imgb_pat);
		if (mSettings.getBoolean("Pat", false)) {
			imgb_pat.setBackgroundResource(R.drawable.teddy_bear_a);
			//img_pat.setPressed(true);
			ll_pat.setPressed(true);
		}
		imgb_pat.setOnClickListener(onC);
		
		
		txt_wifi = (TextView) findViewById(R.id.txt_wifi);
		txt_wifi.setTypeface(font1);
		//img_wifi = (ImageView) findViewById(R.id.img_wifi);
		imgb_wifi = (ImageView) findViewById(R.id.imgb_wifi);
		if (mSettings.getBoolean("Wifi", false)) {
			imgb_wifi.setBackgroundResource(R.drawable.wifi_a);
			//img_wifi.setPressed(true);
			ll_wifi.setPressed(true);
		}
		imgb_wifi.setOnClickListener(onC);
		
		
		txt_smoke = (TextView) findViewById(R.id.txt_smoke);
		txt_smoke.setTypeface(font1);
		//img_smoke = (ImageView) findViewById(R.id.img_smoke);
		imgb_smoke = (ImageView) findViewById(R.id.imgb_smoke);
		if (mSettings.getBoolean("Smoke", false)) {
			imgb_smoke.setBackgroundResource(R.drawable.nuclear_power_plant_a);
			//img_smoke.setPressed(true);
			ll_smoke.setPressed(true);
		}
		imgb_smoke.setOnClickListener(onC);
		
		
		txt_bag = (TextView) findViewById(R.id.txt_bag);
		txt_bag.setTypeface(font1);
		//img_bag = (ImageView) findViewById(R.id.img_bag);
		imgb_bag = (ImageView) findViewById(R.id.imgb_bag);
		if (mSettings.getBoolean("Bag", false)) {
			imgb_bag.setBackgroundResource(R.drawable.treasure_chest1_a);
			//img_bag.setPressed(true);
			ll_bag.setPressed(true);
		}
		imgb_bag.setOnClickListener(onC);
		
		
		
		bt_c = (Button) findViewById(R.id.bt_cancel);
//		bt_c.setTypeface(font1);
		bt_c.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ""
//				img_snow.setPressed(false);
//				img_pos.setPressed(false);
//				img_pat.setPressed(false);
//				img_wifi.setPressed(false);
//				img_smoke.setPressed(false);
//				img_bag.setPressed(false);
//				
//				imgb_snow.setBackgroundResource(R.drawable.tumb);
//				imgb_pos.setBackgroundResource(R.drawable.tumb);
//				imgb_pat.setBackgroundResource(R.drawable.tumb);
//				imgb_wifi.setBackgroundResource(R.drawable.tumb);
//				imgb_smoke.setBackgroundResource(R.drawable.tumb);
//				imgb_bag.setBackgroundResource(R.drawable.tumb);
				
				SharedPreferences.Editor editor = mSettings.edit();
				editor.putBoolean("Snow", false);
				editor.putBoolean("Pos", false);
				editor.putBoolean("Pat", false);
				editor.putBoolean("Wifi", false);
				editor.putBoolean("Smoke", false);
				editor.putBoolean("Bag", false);
				editor.commit();
				Log.d("SHARED", "COMMITED");
				
				/*Intent intent = new Intent(Order_dop.this, HomeScreen.class);
				startActivity(intent);*/
				Order_dop.this.finish();
			}
		});
		
		
	}
	/*
	@Override
	public void onBackPressed() {
		
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					Order_dop.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							Order_dop.super.onBackPressed();
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_order_from, menu);
		return true;
	}

}
