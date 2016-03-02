package com.webcab.elit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.webcab.elit.data.review;
import com.webcab.elit.net.ServiceConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompanyReview extends Activity {

	TextView t1, t2, t3, t4, t5, t6;
	SharedPreferences mSettings;
	ListView lv;
	EditText e1;
	ContextThemeWrapper themedContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company_review);

		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			themedContext = new ContextThemeWrapper( CompanyReview.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
		}
		else {
			themedContext = new ContextThemeWrapper( CompanyReview.this, android.R.style.Theme_Light_NoTitleBar );
		}

		e1 = (EditText) findViewById(R.id.editText1);

		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

        e1.setTypeface(font1);

		t1 = (TextView) findViewById(R.id.txt_t1);
		t1.setTypeface(font1);
		t2 = (TextView) findViewById(R.id.txt_t2);
		t2.setTypeface(font1);
		t3 = (TextView) findViewById(R.id.txt_t3);
		t3.setTypeface(font1);
		t4 = (TextView) findViewById(R.id.txt_t4);
		t4.setTypeface(font1);

		ImageButton i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CompanyReview.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i2 = (ImageButton) findViewById(R.id.it_2);
		i2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CompanyReview.this, Templates.class);
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
                Intent intent = new Intent(CompanyReview.this, Cabinet.class);
                startActivity(intent);
                finish();
            }
        });

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ""
                Intent intent = new Intent(CompanyReview.this, Settings.class);
                startActivity(intent);
                finish();
            }
        });

		Button bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_ok.setTypeface(font1);
		bt_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("lg", "e1 - " + e1.getText().toString());

                if (!e1.getText().toString().equals("")) {
                    Thread th = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            ServiceConnection sc = new ServiceConnection(
                                    getApplicationContext());

                            sc.sendReview(e1.getText().toString());
                            //sc.loadComments();

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        AlertDialog alertDialog = new AlertDialog.Builder(
                                                themedContext).create();
                                        alertDialog
                                                .setMessage("Спасибо за отзыв, нам он очень важен!");
                                        alertDialog
                                                .setButton(
                                                        "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which) {
                                                                dialog.dismiss();

                                                                Intent intent = new Intent(
                                                                        CompanyReview.this,
                                                                        CabinetArch.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });

                                        alertDialog.show();
                                    } catch (Exception e) {
                                        Log.d(Constants.CONTENT_DIRECTORY,
                                                "Show Dialog: "
                                                        + e.getMessage());
                                    }
                                }
                            });

                        }
                    });

                    th.start();
                } else {
                    try {
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                themedContext).create();
                        alertDialog.setMessage("Вы не написали отзыв!");
                        alertDialog.setButton("OK",
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

            }
        });

        ((Button) findViewById(R.id.but_add)).setTypeface(font1);

		Button bt_c = (Button) findViewById(R.id.bt_cancel);
		bt_c.setTypeface(font1);
		bt_c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CompanyReview.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});


		lv = (ListView) findViewById(R.id.listView1);

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ServiceConnection sc = new ServiceConnection(
						getApplicationContext());

				List<review> list = sc.loadComments();
				
				List<String> list2 = new ArrayList<String>();
				for (int i = 0; i<list.size();i++) {
					list2.add(list.get(i).getBodyComment());
				}
				
				final StableArrayAdapter adapter = new StableArrayAdapter(CompanyReview.this,
				        android.R.layout.simple_list_item_1, list2);
				
				runOnUiThread(new Runnable() {
					public void run() {
						lv.setAdapter(adapter);
					}
				});
				
				
			}
		}).start();
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }

		@Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }

	  }
/*
	@Override
	public void onBackPressed() {

		try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					CompanyReview.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					CompanyReview.super.onBackPressed();
				}
			});

			alertDialog.setButton2("Нет",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			alertDialog.show();
		} catch (Exception e) {
			Log.d(Constants.CONTENT_DIRECTORY, "Show Dialog: " + e.getMessage());
		}
	}
*/
}
