package com.webcab.elit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.ProgressBar;

import com.webcab.elit.Utils.Constants;
import com.webcab.elit.Utils.Utilits;
import com.webcab.elit.net.ServiceConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Progress extends Activity {

    private static final String TAG = "PROGRESS_TAG";
    private SharedPreferences mSettings;
    private SharedPreferences userSettings;

    private static final int GET_AUTOSEARCH_TIME = 100;
    private static final int GET_PREORDER_TIME = 200;
    private static final int SYNC_USER_INFO = 300;
    private static final int SAVE_USER_INFO = 400;

    private SharedPreferences.Editor temp;
    private ServiceConnection sc;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GET_AUTOSEARCH_TIME:
                    (new GetAutoSearchTime()).execute();
                    break;
                case GET_PREORDER_TIME:
                    (new GetPreOrderTime()).execute();
                    break;
                case SYNC_USER_INFO:
                    (new GetUserInfo()).execute();
                    break;
                case SAVE_USER_INFO:
                    (new SaveUserInfoToServer()).execute();
                    break;
            }
        }
    };

    private class GetUserInfo extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String phone = mSettings.getString("Phone", "");
            Log.d(TAG, "wanna info with tel = " + phone);
            if (phone != null && !phone.equals("")) {
                return sc.getUserInfo(phone);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && !s.equals("")) {
                Log.d(TAG, "userInfo response = " + s);
                saveUserInfoFromServer(s);
            }
            temp.commit();
        }
    }

    private void saveUserInfoFromServer(String s) {
        /*
        [{"id":1728,"varFirstName":"","varLastName":"","varSurName":"","varTel":"+380506350414","varTel2":"","varEmail":""}]
         */
        String idFromServer = "";
        String firstNameFromServer = "";
        String lastNameFromServer = "";
        String surnameFromServer = "";
        String telFromServer = "";
        String emailFromServer = "";

        String firstNameLocal = userSettings.getString("Name", "");
        String lastNameLocal = userSettings.getString("Patronymic", "");
        String surnameLocal = userSettings.getString("Surname", "");
        String telLocal = userSettings.getString("uPhone", "");
        String emailLocal = userSettings.getString("Email", "");

        //get server data from JSON
        try {
            JSONArray resultJSON = new JSONArray(s);
            JSONObject clientInfo = resultJSON.getJSONObject(0);
            idFromServer = clientInfo.optString("id");
            firstNameFromServer = clientInfo.optString("varFirstName");
            lastNameFromServer = clientInfo.optString("varLastName");
            surnameFromServer = clientInfo.optString("varSurName");
            telFromServer = clientInfo.optString("varTel2");
            emailFromServer = clientInfo.optString("varEmail");
        } catch (JSONException e) {
            Log.d(TAG, "JSON error = " + e.getMessage());
            e.printStackTrace();
        }

        Boolean backSync = false;

        SharedPreferences.Editor userData = userSettings.edit();

        //update fields
        //name
        if (!firstNameFromServer.equals("") && !firstNameFromServer.equals(firstNameLocal)) {
            //rewrite local value with server one
            userData.putString("Name", firstNameFromServer);
        }
        Log.d(TAG, "Sync check: serverName = " + firstNameFromServer + ", localName = " + firstNameLocal);
        if (firstNameFromServer.equals("") && !firstNameLocal.equals("")) {
            //update server if there is local value
            backSync = true;
        }
        //lastName
        if (!lastNameFromServer.equals("") && !lastNameFromServer.equals(lastNameLocal)) {
            //rewrite local value with server one
            userData.putString("Patronymic", lastNameFromServer);
        }
        if (lastNameFromServer.equals("") && !lastNameLocal.equals("")) {
            //update server if there is local value
            backSync = true;
        }
        //surname
        if (!surnameFromServer.equals("") && !surnameFromServer.equals(surnameLocal)) {
            //rewrite local value with server one
            userData.putString("Surname", surnameFromServer);
        }
        if (surnameFromServer.equals("") && !surnameLocal.equals("")) {
            //update server if there is local value
            backSync = true;
        }
        //email
        if (!emailFromServer.equals("") && !emailFromServer.equals(emailLocal)) {
            //rewrite local value with server one
            userData.putString("Email", emailFromServer);
        }
        if (emailFromServer.equals("") && !emailLocal.equals("")) {
            //update server if there is local value
            backSync = true;
        }
        //TODO sync tel2?
        userData.commit();
        if (backSync) {
            handler.sendEmptyMessage(SAVE_USER_INFO);
        }

    }

    private class SaveUserInfoToServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "wanna save user data");
            sc.saveUserInfo();
            return null;
        }
    }

    private class GetPreOrderTime extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            long preOrderTime = sc.getPreorderTime();
            temp.putLong("preorderTime", preOrderTime);
            Log.d(TAG, "preOrderTime time = " + preOrderTime);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            handler.sendEmptyMessage(SYNC_USER_INFO);
        }
    }

    private class GetAutoSearchTime extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String time = sc.getTimeAutoSearch();
            temp.putString("autoSearchTime", time);
            Log.d(TAG, "search time = " + time);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            handler.sendEmptyMessage(GET_PREORDER_TIME);
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);
        sc = new ServiceConnection(Progress.this);
		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        userSettings = getSharedPreferences("mydata", Context.MODE_PRIVATE);

        temp = mSettings.edit();

        handler.sendEmptyMessage(GET_AUTOSEARCH_TIME);


		final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
		pb.setProgress(0);


		
		final Bundle bundle = getIntent().getExtras();
		//bundle = null;
		if (bundle==null) {

			new Thread() {
				public void run() {
					try {

						for (int i = 0; i < 90; i++) {
							pb.setProgress(i);
							sleep(10);
						}

						for (int i = 90; i <= 100; i++) {
							pb.setProgress(i);
							sleep(30);
						}
					} catch (Exception e) {
                        //Mint.logException(e);
                    }
					
					String phone = mSettings.getString("Phone", "");
					String pass = mSettings.getString("Pass", "");
					String serv = mSettings.getString("Server", "");
					String sess = mSettings.getString("PHPSESSID", "");

					SharedPreferences.Editor editor = mSettings.edit();
					editor.clear();
					editor.commit();
					
					editor.putBoolean("Logged", true);
					editor.putString("Phone", phone);
					editor.putString("Pass", pass);
					editor.putString("PHPSESSID", sess);
					editor.putString("Server", serv);
					editor.commit();

//					editor.putBoolean("is3", false);
//					editor.putBoolean("is4", false);
//					editor.putBoolean("is5", false);

					editor.commit();

					Intent i = new Intent(Progress.this, HomeScreen.class);
					startActivity(i);
					finish();
				}
			}.start();
		} else if (bundle.getBoolean("Logged", false)) {
			Thread t = new Thread(new Runnable() {
				public void run() {

					String phone = mSettings.getString("Phone", "");
					String pass = mSettings.getString("Pass", "");
					String s = "";

					pb.setProgress(30);

					Utilits.saveDeviceInfo(Progress.this);
					
					ServiceConnection sc = new ServiceConnection(
							Progress.this);
					s = sc.checkCode(phone, pass);
					
					pb.setProgress(70);

					if (s.equals("success")) {
						Log.d("test", "go go go");

						String serv = mSettings.getString("Server", "");
						String sess = mSettings.getString("PHPSESSID", "");

						SharedPreferences.Editor editor = mSettings.edit();
						editor.clear();
						editor.commit();
						
						pb.setProgress(90);

						editor.putString("Phone", phone);
						editor.putString("Pass", pass);
						editor.putString("PHPSESSID", sess);
						editor.putString("Server", serv);
						editor.putBoolean("Logged", true);
						editor.commit();
						
						pb.setProgress(100);

                        Intent i = new Intent(Progress.this, HomeScreen.class);
                        if (bundle.containsKey(Constants.FORWARD)) {
                            Log.d(TAG, "Template go1");
                            switch (bundle.getInt(Constants.FORWARD)) {
                                case Constants.NEW_ROUTE_TEMPLATE:
                                    Log.d(TAG, "Template go");
                                    i = new Intent(Progress.this, TemplAddRoute.class);
                                    break;
                            }
                        }

                        if (bundle.containsKey("routeId")) {
                            i.putExtra("routeId", bundle.getInt("routeId"));
                        }
						startActivity(i);
						finish();
					} else {
						
						SharedPreferences.Editor editor = mSettings.edit();
						editor.putBoolean("Logged", false);
						editor.commit();
						
						Intent i = new Intent(Progress.this, MainActivity.class);
						startActivity(i);
						finish();
					}

				}
			});
			t.start();
		}
		
	}
	
	@Override
	public void onBackPressed() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_progress, menu);
		return true;
	}

}
