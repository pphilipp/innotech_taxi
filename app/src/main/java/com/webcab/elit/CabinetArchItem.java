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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.webcab.elit.data.GetFullOrderInfo;
import com.webcab.elit.data.order_s;
import com.webcab.elit.net.ServiceConnection;

import java.text.SimpleDateFormat;

public class CabinetArchItem extends Activity {

	TextView t1, t2, t3, t4, t5, t6;
	SharedPreferences mSettings;
	ListView lv;
	byte likeb = 0;
	ImageView like, dislike, middle, vote_face;
    LinearLayout voteLayout;
    Button editOrder;
    Button deleteOrder;
    Context mContext;
	
	int id, rate;
	String from, to, when, price, carInfo, comment;
    Boolean preorder;
    ContextThemeWrapper themedContext;

    private static final int BAD = 2;
    private static final int SO_SO = 3;
    private static final int GOOD = 5;
    //ServiceConnection mServiceConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cabinet_arch_item1);

        mContext = CabinetArchItem.this;
        final ServiceConnection mServiceConnection = new ServiceConnection(mContext);


        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            themedContext = new ContextThemeWrapper( mContext, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
        }
        else {
            themedContext = new ContextThemeWrapper( mContext, android.R.style.Theme_Light_NoTitleBar );
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		
		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

		Typeface font1 = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoCondensed-Regular.ttf");

		t1 = (TextView) findViewById(R.id.txt_t1);
		t1.setTypeface(font1);
		t2 = (TextView) findViewById(R.id.txt_t2);
		t2.setTypeface(font1);
		t3 = (TextView) findViewById(R.id.txt_t3);
		t3.setTypeface(font1);
		t4 = (TextView) findViewById(R.id.txt_t4);
		t4.setTypeface(font1);

		//header setUp
		ImageButton i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CabinetArchItem.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i2 = (ImageButton) findViewById(R.id.it_2);
		i2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CabinetArchItem.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});
		

		ImageButton i3 = (ImageButton) findViewById(R.id.it_3);
		i3.setPressed(true);
		i3.setImageResource(R.drawable.top_bar_grad_active);
		ImageView iv3 = (ImageView) findViewById(R.id.it_3_img);
		iv3.setImageResource(R.drawable.cab_active);
		//t3.setTextColor(Color.rgb(0, 102, 255));
		t3.setTextColor(getResources().getColor(R.color.blue_WebCab));
		i3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CabinetArchItem.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CabinetArchItem.this, Settings.class);
				startActivity(intent);
				finish();
			}
		});
		//end of header setUp




        //may be cause of mistake
        order_s curOrder = null;
		try {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
                curOrder = new order_s(
                        extras.getInt("id"),
                        extras.getInt("rate"),
                        extras.getString("from"),
                        extras.getString("to"),
                        extras.getString("date"),
                        extras.getString("price"),
                        extras.getBoolean("orderStatus"),
                        extras.getLong("serverTime"),
                        extras.getIntegerArrayList("additionalServices"),
                        extras.getInt("orderID"),
                        extras.getInt("carClass"));
                id = curOrder.getId();
                rate = curOrder.getRate();
                price = curOrder.getPrice();
                when = curOrder.getWhen();
                from = curOrder.getFrom();
                to = curOrder.getTo();
                preorder = curOrder.getPreorderStatus();
                //comment = curOrder.getComment();
			}
		} catch (NullPointerException e) {
			Log.d("NullPointerException", "" + e.getMessage());
		}

        final TextView auto_txt = (TextView) findViewById(R.id.auto_txt);
        final order_s finalCurOrder2 = curOrder;
        Thread n = new Thread(new Runnable() {
            @Override
            public void run() {
                carInfo = mServiceConnection.getCarInfo(finalCurOrder2.getId());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        auto_txt.setText(carInfo);
                    }
                });
            }
        });
        n.start();

        voteLayout = (LinearLayout) findViewById(R.id.vote_layout);
		
		TextView txt_date = (TextView) findViewById(R.id.txt_date);
		TextView txt_from = (TextView) findViewById(R.id.txt_from);
		TextView txt_to = (TextView) findViewById(R.id.txt_to);
		TextView txt_price = (TextView) findViewById(R.id.txt_price);

		txt_from.setTypeface(font1);
		txt_to.setTypeface(font1);
        txt_date.setTypeface(font1);
        txt_price.setTypeface(font1);
        auto_txt.setTypeface(font1);


		like = (ImageView) findViewById(R.id.vote_good);
		middle = (ImageView) findViewById(R.id.vote_so_so);
		dislike = (ImageView) findViewById(R.id.vote_bad);
		vote_face = (ImageView) findViewById(R.id.vote_face);

        LinearLayout autoLayout = (LinearLayout) findViewById(R.id.auto_ll);
        LinearLayout rateLayout = (LinearLayout) findViewById(R.id.rate_ll);
        if (preorder) {
            autoLayout.setVisibility(View.GONE);
            rateLayout.setVisibility(View.GONE);
        } else {
            autoLayout.setVisibility(View.VISIBLE);
            rateLayout.setVisibility(View.VISIBLE);
        }

        setUpVotesUI(rate, preorder);

        //there are vote 5,3,2
		like.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                likeb = 5;
                setUpVotesUI(likeb, preorder);
            }
		});

        middle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                likeb = 3;
                setUpVotesUI(likeb, preorder);
            }
        });

        dislike.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                likeb = 2;
                setUpVotesUI(likeb, preorder);
            }
        });


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  dd.MM.yyyy");
		txt_date.setText(sdf.format(curOrder.getWhenDate() * 1000));
		txt_from.setText(from);
        String toAddresses[] = to.split("->");
        if (toAddresses.length > 1)
            to = toAddresses[0] + "...";
		txt_to.setText(to);
		txt_price.setText(price);


		
		
		final EditText prim = (EditText) findViewById(R.id.editText1);
        prim.setTypeface(font1);

        prim.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                prim.setGravity(count == 0 ? Gravity.CENTER : Gravity.LEFT);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



		Button bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_ok.setTypeface(font1);
		bt_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Thread th = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        ServiceConnection sc = new ServiceConnection(
                                getApplicationContext());

                        if (likeb != 0) {
                            sc.saveRate(id, likeb);
                        }

                        if (!prim.getText().toString().equals("")) {
                            sc.saveComHistory(id, prim.getText().toString());
                        }

                        Intent intent = new Intent(CabinetArchItem.this, CabinetArch.class);
                        startActivity(intent);
                        finish();

                    }
                });

                th.start();
            }
        });
		
		Button bt_c = (Button) findViewById(R.id.bt_cancel);
        bt_c.setTypeface(font1);
		bt_c.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CabinetArchItem.this, CabinetArch.class);
                startActivity(intent);
                finish();
            }
        });

        editOrder = (Button) findViewById(R.id.edit_order_btn);
        deleteOrder = (Button) findViewById(R.id.delete_order_btn);
        editOrder.setTypeface(font1);
        deleteOrder.setTypeface(font1);

        editOrder.setVisibility(preorder ? View.VISIBLE : View.GONE);

        final order_s finalCurOrder1 = curOrder;
        deleteOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preorder) {
                    //TODO add comments if needed

                    final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(themedContext);
                    confirmDialog.setTitle("Внимание!");
                    confirmDialog.setMessage("Вы действительно хотите отменить предварительный заказ?");
                    confirmDialog.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Thread mThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    mServiceConnection.cancelPrelimOrder(String.valueOf(finalCurOrder1.getOrderID()));
                                }
                            });
                            mThread.start();
                            Intent intent = new Intent(CabinetArchItem.this, CabinetArch.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    confirmDialog.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    confirmDialog.show();

                } else {

                    final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(themedContext);
                    confirmDialog.setTitle("Внимание!");
                    confirmDialog.setMessage("Вы действительно хотите удалить заказ из истории?");
                    confirmDialog.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Thread mThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    mServiceConnection.deleteArchOrder(finalCurOrder1.getId());
                                }
                            });
                            mThread.start();
                            Intent intent = new Intent(CabinetArchItem.this, CabinetArch.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    confirmDialog.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    confirmDialog.show();

                }

            }
        });

        final order_s finalCurOrder = curOrder;
        editOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GetFullOrderInfo mGetFullOrderInfo = new GetFullOrderInfo(CabinetArchItem.this, finalCurOrder);
                mGetFullOrderInfo.execute();

            }
        });


        //setUp all fonts
        ((TextView) findViewById(R.id.data_text)).setTypeface(font1);
        ((TextView) findViewById(R.id.from_text)).setTypeface(font1);
        ((TextView) findViewById(R.id.where_text)).setTypeface(font1);
        ((TextView) findViewById(R.id.auto_text)).setTypeface(font1);
        ((TextView) findViewById(R.id.price_text)).setTypeface(font1);
        ((TextView) findViewById(R.id.rate_text)).setTypeface(font1);
        ((TextView) findViewById(R.id.rate_title)).setTypeface(font1);
        Button header = (Button) findViewById(R.id.but_add);
        header.setTypeface(font1);


        if (preorder) {
            bt_ok.setText("НАЗАД");
            bt_c.setVisibility(View.GONE);
            bt_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CabinetArchItem.this, CabinetArch.class);
                    startActivity(intent);
                    finish();
                }
            });

            deleteOrder.setVisibility(View.VISIBLE);
            header.setText("Предварительный заказ");
        } else {
            header.setText("Оценка заказа");
            if (rate != 0) {
                deleteOrder.setVisibility(View.VISIBLE);
            } else {
                deleteOrder.setVisibility(View.GONE);
            }
        }



	}

    private void setUpVotesUI(int rate, boolean preorder) {
        //if rate is orders mark then

        if (preorder) {
            voteLayout.setVisibility(View.GONE);
        } else if (rate == 0) {
            voteLayout.setVisibility(View.VISIBLE);
            vote_face.setVisibility(View.INVISIBLE);
        } else {
            voteLayout.setVisibility(View.GONE);
            switch (rate) {
                case BAD:
                    vote_face.setVisibility(View.VISIBLE);
                    vote_face.setImageResource(R.drawable.vote_bad_small);
                    break;
                case SO_SO:
                    vote_face.setVisibility(View.VISIBLE);
                    vote_face.setImageResource(R.drawable.vote_so_so_small);
                    break;
                case GOOD:
                    vote_face.setVisibility(View.VISIBLE);
                    vote_face.setImageResource(R.drawable.vote_good_small);
                    break;
            }
        }
    }
	/*
	@Override
	public void onBackPressed() {
		
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(
					CabinetArchItem.this).create();
			alertDialog.setMessage("Вы действительно хотите выйти?");
			alertDialog.setButton("Да",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							CabinetArchItem.super.onBackPressed();
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
		getMenuInflater().inflate(R.menu.templates, menu);
		return true;
	}
}
