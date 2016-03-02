package com.webcab.elit.data;

import java.util.List;

public class route {
	
	String distance, price, time;
	List<String> routex, routey;
	List<Integer> pointsOnRoutePositions;

	public route(String distance, String price, String time, List<String> routex, List<String> routey) {
		super();
		this.distance = distance;
		this.price = price;
		this.time = time;
		this.routex = routex;
		this.routey = routey;
	}

	public String getDistance() {
		return distance;
	}

	public String getPrice() {
		return price;
	}

	public String getTime() {
		return time;
	}

	public List<String> getRouteX() {
		return routex;
	}

	public List<String> getRouteY() {
		return routey;
	}

	public List<Integer> getPointsOnRoutePositions() {return pointsOnRoutePositions; }

	public void addPointOfRoute(int i) {pointsOnRoutePositions.add(i); }

}
