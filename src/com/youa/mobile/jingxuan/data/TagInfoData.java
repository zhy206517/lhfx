package com.youa.mobile.jingxuan.data;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.youa.mobile.parser.JsonArray;
import com.youa.mobile.parser.JsonObject;

public class TagInfoData {
	public String tagId;
	public String tagName;
	public ArrayList<TagInfoData> mChildren;

	public TagInfoData(JsonObject obj) {
		String str = obj.getString("level");
		long level;
		if(TextUtils.isEmpty(str)){
			level=0;
		}else{
			level=Long.parseLong(str);
		}
		if (level == 2) {
			tagId = obj.getString("tid");
			tagName = obj.getString("name");
			JsonArray array = obj.getJsonArray("tagArr");
			if (array != null && array.size() > 0) {
				mChildren = new ArrayList<TagInfoData>();
				TagInfoData data;
				for (int i = 0; i < array.size(); i++) {
					JsonObject jsonObject = (JsonObject) array.get(i);
					if (jsonObject == null || jsonObject.size() == 0) {
						continue;
					}
					data = new TagInfoData(jsonObject);
					mChildren.add(data);
				}
			}
		} else {
			tagId = obj.getString("tid");
			tagName = obj.getString("tname");
		}
	}

	public TagInfoData() {

	}
}
