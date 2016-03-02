package com.webcab.elit.net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.webcab.elit.Log;
import com.webcab.elit.data.autos;
import com.webcab.elit.data.house;
import com.webcab.elit.data.street;

import android.content.Context;
import android.content.SharedPreferences;

public class SocketConnection {

	private String host = "194.48.212.1";
	private int port = 1112;
	//private String host = "194.48.212.10"; //real
	//private int port = 1113; //real
	// private int timeout = 200;
	Socket s = null;

	public List<street> getStreetName(String str) {
		List<street> mStr = new ArrayList<street>();
		try {
			s = new Socket(host, port);
			s.setSoTimeout(300);

			JSONObject json = new JSONObject();
			json.put("Task", "GetStreetName");
			json.put("StreetName", str);
			System.out.println(json.toString());

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			out.write(json.toString());
			out.newLine();
			out.flush();

			System.out.println("sent");

			System.out.println("open input");

			InputStream inStream = s.getInputStream();
			Scanner in1 = new Scanner(inStream);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			System.out.println("closing socket");
			s.close();

			JSONArray response = new JSONArray(sb.toString());
			for (int i = 0; i < response.length(); i++) {
				JSONObject res = response.getJSONObject(i);
				// Log.v("id", i + " - " + res.optString("id"));
				// Log.v("value", i + " - " + res.optString("value"));
				mStr.add(new street(res.optString("value"), res.optString("id"), res.optString("geox"), res.optString("geoy")));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// ""
			e.printStackTrace();
		}
		return mStr;
	}

	public String isPhoneInBlackList(String p) {
		Log.d("isPhoneInBlackList", "started isPhoneInBlackList");
		String message = "";
		String phone = "";
		try {
			phone = URLEncoder.encode(p, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// ""
			e1.printStackTrace();
		}
		try {
			s = new Socket(host, port);
			s.setSoTimeout(150);

			JSONObject json = new JSONObject();
			json.put("Task", "CheckPhoneInBlackList");
			json.put("phone", phone);
			System.out.println(json.toString());

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			out.write(json.toString());
			out.newLine();
			out.flush();

			System.out.println("sent");

			System.out.println("open input");

			InputStream inStream = s.getInputStream();
			Scanner in1 = new Scanner(inStream);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			System.out.println("closing socket " + sb.toString());
			s.close();

			JSONObject response = new JSONObject(sb.toString());
			message = response.optString("message");
			Log.v("message", "- " + response.optString("message"));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// ""
			e.printStackTrace();
		}

		return message;
	}

	public String getAdditServices() {
		Log.d("getAdditServices", "started getAdditServices");
		String message = "";

		try {
			s = new Socket(host, port);
			s.setSoTimeout(150);

			JSONObject json = new JSONObject();
			json.put("Task", "GetAdditionalServices");
			System.out.println(json.toString());

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			out.write(json.toString());
			out.newLine();
			out.flush();

			System.out.println("sent");

			System.out.println("open input");

			InputStream inStream = s.getInputStream();
			Scanner in1 = new Scanner(inStream);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			System.out.println("closing socket");
			//System.out.println("" + sb.toString());
			s.close();

			JSONObject resp = new JSONObject(sb.toString());
			//message = resp.optString("additionalservices");

			// 03-11 04:24:43.537: I/System.out(1293): "additionalservices": [
			JSONArray response = resp.getJSONArray("additionalservices");
			for (int i = 0; i < response.length(); i++) {
				JSONObject res = response.getJSONObject(i);
				Log.v("id", i + " - " + res.optInt("id") + " - " + res.optString("name") + " - " + res.optString("cost"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// ""
			e.printStackTrace();
		}

		return message;
	}

	public String getAutoT() {
		Log.d("getAutoT", "started getAutoT");
		String message = "";

		try {
			s = new Socket(host, port);
			s.setSoTimeout(150);

			JSONObject json = new JSONObject();
			json.put("Task", "GetTimeAutoSearch");
			System.out.println(json.toString());

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			out.write(json.toString());
			out.newLine();
			out.flush();

			System.out.println("sent");

			System.out.println("open input");

			InputStream inStream = s.getInputStream();
			Scanner in1 = new Scanner(inStream);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			System.out.println("closing socket");
			// System.out.println("" + sb.toString());
			s.close();

			JSONObject response = new JSONObject(sb.toString());
			message = response.optString("timeautosearchanswer");
			Log.v("message", "" + message);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// ""
			e.printStackTrace();
		}

		return message;
	}

	public List<house> getStreetHouse(String streetId, String house) {

		List<house> mHouse = new ArrayList<house>();
		String value, id, x, y;

		try {
			s = new Socket(host, port);
			s.setSoTimeout(200);

			JSONObject json = new JSONObject();
			json.put("Task", "GetStreetHouse");
			json.put("HouseNumber", house);
			json.put("streetID", streetId);
			System.out.println(json.toString());

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			out.write(json.toString());
			out.newLine();
			out.flush();

			System.out.println("sent");

			System.out.println("open input");

			InputStream inStream = s.getInputStream();
			Scanner in1 = new Scanner(inStream);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			System.out.println("closing socket " + sb.toString());
			s.close();

			JSONArray response = new JSONArray(sb.toString());
			for (int i = 0; i < response.length(); i++) {
				JSONObject res = response.getJSONObject(i);
				value = res.optString("value");
				Log.v("value", "- " + value);
				id = res.optString("id");
				Log.v("id", "- " + id);
				x = res.optString("geox");
				Log.v("x", "- " + x);
				y = res.optString("geoy");
				Log.v("y", "- " + y);

				mHouse.add(new house(value, id, x, y));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// ""
			e.printStackTrace();
		}

		return mHouse;
	}

	public void getRoute(Context ctx) {

		SharedPreferences mSettings = ctx.getSharedPreferences("mysettings",
				Context.MODE_PRIVATE);
		String streetId = mSettings.getString("StreetId", "");
		String dom = mSettings.getString("Dom", "");
		String streetId2 = mSettings.getString("StreetId2", "");
		String dom2 = mSettings.getString("Dom2", "");

		try {
			s = new Socket(host, port);
			s.setSoTimeout(1000);

			JSONObject json = new JSONObject();
			json.put("Task", "GetRoute");
			json.put("streetid1", streetId);
			json.put("house1", dom);

			JSONObject js1 = new JSONObject();
			js1.put("streetid", streetId2);
			js1.put("house", dom2);

			JSONArray suborder = new JSONArray();
			suborder.put(0, js1);

			json.put("suborder", suborder);
			json.put("autotariffclassid", "4");

			System.out.println(json.toString());

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			out.write(json.toString());
			out.newLine();
			out.flush();

			System.out.println("sent");

			System.out.println("open input");

			InputStream inStream = s.getInputStream();
			Scanner in1 = new Scanner(inStream);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			System.out.println("closing socket " + sb.toString());
			s.close();

			JSONObject response = new JSONObject(sb.toString());
			String distance = response.optString("distance");
			String time = response.optString("time");
			String price = response.optString("price");

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

			Log.d("getRoute", distance);
			Log.d("getRoute", time);
			Log.d("getRoute", price);
			Log.d("getRoute", "x - " + routex.size());
			Log.d("getRoute", "y - " + routey.size());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// ""
			e.printStackTrace();
		}

	}

	public void createOrder(Context ctx, String phone) {

		SharedPreferences mSettings = ctx.getSharedPreferences("mysettings",
				Context.MODE_PRIVATE);
		String streetId = mSettings.getString("StreetId", "");
		String dom = mSettings.getString("Dom", "");
		String streetId2 = mSettings.getString("StreetId2", "");
		String dom2 = mSettings.getString("Dom2", "");

		try {
			s = new Socket(host, port);
			s.setSoTimeout(400);

			JSONObject json = new JSONObject();
			json.put("Task", "CreateOrder");
			json.put("phone", phone);
			json.put("streetid1", streetId);
			json.put("house1", dom);

			JSONObject js1 = new JSONObject();
			js1.put("streetid", streetId2);
			js1.put("house", dom2);

			JSONArray suborder = new JSONArray();
			suborder.put(0, js1);

			json.put("suborder", suborder);
			json.put("autotariffclassid", "4");

			System.out.println(json.toString());

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			out.write(json.toString());
			out.newLine();
			out.flush();

			System.out.println("sent");

			System.out.println("open input");

			InputStream inStream = s.getInputStream();
			Scanner in1 = new Scanner(inStream);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			System.out.println("closing socket " + sb.toString());
			s.close();

			JSONObject response = new JSONObject(sb.toString());
			int orderId = response.optInt("orderid");
			String autocallsign = response.optString("autocallsign");
			int callsignid = response.optInt("callsignid");
			String statenumber = response.optString("statenumber");
			String carbrand = response.optString("carbrand");
			String carcolor = response.optString("carcolor");
			String drivename = response.optString("drivename");
			String geox = response.optString("geox");
			String geoy = response.optString("geoy");

			Log.d("createOrder", orderId + " " + autocallsign + " "
					+ callsignid + " " + statenumber + " " + carbrand + " "
					+ carcolor + " " + drivename + " " + geox + " " + geoy);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// ""
			e.printStackTrace();
		}

	}
	
	public autos getCoordinatesAuto(String autodriverid) {
		autos auto = null;
		int statusforweborder;
		String year, status, autotariffclassid, autocallsign, callsignid1, statenumber, carbrand, carcolor, drivename, geox, geoy;

		
		Log.d("getAutoT", "started getAutoT");
		String message = "";

		try {
			s = new Socket(host, port);
			s.setSoTimeout(150);

			JSONObject json = new JSONObject();
			json.put("Task", "GetCoordinatesAuto");
			json.put("autodriverid", autodriverid);
			System.out.println(json.toString());

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			out.write(json.toString());
			out.newLine();
			out.flush();

			System.out.println("sent");

			System.out.println("open input");

			InputStream inStream = s.getInputStream();
			Scanner in1 = new Scanner(inStream);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			System.out.println("closing socket");
			System.out.println("" + sb.toString());
			s.close();
			
			JSONObject res = new JSONObject(sb.toString());
			statusforweborder = res.optInt("statusforweborder");
			// Log.v("GetCoordinatesAuto", " statusforweborder - " +
			// statusforweborder);

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

//			JSONObject response = new JSONObject(sb.toString());
//			message = response.optString("timeautosearchanswer");
//			Log.v("message", "" + message);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// ""
			e.printStackTrace();
		}

		return auto;
	}
	
	public String getCoordinatesAllAuto() {
		Log.d("getAutoT", "started getAutoT");
		String message = "";

		try {
			s = new Socket(host, port);
			s.setSoTimeout(150);

			JSONObject json = new JSONObject();
			json.put("Task", "GetCoordinatesAllAuto");
			System.out.println(json.toString());

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			out.write(json.toString());
			out.newLine();
			out.flush();

			System.out.println("sent");

			System.out.println("open input");

			InputStream inStream = s.getInputStream();
			Scanner in1 = new Scanner(inStream);

			StringBuilder sb = new StringBuilder();
			// String line="";

			while (in1.hasNextLine()) {
				// line += in1.nextLine();
				sb.append(in1.nextLine() + "\n");
			}

			System.out.println("closing socket");
			System.out.println("" + sb.toString());
			s.close();

//			JSONObject response = new JSONObject(sb.toString());
//			message = response.optString("timeautosearchanswer");
//			Log.v("message", "" + message);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// ""
			e.printStackTrace();
		}

		return message;
	}

}
