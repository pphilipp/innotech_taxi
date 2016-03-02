package com.webcab.elit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.SyncStateContract.Constants;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webcab.elit.net.ServiceConnection;

import java.util.Date;

public class OrderWait extends Activity {

	TextView timer, t;
	long tm;
	LinearLayout ll;
	Button bt_c;
	String orderId;
	static Activity act_orderWait;
    ContextThemeWrapper themedContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_wait);
		act_orderWait=this;

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            themedContext = new ContextThemeWrapper( act_orderWait, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
        }
        else {
            themedContext = new ContextThemeWrapper( act_orderWait, android.R.style.Theme_Light_NoTitleBar );
        }

		t = (TextView) findViewById(R.id.txt_poisk);
		//inf = (TextView)findViewById(R.id.txt_info);
		timer = (TextView) findViewById(R.id.txt_timer);

		ll = (LinearLayout) findViewById(R.id.ll_oder);

		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

		Typeface arialBlack = Typeface.createFromAsset(getAssets(), "fonts/arialBlack.ttf");
		/*TextView t1 = (TextView) findViewById(R.id.txt_t1);
		t1.setTypeface(font1);*/
		t.setTypeface(font1);
	//	inf.setTypeface(font1);
		timer.setTypeface(arialBlack);

		bt_c = (Button) findViewById(R.id.bt_cancel);
		bt_c.setOnClickListener(listener);

		/*ImageButton i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setPressed(true);
		i1.setClickable(false);
		ImageView iv1 = (ImageView) findViewById(R.id.it_1_img);
		iv1.setImageResource(R.drawable.car_active);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(OrderWait.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});*/

		Bundle bundle = getIntent().getExtras();

		if (bundle==null) 
		{
			Log.d("OrderWait", "0");
		} 
		else if(bundle.containsKey("carOn")) {  //машина еще едет

			Log.d("OrderWait", "carOn");

			//i1.setOnClickListener(null);
		//	inf.setText(bundle.getString("carOn"));
			orderId = bundle.getString("orderId");


			SharedPreferences mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putString("driverId", bundle.getString("driverId"));
			editor.commit();



			long millis = bundle.getLong("millis");

			long ld = new Date().getTime();

			long tm = millis - ld;
			Log.d("OrderWait", "tm - " + tm);
			if (tm>0) {
				timer(tm);
			} else if (tm<0) {
				//				Intent intent = new Intent(OrderWait.this, OrderCarHere.class);
				//				intent.putExtra("carHere", bundle.containsKey("carOn"));
				//				startActivity(intent);
				//				finish();

				t.setText("Машина в пути.");
				ll.removeView(timer);
			}

		} else {                                    //первое открытие таймера

			Log.d("OrderWait", "tm");

			tm = bundle.getLong("tm");
			orderId = bundle.getString("orderId");

			long lDateTime = new Date().getTime();

			// SERVICE SERVICE SERVICE SERVICE
			Intent serviceIntent = new Intent(OrderWait.this, MyService.class);
			serviceIntent.putExtra("seconds", tm);
			serviceIntent.putExtra("millis", lDateTime+tm);
			serviceIntent.putExtra("driverId", bundle.getString("driverId"));
			serviceIntent.putExtra("orderId",orderId);
			serviceIntent.putExtra("car", bundle.getString("car"));
			startService(serviceIntent);
			// SERVICE SERVICE SERVICE SERVICE
			
		//	inf.setText(bundle.getString("car"));
			timer(tm);
		}

	}

	void timer(long s) {
		Log.d("timer", "test - " + tm + " s iss - " + s + " ms");
		CountDownTimer ct3 = new CountDownTimer(s, 1000) {

			public void onTick(long millisUntilFinished) {
				long s = millisUntilFinished / 1000;

				if (s >= 60) {
					long q = s / 60;
					long w = s - (60 * q);
					if (Long.toString(w).length() == 1) {
						timer.setText(q + ":0" + w);
					} else {
						timer.setText(q + ":" + w);
					}
				} else if (s < 60) {
					if (Long.toString(s).length() == 1) {
						timer.setText("0:0" + s);
					} else {
						timer.setText("0:" + s);
					}
				}

			}

			public void onFinish() {
				ll.removeView(timer);
			}
		}.start();
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				AlertDialog alertDialog = new AlertDialog.Builder(
                        themedContext).create();
				alertDialog.setMessage("Вы действительно хотите отменить заказ?");
				alertDialog.setButton("Да",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						Thread t = new Thread(new Runnable() {
							public void run() {

								ServiceConnection sc = new ServiceConnection(
										OrderWait.this);
								sc.cancelOrder(orderId);

								NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
								nm.cancelAll();

								Intent intent = new Intent(
										OrderWait.this,
										HomeScreen.class);
								startActivity(intent);
								((Activity)OrderWait.this).finish();
							}
						});
						t.start();
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
	};

	protected void onRestart() {
		super.onRestart();
		Log.d("OrderWait", "onRestart");
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_order_from, menu);
		return true;
	}
	
}
