package com.webcab.elit.data;

public class addr {
	
	private int id;


	private String title;
	private String serverTemplateID;
	private String desc;
	
	private String str;
	private String strid;
	private String dom;
	private String domid;
	private String parad;
	private String prim;
	
	public addr(int id, String title, String desc, String str, String strid,
			String dom, String domid, String parad, String prim) {
		super();
		this.id = id;
		this.title = title;
		this.desc = desc;
		this.str = str;
		this.strid = strid;
		this.dom = dom;
		this.domid = domid;
		this.parad = parad;
		this.prim = prim;
	}

	public void setServerTemplateID(String serverTemplateID) {
		this.serverTemplateID = serverTemplateID;
	}

	public String getServerTemplateID() {return serverTemplateID;}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public String getStr() {
		return str;
	}
	
	public String getStrid() {
		return strid;
	}
	
	public String getDom() {
		return dom;
	}
	
	public String getDomid() {
		return domid;
	}
	
	public String getParad() {
		return parad;
	}
	
	public String getPrim() {
		return prim;
	}

}
