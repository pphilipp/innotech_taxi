package com.webcab.elit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ClientInfo extends Activity {
	
	SharedPreferences mSettings;
	
	/*TextView t1, t2, t3, t4;*/
	Button bt_ok, bt_c;

	ContextThemeWrapper themedContext;
	
	TextView t_name, t_phone;
	EditText name, phone, secondNameET, surnameET, additionalPhoneET, emailET;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// ""
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		
		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			themedContext = new ContextThemeWrapper( ClientInfo.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
		}
		else {
			themedContext = new ContextThemeWrapper( ClientInfo.this, android.R.style.Theme_Light_NoTitleBar );
		}

		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

	/*	t1 = (TextView) findViewById(R.id.txt_t1);
		t1.setTypeface(font1);
		t2 = (TextView) findViewById(R.id.txt_t2);
		t2.setTypeface(font1);
		t3 = (TextView) findViewById(R.id.txt_t3);
		t3.setTypeface(font1);
		t4 = (TextView) findViewById(R.id.txt_t4);
		t4.setTypeface(font1);
		

		
		ImageButton i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setPressed(true);
		ImageView iv1 = (ImageView) findViewById(R.id.it_1_img);
		iv1.setImageResource(R.drawable.car_active);
		t1.setTextColor(Color.rgb(0, 102, 255));
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(ClientInfo.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i2 = (ImageButton) findViewById(R.id.it_2);
		i2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(ClientInfo.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});
		

		ImageButton i3 = (ImageButton) findViewById(R.id.it_3);
		i3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(ClientInfo.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(ClientInfo.this, Settings.class);
				startActivity(intent);
				finish();
			}
		});
		*/

		t_name = (TextView) findViewById(R.id.txt_name);
		t_name.setTypeface(font1);
		t_phone = (TextView) findViewById(R.id.txt_phone);
		t_phone.setTypeface(font1);

		name = (EditText) findViewById(R.id.et_name);
		phone = (EditText) findViewById(R.id.et_phone);

		name.setTypeface(font1);
		phone.setTypeface(font1);
		((Button) findViewById(R.id.button3)).setTypeface(font1);
		
		name.setText(mSettings.getString("Name", ""));
		phone.setText(mSettings.getString("uPhone", ""));

		name.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    phone.requestFocus();

                 return false;
                 }
				return false;
			}
		});
		
		bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_ok.setTypeface(font1);
		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!phone.getText().toString().equals("")
						&& ((phone.getText().length()==13) || (phone.getText().length()==10))
                        || !name.getText().toString().equals("")) {
					
//					Log.d("log", "len - " + phone.getText().length());
//					Log.d("log", "fr - " + phone.getText().toString().substring(0, 3));
					
					Editor ed = mSettings.edit();

					ed.putString("Name", name.getText().toString());
                    if(phone.getText().length() == 13)
					    ed.putString("uPhone", phone.getText().toString());
					else
                        ed.putString("uPhone", "+38".concat(phone.getText().toString()));
					ed.commit();
					
				/*	Intent intent = new Intent(ClientInfo.this, HomeScreen.class);
					startActivity(intent);*/
					finish();
				} else {
					try {
						AlertDialog alertDialog = new AlertDialog.Builder(
								themedContext).create();
						alertDialog
								.setMessage("Вы должны заполнить все поля (формат телефона +380 XX XXX XX XX)!");
						alertDialog.setButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});

						alertDialog.show();
					} catch (Exception e) {
						Log.d(Constants.CONTENT_DIRECTORY,
								"Show Dialog: " + e.getMessage());
					}
				}
			}
			
		});
		
		bt_c = (Button) findViewById(R.id.bt_cancel);
        bt_c.setTypeface(font1);
		bt_c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Editor ed = mSettings.edit();
				
				ed.putString("Name", "");
				ed.putString("uPhone", "");
				
				ed.commit();
				
				/*Intent intent = new Intent(ClientInfo.this, HomeScreen.class);
				startActivity(intent);*/
				finish();
			}
			
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		Log.d("ClientInfo", "onSTOP");
	}
	/*
	@Override
	public void onBackPressed() {
		
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					ClientInfo.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							ClientInfo.super.onBackPressed();
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
	}*/

}
