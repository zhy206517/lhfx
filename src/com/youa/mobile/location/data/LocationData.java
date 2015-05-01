package com.youa.mobile.location.data;

public class LocationData {
	public int id;
	public int longitude;
	public int latitude;
	public String locName;
	public String sPid;
	public String addName;
	public String type;

	public LocationData() {

	}
	public LocationData(int x, int y, String placeName, String pid,
			String address, String type) {
		this.longitude = x;
		this.latitude = y;
		this.locName = placeName;
		this.sPid = pid;
		this.addName = address;
		this.type = type;
	}
	public LocationData(int id, int x, int y, String placeName, String pid,
			String address, String type) {
		this.id = id;
		this.longitude = x;
		this.latitude = y;
		this.locName = placeName;
		this.sPid = pid;
		this.addName = address;
		this.type = type;
	}
}