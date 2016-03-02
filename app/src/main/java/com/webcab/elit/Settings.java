package com.webcab.elit;



import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Settings extends Activity {
	
	TextView t1, t2, t3, t4, t5, t6,version;
	SharedPreferences mSettings;
	CheckBox chkb;
	ImageButton i1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		Intent mIntent = new Intent(Settings.this, HomeScreen.class);
		mIntent.putExtra("showMap", true);
		startActivity(mIntent);
		//finish();

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
		
		t5 = (TextView) findViewById(R.id.txt_set);
		t5.setTypeface(font1);
		t6 = (TextView) findViewById(R.id.txt_about);
		t6.setTypeface(font1);
		
		version = (TextView) findViewById(R.id.txt_version);
		version.setTypeface(font1);
		
		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
		


        Button about= (Button)findViewById(R.id.bt_about);
        about.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_VIEW);
                // Try Google play
                in.setData(Uri.parse("market://details?id=com.webcab.elit"));
                if (!MyStartActivity(in)) {
                    // Market (Google play) app seems not installed, let's try to
                    // open a webbrowser
                    in.setData(Uri
                            .parse("https://play.google.com/store/apps/details?id=com.webcab.elit"));

                }
            }
        });



		i1 = (ImageButton) findViewById(R.id.it_1);
		i1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Settings.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i2 = (ImageButton) findViewById(R.id.it_2);
		i2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Settings.this, Templates.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i3 = (ImageButton) findViewById(R.id.it_3);
		i3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				Intent intent = new Intent(Settings.this, Cabinet.class);
				startActivity(intent);
				finish();
			}
		});

		ImageButton i4 = (ImageButton) findViewById(R.id.it_4);
		i4.setPressed(true);
		i4.setClickable(false);
		ImageView iv4 = (ImageView) findViewById(R.id.it_4_img);
		iv4.setImageResource(R.drawable.settings_active);
		t4.setTextColor(getResources().getColor(R.color.blue_WebCab));

		Button logout = (Button) findViewById(R.id.logout);
		logout.setTypeface(font1);
		logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = mSettings.edit();
				editor.clear();
				editor.commit();
				
				Intent intent = new Intent(Settings.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	
		try {
			PackageInfo pack=getPackageManager().getPackageInfo(getPackageName(), 0);
			version.setText(pack.versionName);
		} catch (NameNotFoundException e) {
			// ""
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		
		i1.performClick();
	}
    private boolean MyStartActivity(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

}
