package com.webcab.elit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.webcab.elit.adapters.AutoCompleteLoadding;
import com.webcab.elit.adapters.HomeAdapter;
import com.webcab.elit.adapters.StreetAdapter;
import com.webcab.elit.data.TemplatesSyncHelper;
import com.webcab.elit.data.house;
import com.webcab.elit.data.rt;
import com.webcab.elit.data.street;

public class TemplAddRoute extends Activity implements TextWatcher {

	TextView t1, t2, t3, t4, tc;
	Button bt_ok, bt_c;
	EditText aut3, edit_d, ed_n, aut4;

	ContextThemeWrapper themedContext;

	AutoCompleteLoadding aut1, aut2, aut12, aut22;
	HomeAdapter hAdapter;
	
	Spinner spinner, spinner2;

	DBRoutes dbHelper;
	// final String[] mBuildings = { "2", "2А", "2Б", "21/10", "23", "21", "20",
	// "24", "21В", "29Г" };
	String[] data = { "Киев" };
	String[] data2 = { "Без класса", "Стандарт", "Бизнес", "Премиум" };

	String StreetId = "", HouseId = "", StreetId2 = "", HouseId2 = "";
	String id = "";
	boolean editing = false;
    //corresponding server template ID
    private String serverID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.templ_route);

		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			themedContext = new ContextThemeWrapper( TemplAddRoute.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
		}
		else {
			themedContext = new ContextThemeWrapper( TemplAddRoute.this, android.R.style.Theme_Light_NoTitleBar );
		}

		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

		tc = (TextView) findViewById(R.id.textView_city);
		tc.setTypeface(font1);
		t1 = (TextView) findViewById(R.id.txt_t1);
		t1.setTypeface(font1);
		t2 = (TextView) findViewById(R.id.txt_t2);
		t2.setTypeface(font1);
		t3 = (TextView) findViewById(R.id.txt_t3);
		t3.setTypeface(font1);
		t4 = (TextView) findViewById(R.id.txt_t4);
		t4.setTypeface(font1);

		setUpFonts(font1);
		
		ProgressBar pr_str = (ProgressBar) findViewById(R.id.pr_str);
		ProgressBar pr_dom = (ProgressBar) findViewById(R.id.pr_dom);
		ProgressBar pr_str2 = (ProgressBar) findViewById(R.id.pr_str2);
		ProgressBar pr_dom2 = (ProgressBar) findViewById(R.id.pr_dom2);
		
		spinner = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
		spinner.setPrompt("Город");
		spinner.setSelection(0);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data2);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		
		spinner2.setAdapter(adapter2);
		spinner2.setPrompt("Класс авто");
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		aut1 = (AutoCompleteLoadding) findViewById(R.id.aut1);
		aut1.setLoadingIndicator(pr_str);
		aut1.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
		StreetAdapter stAdapter = new StreetAdapter(this, R.layout.street_item,
				R.id.txt_year); // android.R.layout.select_dialog_item
		aut1.setAdapter(stAdapter);

		aut2 = (AutoCompleteLoadding) findViewById(R.id.aut2);
		aut2.setLoadingIndicator(pr_dom);
		Log.d("minimum", "min - " + aut2.getThreshold());
		aut2.setThreshold(1);
		aut2.setEnabled(false);
		aut2.addTextChangedListener(this);


        aut1.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// ""
				if ((actionId == EditorInfo.IME_ACTION_DONE)
						|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    street st = (street) aut1.getAdapter().getItem(0);
                    saveStreet(st);

					return false;
				}
				return false;
			}
		});

		aut1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Log.d("setOnItemClickListener" , "pos - " + arg2);
				Object obj = arg0.getItemAtPosition(arg2); // должно быть
															// "com.webcab.elit.data.street"
				street st = (street) arg0.getItemAtPosition(arg2);
				Log.d("setOnItemClickListener", "type - "
						+ obj.getClass().getName());
				saveStreet(st);

			}
		});

        aut1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && StreetId == null || (StreetId != null && StreetId.equals(""))) {
                    street st = (street) aut1.getAdapter().getItem(0);
                    saveStreet(st);
                }
                if (hasFocus && StreetId != null) {
                    StreetId = "";
                }
            }
        });

		aut2.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// ""
				if ((actionId == EditorInfo.IME_ACTION_DONE)
						|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    house st = (house) aut2.getAdapter().getItem(0);
                    saveHouse(st);

					return false;
				}
				return false;
			}
		});

		aut2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Log.d("setOnItemClickListener" , "pos - " + arg2);
				Object obj = arg0.getItemAtPosition(arg2); // должно быть
															// "com.webcab.elit.data.house"
				house st = (house) arg0.getItemAtPosition(arg2);
				Log.d("setOnItemClickListener", "type - "
						+ obj.getClass().getName());
				saveHouse(st);
			}
		});

        aut2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && HouseId == null || (HouseId != null && HouseId.equals(""))) {
                    house st = (house) aut2.getAdapter().getItem(0);
                    saveHouse(st);
                }
                if (hasFocus && HouseId != null) {
                    HouseId = "";
                }
            }
        });


		aut3 = (EditText) findViewById(R.id.aut3);
		aut4 = (EditText) findViewById(R.id.aut4);
		aut3.setEnabled(true);
		edit_d = (EditText) findViewById(R.id.edit_d);
		ed_n = (EditText) findViewById(R.id.ed_name);
		// edit_d.setEnabled(false);

		aut3.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// ""
				if ((actionId == EditorInfo.IME_ACTION_DONE)
						|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
					// edit_d.setEnabled(true);
					edit_d.setText("");
					edit_d.requestFocus();

					return false;
				}
				return false;
			}
		});
		
		aut12 = (AutoCompleteLoadding) findViewById(R.id.aut12);
		aut12.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
		aut22 = (AutoCompleteLoadding) findViewById(R.id.aut22);
		
		aut12.setLoadingIndicator(pr_str2);
		StreetAdapter stAdapter2 = new StreetAdapter(this, R.layout.street_item,
				R.id.txt_year); // android.R.layout.select_dialog_item
		aut12.setAdapter(stAdapter2);

		aut22.setLoadingIndicator(pr_dom2);
		aut22.setThreshold(1);
		aut22.setEnabled(false);
		aut22.addTextChangedListener(this);

		aut12.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// ""
				if ((actionId == EditorInfo.IME_ACTION_DONE)
						|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    street st = (street) aut12.getAdapter().getItem(0);
                    saveStreet2(st);

					return false;
				}
				return false;
			}
		});

        //when nothing is chosen and focus is lost, store first row of adapter
        aut12.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && StreetId2 == null || (StreetId2 != null && StreetId2.equals(""))) {
                    street st = (street) aut12.getAdapter().getItem(0);
                    saveStreet2(st);
                }
                if (hasFocus && StreetId2 != null) {
                    StreetId2 = "";
                }
            }
        });

		aut12.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Log.d("setOnItemClickListener" , "pos - " + arg2);
				Object obj = arg0.getItemAtPosition(arg2); // должно быть
															// "com.webcab.elit.data.street"
				street st = (street) arg0.getItemAtPosition(arg2);
				Log.d("setOnItemClickListener", "type - "
						+ obj.getClass().getName());
				saveStreet2(st);
			}
		});

		aut22.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// ""
				if ((actionId == EditorInfo.IME_ACTION_DONE)
						|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    house st = (house) aut22.getAdapter().getItem(0);
                    if (st != null) {
                        aut22.setText(st.getValue());
                        HouseId2 = st.getId();
                    }

					return false;
				}
				return false;
			}
		});

        aut22.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && HouseId2 == null || (HouseId2 != null && HouseId2.equals(""))) {
                    house st = (house) aut22.getAdapter().getItem(0);
                    if (st != null) {
                        aut22.setText(st.getValue());
                        HouseId2 = st.getId();
                    }
                }
                if (hasFocus && HouseId2 != null) {
                    HouseId2 = "";
                }
            }
        });

		aut22.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Log.d("setOnItemClickListener" , "pos - " + arg2);
				Object obj = arg0.getItemAtPosition(arg2); // должно быть
															// "com.webcab.elit.data.house"
				house st = (house) arg0.getItemAtPosition(arg2);
				Log.d("setOnItemClickListener", "type - "
						+ obj.getClass().getName());
				aut22.setText(st.getValue());

				HouseId2 = st.getId();
			}
		});

		/*
		 * aut3.setOnKeyListener(new OnKeyListener() {
		 * 
		 * @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
		 * // "" if (keyCode ==
		 * KeyEvent.KEYCODE_ENTER) { edit_d.requestFocus(); Log.d("dftyf",
		 * "enter pressed"); return true; } return false; } });
		 */

		try {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				id = extras.getString("id");
			}

		} catch (NullPointerException e) {
			Log.d("NullPointerException", "" + e.getMessage());
		}

		Log.d("templadd", "id - " + id);

		//corresponding server template ID
		serverID = "";
		
		if (id != "") {
			editing = true;
			rt rt = getRouteFromDB(id);

			if (rt!=null) {
				String str = rt.getStr();
				String dom = rt.getDom();
				String parad = rt.getParad();

				ed_n.setText(rt.getTitle());

				ed_n.setSelection(rt.getTitle().length());
				edit_d.setText(rt.getPrim());
				StreetId = rt.getStrid();
				HouseId = rt.getDomid();
				aut1.setAdapter(null);
				aut1.setText(str);
				aut1.setAdapter(stAdapter);
				//aut1.dismissDropDown();
				Log.d("1", "seted street - " + str);
				aut2.setText(dom);
				Log.d("1", "seted Dom - " + dom);
				aut3.setText(parad);
				Log.d("1", "seted parad - " + parad);

				Log.d("SPINNER", "w - "+Integer.parseInt(rt.getAutoClass()));
				spinner2.setSelection(Integer.parseInt(rt.getAutoClass()));


				String str2 = rt.getStr2();
				String dom2 = rt.getDom2();

				StreetId2 = rt.getStrid2();
				HouseId2 = rt.getDomid2();
				aut12.setAdapter(null);
				aut12.setText(str2);
				aut12.setAdapter(stAdapter2);
				//aut12.dismissDropDown();
				aut22.setText(dom2);
				ed_n.requestFocus();
			}
			
		} else {
			spinner2.setSelection(1);
		}

		ImageButton i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(TemplAddRoute.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i2 = (ImageButton) findViewById(R.id.it_2);
		i2.setPressed(true);
		ImageView iv2 = (ImageView) findViewById(R.id.it_2_img);
		iv2.setImageResource(R.drawable.fold_active);
		//t2.setTextColor(Color.rgb(0, 102, 255));
		t2.setTextColor(getResources().getColor(R.color.blue_WebCab));
		i2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(TemplAddRoute.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i3 = (ImageButton) findViewById(R.id.it_3);
		i3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(TemplAddRoute.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(TemplAddRoute.this, Settings.class);
				startActivity(intent);
				finish();
			}
		});

		bt_ok = (Button) findViewById(R.id.bt_ok);
		final String finalServerID = serverID;
		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

                if (aut22.getAdapter() != null) {
                    house st = (house) aut22.getAdapter().getItem(0);
                    aut22.setText(st.getValue());
                    HouseId2 = st.getId();
                }

				if (!ed_n.getText().toString().equals("")
						&& !aut1.getText().toString().equals("")
						&& !aut2.getText().toString().equals("")
						&& !StreetId.equals("")
						&& !HouseId.equals("")
						&& !StreetId2.equals("")
						&& !HouseId2.equals("")
						&& !aut12.getText().equals("")
						&& !aut22.getText().equals("")
						&& (!aut3.getText().toString().equals("") || !aut4.getText().toString().equals(""))) {

					dbHelper = new DBRoutes(TemplAddRoute.this);
					// подключаемся к БД
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					
					ContentValues cv = new ContentValues();

					cv.put("title", ed_n.getText().toString());
					cv.put("desc", "Киев, " + aut1.getText().toString() + ", "
							+ aut2.getText().toString());
					cv.put("str", aut1.getText().toString());
					cv.put("strid", StreetId);
					cv.put("dom", aut2.getText().toString());
					cv.put("domid", HouseId);
					
					Log.d("spinner", spinner2.getSelectedItemPosition()+"");
					cv.put("auto", spinner2.getSelectedItemPosition()+"");
					
					cv.put("desc2", "Киев, " + aut12.getText().toString() + ", "
							+ aut22.getText().toString());
					cv.put("str2", aut12.getText().toString());
					cv.put("strid2", StreetId2);
					cv.put("dom2", aut22.getText().toString());
					cv.put("domid2", HouseId2);
					
					
					if (!aut3.getText().equals("")) {
						cv.put("parad", aut3.getText().toString());
					}
                    if (!aut4.getText().equals("")) {
                        cv.put(dbHelper.ROUTES_FLAT, aut4.getText().toString());
                    }
					if (!edit_d.getText().equals("")) {
						cv.put("prim", edit_d.getText().toString());
					}

					// вставляем запись и получаем ее ID
					
					if (editing) {
						int rows = db.update("route", cv, "id="+id, null);
						cv.put("city", spinner.getSelectedItem().toString());
						cv.put("serverID", finalServerID);
						TemplatesSyncHelper templatesSyncHelper =
								new TemplatesSyncHelper(TemplAddRoute.this, TemplatesSyncHelper.UPDATE, cv, TemplatesSyncHelper.ROUTE);
						templatesSyncHelper.execute();
						Log.d("TemplAddRoute", "id - " + id + " - row updated - " + rows);
					} else {
						long rowID = db.insert("route", null, cv);
						cv.put("city", spinner.getSelectedItem().toString());
						TemplatesSyncHelper templatesSyncHelper =
								new TemplatesSyncHelper(TemplAddRoute.this, TemplatesSyncHelper.ADD, cv, TemplatesSyncHelper.ROUTE);
						templatesSyncHelper.execute();
						Log.d("TemplAddRoute", "row inserted, ID = " + rowID);
					}
					dbHelper.close();

					Intent intent = new Intent(TemplAddRoute.this,
							Templates.class);
					startActivity(intent);
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
		bt_c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				aut1.setText("");
				aut2.setText("");
				aut3.setText("");

				Intent intent = new Intent(TemplAddRoute.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});

	}

	private rt getRouteFromDB(String id) {

		rt rt = null;

		dbHelper = new DBRoutes(TemplAddRoute.this);
		// подключаемся к БД
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor c = db.query("route", null, "id=" + id, null, null, null,
				null);

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
            int flatColIndex = c.getColumnIndex(dbHelper.ROUTES_FLAT);
			int primColIndex = c.getColumnIndex("prim");

			int descColIndex2 = c.getColumnIndex("desc2");
			int strColIndex2 = c.getColumnIndex("str2");
			int stridColIndex2 = c.getColumnIndex("strid2");
			int domColIndex2 = c.getColumnIndex("dom2");
			int domidColIndex2 = c.getColumnIndex("domid2");

			int autoColIndex = c.getColumnIndex("auto");

			Log.d("SPINER SPINER SPINE", c.getString(autoColIndex) + " " + Integer.parseInt(c.getString(autoColIndex)));
			spinner2.setSelection(3);


			rt = new rt(c.getInt(idColIndex),
					c.getString(titleColIndex), c.getString(descColIndex),
					c.getString(strColIndex), c.getString(stridColIndex),
					c.getString(domColIndex), c.getString(domidColIndex),
					c.getString(paradColIndex), c.getString(primColIndex),
					c.getString(descColIndex2),
					c.getString(strColIndex2), c.getString(stridColIndex2),
					c.getString(domColIndex2), c.getString(domidColIndex2), c.getString(autoColIndex));
			rt.setServerTemplateID(c.getString(c.getColumnIndex(dbHelper.SERVER_TEMPLATE_ID)));
            rt.setFlat(c.getString(flatColIndex));
			serverID = rt.getServerTemplateID();
		} else {
			Log.d("MA", "0 rows");
		}
		return rt;
	}

	private void saveStreet2(street st) {
        if (st != null) {
            aut12.setText(st.getName());

            StreetId2 = st.getId();

            hAdapter = new HomeAdapter(TemplAddRoute.this,
                    R.layout.street_item, R.id.txt_year, StreetId2);
            aut22.setAdapter(hAdapter);
            aut22.setEnabled(true);
            aut22.setText("");
            aut22.requestFocus();
        }
    }

    private void saveHouse(house st) {
        if (st != null) {
            aut2.setText(st.getValue());

            HouseId = st.getId();

            aut3.setText("");
            aut3.setEnabled(true);
            aut3.requestFocus();
        }
    }

    private void saveStreet(street st) {
		if (st != null) {
			aut1.setText(st.getName());

			StreetId = st.getId();

			Log.d("streetid", "streetId - " + StreetId);
			hAdapter = new HomeAdapter(TemplAddRoute.this,
					R.layout.street_item, R.id.txt_year, StreetId);
			aut2.setAdapter(hAdapter);
			aut2.setEnabled(true);
			aut2.setText("");
			aut2.requestFocus();
		}
	}

	private void setUpFonts(Typeface font) {

        ((TextView) findViewById(R.id.txt_year23)).setTypeface(font);
        ((TextView) findViewById(R.id.textView_city1)).setTypeface(font);
        ((TextView) findViewById(R.id.textView_city)).setTypeface(font);
        ((TextView) findViewById(R.id.street_txt)).setTypeface(font);
        ((TextView) findViewById(R.id.house_txt)).setTypeface(font);
        ((TextView) findViewById(R.id.TextView02)).setTypeface(font);
        ((TextView) findViewById(R.id.txt_color)).setTypeface(font);
        ((TextView) findViewById(R.id.txt_year)).setTypeface(font);
        ((TextView) findViewById(R.id.TextView01)).setTypeface(font);

        ((Button) findViewById(R.id.bt_cancel)).setTypeface(font);
        ((Button) findViewById(R.id.bt_ok)).setTypeface(font);
        ((Button) findViewById(R.id.button3)).setTypeface(font);

        ((EditText) findViewById(R.id.ed_name)).setTypeface(font);
        ((EditText) findViewById(R.id.edit_d)).setTypeface(font);

        ((AutoCompleteLoadding) findViewById(R.id.aut1)).setTypeface(font);
        ((AutoCompleteLoadding) findViewById(R.id.aut2)).setTypeface(font);
        ((AutoCompleteLoadding) findViewById(R.id.aut12)).setTypeface(font);
        ((AutoCompleteLoadding) findViewById(R.id.aut22)).setTypeface(font);

        ((AutoCompleteTextView) findViewById(R.id.aut3)).setTypeface(font);
        ((AutoCompleteTextView) findViewById(R.id.aut4)).setTypeface(font);

		((TextView) findViewById(R.id.TextView03)).setTypeface(font);
		((TextView) findViewById(R.id.TextView06)).setTypeface(font);
	}

	/*
	@Override
	public void onBackPressed() {
		
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					TemplAddRoute.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							TemplAddRoute.super.onBackPressed();
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
