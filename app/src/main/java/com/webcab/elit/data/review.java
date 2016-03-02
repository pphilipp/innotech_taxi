package com.webcab.elit.data;

import com.webcab.elit.Log;

public class review {
	
	private int id;
	private String bodyComment;
	private String date;
	private String useful;
	private String Name;
	
	public review(int id, String bodyComment, String date, String useful,
			String name) {
		super();
		this.id = id;
		this.bodyComment = bodyComment;
		this.date = date;
		this.useful = useful;
		Name = name;
	}

	public int getId() {
		return id;
	}

	public String getBodyComment() {
		return bodyComment;
	}

	public String getDate() {
		return date;
	}

	public String getUseful() {
		return useful;
	}

	public String getName() {
		return Name;
	}

	
	

}
