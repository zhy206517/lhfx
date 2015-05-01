package com.youa.mobile.news.provider;
//package com.youa.mobile.news;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.youa.mobile.R;
//import com.youa.mobile.common.base.BasePage;
//import com.youa.mobile.common.db.ConnectManager;
//import com.youa.mobile.news.data.AddMeData;
//import com.youa.mobile.news.data.FavoriteData;
//import com.youa.mobile.news.data.SayMeData;
//import com.youa.mobile.news.exception.DatabaseOperateException;
//import com.youa.mobile.news.provider.AddMeProvider;
//import com.youa.mobile.news.provider.FavoriteProvider;
//import com.youa.mobile.news.provider.SayMeProvider;
//
//public class TestDBPage extends BasePage {
//
//	private Button addSearch;
//	private Button addDelete;
//	private Button addInsert;
//	private Button saySearch;
//	private Button sayDelete;
//	private Button sayInsert;
//	private Button favSearch;
//	private Button favDelete;
//	private Button favInsert;
//	private EditText content;
//	private AddMeProvider addMeProvider = new AddMeProvider();
//	private FavoriteProvider favoriteProvider = new FavoriteProvider();
//	private SayMeProvider sayMeProvider = new SayMeProvider();
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.test_news_db);
//		ConnectManager.initDBConnector(this);
//		initViews();
//	}
//
//	private void initViews() {
//		content = (EditText) findViewById(R.id.content);
//		addSearch = (Button) findViewById(R.id.add_search);
//		addDelete = (Button) findViewById(R.id.add_delete);
//		addInsert = (Button) findViewById(R.id.add_insert);
//		saySearch = (Button) findViewById(R.id.say_search);
//		sayDelete = (Button) findViewById(R.id.say_delete);
//		sayInsert = (Button) findViewById(R.id.say_insert);
//		favSearch = (Button) findViewById(R.id.favorite_search);
//		favDelete = (Button) findViewById(R.id.favorite_delete);
//		favInsert = (Button) findViewById(R.id.favorite_insert);
//		addSearch.setOnClickListener(onClickListener);
//		addDelete.setOnClickListener(onClickListener);
//		addInsert.setOnClickListener(onClickListener);
//		saySearch.setOnClickListener(onClickListener);
//		sayDelete.setOnClickListener(onClickListener);
//		sayInsert.setOnClickListener(onClickListener);
//		favSearch.setOnClickListener(onClickListener);
//		favDelete.setOnClickListener(onClickListener);
//		favInsert.setOnClickListener(onClickListener);
//	}
//	Button.OnClickListener onClickListener = new Button.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.add_search:
//				try {
//						List<AddMeData> list = addMeProvider.query(null, null, null);
//						StringBuffer sb = new StringBuffer();
//						sb.append("add me data size :" + list.size() + "\n");
//						for (AddMeData  data : list) {
//							sb.append(data.getArticleContentImage() + "\n");
//						}
//						content.setText(sb.toString());
//					} catch (DatabaseOperateException e) {
//						e.printStackTrace();
//					}
//				break;
//			case R.id.add_delete:
//				try {
//					addMeProvider.deleteAll();
//				} catch (DatabaseOperateException e) {
//					e.printStackTrace();
//				}
//				break;
//			case R.id.add_insert:
//				try {
//					List<AddMeData> list = new ArrayList<AddMeData>();
//					for (int i = 0; i < 10; i ++) {
//						AddMeData data = new AddMeData();
//						data.setArticleContentImage("http://111.111.111/addme" + i);
//						list.add(data);
//					}
//					addMeProvider.insert(list);
//				} catch (DatabaseOperateException e) {
//					e.printStackTrace();
//				}
//				break;
//			case R.id.say_search:
//				try {
//					List<SayMeData> list = sayMeProvider.query(null, null, null);
//					StringBuffer sb = new StringBuffer();
//					sb.append("say me data size :" + list.size() + "\n");
//					for (SayMeData  data : list) {
//						sb.append(data.getArticleContentImage() + "\n");
//					}
//					content.setText(sb.toString());
//				} catch (DatabaseOperateException e) {
//					e.printStackTrace();
//				}
//				break;
//			case R.id.say_delete:
//				try {
//					sayMeProvider.deleteAll();
//				} catch (DatabaseOperateException e) {
//					e.printStackTrace();
//				}
//				break;
//			case R.id.say_insert:
//				try {
//					List<SayMeData> list = new ArrayList<SayMeData>();
//					for (int i = 0; i < 10; i ++) {
//						SayMeData data = new SayMeData();
//						data.setArticleContentImage("http://111.111.111/sayme" + i);
//						list.add(data);
//					}
//					sayMeProvider.insert(list);
//				} catch (DatabaseOperateException e) {
//					e.printStackTrace();
//				}
//				break;
//			case R.id.favorite_search:
//				try {
//					List<FavoriteData> list = favoriteProvider.query(null, null, null);
//					StringBuffer sb = new StringBuffer();
//					sb.append("fav data size :" + list.size() + "\n");
//					for (FavoriteData  data : list) {
//						sb.append(data.getArticleContentImage() + "\n");
//					}
//					content.setText(sb.toString());
//				} catch (DatabaseOperateException e) {
//					e.printStackTrace();
//				}
//				break;
//			case R.id.favorite_delete:
//				try {
//					favoriteProvider.deleteAll();
//				} catch (DatabaseOperateException e) {
//					e.printStackTrace();
//				}
//				break;
//			case R.id.favorite_insert:
//				try {
//					List<FavoriteData> list = new ArrayList<FavoriteData>();
//					for (int i = 0; i < 10; i ++) {
//						FavoriteData data = new FavoriteData();
//						data.setArticleContentImage("http://111.111.111/favor" + i);
//						list.add(data);
//					}
//					favoriteProvider.insert(list);
//				} catch (DatabaseOperateException e) {
//					e.printStackTrace();
//				}
//				break;
//			}
//		}};
//	
//}
