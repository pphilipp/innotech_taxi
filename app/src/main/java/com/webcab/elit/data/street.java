package com.webcab.elit.data;

public class street {
	
	private String name;
	private String id;
	private String geox;
	private String geoy;
	
	public street(String name, String id, String geox, String geoy) {
		super();
		this.name = name;
		this.id = id;
		this.geox = geox;
		this.geoy = geoy;
	}
	public String getGeox() {
		return geox;
	}
	public String getGeoy() {
		return geoy;
	}
	public String getName() {
		return name;
	}
	public String getId() {
		return id;
	}

}
