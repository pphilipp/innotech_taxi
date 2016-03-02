package com.webcab.elit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.webcab.elit.net.ServiceConnection;

public class CabinetMyData extends Activity {

    private static final String TAG = "CABINET";
    TextView t1, t2, t3, t4, t5, t6;
	SharedPreferences mSettings;
	
	TextView t_name, t_otch, t_surname, t_phone, t_email;
	EditText name, otch, surname, phone, email;

	ContextThemeWrapper themedContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cabinet_my_data);
		
		mSettings = getSharedPreferences("mydata", Context.MODE_PRIVATE);

		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			themedContext = new ContextThemeWrapper( CabinetMyData.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
		}
		else {
			themedContext = new ContextThemeWrapper( CabinetMyData.this, android.R.style.Theme_Light_NoTitleBar );
		}

		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

		//test commit

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		t1 = (TextView) findViewById(R.id.txt_t1);
		t1.setTypeface(font1);
		t2 = (TextView) findViewById(R.id.txt_t2);
		t2.setTypeface(font1);
		t3 = (TextView) findViewById(R.id.txt_t3);
		t3.setTypeface(font1);
		t4 = (TextView) findViewById(R.id.txt_t4);
		t4.setTypeface(font1);
		
		t_name = (TextView) findViewById(R.id.txt_name);
		t_name.setTypeface(font1);
		t_otch = (TextView) findViewById(R.id.txt_otch);
		t_otch.setTypeface(font1);
		t_surname = (TextView) findViewById(R.id.txt_surname);
		t_surname.setTypeface(font1);
		t_phone = (TextView) findViewById(R.id.txt_phone);
		t_phone.setTypeface(font1);
		t_email = (TextView) findViewById(R.id.txt_email);
		t_email.setTypeface(font1);


		ImageButton i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CabinetMyData.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i2 = (ImageButton) findViewById(R.id.it_2);
		i2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CabinetMyData.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});
		

		ImageButton i3 = (ImageButton) findViewById(R.id.it_3);
		i3.setPressed(true);
		ImageView iv3 = (ImageView) findViewById(R.id.it_3_img);
		iv3.setImageResource(R.drawable.cab_active);
		//t3.setTextColor(Color.rgb(0, 102, 255));
		t3.setTextColor(getResources().getColor(R.color.blue_WebCab));
		i3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CabinetMyData.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CabinetMyData.this, Settings.class);
				startActivity(intent);
				finish();
			}
		});
		
		name = (EditText) findViewById(R.id.et_name);
		otch = (EditText) findViewById(R.id.et_otch);
		surname = (EditText) findViewById(R.id.et_surname);
		phone = (EditText) findViewById(R.id.et_phone);
		email = (EditText) findViewById(R.id.et_email);
		
		name.setText(mSettings.getString("Name", ""));
		otch.setText(mSettings.getString("Patronymic", ""));
		surname.setText(mSettings.getString("Surname", ""));
		phone.setText(mSettings.getString("uPhone", ""));
		email.setText(mSettings.getString("Email", ""));
		
		Button bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_ok.setTypeface(font1);
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!name.getText().toString().equals("")) {


                    if ((phone.getText().length() == 13)
                            || (phone.getText().length()==10)
                            || (phone.getText().toString().equals(""))) {
                        if(phone.getText().length()==10){
                            phone.setText("+38".concat(phone.getText().toString()));
                        }
                        Editor ed = mSettings.edit();
                        ed.putString("Name", name.getText().toString());
                        ed.putString("Patronymic", otch.getText().toString());
                        ed.putString("Surname", surname.getText().toString());
                        ed.putString("Email", email.getText().toString());
                        ed.putString("uPhone", phone.getText().toString());
                        ed.commit();
                        (new SaveUserInfoToServer()).execute();
					}
                    else {
						try {
							AlertDialog alertDialog = new AlertDialog.Builder(
									themedContext).create();
							alertDialog
									.setMessage("Формат телефона должен быть +380 XX XXX XX XX или 0 XX XXX XX XX!");
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


				} else {
					try {
						AlertDialog alertDialog = new AlertDialog.Builder(
								themedContext).create();
						alertDialog
								.setMessage("Укажите имя!");
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
		
		Button bt_c = (Button) findViewById(R.id.bt_cancel);
		bt_c.setTypeface(font1);
		bt_c.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CabinetMyData.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private class SaveUserInfoToServer extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
            ServiceConnection sc = new ServiceConnection(CabinetMyData.this);
			Log.d(TAG, "wanna save user data");
			sc.saveUserInfo();
			return null;
		}

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.templates, menu);
		return true;
	}
}
