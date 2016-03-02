package com.webcab.elit.data;

public class addr2 {

	String sreetname, streetid, housenumber, houseID, geox, geoy;

	public addr2(String sreetname, String streetid, String housenumber,
			String geox, String geoy) {
		super();
		this.sreetname = sreetname;
		this.streetid = streetid;
		this.housenumber = housenumber;
		this.geox = geox;
		this.geoy = geoy;
	}

	public String getSreetname() {
		return sreetname;
	}

	public String getStreetid() {
		return streetid;
	}

	public String getHousenumber() {
		return housenumber;
	}

	public String getGeox() {
		return geox;
	}

	public String getGeoy() {
		return geoy;
	}

	public String getHouseID() {
		return houseID;
	}

	public void setHouseID(String houseID) {this.houseID = houseID;}

}
