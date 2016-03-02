package com.webcab.elit.data;

public class rt {
	
	private int id;
	private String serverTemplateID;
	private String title;

	private String city;
	private String desc;
	
	private String str;
	private String strid;
	private String dom;
	private String domid;
	private String parad;
	private String flat;
	private String prim;

	public String getFlat() {
		return flat;
	}

	public void setFlat(String flat) {
		this.flat = flat;
	}

	private String desc2;
	public String getDesc2() {
		return desc2;
	}

	public String getStr2() {
		return str2;
	}

	public String getStrid2() {
		return strid2;
	}

	public String getDom2() {
		return dom2;
	}

	public String getDomid2() {
		return domid2;
	}

	private String str2;
	private String strid2;
	private String dom2;
	private String domid2;
	
	private String auto;
	
	public String getAutoClass() {
		return auto;
	}
	
	public rt(int id, String title, String desc, String str, String strid,
			String dom, String domid, String parad, String prim, String desc2, String str2, String strid2, String dom2,
			String domid2, String autoClass) {
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
		
		this.desc2 = desc2;
		this.str2 = str2;
		this.strid2 = strid2;
		this.dom2 = dom2;
		this.domid2 = domid2;
		
		this.auto = autoClass;
	}

	public void setServerTemplateID(String serverTemplateID) {
		this.serverTemplateID = serverTemplateID;
	}

	public String getServerTemplateID() {return serverTemplateID;}

	public void setCity(String city) {
		this.city = city;
	}

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

	public String getCity() { return city;}

}
