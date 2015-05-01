package com.youa.mobile.common.data;

import java.util.List;


public interface FeedData {

	public String getUserName();

	public String getHeaderImgid();

	public String getTime();

	public String getLike_num();

	public String getComment_num();

	public String getTranspond_num();

	public String getContent();

	public String getPostId();

	public String getPublicId();

	public String getFeedType();

	public String getOriginId();

	public String getOriginUid();
	
	public List<ImageData> getImageData();
	
	public interface ImageData {
		public String getImageId();
		
		public String getImageDesc();
	}
}
