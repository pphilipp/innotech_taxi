package com.webcab.elit;


import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ZaGorod extends Activity {

	Button bt_ok, bt_c;
	SharedPreferences mSettings;
	EditText nasP;

	ContextThemeWrapper themedContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_za_gorod);
		
		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			themedContext = new ContextThemeWrapper( ZaGorod.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
		}
		else {
			themedContext = new ContextThemeWrapper( ZaGorod.this, android.R.style.Theme_Light_NoTitleBar );
		}
		
		nasP = (EditText) findViewById(R.id.edit_nasp);
		
		if (mSettings.contains("NasP")) {
			nasP.setText(mSettings.getString("NasP", ""));
		}
		
		bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// ""
				if (!nasP.getText().toString().equals("")) {
					SharedPreferences.Editor editor = mSettings.edit();
					editor.putString("NasP", nasP.getText().toString());
					editor.commit();
					
					Intent intent = new Intent(ZaGorod.this, HomeScreen.class);
					startActivity(intent);
					finish();
				} else {
					try {
						AlertDialog alertDialog = new AlertDialog.Builder(
                                themedContext).create();
						alertDialog
								.setMessage("Вы должны заполнить поля со зведочками (*)");
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
		bt_c.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ""
				nasP.setText("");
				
				SharedPreferences.Editor editor = mSettings.edit();
				editor.putString("NasP", "");
				editor.commit();
				Log.d("SHARED", "COMMITED");
				
				Intent intent = new Intent(ZaGorod.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});
		
	}
	
/*	@Override
	public void onBackPressed() {
		
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					ZaGorod.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							ZaGorod.super.onBackPressed();
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
		getMenuInflater().inflate(R.menu.activity_za_gorod, menu);
		return true;
	}

}
