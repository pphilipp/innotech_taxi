package com.webcab.elit.data;

public class templ {
	
	private int id;
	private String serverTemplID;
	private String title;
	private String desc;
	private boolean isAddr; //if true  it is address else it is route

	public String getServerTemplID() {return serverTemplID;}
	public int getId() {return id;}
	public String getTitle() {
		return title;
	}
	public String getDesc() {
		return desc;
	}
	public boolean isAddr() {
		return isAddr;
	}
	public templ(int id, String title, String desc, boolean isAddr) {
		super();
		this.id = id;
		this.title = title;
		this.desc = desc;
		this.isAddr = isAddr;
	}

	public void setServerTemplID(String serverTemplID) {
		this.serverTemplID = serverTemplID;
	}

}
