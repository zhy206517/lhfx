package com.youa.mobile.input.data;

import java.io.Serializable;

import android.text.TextUtils;

import com.youa.mobile.parser.JsonObject;

public class ImageData implements Serializable {
	public String imageId;
	public String imagePath;
	public String imageDes;
	public int width;
	public int height;

	public ImageData() {
	}

	public ImageData(JsonObject obj) {
		imageId= obj.getString("imageId");
		imagePath = obj.getString("imagePath");
		imageDes = obj.getString("imageDes");
		width = TextUtils.isEmpty(obj.getString("width")) ? 0 : Integer
				.parseInt(obj.getString("width"));
		height = TextUtils.isEmpty(obj.getString("height")) ? 0 : Integer
				.parseInt(obj.getString("height"));
	}

	public JsonObject getJsonObject() {
		JsonObject json = new JsonObject();
		json.put("imageId", imageId);
		json.put("imagePath", imagePath);
		json.put("imageDes", imageDes);
		json.put("width", width);
		json.put("height", height);
		return json;
	}
}
