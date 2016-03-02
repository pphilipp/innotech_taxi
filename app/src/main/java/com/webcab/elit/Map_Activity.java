package com.webcab.elit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.webcab.elit.data.addr2;
import com.webcab.elit.net.ServiceConnection;

//public class Map_Activity extends MapActivity {
public class Map_Activity extends FragmentActivity {

	private GoogleMap mMap;
	//MapController mc;
	LatLng p;
	SharedPreferences mSettings;
	ImageView pin;
	String wh;
	LocationManager locMan;
    TextView addressTV;

    Typeface font1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

        font1 = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");

        addressTV = (TextView) findViewById(R.id.address_tv);
        addressTV.setTypeface(font1);

		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.4331, 30.5128), 6));
			mMap.setMyLocationEnabled(true);

            // Get the button view
            View locationButton = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getView().findViewById(Integer.parseInt("2"));

            // and next place it, for exemple, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 30, 30);

            //			mMap.getUiSettings().setMyLocationButtonEnabled(true);
			//
			//			mMap.setMyLocationEnabled(true);

			// setupMapView();
		}



        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                p = cameraPosition.target;
                Thread getAddressThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ServiceConnection sc = new ServiceConnection(
                                getApplicationContext());
                        final addr2 curAddress = sc.getAddressFromGeo(String.valueOf(p.latitude), String.valueOf(p.longitude));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (curAddress != null
                                        && !curAddress.getSreetname().equals("")
                                        && !curAddress.getHousenumber().equals(""))
                                    addressTV.setText(curAddress.getSreetname() + ", " + curAddress.getHousenumber());
                                else
                                    addressTV.setText("Адрес не определен");
                            }
                        });
                    }
                });
                getAddressThread.start();
            }
        });


		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
		Bundle extras = getIntent().getExtras();
		changed=false;
		pin = (ImageView) findViewById(R.id.pin);
		if (extras.containsKey("wh")) {
			wh = extras.getString("wh");
			Log.d("test", "wh - " + wh);
			if (wh.equals("1")) {
				pin.setImageResource(R.drawable.flag_red);
			} else if (wh.equals("2")) {
				pin.setImageResource(R.drawable.flag_g);
			}
		}

		String[] coordinates = { "50.4331", "30.5128" };


		if (mSettings.contains("Lon" + wh) && mSettings.contains("Lat" + wh)) {
			coordinates[0] = mSettings.getString("Lat" + wh, "50.4331");
			coordinates[1] = mSettings.getString("Lon" + wh, "30.5128");
		}
		if (coordinates[0].equals("50.4331") && coordinates[1].equals("30.5128") ){
			locMan= (LocationManager) getSystemService(LOCATION_SERVICE);
			Criteria crit=new Criteria();
			final String provider = locMan.getBestProvider(crit, true);
			if (provider != null){
				locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListner);
				locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, lListner);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (!changed){
                            if(provider != null){
                                Location loc= locMan.getLastKnownLocation(provider);
                                if (loc==null)
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(Map_Activity.this, "Не удалось определить координаты", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                else
                                    makeCoords(new String[] {loc.getLatitude()+"",loc.getLongitude()+"" });
                                locMan.removeUpdates(lListner);
                            }
                            else {
                                Toast.makeText(Map_Activity.this, "Включите определение координат в настройках телефона", Toast.LENGTH_LONG).show();
                            }

						}
					}
				}, 5000L);
			}
			else{
                Toast.makeText(Map_Activity.this, "Включите определение координат в настройках телефона", Toast.LENGTH_LONG).show();
                makeCoords(coordinates);
			}
		}
		else
			makeCoords(coordinates);


        Button btn_cancel = (Button) findViewById(R.id.bt_cancel);
        btn_cancel.setTypeface(font1);
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button btn_ok = (Button) findViewById(R.id.bt_ok);
        btn_ok.setTypeface(font1);
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                p = mMap.getCameraPosition().target;
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("Lon" + wh, "" + p.longitude);
                editor.putString("Lat" + wh, "" + p.latitude);
                editor.commit();
                finish();
            }
        });

        ((Button) findViewById(R.id.handle1)).setTypeface(font1);


	}


    private void makeCoords(String[] coordinates) {
		final double lat = Double.parseDouble(coordinates[0]);
		final double lng = Double.parseDouble(coordinates[1]);

		p = new LatLng(lat, lng);

		mMap.moveCamera(CameraUpdateFactory.newLatLng(p));
		mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1500, null);

		Button bt = (Button) findViewById(R.id.bt_ok);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ""
				p = mMap.getCameraPosition().target;
				SharedPreferences.Editor editor = mSettings.edit();
				editor.putString("Lon" + wh, "" + p.longitude);
				editor.putString("Lat" + wh, "" + p.latitude);
				editor.commit();
				finish();
			}
		});
	}
	private boolean changed=false;
	LocationListener lListner=new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// ""

		}

		@Override
		public void onProviderEnabled(String provider) {
			// ""

		}

		@Override
		public void onProviderDisabled(String provider) {
			// ""

		}

		@Override
		public void onLocationChanged(Location location) {
			changed=true;
			makeCoords(new String[] { location.getLatitude()+"",location.getLongitude()+"" } );
			locMan.removeUpdates(lListner);
		}
	};
	
	/*
	private void setupMapView(){
		UiSettings settings = mMap.getUiSettings();        
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
				new CameraPosition(new LatLng(50.0, 30.5), 
						13.5f, 30f, 112.5f))); // zoom, tilt, bearing
		mMap.setTrafficEnabled(true);
		settings.setAllGesturesEnabled(true);
		settings.setCompassEnabled(true);
		settings.setMyLocationButtonEnabled(true);
		settings.setRotateGesturesEnabled(true);
		settings.setScrollGesturesEnabled(true);
		settings.setTiltGesturesEnabled(true);
		settings.setZoomControlsEnabled(true);
		settings.setZoomGesturesEnabled(true);
	}
*/
	
}
