package com.youa.mobile.more.data;

public class JptjData {

	// public ArrayList<Jptj> jptjs;
	//
	// public static class Jptj {
	public String name;
	public String info;
	public String pic;
	public String url;
	public int picId;

	public JptjData(String name, String desc, String image_id, String href) {
		super();
		this.name = name;
		this.info = desc;
		this.pic = image_id;
		this.url = href;
	}

	public JptjData(String name, String desc, int image_id, String href) {
		super();
		this.name = name;
		this.info = desc;
		this.picId = image_id;
		this.url = href;
	}
	// }
}
