package com.webcab.elit.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.webcab.elit.Log;
import com.webcab.elit.Utils.Utilits;
import com.webcab.elit.data.addr2;
import com.webcab.elit.data.autos;
import com.webcab.elit.data.house;
import com.webcab.elit.data.order;
import com.webcab.elit.data.order_s;
import com.webcab.elit.data.review;
import com.webcab.elit.data.route;
import com.webcab.elit.data.street;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServiceConnection {

	private static final int ANDROID_OS_CODE = 1;
	private static final String PREF_GCM_REG_ID = "PREF_GCM_REG_ID";
	private static final String TAG = "SC_TAG";
	private Context ctx;
	SharedPreferences mSettings;
    SharedPreferences mSettings2;
    private final byte COND = 3;
	private final byte POS = 15;
	private final byte ANIMAL = 34;
	private final byte WIFI = 33;
	private final byte NOSMOKE = 35;
	private final byte BAG = 11;

    private Boolean test = false;

	public ServiceConnection(Context ctx) {
		super();
		this.ctx = ctx;
		mSettings = ctx.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        mSettings2 = ctx.getSharedPreferences("mydata", Context.MODE_PRIVATE);
	}

	FileWriter writer;
	File file;

	private String getHost() {

		String h = "";

		if (mSettings.contains("Server")) {

			h = mSettings.getString("Server", "");
		}

		//		if (mSettings.contains("realServer")) {
		//			//Log.d("shared", "contains realServer");
		//			if (mSettings.getBoolean("realServer", false)) {
		//				h = "http://194.48.212.13/mobile/";
		//				Log.d("host", "server is real");
		//			}
		//		} else {
		//			SharedPreferences.Editor editor = mSettings.edit();
		//			editor.putBoolean("realServer", false);
		//			editor.commit();
		//		}

		return h;
	}

    public String getStreetCustom(String street) {
        String res = "";
        String script = "ind.php";
        try {
            street = URLEncoder.encode(street, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String urlParameters = "term=" + street;
        res = postRequest(script, urlParameters);
        return res;
    }

    public String getHouseCustom(int streetID, String house) {
        String res = "";
        String script = "ind2.php";
        String houseForWeb ="";
        try {
            houseForWeb = URLEncoder.encode(house, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String urlParameters = "idstr=" + streetID + "&term=" + houseForWeb;
        try {
            JSONArray houses = new JSONArray(postRequest(script, urlParameters));
            for (int i = 0; i < houses.length(); i++) {
                JSONObject obj = (JSONObject) houses.get(i);
                if (obj.optString("value").equals(house)) {
                    return obj.toString();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean deleteArchOrder(int id) {
        String script = "templateAll.php";
        String urlParameters = "func=deleteHistoryRow&id=" + id;
        try {
            JSONObject res = new JSONObject(postRequest(script, urlParameters));
            if (res != null && res.optString("message").equals("success"))
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

	public List<street> getStreet(String str) {
		InputStream is = null;
		List<street> mStr = new ArrayList<street>();
		String street = "";
		try {
			street = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// ""
			e1.printStackTrace();
		}

		try {
			JSONArray jArray = new JSONArray(postRequest("ind.php", "term=" + street));
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject res = jArray.getJSONObject(i);
				// Log.v("id", i + " - " + res.optString("id"));
				// Log.v("value", i + " - " + res.optString("value"));
				Log.d("PRELIM_CHECK", "street JSON = " + res.toString());
				mStr.add(new street(res.optString("value"),
						res.optString("id"), res.optString("geox"), res
						.optString("geoy")));
			}

		} catch (JSONException e) {
            //Mint.logException(e);
			Log.e("log_tag", "Error parsing data " + e.getMessage());
			Log.e("PRELIM_CHECK", "Error parsing data " + e.getMessage());
		} catch (NullPointerException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}

		return mStr;
	}

	public List<house> getStreetHouse(String streetId, String house) {

		List<house> mHouse = new ArrayList<house>();
		String value, id, x, y;

		InputStream is = null;
		String hs = "";

		try {
			hs = URLEncoder.encode(house, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
            //Mint.logException(e1);
			// ""
			e1.printStackTrace();
		}

		try {
			JSONArray response = new JSONArray(postRequest("ind2.php", "idstr=" + streetId + "&term=" + hs));
			for (int i = 0; i < response.length(); i++) {
				JSONObject res = response.getJSONObject(i);
				value = res.optString("value");
				// Log.v("value", "- " + value);
				id = res.optString("id");
				// Log.v("id", "- " + id);
				x = res.optString("geox");
				// Log.v("x", "- " + x);
				y = res.optString("geoy");
				// Log.v("y", "- " + y);

				mHouse.add(new house(value, id, x, y));
			}

		} catch (JSONException e) {
            //Mint.logException(e);
			Log.e("log_tag", "Error parsing data " + e.toString());
		} catch (NullPointerException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}
		Log.d("myLogs", "result = " + mHouse.get(0).getValue());
		return mHouse;
	}

	public void checkPhone(String str, boolean utf) {
		String urlParameters = "func=checkphone&phone=" + str;
		if (utf) {
			try {
				urlParameters = "func=checkphone&phone="
						+ URLEncoder.encode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
                //Mint.logException(e);
				// ""
				e.printStackTrace();
			}
		}
		postRequest("createorder.php", urlParameters);
	}

	public route getRoute(Context ctx) {

		Log.d("getRoute", "in Here");
		route r = null;

		String streetId = mSettings.getString("StreetId", "");
		String dom = "";
		String dom2 = "";

		final long currentTime = System.currentTimeMillis();
		long orderTime = mSettings.getLong("timeToServer", currentTime);
		if (orderTime == 0) {
			orderTime = currentTime;
		}

		try {
			dom = URLEncoder.encode(mSettings.getString("Dom", ""), "UTF-8");
			dom2 = URLEncoder.encode(mSettings.getString("Dom2", ""), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
            //Mint.logException(e1);
			// ""
			e1.printStackTrace();
		}
		String streetId2 = mSettings.getString("StreetId2", "");



		try {
            JSONArray suborder = new JSONArray();
            if (!mSettings.contains("longRoute") || mSettings.getString("longRoute", "").equals("")) {
                JSONObject js1 = new JSONObject();
                js1.put("streetid", streetId2);
                js1.put("house", dom2);

                suborder.put(0, js1);
            } else {
                JSONArray subOrdersJSONArray = new JSONArray(mSettings.getString("longRoute", ""));
				Log.d("Address_check", "JSON = " + subOrdersJSONArray);
                for (int i = 0; i < subOrdersJSONArray.length(); i++) {
                    addr2 curAddress = Utilits.getAddressFromJSON((JSONObject) subOrdersJSONArray.get(i));
                    JSONObject point = new JSONObject();
                    point.put("streetid", URLEncoder.encode(curAddress.getStreetid(), "UTF-8"));
                    point.put("house", URLEncoder.encode(curAddress.getHousenumber(), "UTF-8"));
                    suborder.put(point);
                }
            }

			// JSONObject json = new JSONObject();
			// json.put("suborder", suborder);

			String cl = "2";
			if (mSettings.contains("Car")) {
				String st = mSettings.getString("Car", "");
				if (st.equals("0")) {
					cl = "4";
				} else if (st.equals("1")) {
					cl = "2";
				} else if (st.equals("2")) {
					cl = "1";
				} else if (st.equals("3")) {
					cl = "5";
				}
			}

			String ads = "";
			if ((mSettings.getBoolean("Snow", false))
					|| (mSettings.getBoolean("Pos", false))
					|| (mSettings.getBoolean("Pat", false))
					|| (mSettings.getBoolean("Wifi", false))
					|| (mSettings.getBoolean("Smoke", false))
					|| (mSettings.getBoolean("Bag", false))) {

				if (mSettings.getBoolean("Snow", false)) {
					if (ads.equals("")) {
						ads += COND;
					} else {
						ads += "," + COND;
					}
				}

				if (mSettings.getBoolean("Pos", false)) {
					if (ads.equals("")) {
						ads += POS;
					} else {
						ads += "," + POS;
					}
				}

				if (mSettings.getBoolean("Pat", false)) {
					if (ads.equals("")) {
						ads += ANIMAL;
					} else {
						ads += "," + ANIMAL;
					}
				}

				if (mSettings.getBoolean("Wifi", false)) {
					if (ads.equals("")) {
						ads += WIFI;
					} else {
						ads += "," + WIFI;
					}
				}

				if (mSettings.getBoolean("Smoke", false)) {
					if (ads.equals("")) {
						ads += NOSMOKE;
					} else {
						ads += "," + NOSMOKE;
					}
				}

				if (mSettings.getBoolean("Bag", false)) {
					if (ads.equals("")) {
						ads += BAG;
					} else {
						ads += "," + BAG;
					}
				}

			}

			String urlParameters =
							"streetFromID=" + streetId +
							"&BuildFrom=" + dom +
							"&ClassAvto=" + cl +
							"&date=" + Math.round(orderTime / 1000) +
							"&additionalservices=[" + ads + "]" +
							"&arrObjectsJSON=" + suborder.toString();

			String res = postRequest("getroute.php", urlParameters);

			Log.d("route", "q - " + urlParameters);
			Log.d("route", "q - " + res);

			String res2 = res.substring(res.indexOf("{"), (res.indexOf("}") + 1));
			JSONObject response = new JSONObject(res2);
			String distance = response.optString("distance");

			Log.d("getRoute", distance);

			String time = response.optString("time");

			Log.d("getRoute", time);

			String price = response.optString("price");

			Log.d("getRoute", price);

			List<String> routex = new ArrayList<String>();

			JSONArray respx = response.getJSONArray("routex");
			for (int i = 0; i < respx.length(); i++) {
				routex.add(respx.getString(i));
			}

			List<String> routey = new ArrayList<String>();

			JSONArray respy = response.getJSONArray("routey");
			for (int i = 0; i < respy.length(); i++) {
				routey.add(respy.getString(i));
			}

			Log.d("getRoute", "x - " + routex.size());
			Log.d("getRoute", "y - " + routey.size());

			r = new route(distance, price, time, routex, routey);

		} catch (JSONException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}
		catch (NullPointerException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (Exception e) {
            //Mint.logException(e);
			e.printStackTrace();
		}

		return r;
	}

	public order createOrder(Context ctx, String fromStr, String price, String toStr) {

		Log.d("createOrder", "in Here");
		order or = null;

		String phone = "", phone2 = "", name = "";

		if (!mSettings.getString("uPhone", "").equals("") || !mSettings.getString("Name","").equals("")) {
            try {
            if(mSettings.getString("Name", "").equals("")){
                    name = URLEncoder.encode(mSettings2.getString("Name", ""), "UTF-8");
            }
            else{
                    name = URLEncoder.encode(mSettings.getString("Name", ""), "UTF-8");
            }
            if (mSettings.getString("uPhone", "").equals("")){
                phone = URLEncoder.encode(mSettings.getString("Phone", ""), "UTF-8");
            } else {
                phone = URLEncoder.encode(mSettings.getString("uPhone", ""), "UTF-8");
            }

            phone2 = URLEncoder.encode(mSettings.getString("Phone", ""), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                //Mint.logException(e);
                e.printStackTrace();
            }
		} else {

			try {
				phone2 = URLEncoder.encode(mSettings2.getString("uPhone", ""), "UTF-8");
				phone = URLEncoder.encode(mSettings.getString("Phone", ""), "UTF-8");
				if (!mSettings2.getString("Name", "").equals("")) {
					name = URLEncoder.encode(mSettings2.getString("Name", ""), "UTF-8");
				} else {
					name = "unnamedAndroid";
				}

			} catch (UnsupportedEncodingException e) {
                //Mint.logException(e);
				e.printStackTrace();
			}
		}

		String streetId = mSettings.getString("StreetId", "");
		String streetId2 = mSettings.getString("StreetId2", "");
		String opt = mSettings.getString("Prim", "");

		String dom = "";
		String dom2 = "";
		String flat = "";
        String longRoutePoints = "";
		String parad = "";
		String orderID = mSettings.getString("OrderId", "0").equals("") ? "0" : mSettings.getString("OrderId", "0");
		try {
			dom = URLEncoder.encode(mSettings.getString("Dom", ""), "UTF-8");
			dom2 = URLEncoder.encode(mSettings.getString("Dom2", ""), "UTF-8");
			flat = URLEncoder.encode(mSettings.getString("flat", ""), "UTF-8");
			parad = URLEncoder.encode(mSettings.getString("Parad", ""), "UTF-8");
			orderID = URLEncoder.encode(orderID, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
            //Mint.logException(e1);
			// ""
			e1.printStackTrace();
		}

		try {
			JSONArray suborder = new JSONArray();
			if (!mSettings.contains("longRoute") || mSettings.getString("longRoute", "").equals("")) {
				JSONObject js1 = new JSONObject();
				js1.put("streetid", streetId2);
				js1.put("house", dom2);

				suborder.put(0, js1);
                longRoutePoints = toStr;
			} else {
				JSONArray subOrdersJSONArray = new JSONArray(mSettings.getString("longRoute", ""));
				for (int i = 0; i < subOrdersJSONArray.length(); i++) {
					addr2 curAddress = Utilits.getAddressFromJSON((JSONObject) subOrdersJSONArray.get(i));
					JSONObject point = new JSONObject();
					point.put("streetid", URLEncoder.encode(curAddress.getStreetid(), "UTF-8"));
					point.put("house", URLEncoder.encode(curAddress.getHousenumber(), "UTF-8"));
					suborder.put(point);

                    longRoutePoints += i == 0 ? curAddress.getSreetname() + ", " + curAddress.getHousenumber()
                            :  "->" + curAddress.getSreetname() + ", " + curAddress.getHousenumber();
				}
                Log.d("ROUTE_CHECK", "longRoute");
			}

			String cl = "2";
			if (mSettings.contains("Car")) {
				String st = mSettings.getString("Car", "");
				if (st.equals("0")) {
					cl = "4";
				} else if (st.equals("1")) {
					cl = "2";
				} else if (st.equals("2")) {
					cl = "1";
				} else if (st.equals("3")) {
					cl = "5";
				}
			}

			String ads = "";
			if ((mSettings.getBoolean("Snow", false))
					|| (mSettings.getBoolean("Pos", false))
					|| (mSettings.getBoolean("Pat", false))
					|| (mSettings.getBoolean("Wifi", false))
					|| (mSettings.getBoolean("Smoke", false))
					|| (mSettings.getBoolean("Bag", false))) {

				if (mSettings.getBoolean("Snow", false)) {
					if (ads.equals("")) {
						ads += COND;
					} else {
						ads += "," + COND;
					}
				}

				if (mSettings.getBoolean("Pos", false)) {
					if (ads.equals("")) {
						ads += POS;
					} else {
						ads += "," + POS;
					}
				}

				if (mSettings.getBoolean("Pat", false)) {
					if (ads.equals("")) {
						ads += ANIMAL;
					} else {
						ads += "," + ANIMAL;
					}
				}

				if (mSettings.getBoolean("Wifi", false)) {
					if (ads.equals("")) {
						ads += WIFI;
					} else {
						ads += "," + WIFI;
					}
				}

				if (mSettings.getBoolean("Smoke", false)) {
					if (ads.equals("")) {
						ads += NOSMOKE;
					} else {
						ads += "," + NOSMOKE;
					}
				}

				if (mSettings.getBoolean("Bag", false)) {
					if (ads.equals("")) {
						ads += BAG;
					} else {
						ads += "," + BAG;
					}
				}

			}

			try {
				fromStr = URLEncoder.encode(fromStr, "UTF-8");
				//toStr = URLEncoder.encode(toStr, "UTF-8");
				toStr = URLEncoder.encode(longRoutePoints, "UTF-8");
				opt = URLEncoder.encode(opt, "UTF-8");
			} catch (UnsupportedEncodingException e) {
                //Mint.logException(e);
				// ""
				e.printStackTrace();
			}
			String pushRegID = getSharedPreferences().getString(PREF_GCM_REG_ID, "");
			JSONObject pushJSON = new JSONObject();
			pushJSON.put("os", ANDROID_OS_CODE);
			pushJSON.put("token", pushRegID);

			String urlParameters = "func=CreateOrder&nameUser=" + name
					+ "&fromStr=" + fromStr
					+ "&nameUser2="
					+ "&orderid=" + orderID
					+ "&frontdoor=" + parad
					+ "&optation="
					+ opt + "&price=" + price + "&toStr=" + toStr
					+ "&flat=" + flat + "&streetFromID=" + streetId
					+ "&phone=" + phone
					+ "&phone2=" + phone2
					+ "&BuildFrom=" + dom
					+ "&ClassAvto=" + cl
					+ "&additionalservices=[" + ads + "]"
					+ "&arrObjectsJSON=" + suborder.toString()
					+ "&push=" + pushJSON.toString();


			Log.d("order_check", "urlParam = " + urlParameters);

            String mockResponse = "{\"autodriverid\":\"5782\"," +
                    "\"orderid\":9018225," +
                    "\"ordertodatetime\":\"2016-01-14T00:20:13\"," +
                    "\"autocallsign\":\"333\"," +
                    "\"callsignid\":9662," +
                    "\"statenumber\":\"АА 0641 МО\"," +
                    "\"carbrand\":\"Пежо 407\"," +
                    "\"carcolor\":\"Красный\"," +
                    "\"drivename\":\"Зубро Віталій Юрійович\"," +
                    "\"geox\":\"30.5171\",\"geoy\":\"50.4394\"," +
                    "\"driverphone\":\"+380917871333\"," +
                    "\"err\":null," +
                    "\"isadvanced\":false," +
                    "\"message\":null," +
                    "\"durationToOrder\":\"780\"}";

            JSONObject response;
            if (test) {
                response = new JSONObject(mockResponse);
            } else {
                response = new JSONObject(postRequest("create_order.php", urlParameters));
            }

			Log.d("createOrder", "response = " + response);

			String autodriverid = response.optString("autodriverid");
			Log.d("createOrder", "" + autodriverid);

			int orderid = response.optInt("orderid");
			Log.d("createOrder", "" + orderid);

			String autocallsign = response.optString("autocallsign");
			Log.d("createOrder", autocallsign);

			int callsignid = response.optInt("callsignid");
			Log.d("createOrder", "" + callsignid);

			String statenumber = response.optString("statenumber");
			Log.d("statenumber", statenumber);

			String carbrand = response.optString("carbrand");
			Log.d("createOrder", carbrand);

			String carcolor = response.optString("carcolor");
			Log.d("createOrder", carcolor);

			String drivename = response.optString("drivename");
			Log.d("createOrder", drivename);

			String geox = response.optString("geox");
			Log.d("createOrder", geox);

			String geoy = response.optString("geoy");
			Log.d("createOrder", geoy);

			Long time = response.optLong("durationToOrder");
			Log.d("createOrder", time+"");

			String driverPhone=response.optString("driverphone");
			Log.d("createOrder", driverPhone+"");


			or = new order(orderid, autocallsign, callsignid, statenumber,
					carbrand, carcolor, drivename, geox, geoy, autodriverid, time, driverPhone);

		} catch (JSONException e) {//Mint.logException(e);

			// ""
			Log.d("myLogs", "error1 in sc = " + e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e) {//Mint.logException(e);
			// ""
			Log.d("myLogs", "error2 in sc = " + e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return or;
	}

	private SharedPreferences prefs;

	private SharedPreferences getSharedPreferences() {
		if (prefs == null) {
			prefs = ctx.getSharedPreferences(
					"AndroidSRCDemo", Context.MODE_PRIVATE);
		}
		return prefs;
	}

    public JSONObject createPrelimOrder (Context ctx, String fromStr, String price, String toStr, long dateTime, String flat){
        //TODO функция отправляющая на сервер предварительный заказ


		Log.d("PREL_ORDER", "PRE_ORDER");
        JSONObject serverResponse = null;
        String phone = "", phone2 = "", name = "", orderID = "0";

        if (!mSettings.getString("uPhone", "").equals("") || !mSettings.getString("Name","").equals("")) {
            try {
                if(mSettings.getString("Name", "").equals("")){
                    name = URLEncoder.encode(mSettings2.getString("Name", ""), "UTF-8");
                }
                else{
                    name = URLEncoder.encode(mSettings.getString("Name", ""), "UTF-8");
                }
                if (mSettings.getString("uPhone", "").equals("")){
                    phone = URLEncoder.encode(mSettings.getString("Phone", ""), "UTF-8");
                } else {
                    phone = URLEncoder.encode(mSettings.getString("uPhone", ""), "UTF-8");
                }

                phone2 = URLEncoder.encode(mSettings.getString("Phone", ""), "UTF-8");
            } catch (UnsupportedEncodingException e) { //Mint.logException(e);
                e.printStackTrace();
            }
        } else {

            try {
                phone2 = URLEncoder.encode(mSettings2.getString("uPhone", ""), "UTF-8");
                phone = URLEncoder.encode(mSettings.getString("Phone", ""), "UTF-8");
                if (!mSettings2.getString("Name", "").equals("")) {
                    name = URLEncoder.encode(mSettings2.getString("Name", ""), "UTF-8");
                } else {
                    name = "unnamedAndroid";
                }

            } catch (UnsupportedEncodingException e) { //Mint.logException(e);
                e.printStackTrace();
            }
        }

		orderID = mSettings.getString("OrderId", "0").equals("") ? "0" : mSettings.getString("OrderId", "0");
        /*if (!mSettings.getString("OrderId", "").equals(""))
            orderID = mSettings.getString("OrderId", "0");*/

        String streetId = mSettings.getString("StreetId", "");
        String streetId2 = mSettings.getString("StreetId2", "");
        String parad = mSettings.getString("Parad", "");
        String opt = mSettings.getString("Prim", "");
        //		Log.d("opt", ""+opt.length());
        //		opt = opt.concat(opt);
        //		Log.d("opt", ""+opt.length());
        //		opt = opt.replaceAll("\n", "");
        //		Log.d("opt", ""+opt.length());
        String dom = "";
        String dom2 = "";
        String longRoutePoints = "";
        try {
            dom = URLEncoder.encode(mSettings.getString("Dom", ""), "UTF-8");
            dom2 = URLEncoder.encode(mSettings.getString("Dom2", ""), "UTF-8");
            orderID = URLEncoder.encode(orderID, "UTF-8");
        } catch (UnsupportedEncodingException e1) { //Mint.logException(e1);
            // ""
            e1.printStackTrace();
        }

        try {
            JSONArray suborder = new JSONArray();
            if (!mSettings.contains("longRoute") || mSettings.getString("longRoute", "").equals("")) {
                JSONObject js1 = new JSONObject();
                js1.put("streetid", streetId2);
                js1.put("house", dom2);

                suborder.put(0, js1);
                longRoutePoints = toStr;
            } else {
                JSONArray subOrdersJSONArray = new JSONArray(mSettings.getString("longRoute", ""));
                for (int i = 0; i < subOrdersJSONArray.length(); i++) {
                    addr2 curAddress = Utilits.getAddressFromJSON((JSONObject) subOrdersJSONArray.get(i));
                    JSONObject point = new JSONObject();
                    point.put("streetid", curAddress.getStreetid());
                    point.put("house", curAddress.getHousenumber());
                    suborder.put(point);
                    longRoutePoints += i == 0 ? curAddress.getSreetname() + ", " + curAddress.getHousenumber()
                            :  "->" + curAddress.getSreetname() + ", " + curAddress.getHousenumber();
                }
            }

            // JSONObject json = new JSONObject();
            // json.put("suborder", suborder);

            String cl = "2";
            if (mSettings.contains("Car")) {
                String st = mSettings.getString("Car", "");
                if (st.equals("0")) {
                    cl = "4";
                } else if (st.equals("1")) {
                    cl = "2";
                } else if (st.equals("2")) {
                    cl = "1";
                } else if (st.equals("3")) {
                    cl = "5";
                }
            }

            String ads = "";
            if ((mSettings.getBoolean("Snow", false))
                    || (mSettings.getBoolean("Pos", false))
                    || (mSettings.getBoolean("Pat", false))
                    || (mSettings.getBoolean("Wifi", false))
                    || (mSettings.getBoolean("Smoke", false))
                    || (mSettings.getBoolean("Bag", false))) {

                if (mSettings.getBoolean("Snow", false)) {
                    if (ads.equals("")) {
                        ads += COND;
                    } else {
                        ads += "," + COND;
                    }
                }

                if (mSettings.getBoolean("Pos", false)) {
                    if (ads.equals("")) {
                        ads += POS;
                    } else {
                        ads += "," + POS;
                    }
                }

                if (mSettings.getBoolean("Pat", false)) {
                    if (ads.equals("")) {
                        ads += ANIMAL;
                    } else {
                        ads += "," + ANIMAL;
                    }
                }

                if (mSettings.getBoolean("Wifi", false)) {
                    if (ads.equals("")) {
                        ads += WIFI;
                    } else {
                        ads += "," + WIFI;
                    }
                }

                if (mSettings.getBoolean("Smoke", false)) {
                    if (ads.equals("")) {
                        ads += NOSMOKE;
                    } else {
                        ads += "," + NOSMOKE;
                    }
                }

                if (mSettings.getBoolean("Bag", false)) {
                    if (ads.equals("")) {
                        ads += BAG;
                    } else {
                        ads += "," + BAG;
                    }
                }

            }

            try {
                fromStr = URLEncoder.encode(fromStr, "UTF-8");
                //toStr = URLEncoder.encode(toStr, "UTF-8");
                toStr = URLEncoder.encode(longRoutePoints, "UTF-8");
                opt = URLEncoder.encode(opt, "UTF-8");
            } catch (UnsupportedEncodingException e) { //Mint.logException(e);
                // ""
                e.printStackTrace();
            }

			String pushRegID = getSharedPreferences().getString(PREF_GCM_REG_ID, "");
			JSONObject pushJSON = new JSONObject();
			pushJSON.put("os", ANDROID_OS_CODE);
			pushJSON.put("token", pushRegID);


			final String urlParameters = "func=CreateOrder&nameUser=" + name
                    + "&fromStr=" + fromStr
                    + "&nameUser2=&orderid=" + orderID
					+ "&frontdoor=0&optation="
                    + opt + "&price=" + price + "&toStr=" + toStr
                    + "&flat=0&streetFromID=" + streetId
                    + "&phone=" + phone
                    + "&phone2=" + phone2
                    + "&BuildFrom=" + dom
                    + "&ClassAvto=" + cl
                    + "&additionalservices=[" + ads + "]"
                    + "&arrObjectsJSON=" + suborder.toString()
                    + "&date=" + Math.round(dateTime/1000)
					+ "&push=" + pushJSON.toString();

            Log.d("CHECK_ORDER", "urlParams = " + urlParameters);

			String response = postRequest("create_order.php", urlParameters);
			Log.d("Prelim", "resp = " + response);
            serverResponse = new JSONObject(response);



        } catch (JSONException e) { //Mint.logException(e);
            // ""
            Log.d("Prelim", "JSON error " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) { //Mint.logException(e);
            // ""
            Log.d("Prelim", "NULLPointer error " + e.getMessage());
            e.printStackTrace();
        }
        return serverResponse;
    }


	public long confirmOrder(String order_id) {

		long res = 0;

		Log.d("confirmOrder", "in Here");

		try {

            String mockResponse = "{\"orderid\":9018225," +
                    "\"ordertodatetime\":\"2016-01-14T00:20:13\"," +
                    "\"durationToOrder\":\"644964,7132\"}";

			String urlParameters = "func=WebOrderConfirm&orderid=" + order_id;

            JSONObject response;
            if (test) {
                response = new JSONObject(mockResponse);
            } else {
                response = new JSONObject(postRequest("createorder.php", urlParameters));
            }

			int orderid = response.optInt("orderid");
			Log.d("confirmOrder", "" + orderid);

			String ordertodatetime = response.optString("ordertodatetime");
			Log.d("confirmOrder", ordertodatetime);

			String durationToOrder = response.optString("durationToOrder");
			Log.d("durationToOrder", "111111111111 11111111111 "
					+ durationToOrder);

			if (durationToOrder!="")
				res = (long) Double.parseDouble(durationToOrder.replace(",", "."));
			else
				res=0;
			Log.d("res", "111111111111 11111111111 "
					+ res);


		} catch (JSONException e) { //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (NullPointerException e) { //Mint.logException(e);
			// ""
			e.printStackTrace();
		}

		return res;
	}

	public Boolean confirmPreOrder(String order_id, String callsignid) {

		Boolean res = false;
        String message = "";

		try {

			String urlParameters = "func=WebOrderConfirmPreorder&orderid=" + order_id + "&callsignid=" + callsignid;

			JSONObject response = new JSONObject(postRequest("createorder.php", urlParameters));
			message = response.optString("message", "");

		} catch (JSONException e) { //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (NullPointerException e) { //Mint.logException(e);
			// ""
			e.printStackTrace();
		}

        if (message.equals("success")) {
            res = true;
        }

		return res;
	}

	public void getAdditServices() {

		Log.d("GetAdditionalServices", "in Here");
		order or = null;

		String urlParameters = "func=GetAdditionalServices";

		String request = getHost() + "createorder.php";

		postRequest("createorder.php", urlParameters);
	}

	public String getTimeAutoSearch() {

		String timeautosearchanswer = "";
		Log.d("GetTimeAutoSearch", "in Here");

		try {
			String urlParameters = "func=GetTimeAutoSearch";

			String request = getHost() + "createorder.php";


			JSONObject response = new JSONObject(postRequest("createorder.php", urlParameters));
			timeautosearchanswer = response.optString("timeautosearchanswer");
			Log.d("GetTimeAutoSearch", "" + timeautosearchanswer);

		} catch (JSONException e) { //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (NullPointerException e) { //Mint.logException(e);
			// ""
			e.printStackTrace();
		}

		return timeautosearchanswer;
	}

	public void cancelOrder(String order_id) {

		String timeautosearchanswer = "";
		Log.d("cancelOrder", "in Here");
		String urlParameters = "func=CancelOrder&orderid=" + order_id;

		postRequest("createorder.php", urlParameters);
	}

    public void cancelPrelimOrder(String order_id) {
//TODO функция отправляющая отмену заказа на сервер
        String timeautosearchanswer = "";
        String message = "test";
        Log.d("cancelOrder", "in Here");
        String urlParameters = "func=CancelOrder&orderid=" + order_id + "&message=" + message;

        Log.d("DELETE_CHECK", "delete response = " + postRequest("createorder.php", urlParameters));

    }

	public List<autos> getCoordAll() {

		List<autos> autos = new ArrayList<autos>();
		int statusforweborder;
		String year, status, autotariffclassid, autocallsign, callsignid, statenumber, carbrand, carcolor, drivename, geox, geoy;

		Log.d("getCoordAll", "in Here");

		try {
			String urlParameters = "func=GetCoordinatesAllAuto";

			JSONArray response = new JSONArray(postRequest("getauto.php", urlParameters));
			for (int i = 0; i < response.length(); i++) {
				JSONObject res = response.getJSONObject(i);
				statusforweborder = res.optInt("statusforweborder");
				// Log.v("getCoordAll", " statusforweborder - " +
				// statusforweborder);

				year = res.optString("year");
				// Log.v("getCoordAll", " year - " + year);

				status = res.optString("status");
				// Log.v("getCoordAll", " status - " + status);

				autotariffclassid = res.optString("autotariffclassid");
				// Log.v("getCoordAll", " autotariffclassid - " +
				// autotariffclassid);

				autocallsign = res.optString("autocallsign");
				// Log.v("getCoordAll", " autocallsign - " + autocallsign);

				callsignid = res.optString("callsignid");
				// Log.v("getCoordAll", " callsignid - " + callsignid);

				statenumber = res.optString("statenumber");
				// Log.v("getCoordAll", " statenumber - " + statenumber);

				carbrand = res.optString("carbrand");
				// Log.v("getCoordAll", " carbrand - " + carbrand);

				carcolor = res.optString("carcolor");
				// Log.v("getCoordAll", " carcolor - " + carcolor);

				drivename = res.optString("drivename");
				// Log.v("getCoordAll", " drivename - " + drivename);

				geox = res.optString("geox");
				// Log.v("getCoordAll", " geox - " + geox);

				geoy = res.optString("geoy");
//				Log.i("getCoordAll", " geo - " + geoy+"-"+geox);
Log.i("getAutoCoords", res.toString());

				autos.add(new autos(statusforweborder, year, status,
						autotariffclassid, autocallsign, callsignid,
						statenumber, carbrand, carcolor, drivename, geox, geoy));
			}

		} catch (JSONException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (NullPointerException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}

		return autos;
	}

	public autos getCoordAuto(String callsignid) {

		autos auto = null;
		int statusforweborder;
		String year, status, autotariffclassid, autocallsign, callsignid1, statenumber, carbrand, carcolor, drivename, geox, geoy;

		try {
			String urlParameters = "func=GetCoordinatesAuto&callsignid="
					+ callsignid;

            String mockResponse = "{\"statusforweborder\":0," +
                    "\"year\":\"2005\"," +
                    "\"status\":\"6\"," +
                    "\"autotariffclassid\":\"1\"," +
                    "\"autocallsign\":\"333\"," +
                    "\"callsignid\":\"9662\"," +
                    "\"statenumber\":\"\"," +
                    "\"carbrand\":\"Пежо 407\"," +
                    "\"carcolor\":\"Красный\"," +
                    "\"drivename\":\"\"," +
                    "\"geox\":\"30.5171\"," +
                    "\"geoy\":\"50.4394\"," +
                    "\"driverphone\":\"\"}";

            JSONObject res;
            if (test) {
                res = new JSONObject(mockResponse);
            } else {
                res = new JSONObject(postRequest("getauto.php", urlParameters));
            }


			Log.d(TAG, "auto response = " + res.toString());
			statusforweborder = res.optInt("statusforweborder");
			Log.v("GetCoordinatesAuto", " statusforweborder - "
					+ statusforweborder);

			year = res.optString("year");
			// Log.v("GetCoordinatesAuto", " year - " + year);

			status = res.optString("status");
			// Log.v("GetCoordinatesAuto", " status - " + status);

			autotariffclassid = res.optString("autotariffclassid");
			// Log.v("GetCoordinatesAuto", " autotariffclassid - " +
			// autotariffclassid);

			autocallsign = res.optString("autocallsign");
			// Log.v("GetCoordinatesAuto", " autocallsign - " + autocallsign);

			callsignid1 = res.optString("callsignid");
			// Log.v("GetCoordinatesAuto", " callsignid - " + callsignid1);

			statenumber = res.optString("statenumber");
			// Log.v("GetCoordinatesAuto", " statenumber - " + statenumber);

			carbrand = res.optString("carbrand");
			// Log.v("GetCoordinatesAuto", " carbrand - " + carbrand);

			carcolor = res.optString("carcolor");
			// Log.v("GetCoordinatesAuto", " carcolor - " + carcolor);

			drivename = res.optString("drivename");
			// Log.v("GetCoordinatesAuto", " drivename - " + drivename);

			geox = res.optString("geox");
			// Log.v("GetCoordinatesAuto", " geox - " + geox);

			geoy = res.optString("geoy");
			// Log.v("GetCoordinatesAuto", " geoy - " + geoy);

			auto = new autos(statusforweborder, year, status,
					autotariffclassid, autocallsign, callsignid1, statenumber,
					carbrand, carcolor, drivename, geox, geoy);

		} catch (JSONException e) {
            Log.d(TAG, "getAutoCoord error = " + e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e) {
			Log.d(TAG, "getAutoCoord error = " + e.getMessage());
			e.printStackTrace();
		}

		return auto;
	}

	public String postRequest(String script2, String urlParameters2) {

		String script;

		final String urlParameters = urlParameters2;

		if (mSettings.contains("PHPSESSID") && !mSettings.getString("PHPSESSID", "").equals("")) {
			script = script2 + "?PHPSESSID=" + mSettings.getString("PHPSESSID", "");
		} else {
			script = script2;
		}

		String res="";

		try {

			String request = getHost() + script;
			Log.d("postRequest", request);
			Log.d("postRequest", urlParameters);
			URL url;
			url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(35000);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");

			//			if (mSettings.contains("Cookie")) {
			//				String cookie = mSettings.getString("Cookie", "");
			//				
			//				connection.setRequestProperty("Cookie", cookie);
			//
			//				Log.d("postRequest",
			//						"Cookies " + mSettings.getString("Cookie", ""));
			//			}

			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			//			connection.setRequestProperty("Content-Length",
			//					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			//			String r = connection.getHeaderField("Set-Cookie");
			//
			//			if (r!=null && !r.equals("")) {
			//				SharedPreferences.Editor editor = mSettings.edit();
			//				editor.putString("Cookie", r);
			//				Log.d("SHARED", "puted cookie " + r);
			//				editor.commit();
			//			}

			InputStream in = connection.getInputStream();

			Scanner in1 = new Scanner(in);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			connection.disconnect();


			//Log.d("postRequest", sb.toString());
			Log.d("Prelim", sb.toString());

			res = sb.toString();

		} catch (MalformedURLException e3) {
            //Mint.logException(e3);
			// ""
			e3.printStackTrace();
			Log.d("PostRequest", "error = " + e3.getMessage());
		} catch (ProtocolException e) {
            //Mint.logException(e);
			Log.d("PostRequest", "error = " + e.getMessage());
			// ""
			e.printStackTrace();
		} catch (IOException e) {
            //Mint.logException(e);
			// ""
			Log.d("PostRequest", "error = " + e.getMessage());
			e.printStackTrace();
		}
		catch (NullPointerException e) {
            //Mint.logException(e);
			Log.d("PostRequest", "error = " + e.getMessage());
			// ""
			e.printStackTrace();
		} catch (Exception e) {
            //Mint.logException(e);
			// ""
			Log.d("PostRequest", "error = " + e.getMessage());
			e.printStackTrace();
		} 

		return res;
	}

    public String getCarInfo(int id) {
        String script = "templateAll.php";
        String urlParameters = "func=selectHistoryRow&id=" + id;

        JSONObject car = null;
        try {
            JSONArray carInfo = new JSONArray(postRequest(script, urlParameters));
            car = carInfo.getJSONObject(0);
            //Log.d("CAR_INFO", "car info = " + car.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String res = car != null ? car.optString("textCarInfo") : "Нет информации об авто";
		Log.d("CAR_INFO", "car = " + res);
        String carInfo[] = res.split(",");
        String result = "";
        if (carInfo.length > 1)
            for (int i = 1; i < carInfo.length; i++)
                if (i == 1)
                    result += carInfo[i];
                else
                    result += ", " + carInfo[i];
        //String result = res.length() > 6 ? res.substring(0, res.length() - 6) : res;
        return result;
    }

	public addr2 getAddressFromGeo(String x, String y) {

		addr2 addr = null;

		String urlParameters = "func=GetAddressFromGeo&x=" + x + "&y=" + y;
		String script = "createorder.php";
		String r = postRequest(script, urlParameters);

		String sreetname, streetid, housenumber, geox, geoy;

		if (!r.equals("")) {

			JSONObject result = null;
			try {
				result = new JSONObject(r);

				sreetname = result.optString("sreetname");
				Log.v("GetCoordinatesAuto", " sreetname - " + sreetname);

				streetid = result.optString("streetid");
				Log.v("GetCoordinatesAuto", " streetid - " + streetid);

				housenumber = result.optString("housenumber");
				Log.v("GetCoordinatesAuto", " housenumber - " + housenumber);

				geox = result.optString("geox");
				Log.v("GetCoordinatesAuto", " geox - " + geox);

				geoy = result.optString("geoy");
				Log.v("GetCoordinatesAuto", " geoy - " + geoy);


				addr = new addr2(sreetname, streetid, housenumber, geox, geoy);
			} catch (JSONException e) {
                //Mint.logException(e);
				// ""
				e.printStackTrace();
			} catch (NullPointerException e) {
                //Mint.logException(e);
				// ""
				e.printStackTrace();
			}

		}

		return addr;

	}

	public List<order_s> loadHistory(String phone) {

		List<order_s> ord = new ArrayList<order_s>();

		String numb = null;
		try {
			numb = URLEncoder.encode(phone, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
            //Mint.logException(e1);
			// ""
			e1.printStackTrace();

			return ord;
		}

		String urlParameters = "func=loadHistory&phone=" + numb;
		String script = "templateAll.php";

        String mockResponse = "[{\"id\":3039,\"From\":\"ТЕСТ ОФИС, 1000\"," +
                "\"To\":\"Хрещатик вул., 13\"," +
                "\"When\":\"24-12-2015 15:45\"," +
                "\"WhenDate\":1450964700," +
                "\"Price\":\"80\"," +
                "\"Rate\":0," +
                "\"preorder\":0," +
                "\"status\":1," +
                "\"orderid\":\"9018225\"," +
                "\"additionalServices\":\"[]\"," +
                "\"classAvto\":2,\"callsignid\":9662}]";

        /*String mockResponse = "{\"autodriverid\":\"5782\"," +
                "\"orderid\":9018225," +
                "\"ordertodatetime\":\"2016-01-14T00:20:13\"," +
                "\"autocallsign\":\"333\"," +
                "\"callsignid\":9662," +
                "\"statenumber\":\"АА 0641 МО\"," +
                "\"carbrand\":\"Пежо 407\"," +
                "\"carcolor\":\"Красный\"," +
                "\"drivename\":\"Зубро Віталій Юрійович\"," +
                "\"geox\":\"30.5171\",\"geoy\":\"50.4394\"," +
                "\"driverphone\":\"+380917871333\"," +
                "\"err\":null," +
                "\"isadvanced\":false," +
                "\"message\":null," +
                "\"durationToOrder\":\"780\"}";*/

        String r;
        if (test) {
            r = mockResponse;
        } else {
            r = postRequest(script, urlParameters);
        }


		int id, Rate;
		String From, To, When, Price;

		if (r!=null && !r.equals("") && !r.equals("null")) {

			JSONObject jo = null;
			try {
				jo = new JSONObject(r);
			} catch (JSONException e1) {
                //Mint.logException(e1);
				// ""
				e1.printStackTrace();
			}
			try {
				String message = jo.optString("message");
				if (message.equals("EmptyHistory")) {
					return ord;
				}
			} catch (Exception e) {
                //Mint.logException(e);
				Log.d("eroor", "456"+e.getMessage());
			}



			try {

				//r = r.substring(1, r.length()-1);
				//Log.d("serviceConn", r + " " + r.length());

				JSONArray response = new JSONArray(r);
                Log.d("HISTORY", "history resp = " + response);

				for (int i = 0; i < response.length(); i++) {
					JSONObject res = response.getJSONObject(i);

					id = res.optInt("id");
					Log.v("loadHistory", " id - " + id);

					From = res.optString("From");
					Log.v("loadHistory", " From - " + From);

					To = res.optString("To");
					Log.v("loadHistory", " To - " + To);

					When = res.optString("When");
					Log.v("loadHistory", " When - " + When);

					Price = res.optString("Price");
					Log.v("loadHistory", " Price - " + Price);

					Rate = res.optInt("Rate");
					Log.v("loadHistory", " Rate - " + Rate);

                    boolean preorderStatus = res.optInt("preorder") == 1;

                    long whenDate = res.optLong("WhenDate");

                    int orderID = res.optInt("orderid");

                    int carClass = res.optInt("classAvto");

                    String addServ = res.optString("additionalServices");
					ArrayList<Integer> additionalServices = new ArrayList<>();
                    if (addServ.length() > 2) {
						addServ = addServ.substring(1, addServ.length() - 1);
						if (addServ.length() > 0) {
							String[] items = addServ.split(",");
							for (int j = 0; j < items.length; j++) {
								try {
									additionalServices.add(j, Integer.parseInt(items[j]));
								} catch (NumberFormatException nfe) {};
							}

						}
					}
					order_s newOrder = new order_s(id, Rate, From, To, When, Price, preorderStatus, whenDate, additionalServices, orderID, carClass);
					newOrder.setCallsignid(res.optInt("callsignid"));
					Log.d(TAG, "callsignid = " + newOrder.getCallsignid());
					ord.add(newOrder);

				}

			} catch (JSONException e) {
                //Mint.logException(e);
				// ""
				e.printStackTrace();
			} catch (NullPointerException e) {
                //Mint.logException(e);
				// ""
				e.printStackTrace();
			}
		}

		return ord;

	}

//    public List<order_s> loadPrelim(String phone){
        //TODO функция получающая предварительные заказы с сервера для отображения в архиве
//        List<order_s> ord = new ArrayList<order_s>();
//
//        String numb = null;
//        try {
//            numb = URLEncoder.encode(phone, "UTF-8");
//        } catch (UnsupportedEncodingException e1) {
//            // ""
//            e1.printStackTrace();
//
//            return ord;
//        }
//
//        String urlParameters = "func=loadPrelim&phone=" + numb;
//        String script = "templateAll.php";
//        String r = postRequest(script, urlParameters);
//
//        int id, Rate;
//        String From, To, When, Price, Time;
//
//        if (r!=null && !r.equals("") && !r.equals("null")) {
//
//            JSONObject jo = null;
//            try {
//                jo = new JSONObject(r);
//            } catch (JSONException e1) {
//                // ""
//                e1.printStackTrace();
//            }
//            try {
//                String message = jo.optString("message");
//                if (message.equals("EmptyHistory")) {
//                    return ord;
//                }
//            } catch (Exception e) {
//                Log.d("eroor", "456"+e.getMessage());
//            }
//
//
//
//            try {
//
//                //r = r.substring(1, r.length()-1);
//                //Log.d("serviceConn", r + " " + r.length());
//
//                JSONArray response = new JSONArray(r);
//
//                for (int i = 0; i < response.length(); i++) {
//                    JSONObject res = response.getJSONObject(i);
//
//                    id = res.optInt("id");
//                    Log.v("loadHistory", " id - " + id);
//
//                    From = res.optString("From");
//                    Log.v("loadHistory", " From - " + From);
//
//                    To = res.optString("To");
//                    Log.v("loadHistory", " To - " + To);
//
//                    When = res.optString("When");
//                    Log.v("loadHistory", " When - " + When);
//
//                    Price = res.optString("Price");
//                    Log.v("loadHistory", " Price - " + Price);
//
//                    Rate = res.optInt("Rate");
//                    Log.v("loadHistory", " Rate - " + Rate);
//
//                    Time = res.optLong("Time");
//                    Log.v("loadHistory", " Time - " + Time);
//
//                    ord.add(new order_s(id, Rate, From, To, When, Price));
//                }
//
//            } catch (JSONException e) {
//                // ""
//                e.printStackTrace();
//            } catch (NullPointerException e) {
//                // ""
//                e.printStackTrace();
//            }
//        }
//
//        return ord;
//    }



	public void saveRate(int id, int rate) {

		String urlParameters = "func=saveRate&rate=" + rate + "&id=" + id;
		String script = "templateAll.php";
		String r = postRequest(script, urlParameters);

		Log.d("saveRate", r);

	}

	public String getMessageArrivedAuto() {

		String r = "";

		String urlParameters = "func=GetMessageArrivedAuto";
		String script = "createorder.php";
		r += postRequest(script, urlParameters);

		Log.d("getMessageArrivedAuto", r);

		return r;
	}

	public void saveComHistory(int id, String comment) {

		try {
			comment = URLEncoder.encode(comment, "UTF-8");
		} catch (UnsupportedEncodingException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}

		String urlParameters = "func=saveComHistory&state=0&comments=" + comment + "&GlobalID=" + id;
		String script = "templateAll.php";
		String r = postRequest(script, urlParameters);

		Log.d("saveComHistory", r);

	}

	public String getCode(String numb) {

		String message = "";
		String s = "";
		String urlParameters = null;
		try {
			urlParameters = "func=code&tel=" + URLEncoder.encode(numb, "UTF-8");

			String request = getHost() + "reglogand.php";
			Log.d("getCode", request);
			Log.d("getCode", urlParameters);

			URL url;
			url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			InputStream in = connection.getInputStream();

			InputStreamReader isw = new InputStreamReader(in);

			int data = isw.read();
			while (data != -1) {
				char current = (char) data;
				data = isw.read();
				s += current;
			}
			Log.d("getCode", "RESPONSE - " + s);

			connection.disconnect();

			JSONObject result = null;

			result = new JSONObject(s);

			message = result.optString("message");
			Log.v("getCode", " message - " + message);

		} catch (UnsupportedEncodingException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (MalformedURLException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (IOException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (JSONException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (NullPointerException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}


		return message;

	}

	public String checkCode(String numb, String pass) {
		//testable

		String message = "";
		String s = "";
		int version = mSettings.getInt("app_version", -1);
		int os = mSettings.getInt("os_version", -1);
		String deviceName = mSettings.getString("device_name", "");

		try {
			/*final String urlParameters = "func=LoginToken&login="
					+ URLEncoder.encode(numb, "UTF-8") + "&password=" + pass;*/

			final String urlParameters = "func=LoginToken&login="
					+ URLEncoder.encode(numb, "UTF-8") + "&password=" + pass
					+ "&version=" + version
					+ "&os=" + os
					+ "&device=" + deviceName;

			String request = getHost() + "reglogand.php";
			Log.d("host", request);
			Log.d("POST PARAMS", urlParameters);
			URL url;
			url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			InputStream in = connection.getInputStream();

			InputStreamReader isw = new InputStreamReader(in);

			int data = isw.read();
			while (data != -1) {
				char current = (char) data;
				data = isw.read();
				s += current;
			}
			Log.d("checkCode", "RESPONSE - " + s);

			connection.disconnect();

			JSONObject result = null;
			result = new JSONObject(s);

			message = result.optString("message");
			Log.v("checkCode", " message - " + message);

			if (message.equals("success")) {
				String sess = result.optString("session");

				SharedPreferences.Editor editor = mSettings.edit();
				editor.putString("PHPSESSID", sess);
				editor.commit();

				Log.d("SHARED", "puted PHPSESSID " + mSettings.getString("PHPSESSID", ""));
			}

		} catch (MalformedURLException e3) {
            //Mint.logException(e3);
			// ""
			e3.printStackTrace();
		} catch (ProtocolException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (IOException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (JSONException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (NullPointerException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}

		return message;
	}



	public void getServer() {
        //testable
		PackageInfo pInfo = null;
		String version = "";
		try {
			pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			version = pInfo.versionName;
		} catch (NameNotFoundException e1) {
            //Mint.logException(e1);
			// ""
			e1.printStackTrace();
		}

		//String script = "http://194.48.212.13/mobile/getURL.php";
		String script = "http://194.48.212.13/mobile/getURL2.php";
		//String script = "http://web-cab.elit-taxi.ua/mobile/getURL2.php"; //test Sergey
//		String script = "http://monit.elit-taxi.ua/TaxiDrive/mobile/getURL.php"; //test-server
		String urlParameters = "json={\"Task\":\"GetURL\",\"version\":\"" + version + "\"}";

		String res = "";

		try {

			Log.d("getURL", script);
			Log.d("getURL", urlParameters);

			URL url;
			url = new URL(script);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			InputStream in = connection.getInputStream();

			Scanner in1 = new Scanner(in);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			connection.disconnect();


			Log.d("postRequest", sb.toString());

			JSONObject result = null;
			result = new JSONObject(sb.toString());

			String message = result.optString("message");
			Log.v("getURL", " message - " + message);

			if (message.equals("success")) {
				String serv = result.optString("url");

				res = serv;
			}
		} catch (MalformedURLException e3) {
            //Mint.logException(e3);
			// ""
			e3.printStackTrace();
		} catch (ProtocolException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (IOException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}
		catch (NullPointerException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		} catch (Exception e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}

		Editor editor = mSettings.edit();
        Log.i("Server_check", res);
		editor.putString("Server", res);
		editor.commit();

		Log.d("SHARED", "puted Server " + mSettings.getString("Server", ""));

	}

	public String sendError(String phone, String error, String title) {

		String device = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;

		PackageInfo pInfo = null;
		String version = "";
		try {
			pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			version = pInfo.versionName;
		} catch (NameNotFoundException e1) {
            //Mint.logException(e1);
			// ""
			e1.printStackTrace();
		}

		String message = "";
		String numb = null;
		try {
			numb = URLEncoder.encode(phone, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
            //Mint.logException(e1);
			// ""
			e1.printStackTrace();
		}

		String urlParameters = "json={\"Task\":\"WriteError\",\"sysname\":\"" + "android" + "\"" +
				",\"phone\":\"" + numb + "\",\"error\":\"" + error + "\",\"version\":\""
				+ version + "\",\"device\":\"" + device + "\"}";
		String script = "writeError.php";
		String r = postRequest(script, urlParameters);

		JSONObject jso = null;
		try {
			jso = new JSONObject(r);
		} catch (JSONException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}
		message += jso.optString("message");

		Log.d("sendError", message);

		return message;
	}

	public void sendReview(String string) {
        //testable

		String phone = mSettings.getString("Phone", "");
		try {
			phone = URLEncoder.encode(phone, "UTF-8");
			string = URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}

		String urlParameters = "func=saveUserComments&phone=" + phone + "&body=" + string;
		String script = "templateAll.php";
		String r = postRequest(script, urlParameters);

		Log.d("sendReview", r);
	}

	public List<review> loadComments() {
        //testable

		List<review> ls = new ArrayList<review>();

		String urlParameters = "func=loadComments";
		String script = "templateAll.php";
		String r = postRequest(script, urlParameters);

		//Log.d("loadComments", r);

		try {
			JSONArray jArray = new JSONArray(r);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject res = jArray.getJSONObject(i);

				ls.add(new review(res.optInt("id"), res.optString("bodyComment"),
						res.optString("date"), res.optString("useful"), res.optString("Name")));
			}



		} catch (JSONException e) {
            //Mint.logException(e);
			// ""
			e.printStackTrace();
		}

		return ls;
	}

	public String getUserInfo(String tel) {

		String script = "templateAll.php";
		String phone = mSettings.getString("Phone", "");
		try {
			phone = URLEncoder.encode(phone, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String urlParameters = "func=loadMyInfo" + "&phone=" + phone;
		String res = postRequest(script, urlParameters);
		Log.d(TAG, "getUserInfo res = " + res);

		return res;
	}

	public Boolean saveUserInfo() {
		String script = "templateAll.php";

		SharedPreferences userSettings = ctx.getSharedPreferences("mydata", Context.MODE_PRIVATE);

		String phone = mSettings.getString("Phone", "");
		String firstName = userSettings.getString("Name", "");
		String lastName = userSettings.getString("Patronymic", "");
		String surname = userSettings.getString("Surname", "");
		String email = userSettings.getString("Email", "");
		//String tel2 = mSettings.getString("uPhone", "");
		try {
			phone = URLEncoder.encode(phone, "UTF-8");
			firstName = URLEncoder.encode(firstName, "UTF-8");
			lastName = URLEncoder.encode(lastName, "UTF-8");
			surname = URLEncoder.encode(surname, "UTF-8");
			email = URLEncoder.encode(email, "UTF-8");
			//tel2 = URLEncoder.encode(tel2, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String urlParameters =
				"saveMyInfo=true" +
				"&phone=" + phone +
				"&varFirstName=" + firstName +
				"&varLastName=" + lastName +
				"&varSurName=" + surname +
				"&varTel2=" +
				"&varEmail=" + email;

		String result = postRequest(script, urlParameters);
		Log.d(TAG, "save userInfo res = " + result);

		return false;
	}


    public long getPreorderTime(){
        //testable
		//TODO функция берущая PREORDER_TIME с сервера
        final long[] preorderTime = {3000000l}; //default value
        final String script = "createorder.php"; //may be bug because of php file update
		final String urlParametrs = "func=PreOrderTime";

                try {
                    JSONObject mJSON = new JSONObject(postRequest(script, urlParametrs));
                    preorderTime[0] = mJSON.optLong("preordertime") * 1000;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("PREORDER_TIME", "JSON error = " + e.getMessage());
                }
        return preorderTime[0];
    }



}
