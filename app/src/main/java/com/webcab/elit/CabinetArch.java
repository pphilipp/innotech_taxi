package com.webcab.elit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.webcab.elit.adapters.ArchiveAdapter;
import com.webcab.elit.data.order_s;
import com.webcab.elit.net.ServiceConnection;

import java.util.Collections;
import java.util.List;

public class CabinetArch extends Activity {

    TextView t1, t2, t3, t4, t5, t6;
    SharedPreferences mSettings;
    ListView lv;
    List<order_s> ord;
    ArchiveAdapter adapter;
    public static final int BY_DATE = 1;
    public static final int BY_FROM = 2;
    public static final int BY_TO = 3;

    TextView txt_year;
    TextView textView3;
    TextView txt_color;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cabinet_arch);

        mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        Typeface font1 = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

		/**/
        t1 = (TextView) findViewById(R.id.txt_t1);
        t1.setTypeface(font1);
		t2 = (TextView) findViewById(R.id.txt_t2);
		t2.setTypeface(font1);
		t3 = (TextView) findViewById(R.id.txt_t3);
		t3.setTypeface(font1);
		t4 = (TextView) findViewById(R.id.txt_t4);
		t4.setTypeface(font1);
        Button mBtn = (Button) findViewById(R.id.but_add);
        mBtn.setTypeface(font1);

        txt_year = (TextView) findViewById(R.id.txt_year);
        txt_year.setTypeface(font1);
        txt_year.setBackgroundResource(R.drawable.top_bar_grad_active);

        txt_color = (TextView) findViewById(R.id.txt_color);
        txt_color.setTypeface(font1);
        txt_color.setBackgroundResource(R.drawable.top_bar_grad_inactive);

        textView3 = (TextView) findViewById(R.id.textView3);
        textView3.setTypeface(font1);
        textView3.setBackgroundResource(R.drawable.top_bar_grad_inactive);


		ImageButton i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // ""
                Intent intent = new Intent(CabinetArch.this, HomeScreen.class);
                startActivity(intent);
                finish();
            }
        });

		ImageButton i2 = (ImageButton) findViewById(R.id.it_2);
		i2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // ""
                Intent intent = new Intent(CabinetArch.this, Templates.class);
                startActivity(intent);
                finish();
            }
        });
		

		ImageButton i3 = (ImageButton) findViewById(R.id.it_3);
		i3.setPressed(true);
		ImageView iv3 = (ImageView) findViewById(R.id.it_3_img);
		iv3.setImageResource(R.drawable.cab_active);
		t3.setTextColor(Color.rgb(0, 102, 255));
		i3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(CabinetArch.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // ""
                Intent intent = new Intent(CabinetArch.this, Settings.class);
                startActivity(intent);
                finish();
            }
        });
		/**/

        Button btnBack = (Button) findViewById(R.id.bt_ok);
        btnBack.setTypeface(font1);
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CabinetArch.this, Cabinet.class);
                startActivity(intent);
                finish();
            }
        });

        lv = (ListView) findViewById(R.id.listView1);


        Thread th = new Thread(new Runnable() {

            @Override
            public void run() {
                ServiceConnection sc = new ServiceConnection(
                        getApplicationContext());

                final String phone = mSettings.getString("Phone", "");
                ord = sc.loadHistory(phone);
                Collections.sort(ord);

                if (ord.size() == 0) {
                    Log.d("Cabinet", "empty");

                    runOnUiThread(new Runnable() {
                        public void run() {
                            TextView tv = (TextView) findViewById(R.id.no_arch);
                            tv.setVisibility(View.VISIBLE);
                            tv.setText("Архив пуст");
                        }
                    });
                } else {
                    Log.d("Cabinet", "arch - " + ord.size());

                    adapter = new ArchiveAdapter(CabinetArch.this, ord);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            lv.setAdapter(adapter);
                        }
                    });
                }

            }
        });

        th.start();

        setUpSorting(BY_DATE);

        txt_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSorting(BY_DATE);
            }
        });

        txt_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSorting(BY_FROM);
            }
        });
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSorting(BY_TO);
            }
        });
    }

    private void setUpSorting (int sortBy) {
        switch (sortBy) {
            case BY_DATE:
                txt_year.setBackgroundResource(R.drawable.top_bar_grad_active);
                txt_color.setBackgroundResource(R.drawable.top_bar_grad_inactive);
                textView3.setBackgroundResource(R.drawable.top_bar_grad_inactive);
                if (ord != null && ord.size() > 1)
                    Collections.sort(ord);
                break;
            case BY_FROM:
                txt_year.setBackgroundResource(R.drawable.top_bar_grad_inactive);
                txt_color.setBackgroundResource(R.drawable.top_bar_grad_active);
                textView3.setBackgroundResource(R.drawable.top_bar_grad_inactive);
                if (ord != null && ord.size() > 1)
                    Collections.sort(ord, order_s.orderFromComparator);
                break;
            case BY_TO:
                txt_year.setBackgroundResource(R.drawable.top_bar_grad_inactive);
                txt_color.setBackgroundResource(R.drawable.top_bar_grad_inactive);
                textView3.setBackgroundResource(R.drawable.top_bar_grad_active);
                if (ord != null && ord.size() > 1)
                    Collections.sort(ord, order_s.orderToComparator);
                break;
        }
        if (adapter != null)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.templates, menu);
        return true;
    }
}
