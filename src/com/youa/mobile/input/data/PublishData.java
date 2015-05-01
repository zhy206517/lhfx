package com.youa.mobile.input.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.login.auth.BaseToken;

public class PublishData implements Serializable {
	private boolean state = true;
	private String userId;
	private String consumePlace;
	private String consumeAvPrice;
	public String mPeopleNum;
	public String mConsumePrice;
	private boolean isManyPeople;
	private ArrayList<ImageData> contentImage;
	private String content;
	private int latitude;
	private int longitude;
	private String plid;
	private String type;
	private List<BaseToken> tokens;

	public PublishData(String content, ArrayList contentImage,
			String consumePlace, String consumePrice, boolean isManyPeople,
			int latitude, int longitude, String plid,String price,String peopleNum) {
		userId = ApplicationManager.getInstance().getUserId();
		this.consumePlace = consumePlace;
		this.consumeAvPrice = consumePrice;
		this.isManyPeople = isManyPeople;
		this.contentImage = contentImage;
		this.content = content;
		this.latitude = latitude;
		this.longitude = longitude;
		this.plid = plid;
		this.mPeopleNum=price;
		this.mConsumePrice=peopleNum;
	}

	public PublishData(String content, ArrayList contentImage,
			String consumePlace, String consumePrice, boolean isManyPeople,
			int latitude, int longitude, String plid, String type, List<BaseToken> tokens) {
		userId = ApplicationManager.getInstance().getUserId();
		this.consumePlace = consumePlace;
		this.consumeAvPrice = consumePrice;
		this.isManyPeople = isManyPeople;
		this.contentImage = contentImage;
		this.content = content;
		this.latitude = latitude;
		this.longitude = longitude;
		this.plid = plid;
		this.type = type;
		this.tokens = tokens;
	}

	public PublishData() {
		userId = ApplicationManager.getInstance().getUserId();
	}

	public String getPlaceType() {
		return type;
	}

	public void setPlaceType(String type) {
		this.type = type;
	}

	public boolean getPublishState() {
		return state;
	}

	public void setPublishState(boolean state) {
		this.state = state;
	}

	public String getPlid() {
		return plid;
	}

	public void setPlid(String plid) {
		this.plid = plid;
	}

	public String getConsumePlace() {
		return consumePlace;
	}

	public void setConsumePlace(String consumePlace) {
		this.consumePlace = consumePlace;
	}

	public String getConsumePrice() {
		return consumeAvPrice;
	}

	public void setConsumePrice(String consumePrice) {
		this.consumeAvPrice = consumePrice;
	}

	public ArrayList<ImageData> getContentImage() {
		if (contentImage == null) {
			return new ArrayList<ImageData>();
		}
		return contentImage;
	}

	public void setContentImage(ArrayList imageData) {
		this.contentImage = imageData;
	}

	public void delContentImage(String path) {
		if (contentImage != null) {
			ImageData data;
			for (int i = 0; i < contentImage.size(); i++) {
				data = contentImage.get(i);
				if (path.equals(data.imagePath)) {
					contentImage.remove(i);
					break;
				}
			}
		}
	}

	public void addContentImage(ImageData imageData) {
		if (contentImage != null) {
			contentImage.add(imageData);
		} else {
			contentImage = new ArrayList<ImageData>();
			contentImage.add(imageData);
		}
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserId() {
		return userId;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public boolean isManyPeople() {
		return isManyPeople;
	}

	public void setManyPeople(boolean isManyPeople) {
		this.isManyPeople = isManyPeople;
	}

	public List<BaseToken> getTokens() {
		return tokens;
	}

	public void setTokens(List<BaseToken> tokens) {
		this.tokens = tokens;
	}
	
}
