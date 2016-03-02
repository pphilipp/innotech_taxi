package com.webcab.elit.data;

public class autos {

	int statusforweborder;
	String year, status, autotariffclassid, autocallsign, callsignid,
			statenumber, carbrand, carcolor, drivename, geox, geoy;
	
	public autos(int statusforweborder, String year, String status,
			String autotariffclassid, String autocallsign, String callsignid,
			String statenumber, String carbrand, String carcolor,
			String drivename, String geox, String geoy) {
		super();
		this.statusforweborder = statusforweborder;
		this.year = year;
		this.status = status;
		this.autotariffclassid = autotariffclassid;
		this.autocallsign = autocallsign;
		this.callsignid = callsignid;
		this.statenumber = statenumber;
		this.carbrand = carbrand;
		this.carcolor = carcolor;
		this.drivename = drivename;
		this.geox = geox;
		this.geoy = geoy;
	}

	public int getStatusforweborder() {
		return statusforweborder;
	}

	public String getYear() {
		return year;
	}

	public String getStatus() {
		return status;
	}

	public String getAutotariffclassid() {
		return autotariffclassid;
	}

	public String getAutocallsign() {
		return autocallsign;
	}

	public String getCallsignid() {
		return callsignid;
	}

	public String getStatenumber() {
		return statenumber.equals("") ? "-- --" : statenumber;
	}

	public String getCarbrand() {
		return carbrand;
	}

	public String getCarcolor() {
		return carcolor;
	}

	public String getDrivename() {
		return drivename;
	}

	public String getGeox() {
		return geox;
	}

	public String getGeoy() {
		return geoy;
	}

}
