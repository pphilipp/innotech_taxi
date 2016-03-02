/*package com.webcab.elit;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.webcab.elit.net.ServiceConnection;

public class OrderCarHere extends Activity {

	//TextView inf;
	long tm;
	Button bt_ok;
	String orderId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_car_here);
		//OrderWait.act_orderWait.finish();
		bt_ok = (Button) findViewById(R.id.bt_ok);
		inf = (TextView)findViewById(R.id.txt_info);
		 
		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/GOTHIC.TTF");

		inf.setTypeface(font1);
		
		bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_ok.setTypeface(font1);
		
		
		TextView t1 = (TextView) findViewById(R.id.txt_t1);
		t1.setTypeface(font1);
		
		ImageButton i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setPressed(true);
		i1.setClickable(false);
		ImageView iv1 = (ImageView) findViewById(R.id.it_1_img);
		iv1.setImageResource(R.drawable.car_active);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(OrderCarHere.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});
		((findViewById(R.id.but_call))).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"+CreateOrder.or.getPhone()));
				startActivity(callIntent);
			}
		});
		
		Bundle bundle = getIntent().getExtras();
		
		if (bundle==null) {
			Log.d("OrderWait", "0");
		} else  {  //после приезда машины
			
			SharedPreferences mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putString("driverId", bundle.getString("driverId"));
			editor.commit();
			
			//TODO
			
			final String car = bundle.getString("carHere");
			
			Thread t = new Thread(new Runnable() {
				public void run() {

					ServiceConnection sc2 = new ServiceConnection(
							OrderCarHere.this);
					final String message = sc2.getMessageArrivedAuto();
					
					try {
						JSONObject result = new JSONObject(message);
						final String mes = result.optString("Message");
						
						runOnUiThread(new Runnable() {
							public void run() {
								//inf.setText(Html.fromHtml("<b>"+mes + "</b><br>" + car));
								((TextView)findViewById(R.id.txt_color)).setText("color");
								((TextView)findViewById(R.id.txt_num)).setText("num");
								((TextView)findViewById(R.id.txt_year)).setText("year");
								((TextView)findViewById(R.id.txt_status)).setText("status");
								bt_ok.setOnClickListener(listener);
							}
						});
						
					} catch (JSONException e) {
						// ""
						Log.d("error", e.getMessage());
					}
				}
			});
			t.start();
		}

	}
	
	OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			try {
				AlertDialog alertDialog = new AlertDialog.Builder(
						OrderCarHere.this).create();
				alertDialog.setMessage("Вы действительно хотите выйти?");
				alertDialog.setCancelable(false);
				alertDialog.setButton("Да",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						});

				alertDialog.setButton2("Нет",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
			} catch (Exception e) {
				Log.d(Constants.CONTENT_DIRECTORY,
						"Show Dialog: " + e.getMessage());
			}
			
		}
	};

}
*/