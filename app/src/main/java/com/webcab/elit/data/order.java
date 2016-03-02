package com.webcab.elit.data;

public class order {

	int orderid, callsignid;
	String autocallsign, statenumber, carbrand, carcolor, drivename, geox, geoy, autodriverid, driverPhone;
	Long time;

	public order(int orderid, String autocallsign, int callsignid,
			String statenumber, String carbrand, String carcolor,
			String drivename, String geox, String geoy, String autodriverid, Long time, String phone) {
		super();
		this.orderid = orderid;
		this.autocallsign = autocallsign;
		this.callsignid = callsignid;
		this.statenumber = statenumber;
		this.carbrand = carbrand;
		this.carcolor = carcolor;
		this.drivename = drivename;
		this.geox = geox;
		this.geoy = geoy;
		this.autodriverid = autodriverid;
		this.time=time;
		this.driverPhone=phone;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	public void setCallsignid(int callsignid) {
		this.callsignid = callsignid;
	}

	public void setAutocallsign(String autocallsign) {
		this.autocallsign = autocallsign;
	}

	public void setStatenumber(String statenumber) {
		this.statenumber = statenumber;
	}

	public void setCarbrand(String carbrand) {
		this.carbrand = carbrand;
	}

	public void setCarcolor(String carcolor) {
		this.carcolor = carcolor;
	}

	public void setDrivename(String drivename) {
		this.drivename = drivename;
	}

	public void setGeox(String geox) {
		this.geox = geox;
	}

	public void setGeoy(String geoy) {
		this.geoy = geoy;
	}

	public void setAutodriverid(String autodriverid) {
		this.autodriverid = autodriverid;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public int getOrderid() {
		return orderid;
	}

	public String getAutocallsign() {
		return autocallsign;
	}

	public int getCallsignid() {
		return callsignid;
	}

	public String getStatenumber() {
		return statenumber;
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

	public String getAutodriverid() {
		return autodriverid;
	}
	public Long getTime(){
		return time;
	}
	public String getPhone(){
		return driverPhone;
	}
}
