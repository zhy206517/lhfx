package com.youa.mobile.common.widget;

import java.util.Random;

import android.util.Log;

import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;

public class FlowTag {
	private int flowId;
	private String fileName;
	HomeData data;
	public static int imageWidth;
	public int imageHeight;
	public int ItemWidth;
	public int ItemHeight;
	public boolean hasPic = false;
	private static int SMALL_HEIGHT[] = new int[] { 90, 220, 180, 120 };
	private static int NORMAL_HEIGHT[] = new int[] { 135, 330, 270, 180 };
	private static int LARGE_HEIGHT[] = new int[] { 180, 440, 360, 240 };
	private static int SMALL_WIDTH = 110;
	private static int NORMAL_WIDTH = 160;
	private static int LARGE_WIDTH = 220;
	static {
		if (ApplicationManager.getInstance().getDensityDpi() <= 160) {
			imageWidth = SMALL_WIDTH;
		} else if (ApplicationManager.getInstance().getDensityDpi() <= 240) {
			imageWidth = NORMAL_WIDTH;
		} else {
			imageWidth = LARGE_WIDTH;
		}
	}
	public HomeData getData() {
		return data;
	}

	public void setData(HomeData data) {
		this.data = data;
		if(getUser().contentImg[0].width!=0&&getUser().contentImg[0].height!=0){
			hasPic=true;
		}
	}

	public final int what = 1;
	public User user;

	public User getUser() {
		if (!"0".equals(data.PublicUser.feedType)) {
			user = data.originUser;
		} else {
			user = data.PublicUser;
		}
		return user;
	}

	public int getFlowId() {
		return flowId;
	}

	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getItemWidth() {
		return ItemWidth;
	}

	public void setItemWidth(int itemWidth) {
		ItemWidth = itemWidth;
	}

	public void setItemHeight(int itemHeight) {
		ItemHeight = itemHeight;
	}

	// public int getImageWidth() {
	// // imageWidth = user.contentImg[0].width;
	// return imageWidth;
	// }

	public int getImageHeight() {
		imageHeight = getUser().contentImg[0].height;
		if (imageHeight == 0) {
			getRandomHeight();
		}else{
			imageHeight=imageHeight*imageWidth/getUser().contentImg[0].width;
			if(imageHeight>ApplicationManager.getInstance().getHeight()){
				getRandomHeight();
				hasPic=false;
			}
		}
		return imageHeight;
		// imageWidth = user.contentImg[0].height;
	}

	public void getRandomHeight() {
		Random r = new Random();
		int i = r.nextInt(4);
		if (imageWidth == SMALL_WIDTH) {
			imageHeight = SMALL_HEIGHT[i];
		} else if (imageWidth == NORMAL_WIDTH) {
			imageHeight = NORMAL_HEIGHT[i];
		} else {
			imageHeight = LARGE_HEIGHT[i];
		}
	}
}
