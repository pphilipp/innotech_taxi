package com.webcab.elit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.webcab.elit.adapters.AutoCompleteLoadding;
import com.webcab.elit.adapters.HomeAdapter;
import com.webcab.elit.adapters.StreetAdapter;
import com.webcab.elit.data.addr2;
import com.webcab.elit.data.house;
import com.webcab.elit.data.street;
import com.webcab.elit.net.ServiceConnection;

public class Order_to extends Activity implements TextWatcher {

	SharedPreferences mSettings;

	TextView /*t1, t2, t3, t4, tc,*/ txt_coord;
	Button bt_ok, bt_c;
	int id;

	AutoCompleteLoadding aut1, aut2;
    EditText aut3;
	HomeAdapter hAdapter;
    house selectHouse;
    boolean aut1_selected, aut2_selected;
    ScrollView scrollView;
	ContextThemeWrapper themedContext;
    //String[] data = { "Киев", "Еду за город" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_to);

		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			themedContext = new ContextThemeWrapper( Order_to.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
		}
		else {
			themedContext = new ContextThemeWrapper( Order_to.this, android.R.style.Theme_Light_NoTitleBar );
		}

		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");
		/*	tc = (TextView) findViewById(R.id.textView_city);
		tc.setTypeface(font1);
		t1 = (TextView) findViewById(R.id.txt_t1);
		t1.setTypeface(font1);
		t2 = (TextView) findViewById(R.id.txt_t2);
		t2.setTypeface(font1);
		t3 = (TextView) findViewById(R.id.txt_t3);
		t3.setTypeface(font1);
		t4 = (TextView) findViewById(R.id.txt_t4);
		t4.setTypeface(font1);
		 */
		ProgressBar pr_str = (ProgressBar) findViewById(R.id.pr_str);
		ProgressBar pr_dom = (ProgressBar) findViewById(R.id.pr_dom);

        scrollView = (ScrollView) findViewById(R.id.scrollView1);

		aut1 = (AutoCompleteLoadding) findViewById(R.id.aut1);
		aut1.setLoadingIndicator(pr_str);
		final StreetAdapter stAdapter = new StreetAdapter(this, R.layout.street_item, R.id.txt_year); //android.R.layout.select_dialog_item
		aut1.setAdapter(stAdapter);

		aut2 = (AutoCompleteLoadding) findViewById(R.id.aut2);
		aut2.setLoadingIndicator(pr_dom);
		Log.d("minimum","min - " + aut2.getThreshold());
		aut2.setThreshold(1);
		aut2.setEnabled(true);
		aut2.addTextChangedListener(this);
        aut3 = (EditText) findViewById(R.id.aut3);

		aut1.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // ""
				if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    street st = null;
                    st = (street) aut1.getAdapter().getItem(0);
                    if(st != null){
                        aut1.setText(st.getName());
                        //aut1.setSelection(st.getName().length());

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("StreetId2", st.getId());

                        editor.putString("Lat2", st.getGeoy());
                        editor.putString("Lon2", st.getGeox());
//				txt_coord.setText(st.getGeoy() + ", " + st.getGeox());

                        editor.putString("HouseId2", "");

                        editor.putString("tox", st.getGeox());
                        editor.putString("toy", st.getGeoy());
                        Log.d("SHARED", "puted " + st.getGeox() + " - " + st.getGeoy() + " to tox and toy");
                        Log.d("SHARED", "puted " + st.getId() + " to StreetId2");
                        editor.commit();
                        aut1.dismissDropDown();

                        String streetId = mSettings.getString("StreetId2", "");
                        Log.d("streetid", "streetId2 - " + streetId);
                        hAdapter = new HomeAdapter(Order_to.this, R.layout.street_item, R.id.txt_year, streetId);
                        hAdapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if(aut2.getDropDownAnchor() != 0)
                                    selectHouse = hAdapter.getItem(0);
                            }
                        });
                        aut2.setAdapter(hAdapter);
                        aut2.setEnabled(true);
                        aut2.setText("");
                        aut2.requestFocus();
                        aut2.requestFocus();
                        aut2_selected = false;
                    }
					return false;
				}
				return false;
			}
		});
		aut1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//Log.d("setOnItemClickListener" , "pos - " + arg2);
				Object obj = arg0.getItemAtPosition(arg2); //должно быть "com.webcab.elit.data.street"
				street st = (street) arg0.getItemAtPosition(arg2);
                if(st != null){
                    Log.d("setOnItemClickListener" , "type - " + obj.getClass().getName());
                    //Log.d("setOnItemClickListener" , "name - " + st.getName() + " id - " + st.getId());
                    aut1.setText(st.getName());
                    //aut1.setSelection(st.getName().length());

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("StreetId2", st.getId());

                    editor.putString("Lat2", st.getGeoy());
                    editor.putString("Lon2", st.getGeox());
//				txt_coord.setText(st.getGeoy() + ", " + st.getGeox());

                    editor.putString("HouseId2", "");

                    editor.putString("tox", st.getGeox());
                    editor.putString("toy", st.getGeoy());
                    Log.d("SHARED", "puted " + st.getGeox() + " - " + st.getGeoy() + " to tox and toy");
                    Log.d("SHARED", "puted " + st.getId() + " to StreetId2");
                    editor.commit();


                    String streetId = mSettings.getString("StreetId2", "");
                    Log.d("streetid", "streetId2 - " + streetId);
                    hAdapter = new HomeAdapter(Order_to.this, R.layout.street_item, R.id.txt_year, streetId);
                    hAdapter.registerDataSetObserver(new DataSetObserver() {
                        @Override
                        public void onChanged() {
                            super.onChanged();
                            if(aut2.getDropDownAnchor() != 0)
                                selectHouse = hAdapter.getItem(0);
                        }
                    });
                    aut2.setAdapter(hAdapter);
                    aut2.setEnabled(true);
                    aut2.setText("");
                    aut2.requestFocus();
                    aut2_selected = false;
                }

			}
		});

		aut2.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // ""
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    aut2_selected = true;
                    house st = null;
                    st = (house) aut2.getAdapter().getItem(0);
                    if(st != null){
                        aut2.setText(st.getValue());

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString("HouseId2", st.getId());

                        editor.putString("Lat2", st.getY());
                        editor.putString("Lon2", st.getX());
//				txt_coord.setText(st.getY() + ", " + st.getX());

                        Log.d("SHARED", "puted " + st.getId() + " to HouseId2");
                        editor.putString("tox", st.getX());
                        editor.putString("toy", st.getY());
                        Log.d("SHARED", "puted " + st.getX() + " - " + st.getY() + " to tox and toy");
                        editor.commit();
                        aut3.setText("");
                        aut3.setEnabled(true);
                        aut3.requestFocus();
                    }
                    return false;
                }
                return false;
            }
        });

		aut2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//Log.d("setOnItemClickListener" , "pos - " + arg2);
				Object obj = arg0.getItemAtPosition(arg2); //должно быть "com.webcab.elit.data.house"
				house st = (house) arg0.getItemAtPosition(arg2);
                if(st != null){
                    aut2_selected = true;
                    Log.d("setOnItemClickListener" , "type - " + obj.getClass().getName());
                    //Log.d("setOnItemClickListener" , "name - " + st.getName() + " id - " + st.getId());
                    aut2.setText(st.getValue());
                    //aut1.setSelection(st.getName().length());

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("HouseId2", st.getId());

                    editor.putString("Lat2", st.getY());
                    editor.putString("Lon2", st.getX());
//				txt_coord.setText(st.getY() + ", " + st.getX());

                    Log.d("SHARED", "puted " + st.getId() + " to HouseId2");
                    editor.putString("tox", st.getX());
                    editor.putString("toy", st.getY());
                    Log.d("SHARED", "puted " + st.getX() + " - " + st.getY() + " to tox and toy");
                    editor.commit();
                    aut3.setText("");
                    aut3.setEnabled(true);
                    aut3.requestFocus();
                }

			}
		});
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(selectHouse != null && !aut2_selected){
                    aut2.setText(selectHouse.getValue());
                    aut2.dismissDropDown();
                }
                //TODO тестирование определение номера дома без выбора из списка
                return true;
            }
        });

//        aut2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(!b){
//                    house st = null;
//                    st = (house) aut2.getAdapter().getItem(0);
//                    if(st != null) {
//                        aut2.setText(st.getValue());
//
//                        SharedPreferences.Editor editor = mSettings.edit();
//                        editor.putString("HouseId2", st.getId());
//
//                        editor.putString("Lat2", st.getY());
//                        editor.putString("Lon2", st.getX());
////				txt_coord.setText(st.getY() + ", " + st.getX());
//
//                        Log.d("SHARED", "puted " + st.getId() + " to HouseId2");
//                        editor.putString("tox", st.getX());
//                        editor.putString("toy", st.getY());
//                        Log.d("SHARED", "puted " + st.getX() + " - " + st.getY() + " to tox and toy");
//                        editor.commit();
//                        aut3.setText("");
//                        aut3.setEnabled(true);
//                        aut3.requestFocus();
//                    }
//                }
//            }
//        });

		String str = mSettings.getString("Street2", "");
		String dom = mSettings.getString("Dom2", "");
        String parad = mSettings.getString("Parad2","");

		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("is"+id, true);

		editor.commit();		

		if (mSettings.contains("Street2")) {
			aut1.setText(str);
			Log.d("shared", "seted street2 - " + str);
		}
		if (mSettings.contains("Dom2")) {
			aut2.setText(dom);
			Log.d("shared", "seted Dom2 - " + dom);
		}
        if (mSettings.contains("Parad2")){
            aut3.setText(parad);
        }

		bt_ok = (Button) findViewById(R.id.bt_ok);
//		bt_ok.setTypeface(font1);
		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
                if (selectHouse != null && !aut2_selected) {
                    aut2.setText(selectHouse.getValue());

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("HouseId2", selectHouse.getId());

                    editor.putString("Lat2", selectHouse.getY());
                    editor.putString("Lon2", selectHouse.getX());
//				txt_coord.setText(st.getY() + ", " + st.getX());

                    Log.d("SHARED", "puted " + selectHouse.getId() + " to HouseId2");
                    editor.putString("tox", selectHouse.getX());
                    editor.putString("toy", selectHouse.getY());
                    Log.d("SHARED", "puted " + selectHouse.getX() + " - " + selectHouse.getY() + " to tox and toy");
                    editor.commit();
                }

				if ((!aut1.getText().toString().equals(""))&&(!aut2.getText().toString().equals(""))&&
						(!mSettings.getString("StreetId2", "").equals(""))&&(!mSettings.getString("HouseId2", "").equals(""))) {
					SharedPreferences.Editor editor = mSettings.edit();
					editor.putString("Street2", aut1.getText().toString());
					Log.d("SHARED", "puted " + aut1.getText().toString() + " to Street2");
					editor.putString("Dom2", aut2.getText().toString());
					Log.d("SHARED", "puted " + aut2.getText().toString() + " to Dom2");
                    editor.putString("Parad2", aut3.getText().toString());
                    Log.d("SHARED", "puted " + aut2.getText().toString() + " to Parad2");
                    editor.commit();
					Log.d("SHARED", "COMMITED");

					/*	Intent intent = new Intent(Order_to.this, HomeScreen.class);
					startActivity(intent);*/
					finish();
				} else {
					try {
						AlertDialog alertDialog = new AlertDialog.Builder(
                                themedContext).create();
						alertDialog
						.setMessage("Вы должны заполнить поля со зведочками (*) из предложенных!");
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
//		bt_c.setTypeface(font1);
		bt_c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});


		Button t = (Button) findViewById(R.id.button3);
	}

	@Override
	protected void onRestart() {
		// ""
		super.onRestart();
		Log.d("log activity", "onRestart homescreen");
		if ((mSettings.contains("Lat2")) && (mSettings.contains("Lon2"))) {
			txt_coord.setText(mSettings.getString("Lat2", "") + ", " + mSettings.getString("Lon2", ""));

			Thread th = new Thread(new Runnable() {

				@Override
				public void run() {
					ServiceConnection sc = new ServiceConnection(
							getApplicationContext());

					final addr2 addr = sc.getAddressFromGeo(mSettings.getString("Lat2", ""), mSettings.getString("Lon2", ""));

					if (addr != null) {

						runOnUiThread(new Runnable() {
							public void run() {
								aut1.setText(addr.getSreetname());
								aut2.setText(addr.getHousenumber());
							}
						});

						SharedPreferences.Editor editor = mSettings.edit();
						editor.putString("StreetId2", addr.getStreetid());
						editor.putString("HouseId2", "1");
						editor.commit();
					}


				}
			});

			th.start();
		} else {
			txt_coord.setText("");
		}
	}

	@Override
	protected void onResume() {
		// ""
		super.onResume();

	}

	@Override
	protected void onStart() {
		// ""
		super.onStart();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_order_from, menu);
		return true;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// ""

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// ""

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// ""

	}

}
