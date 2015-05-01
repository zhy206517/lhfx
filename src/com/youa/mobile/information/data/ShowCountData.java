package com.youa.mobile.information.data;

public class ShowCountData {
	private int feedCount;
	private int favorCount;
	private int attentCount;
	private int fansCount;
	private int newsCount;
	private int belikeCount;
	private int commentCount;
	private int addmeCount;

	public ShowCountData(int feedCount, int favorCount, int attentCount,
			int fansCount, int totalNum, int belikeCount, int commentCount,
			int addmeCount) {
		this.feedCount = feedCount;
		this.favorCount = favorCount;
		this.attentCount = attentCount;
		this.fansCount = fansCount;
		this.newsCount = totalNum;
		this.belikeCount = belikeCount;
		this.commentCount = commentCount;
		this.addmeCount = addmeCount;

	}

	public int getFeedCount() {
		return feedCount;
	}

	public int getFavorCount() {
		return favorCount;
	}

	public int getAttentCount() {
		return attentCount;
	}

	public int getFansCount() {
		return fansCount;
	}

	public int getNewsCount() {
		return newsCount;
	}

	public int getBelikeCount() {
		return belikeCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public int getAddmeCount() {
		return addmeCount;
	}
}
