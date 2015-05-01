package com.youa.mobile.life.data;

import com.youa.mobile.parser.JsonObject;

public class SuperPeopleClassify {
	public String id;
	public String name;
	public String isLeaf;
	public String  level;
	public SuperPeopleClassify(JsonObject jsonObj){
		id = jsonObj.getString("id");
		name = jsonObj.getString("name");
		isLeaf= jsonObj.getString(isLeaf);
		level= jsonObj.getString("level");
	}
}
