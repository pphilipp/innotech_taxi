package com.webcab.elit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract.Constants;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.webcab.elit.Utils.Utilits;
import com.webcab.elit.adapters.CrashManager;
import com.webcab.elit.net.ServiceConnection;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends Activity {

    private static final String TAG = "MAIN_ACTIVITY_TAG";
    int gen = -1;
	SharedPreferences mSettings;
	String s;
    final public static int UKR = 0;
    final public static int RUS = 1;
    final public static int USA = 2;
    private int country = 0;
    private String countryISO = "ua";
    boolean isPhoneValid = false;
    EditText phoneNumber;
    private String userPhoneNumber;
    ContextThemeWrapper themedContext;

    private Intent progressIntent;



    static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());


        Crashlytics.setUserEmail(getEmail(this));
        new Thread(new CrashManager(getApplicationContext())).start();
		setContentView(R.layout.activity_main);

        //create context to show nice alert dialogs
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            themedContext = new ContextThemeWrapper( MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
        }
        else {
            themedContext = new ContextThemeWrapper( MainActivity.this, android.R.style.Theme_Light_NoTitleBar );
        }

		
		mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        progressIntent = new Intent(MainActivity.this, Progress.class);

        SharedPreferences mSettingsOrder = getSharedPreferences(Utilits.Constants.ORDER_SETTINGS_NAME, Context.MODE_PRIVATE);
        //mSettingsOrder.edit().clear().commit();
        if (getIntent() != null && getIntent().hasExtra("payLoad")) {
            mSettingsOrder.edit().putInt(Utilits.Constants.ORDER_TYPE, Utilits.Constants.NOTIFICATION_CURRENT_ORDER).commit();
        } else {
            mSettingsOrder.edit().putInt(Utilits.Constants.ORDER_TYPE, Utilits.Constants.SIMPLE_CURRENT_ORDER).commit();
        }


        //if device is not online - finish app
		if (!isOnline()) {
			try {
				AlertDialog alertDialog = new AlertDialog.Builder(
                        themedContext)
						.create();
				alertDialog
						.setMessage("Отсутствует интернет соединение. Попробуйте позже.");
				alertDialog
						.setButton(
								"OK",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int which) {
										finish();
									}
								});
				alertDialog.setCancelable(false);
				alertDialog.show();
			} catch (Exception e) {
                //Mint.logException(e);
				Log.d(Constants.CONTENT_DIRECTORY,
						"Show Dialog: "
								+ e.getMessage());
			}
		}

        //read/create gcmID
        if (isGoogelPlayInstalled()) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());

            // Read saved registration id from shared preferences.
            gcmRegId = getSharedPreferences().getString(PREF_GCM_REG_ID, "");
            Log.d("GCM_TEST", "id = " + gcmRegId);

            if (TextUtils.isEmpty(gcmRegId)) {
                handler.sendEmptyMessage(MSG_REGISTER_WITH_GCM);
            }else{
                //Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_SHORT).show();
            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    themedContext)
                    .create();
            alertDialog
                    .setMessage(getResources().getString(R.string.no_google_services));
            alertDialog
                    .setButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    finish();
                                }
                            });
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
		
		final ProgressDialog pd = new ProgressDialog(themedContext);
	    pd.setTitle("Загрузка данных");
	    pd.setMessage("Получение адреса сервера");
	    pd.setCancelable(false);
	    pd.show();
	    
	    new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				ServiceConnection sc = new ServiceConnection(MainActivity.this);
				sc.getServer();
				
				pd.dismiss();
				
				if ((mSettings.contains("Logged")) && mSettings.getBoolean("Logged", false)) {
					Log.d("shared", "Logged = true");
					
					Intent i = progressIntent;
					i.putExtra("Logged", true);
					startActivity(i);
					finish();

				}
			}
		}).start();
		


		final CharSequence[] gender = { "Ukraine (+380 XX XXX XX XX)",
				"Russia   (+7 XXX XXX XX XX)", "USA       (+1 XXX XXX XX XXX)" };

        Typeface font1 = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");
		final Button b3 = (Button) findViewById(R.id.button3);
		b3.setTypeface(font1);

		final TextView t1 = (TextView) findViewById(R.id.txt_year);
		t1.setTypeface(font1);
		final TextView t2 = (TextView) findViewById(R.id.txt_color);
		t2.setTypeface(font1);
		phoneNumber = (EditText) findViewById(R.id.editText1);
		phoneNumber.setTypeface(font2);

		final EditText code = (EditText) findViewById(R.id.editText2);
		code.setTypeface(font2);
		final Button getCode = (Button) findViewById(R.id.button_call);
		getCode.setTypeface(font1);
		final Button confirmCode = (Button) findViewById(R.id.button2);
		confirmCode.setTypeface(font1);
		final TextView txt_h = (TextView) findViewById(R.id.textHelp);
		txt_h.setText("+380 XX XXX XX XX " + "(" + "Ukraine" + ")");

		ImageButton hl = (ImageButton) findViewById(R.id.help);
		hl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder alert = new AlertDialog.Builder(
                        themedContext);
				alert.setTitle("Веберите страну вашего оператора");
				alert.setSingleChoiceItems(gender, gen,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								gen = which;

								if (gen == 0) {
									phoneNumber.setText("+380");
									txt_h.setText("+380 XX XXX XX XX"
											+ " (Ukraine)");
									phoneNumber.setSelection(phoneNumber.getText().length());
                                    country = UKR;
                                    dialog.dismiss();
								} else if (gen == 1) {
									phoneNumber.setText("+7");
									txt_h.setText("+7 XXX XXX XX XX"
											+ " (Russia)");
									phoneNumber.setSelection(phoneNumber.getText().length());
									country = RUS;
									dialog.dismiss();
								} else if (gen == 2) {
									phoneNumber.setText("+1");
									txt_h.setText("+1 XXX XXX XX XX"
											+ " (USA)");
									phoneNumber.setSelection(phoneNumber.getText().length());
									country = USA;
                                    dialog.dismiss();
								}
								phoneNumber.requestFocus();
							}
						});
				alert.show();
			}
		});

        TelephonyManager tMgr = null;
		try {
            tMgr = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);

            //get country ISO, "ua" is default value
            countryISO = (tMgr.getNetworkCountryIso() != null && !tMgr.getNetworkCountryIso().equals("")) ? tMgr.getNetworkCountryIso() : "ua";

            userPhoneNumber = getUserPhoneNumber(tMgr, countryISO);
            phoneNumber.setText(userPhoneNumber);

            Log.e("TAAAAAAG","telNomber = " + userPhoneNumber);

			phoneNumber.setSelection(phoneNumber.getText().length());
		} catch (Exception e) {
            countryISO = "ua";
            userPhoneNumber = "+38";
			Log.e("teleph", e.getMessage());
		}
		
		if (phoneNumber.getText().length() == 0) {
			if (mSettings.contains("Phone")) {
				phoneNumber.setText(mSettings.getString("Phone", ""));
			}
		}

		if (phoneNumber.getText().length() != 13) {
            getCode.setEnabled(false);
		}

		code.setEnabled(false);
        confirmCode.setEnabled(false);
        if (countryISO.equals("ua") && userPhoneNumber.length() == 13) {
            phoneNumber.setBackgroundResource(R.drawable.edittext_correct_border);
            isPhoneValid = true;
            getCode.setEnabled(true);
            code.setEnabled(true);
        }
        if (countryISO.equals("us") && userPhoneNumber.length() == 12) {
            phoneNumber.setBackgroundResource(R.drawable.edittext_correct_border);
            isPhoneValid = true;
            getCode.setEnabled(true);
            code.setEnabled(true);
        }
        if (countryISO.equals("ru") && userPhoneNumber.length() == 12) {
            phoneNumber.setBackgroundResource(R.drawable.edittext_correct_border);
            isPhoneValid = true;
            getCode.setEnabled(true);
            code.setEnabled(true);
        }
		phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (phoneNumber.getText().toString().length() > 0)
                    phoneNumber.setTextSize(24);

                Log.d(TAG, "country = " + country);
                country = getCountry(s);
                switch (country) {

                    case UKR:
                        if ((s.length() == 13 && s.charAt(0) == '+')
                                || (s.length() == 10 && s.charAt(0) != '+')) {
                            getCode.setEnabled(true);
                            code.setEnabled(true);
                            isPhoneValid = true;
                        } else {
                            code.setText("");
                            code.setEnabled(false);
                            getCode.setEnabled(false);
                            confirmCode.setEnabled(false);
                            isPhoneValid = false;
                        }
                        break;
                    case RUS:
                        if (s.length() == 12 && s.charAt(0) == '+') {
                            getCode.setEnabled(true);
                            code.setEnabled(true);
                            isPhoneValid = true;
                        } else {
                            code.setText("");
                            code.setEnabled(false);
                            getCode.setEnabled(false);
                            confirmCode.setEnabled(false);
                            isPhoneValid = false;
                        }
                        break;
                    case USA:
                        if (s.length() == 12 && s.charAt(0) == '+') {
                            getCode.setEnabled(true);
                            code.setEnabled(true);
                            isPhoneValid = true;
                        } else {
                            code.setText("");
                            code.setEnabled(false);
                            getCode.setEnabled(false);
                            confirmCode.setEnabled(false);
                            isPhoneValid = false;
                        }
                        break;

                }
                setUpEditText(s.toString());
                phoneNumber.setBackgroundResource(isPhoneValid ? R.drawable.edittext_correct_border : R.drawable.edittext_active_border);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (country) {
                    case UKR:
                        if ((s.length() == 13 && s.charAt(0) == '+')
                                || (s.length() == 10 && s.charAt(0) != '+')) {
                            getCode.setEnabled(true);
                            isPhoneValid = true;
                        } else {
                            getCode.setEnabled(false);
                            confirmCode.setEnabled(false);
                            isPhoneValid = false;
                        }
                        break;
                    case RUS:
                        if (s.length() == 12 && s.charAt(0) == '+') {
                            getCode.setEnabled(true);
                            isPhoneValid = true;
                        } else {
                            getCode.setEnabled(false);
                            confirmCode.setEnabled(false);
                            isPhoneValid = false;
                        }
                        break;
                    case 2:
                        if (s.length() == 12 && s.charAt(0) == '+') {
                            getCode.setEnabled(true);
                            isPhoneValid = true;
                        } else {
                            getCode.setEnabled(false);
                            confirmCode.setEnabled(false);
                            isPhoneValid = false;
                        }
                        break;
                }
                setUpEditText(s.toString());
            }
        });

		code.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if ((code.getText().toString().length() == 4)
                        && isPhoneValid) {
                    confirmCode.setEnabled(true);
                } else
                    confirmCode.setEnabled(false);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if ((code.getText().toString().length() == 4)
                        && isPhoneValid) {
                    confirmCode.setEnabled(true);
                } else
                    confirmCode.setEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable s) {
                // ""
                if ((code.getText().toString().length() == 4)
                        && isPhoneValid) {
                    confirmCode.setEnabled(true);
                } else
                    confirmCode.setEnabled(false);
            }
        });

		getCode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isPhoneValid) {
                    code.setText("");

                    final String numb;
                    if (country == UKR && phoneNumber.getText().length() == 10) {
                        numb = "+38".concat(phoneNumber.getText().toString());
                    } else {
                        numb = phoneNumber.getText().toString();
                    }
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("Phone", phoneNumber.getText().toString());
                    editor.commit();

                    Thread t = new Thread(new Runnable() {
                        public void run() {


                            ServiceConnection sc = new ServiceConnection(MainActivity.this);
                            s = sc.getCode(numb);

                            Log.d("getcode", "res - " + s);

                            if (s.equals("success")) {

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            AlertDialog alertDialog = new AlertDialog.Builder(
                                                    themedContext)
                                                    .create();
                                            alertDialog
                                                    .setMessage("Сейчас система отправит на Ваш телефон СМС-сообщение с кодом для авторизации");
                                            alertDialog
                                                    .setButton(
                                                            "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    code.setEnabled(true);
                                                                    code.requestFocus();
                                                                }
                                                            });
                                            alertDialog.setCancelable(false);
                                            alertDialog.show();
                                        } catch (Exception e) {
                                            Log.d(Constants.CONTENT_DIRECTORY,
                                                    "Show Dialog: "
                                                            + e.getMessage());
                                        }
                                    }
                                });
                            } else if (s.equals("block")) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            AlertDialog alertDialog = new AlertDialog.Builder(
                                                    themedContext)
                                                    .create();
                                            alertDialog
                                                    //.setMessage("Ваш номер временно заблокирован.");
                                                    .setMessage(getResources().getString(R.string.user_ban));
                                            alertDialog
                                                    .setButton(
                                                            "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    // code.setEnabled(true);
                                                                    // code.requestFocus();
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
                            } else if (s.equals("limitSendSMS")) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            AlertDialog alertDialog = new AlertDialog.Builder(
                                                    themedContext)
                                                    .create();
                                            alertDialog
                                                    .setMessage("Лимит отсылки смс сообщений на один номер достигнут.");
                                            alertDialog
                                                    .setButton(
                                                            "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    // code.setEnabled(true);
                                                                    // code.requestFocus();
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
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            AlertDialog alertDialog = new AlertDialog.Builder(
                                                    themedContext)
                                                    .create();
                                            alertDialog
                                                    .setMessage("Произошла ошибка, попробуйте еще раз.");
                                            alertDialog
                                                    .setButton(
                                                            "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    // code.setEnabled(true);
                                                                    // code.requestFocus();
                                                                }
                                                            });

                                            alertDialog.show();
                                        } catch (Exception e) {
                                            //Mint.logException(e);
                                            Log.d(Constants.CONTENT_DIRECTORY,
                                                    "Show Dialog: "
                                                            + e.getMessage());
                                        }
                                    }
                                });
                            }
                        }
                    });
                    t.start();
                }

            }
        });

		confirmCode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if ((code.getText().toString().length() == 4)
                        && isPhoneValid) {

                    s = "";

                    Thread t = new Thread(new Runnable() {
                        public void run() {

                            final String numb;
                            if (country == UKR && phoneNumber.getText().length() == 10) {
                                numb = "+38".concat(phoneNumber.getText().toString());
                            } else {
                                numb = phoneNumber.getText().toString();
                            }
                            final String pass = code.getText().toString();

                            Utilits.saveDeviceInfo(MainActivity.this);

                            ServiceConnection sc = new ServiceConnection(MainActivity.this);
                            s = sc.checkCode(numb, pass);
                            if (s.equals("success")) {
                                Log.d("test", "go go go");

                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putString("Phone", numb);
                                editor.putString("Pass", pass);
                                Log.d("SHARED", "puted " + numb + " to Phone");
                                Log.d("SHARED", "puted " + pass + " to Pass");
                                editor.commit();


                                Intent i = progressIntent;
                                startActivity(i);
                                finish();
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            AlertDialog alertDialog = new AlertDialog.Builder(
                                                    themedContext)
                                                    .create();
                                            alertDialog
                                                    .setMessage("Ошибка авторизации.");
                                            alertDialog
                                                    .setButton(
                                                            "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {

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

                        }
                    });
                    t.start();

                }
            }
        });

	    if (mSettings.contains("Phone")) {
            Crashlytics.setUserIdentifier(mSettings.getString("Phone", ""));
        } else {
            Crashlytics.setUserIdentifier("devPN:" + tMgr.getLine1Number());
        }

        setUpEditText(phoneNumber.getText().toString());
	}

    private int getCountry(CharSequence s) {
        if (s.length() > 1) {
            String code = s.toString().substring(0, 2);
            Log.d(TAG, "code = " + code);
            if (code.equals("+3")) {
                return UKR;
            }
            if (code.equals("+1")) {
                return USA;
            }
            if (code.equals("+7")) {
                return RUS;
            }
            return -1;
        } else {
            return -1;
        }
    }

    /**
     * Method returns formatted telephone number due to country network info
     * @param tMgr - TelephonyManager
     * @param countryISO - ua for Ukraine, us for USA, ru for Russia (String)
     * @return - returns formatted number (String)
     */
    private String getUserPhoneNumber(TelephonyManager tMgr, String countryISO) {
        String phoneNumber = "+38";
        String curNumber = tMgr.getLine1Number();
        String curShortNumber = "";
        if (curNumber != null && !curNumber.equals("") && curNumber.length() > 10)
            curShortNumber = curNumber.substring(curNumber.length() - 10);
        if (countryISO.equals("ua"))
            phoneNumber = "+38" + curShortNumber;
        if (countryISO.equals("us"))
            phoneNumber = "+1" + curShortNumber;
        if (countryISO.equals("ru"))
            phoneNumber = "+7" + curShortNumber;

        return phoneNumber;
    }

    private void setUpEditText(String mText) {
        if (mText != null) {
            phoneNumber.setTextSize(40);
            phoneNumber.setPadding(2, 1, 0, 0);
            if (mText.length() > 4) {
                phoneNumber.setEms(22);
                SpannableString ss = new SpannableString(mText);
                ss.setSpan(new RelativeSizeSpan(0.6f), 0, 3, 0);
                //phoneNumber.setText(ss);
            } else if (mText.equals("")) {
                phoneNumber.setTextSize(25);
                phoneNumber.setEms(22);
            }
        } else {
            phoneNumber.setTextSize(25);
            phoneNumber.setEms(22);
        }
    }
	

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni!=null && ni.isAvailable() && ni.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    //GCM
    // Registration Id from GCM
    private static final String PREF_GCM_REG_ID = "PREF_GCM_REG_ID";
    private SharedPreferences prefs;
    // Your project number and web server url. Please change below.
    private static final String GCM_SENDER_ID = "682244301681";

    GoogleCloudMessaging gcm;

    private static final int ACTION_PLAY_SERVICES_DIALOG = 100;
    protected static final int MSG_REGISTER_WITH_GCM = 101;
    protected static final int MSG_REGISTER_WEB_SERVER = 102;
    protected static final int MSG_REGISTER_WEB_SERVER_SUCCESS = 103;
    protected static final int MSG_REGISTER_WEB_SERVER_FAILURE = 104;
    private String gcmRegId;

    public boolean isGoogelPlayInstalled() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        ACTION_PLAY_SERVICES_DIALOG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Google Play Service is not installed",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;

    }

    private SharedPreferences getSharedPreferences() {
        if (prefs == null) {
            prefs = getApplicationContext().getSharedPreferences(
                    "AndroidSRCDemo", Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public void saveInSharedPref(String result) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_GCM_REG_ID, result);
        editor.commit();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_WITH_GCM:
                    new GCMRegistrationTask().execute();
                    break;
                case MSG_REGISTER_WEB_SERVER:
                    //new WebServerRegistrationTask().execute();
                    break;
                case MSG_REGISTER_WEB_SERVER_SUCCESS:
                    Toast.makeText(getApplicationContext(),
                            "registered with web server", Toast.LENGTH_LONG).show();
                    break;
                case MSG_REGISTER_WEB_SERVER_FAILURE:
                    Toast.makeText(getApplicationContext(),
                            "registration with web server failed",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };



    private class GCMRegistrationTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            if (gcm == null && isGoogelPlayInstalled()) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            }
            try {
                gcmRegId = gcm.register(GCM_SENDER_ID);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "error while registering gcm - " + e.getMessage());
            }

            return gcmRegId;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(getApplicationContext(), "registered with GCM",
                        Toast.LENGTH_LONG).show();
                saveInSharedPref(result);
            }
        }

    }


}
