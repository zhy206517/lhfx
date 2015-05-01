package com.youa.mobile.life.data;

public class LifeItemData {

	private String title;
	private int resId;
	private Class turnTo;

	public LifeItemData(String title, int resId, Class turnTo) {
		this.title = title;
		this.resId = resId;
		this.turnTo = turnTo;
	}

	public String getTitle() {
		return title;
	}

	public int getResId() {
		return resId;
	}

	public Class getTurnTo() {
		return turnTo;
	}

	
}
