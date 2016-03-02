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
import com.webcab.elit.data.addr;
import com.webcab.elit.data.house;
import com.webcab.elit.data.street;

import java.util.ArrayList;
import java.util.List;

public class TemplAddAddr extends Activity implements TextWatcher {

	TextView t1, t2, t3, t4, tc;
	Button bt_ok, bt_c;
	EditText aut3, edit_d, ed_n, aut4;

	AutoCompleteLoadding aut1, aut2;
	HomeAdapter hAdapter;
	ContextThemeWrapper themedContext;

	DBPoints dbHelper;
	// final String[] mBuildings = { "2", "2А", "2Б", "21/10", "23", "21", "20",
	// "24", "21В", "29Г" };
	String[] data = { "Киев" };

	String StreetId = "", HouseId = "";
	String id = "";
	boolean editing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.templ_adr);

		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			themedContext = new ContextThemeWrapper( TemplAddAddr.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
		}
		else {
			themedContext = new ContextThemeWrapper( TemplAddAddr.this, android.R.style.Theme_Light_NoTitleBar );
		}

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

		//setUp all fonts
		setUpfonts(font1);
		
		ProgressBar pr_str = (ProgressBar) findViewById(R.id.pr_str);
		ProgressBar pr_dom = (ProgressBar) findViewById(R.id.pr_dom);

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

		//when street is chosen
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

        //when nothing is chosen and focus is lost, store first row of adapter
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
                    saveHome(st);

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
                saveHome(st);

			}
		});

        aut2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && HouseId == null || (HouseId != null && HouseId.equals(""))) {
                    house st = (house) aut2.getAdapter().getItem(0);
                    saveHome(st);
                }
                if (hasFocus && HouseId != null) {
                    HouseId = "";
                }
            }
        });


		aut3 = (EditText) findViewById(R.id.aut3);
		aut3.setEnabled(true);
		aut4 = (EditText) findViewById(R.id.aut4);

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
		String serverID = "";
		
		if (id != "") {
			editing = true;
			List<addr> rt = new ArrayList<addr>();

			dbHelper = new DBPoints(TemplAddAddr.this);
			// подключаемся к БД
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			Cursor c = db.query("addr", null, "id=" + id, null, null, null,
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
				int primColIndex = c.getColumnIndex("prim");
				serverID = c.getString(c.getColumnIndex(dbHelper.SERVER_TEMPLATE_ID));

				do {
					addr currentAddress = new addr(c.getInt(idColIndex), c
							.getString(titleColIndex), c
							.getString(descColIndex), c.getString(strColIndex),
							c.getString(stridColIndex), c
							.getString(domColIndex), c
							.getString(domidColIndex), c
							.getString(paradColIndex), c
							.getString(primColIndex));
					currentAddress.setServerTemplateID(serverID);
					rt.add(currentAddress);

				} while (c.moveToNext());
			} else {
				Log.d("MA", "0 rows");
			}

			Log.d("MA", c.getCount() + " rows geted");

			String str = rt.get(0).getStr();
			String dom = rt.get(0).getDom();
			String parad = rt.get(0).getParad();

			ed_n.setText(rt.get(0).getTitle());
			edit_d.setText(rt.get(0).getPrim());
			StreetId = rt.get(0).getStrid();
			HouseId = rt.get(0).getDomid();
			aut1.setText(str);
			Log.d("shared", "seted street - " + str);
			aut2.setText(dom);
			Log.d("shared", "seted Dom - " + dom);
			aut3.setText(parad);
			Log.d("shared", "seted parad - " + parad);
		}

		ImageButton i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(TemplAddAddr.this, HomeScreen.class);
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
				Intent intent = new Intent(TemplAddAddr.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i3 = (ImageButton) findViewById(R.id.it_3);
		i3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(TemplAddAddr.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(TemplAddAddr.this, Settings.class);
				startActivity(intent);
				finish();
			}
		});

		final Spinner spinner = (Spinner) findViewById(R.id.spinner1);

		bt_ok = (Button) findViewById(R.id.bt_ok);
		final String finalServerID = serverID;
		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				if ((!ed_n.getText().toString().equals(""))
						&& (!aut1.getText().toString().equals(""))
						&& (!aut2.getText().toString().equals(""))
						&& (!StreetId.equals("")) && (!HouseId.equals(""))
                        && (!aut4.getText().toString().equals("") || !aut3.getText().toString().equals(""))) {

					ContentValues cv = new ContentValues();

					dbHelper = new DBPoints(TemplAddAddr.this);
					// подключаемся к БД
					SQLiteDatabase db = dbHelper.getWritableDatabase();

					cv.put("title", ed_n.getText().toString());
					cv.put("desc", "Киев, " + aut1.getText().toString() + ", "
							+ aut2.getText().toString());
					cv.put("str", aut1.getText().toString());
					cv.put("strid", StreetId);
					cv.put("dom", aut2.getText().toString());
					cv.put("domid", HouseId);
					cv.put("parad", aut3.getText().toString());
					cv.put("prim", edit_d.getText().toString());
                    //TODO modify BD, add flat
					// вставляем запись и получаем ее ID

					if (editing) {
						int rows = db.update("addr", cv, "id="+id, null);
						cv.put("city", spinner.getSelectedItem().toString());
						cv.put("serverID", finalServerID);
						TemplatesSyncHelper templatesSyncHelper = new TemplatesSyncHelper(TemplAddAddr.this,
								TemplatesSyncHelper.UPDATE, cv, TemplatesSyncHelper.ADDRESS);
						templatesSyncHelper.execute();
						Log.d("mainActivity", "id - " + id + " - row updated - " + rows);
					} else {
						long rowID = db.insert("addr", null, cv);
						cv.put("city", spinner.getSelectedItem().toString());
						TemplatesSyncHelper templatesSyncHelper = new TemplatesSyncHelper(TemplAddAddr.this,
								TemplatesSyncHelper.ADD, cv, TemplatesSyncHelper.ADDRESS);
						templatesSyncHelper.execute();
						Log.d("mainActivity", "row inserted, ID = " + rowID);
					}

					dbHelper.close();

					Intent intent = new Intent(TemplAddAddr.this,
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

				Intent intent = new Intent(TemplAddAddr.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});

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

	}

    private void saveHome(house st) {
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
            hAdapter = new HomeAdapter(TemplAddAddr.this,
                    R.layout.street_item, R.id.txt_year, StreetId);
            aut2.setAdapter(hAdapter);
            aut2.setEnabled(true);
            aut2.setText("");
            aut2.requestFocus();
        }
	}

	private void setUpfonts(Typeface font) {
		/*
		* Spinner spinner1
		* */
		((Button) findViewById(R.id.button3)).setTypeface(font);
		((TextView) findViewById(R.id.txt_year)).setTypeface(font);
		((TextView) findViewById(R.id.txt_year1)).setTypeface(font);
		((TextView) findViewById(R.id.TextView01)).setTypeface(font);
		((TextView) findViewById(R.id.TextView02)).setTypeface(font);
		((TextView) findViewById(R.id.txt_color)).setTypeface(font);
		((EditText) findViewById(R.id.ed_name)).setTypeface(font);
		((TextView) findViewById(R.id.textView_city)).setTypeface(font);
		((EditText) findViewById(R.id.edit_d)).setTypeface(font);
		((AutoCompleteLoadding) findViewById(R.id.aut1)).setTypeface(font);
		((AutoCompleteLoadding) findViewById(R.id.aut2)).setTypeface(font);
		((AutoCompleteTextView) findViewById(R.id.aut3)).setTypeface(font);
		((AutoCompleteTextView) findViewById(R.id.aut4)).setTypeface(font);
		((Button) findViewById(R.id.bt_cancel)).setTypeface(font);
		((Button) findViewById(R.id.bt_ok)).setTypeface(font);
		((TextView) findViewById(R.id.TextView03)).setTypeface(font);
		((TextView) findViewById(R.id.TextView06)).setTypeface(font);
	}

	/*
	@Override
	public void onBackPressed() {
		
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					TemplAddAddr.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							TemplAddAddr.super.onBackPressed();
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
