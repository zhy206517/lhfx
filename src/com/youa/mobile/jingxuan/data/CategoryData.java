package com.youa.mobile.jingxuan.data;

import java.util.ArrayList;

import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class CategoryData {
	public String mId;
	public String mName;
	public ArrayList<TagInfoData> mChildren;

	public CategoryData(JsonObject obj) {
		mId = obj.getString("clsid");
		mName = obj.getString("name");
		JsonArray childArr = obj.getJsonArray("children");
		if (childArr != null && childArr.size() > 0) {
			TagInfoData data;
			for (int i = 0; i < childArr.size(); i++) {
				JsonObject jsonObj = (JsonObject) childArr.get(i);
				if (jsonObj == null || jsonObj.size() == 0) {
					continue;
				}
				data = new TagInfoData(jsonObj);
				if (mChildren == null) {
					mChildren = new ArrayList<TagInfoData>();
				}
				mChildren.add(data);
			}
		}

	}
}
