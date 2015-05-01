package com.youa.mobile.news;

import android.content.Intent;
import android.os.Bundle;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseOptionPage;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.content.ContentOriginActivity;

public class FavoriteDialogPage extends BaseOptionPage {

	public static final int RESULT_SHOW_FEED = 0;
	public static final int RESULT_SHOW_PEOPLE = 1;
	public static final String FEED_ID = "feed";
	private String feedId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		button1.setText(R.string.news_farorite_people);
		button2.setText(R.string.news_farorite_show);
		feedId = getIntent().getExtras().getString(FEED_ID);
	}

	@Override
	protected void onButton1Click() {
		startFavoritePeoplePage();
	}

	@Override
	protected void onButton2Click() {
		startShowFeedPage();
	}

	private void startShowFeedPage() {
		Intent intent = new Intent(this, ContentOriginActivity.class);
		intent.putExtra(ContentOriginActivity.ORIGIN_UESER_ID, ApplicationManager.getInstance().getUserId());
		intent.putExtra(ContentOriginActivity.ORIGIN_FEED_ID, feedId);
		startActivity(intent);
		finish();
	}

	private void startFavoritePeoplePage() {
		Intent intent = new Intent(this, FavoritePeopleListPage.class);
		intent.putExtra(FavoritePeopleListPage.KEY_FEED_ID, feedId);
		startActivity(intent);
		finish();
	}

}
