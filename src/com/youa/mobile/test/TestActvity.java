package com.youa.mobile.test;


import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.http.netsynchronized.FileUploader;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.SavePathManager;
import com.youa.mobile.common.util.EmotionHelper;
import com.youa.mobile.common.util.picture.UserImageLoader;
import com.youa.mobile.common.util.picture.ViewPicturePage;
import com.youa.mobile.login.action.LoginAction;
import com.youa.mobile.utils.Tools;

public class TestActvity extends BasePage {

	private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testlayout);
//		login();
		ApplicationManager.getInstance().init(this);
		Tools.context = this;
		initView();
		setImageData();
		View view1 = findViewById(R.id.testid1);

		view1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
			}
		});
		
		View view2 = findViewById(R.id.testid2);

		view2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				System.out.println("View-onclick");
				 Map<String, Object> paramsMap = new HashMap<String, Object>();
				 paramsMap.put("uname", "lehoqa45@baidu.com");
				 paramsMap.put("password", "123456");
				 
				 ActionController.post(
						 TestActvity.this, 
						 LoginAction.class, 
						 paramsMap, 
						 new LoginAction.ILoginResultListener() {
							@Override
							public void onFail(int resourceID) {
								hideProgressDialog();
								showToast(resourceID);
							}

							@Override
							public void onFinish() {
								showToast("登陆成功");
								System.out.println("onFinish:");
								hideProgressDialog();
							}

							@Override
							public void onStart() {
								showProgressDialog(
										TestActvity.this,
										R.string.test_title,
										R.string.test_content);
								System.out.println("onStart");
							}							 
						 },
						 true);
				 
			}
		});
		
		View view3 = findViewById(R.id.testid3);
		view3.setOnClickListener(new OnClickListener(){
			//www.people.com.cn_h_pic_20111209_95_6137217817276002999.jpg
			@Override
			public void onClick(View v) {
				FileUploader fileUploader = null;
				try {
					fileUploader = new FileUploader(
							SavePathManager.getImagePath() +"313d852fcb2a2734c9569b15--50-50-1",
							false
							);

					String result = fileUploader.startUpLoad(TestActvity.this);
					System.out.println("##############:" +result);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			}
			
		});
		String aaa = "啊啊啊啊啊啊[哈哈]啊啊啊[可怜]啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"+
				"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"+
				"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"+
				"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"+
				"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"+
				"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"+
				"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"+
				"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"+
				"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊"+
				"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊111啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿啊啊啊啊啊啊";
//		String htmlText = EmotionHelper.genHTMLText(
//				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa111bbbb"+
//				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa111bbbb"+
//				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa111bbbb"+
//				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa111bbbb"+
//				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa111bbbb"+
//				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa111bbbb"+
//				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa111bbbb"+
//				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa111bbbb"+
//				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa111bbbb"+
//				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa111bbbb"
//				);
//		System.out.println("#################htmlText:" + htmlText);
//		ImageGetter imageGetter = EmotionHelper.getImageGetter(this, 12);
//		Spanned resultText = Html.fromHtml(htmlText, imageGetter, null);
		
		TextView textView= (TextView)findViewById(R.id.testtext);
		textView.setText("abcdefg");
		textView.append(EmotionHelper.parseToImageText(this, aaa, 16));
//		Tools.setLimitText(textView, 5, 50);
	}
	
	private void showToast(final String str) {
		mHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(
						TestActvity.this, 
						str, 
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	private void initView() {
		final ImageView view = (ImageView)findViewById(R.id.imageview);
		view.setBackgroundColor(Color.RED);
		view.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent in = new Intent(TestActvity.this, ViewPicturePage.class);
				in.putExtra(ViewPicturePage.EXTRA_IMG_PATH, SavePathManager.changeURLToPath("http://www.people.com.cn/h/pic/20111209/95/6137217817276002999.jpg"));
				in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				in.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				TestActvity.this.startActivity(in);
			}
		});
	}
	private void setImageData() {
		final ImageView view = (ImageView)findViewById(R.id.imageview);
		UserImageLoader.getInstance().loadDrawable(
				TestActvity.this, 
				"http://10.38.50.33:8645/img_new/56945f1ef0126ebc11d8f76e--50-50-1", 
				new UserImageLoader.OnImageLoadListener(){

					@Override
					public void onImageLoaded(Drawable imageDrawable,
							String imageUrl) {
						view.setImageDrawable(imageDrawable);
					}

					@Override
					public void onImageLoaded(Bitmap bitmap) {
						// TODO Auto-generated method stub
						
					}
					
				});
	}
	
	public void login() {
		
	}
}
