package com.youa.mobile.news;

import android.content.Intent;
import android.os.Bundle;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseOptionPage;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.input.CommentPage;
import com.youa.mobile.news.util.NewsUtil;

public class CommentDialogPage extends BaseOptionPage {

	public static final int RESULT_COMMENT = 0;
	public static final int RESULT_SHOW = 1;
	private String postId;
	private String orgId;
	private boolean isComment;
	private String cmtId;
	private String replyName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		button1.setText(R.string.news_comment_reply);
		button2.setText(R.string.news_farorite_show);//news_comment_show
		isComment 	= getIntent().getExtras().getBoolean(CommentPage.KEY_TYPE);
		postId 		= getIntent().getExtras().getString(CommentPage.KEY_SOURCE);
		orgId 		= getIntent().getExtras().getString(CommentPage.KEY_ORG_ID);
		cmtId 		= getIntent().getExtras().getString(CommentPage.KEY_CMT_ID);
		replyName 	= getIntent().getExtras().getString(CommentPage.KEY_REPLY_NAME);
		NewsUtil.LOGD("CommentDialogPage", "" + isComment);
		NewsUtil.LOGD("CommentDialogPage", postId);
		NewsUtil.LOGD("CommentDialogPage", orgId);
		NewsUtil.LOGD("CommentDialogPage", cmtId);
		
	}

	@Override
	protected void onButton1Click() {
		startCommentPage();
	}

	@Override
	protected void onButton2Click() {
		startShowFeedPage();
	}

	private void startShowFeedPage() {
		Bundle bundle = new Bundle();
		bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID, postId);
		Intent intent = new Intent(this, ContentOriginActivity.class);
    	intent.putExtras(bundle);
    	startActivity(intent);
		finish();
	}

	private void startCommentPage() {
		Intent intent = new Intent(this, CommentPage.class);
		String id;
		if (isComment) {
			id = cmtId;
		} else {
			id = orgId;
		}
		intent.putExtra(CommentPage.KEY_DEFAULT_CONTENT, "回复@" + replyName + ":");
		intent.putExtra(CommentPage.KEY_SOURCE, postId);
		intent.putExtra(CommentPage.KEY_ORG_ID, id);
		startActivity(intent);
		finish();
	}

}
