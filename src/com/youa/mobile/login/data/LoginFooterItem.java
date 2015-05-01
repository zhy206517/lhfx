package com.youa.mobile.login.data;

public class LoginFooterItem {
	private int logoResId;
	private String itemText;
	private Class turnTo;
	
	public LoginFooterItem(String itemText, int logoResId, Class turnTo) {
		this.itemText = itemText;
		this.logoResId = logoResId;
		this.turnTo = turnTo;
	}
	
	public int getLogoResId() {
		return logoResId;
	}

	public void setLogoResId(int logoResId) {
		this.logoResId = logoResId;
	}

	public String getItemText() {
		return itemText;
	}
	public void setItemText(String itemText) {
		this.itemText = itemText;
	}
	public Class getTurnTo() {
		return turnTo;
	}
}
