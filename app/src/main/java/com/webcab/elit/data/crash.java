package com.webcab.elit.data;

public class crash {
	
	private int id;
	private String title;
	private String error;
	private int sent;
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getError() {
		return error;
	}
	public int getSent() {
		return sent;
	}
	public crash(int id, String title, String error, int sent) {
		super();
		this.id = id;
		this.title = title;
		this.error = error;
		this.sent = sent;
	}

}
