package com.youa.mobile.jingxuan.data;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.text.TextUtils;
import android.util.Log;

public class AlbumData {

	private String count_key = "topic.count";
	private String common_key = "topic";
	private String link_key = ".link";
	private String image_key = ".image";
	private String text_key = ".text";
	public List<AlbumItemData> AlbumItemDataList = new ArrayList<AlbumItemData>();

	public AlbumData(Properties p) {
		String count = p.getProperty(count_key);
		if (!TextUtils.isEmpty(count)) {
			int index = Integer.parseInt(count);
			AlbumItemData itemData = null;
			for (int i = 1; i <= index; i++) {
				itemData = new AlbumItemData();
				itemData.link = getTopicLink(p.getProperty(common_key + i
						+ link_key));
				itemData.image = getImageId(p.getProperty(common_key + i
						+ image_key));
				itemData.text = isoToGbk(p.getProperty(common_key + i
						+ text_key));
				AlbumItemDataList.add(itemData);
			}
		}
	}

	/**
	 * 将ISO-8859-1转化为GBK
	 * 
	 * @param para
	 * @return
	 */
	private String isoToGbk(String para) {
		try {
			return new String(para.getBytes("ISO-8859-1"), "gbk");
		} catch (UnsupportedEncodingException e) {
			return "";
		} catch (Exception e) {
			return "";
		}
	}

	private String getImageId(String imgUrl) {
		String imageId = null;
		if (!TextUtils.isEmpty(imgUrl)) {
			String[] str = imgUrl.split("/");
			imageId = str[str.length - 1];
		}
		return imageId;
	}

	private String getTopicLink(String uri) {
		String link = null;
		if (!TextUtils.isEmpty(uri)) {
			String[] str = uri.split("/");
			link = str[str.length - 1];
		}
		return link;
	}
}
