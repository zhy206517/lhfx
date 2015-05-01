package com.youa.mobile.common.widget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseOptionPage;
import com.youa.mobile.common.manager.SavePathManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.common.util.picture.PickImage;

public class TakePicturePage extends BaseOptionPage {
	public static final String TAG = "TackPicturePage";
	public static final String KEY_OPERATE_TYPE = "type";
	public static final String KEY_OPERATE_ISCROP = "iscrop";
	public static final String OPERATE_TYPE_ADD = "add";
	public static final String OPERATE_TYPE_SELECT = "select";
	public static final String RESULT_PATH = "path";
	public static final int REQUESTCODE_CROP = 1;
	private boolean isCrop = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		String type = getIntent().getStringExtra(KEY_OPERATE_TYPE);
		isCrop = getIntent().getBooleanExtra(KEY_OPERATE_ISCROP, false);

		button1.setText(R.string.viewpic_take_photo_from_camera);
		
		if(OPERATE_TYPE_ADD.equals(type)) {
			button2.setText(R.string.viewpic_take_photo_from_gallery_add);
			
		} else {
			button2.setText(R.string.viewpic_take_photo_from_gallery_select);
		}
		
	}

	@Override
	protected void onButton1Click() {
		PickImage.getInstance().doTakePhotoFromCamera(this, true);
	}

	@Override
	protected void onButton2Click() {
		PickImage.getInstance().doPickPhotoFromGallery(this, true);
	}
	
	
	
	

  @Override
  protected void onActivityResult(
	int requestCode, 
	int resultCode, 
	final Intent data) {
//	System.out.println("#########################-onActivityResult:" + resultCode);
//	System.out.println("#########################-onActivityResult:" + requestCode);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		//接收选图片结果
		case PickImage.REQEUST_CODE_PHOTO_PICK: {

			Uri fileUri = data.getData();
			if (fileUri == null) {
				fileUri = Uri.parse(data.getAction());
			}
			if (fileUri != null) {
				if (isCrop) {
					doCropPhoto(fileUri);
				} else {
					String path = null;
					try {
						path = ImageUtil.decodeToFile(getContentResolver(),
								fileUri, 600, 1024); // 600 最大宽度，1024最大高度
					} catch (Exception e) {
						e.printStackTrace();
					}
					finish(path);
				}
			}
			break;
		}
		case REQUESTCODE_CROP:{
			// //如果是图库类应用，如果Extra_Output没起作用，应该返回文件的Uri
			Bitmap photo1 = data.getParcelableExtra("data");
			File imageFile = null;
			if (photo1 != null) {
				try {
					imageFile = new File(SavePathManager.getImagePath()
							+ new Date().getTime());
					photo1.compress(Bitmap.CompressFormat.PNG, 100,
							new FileOutputStream(imageFile));
				} catch (IOException e) {
					e.printStackTrace();
					showToast(R.string.viewpic_error_cannot_create_file);
				}
			}
			finish(imageFile == null ? null : imageFile.getAbsolutePath());
			break;
		}
		case PickImage.REQUEST_CODE_CAMERA:{
			// //不然就是照相了，照相没放到指定位置的话，就是直接把bmp传回来了，一样，自己存文件
			PickImage pickImage = PickImage.getInstance();
			File imageFile = pickImage.getSelectedFile();
			if (imageFile == null) {
				imageFile = pickImage.getTempFile();
			}

			if(!imageFile.exists() && data != null && data.getExtras()!=null) {
				Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
				if (isCrop) {
					doCropPhoto(cameraBitmap);
				} else {
					String path = null;
					try {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						cameraBitmap.compress(Bitmap.CompressFormat.PNG, 100,
								baos);

						path = ImageUtil.decodeToFile(baos.toByteArray(), 600,
								1024); // 600 最大宽度，1024最大高度
					} catch (Exception e) {
						e.printStackTrace();
					}
					finish(path);
				}
			} else {
				if(isCrop) {
					doCropPhoto(Uri.fromFile(imageFile));
				} else {
					int rotate = ImageUtil.getExifOrientation(imageFile.getAbsolutePath());

					String path = null;
					try {
						path = ImageUtil.decodeToFile(imageFile.getAbsolutePath(), 600, 1024, rotate); // 600 最大宽度，1024最大高度
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					finish(path);
				}
			}
			break;
		}
		}
	}
  
//   protected void doCropPhoto(File file) {
//	   Intent intent = getCropImageIntent(Uri.fromFile(file));
//	   startActivityForResult(intent, REQUESTCODE_CROP);
//
//   }
   
  	private void finish(String path) {
  		Intent resultintent = new Intent();
		resultintent.putExtra(RESULT_PATH, path);				
		setResult(RESULT_OK, resultintent);
		finish();
  	}
	protected void doCropPhoto(Uri uri) {
	   
	  Intent intent = getCropImageIntent();
	  intent.setDataAndType(uri, "image/*");
	  startActivityForResult(intent, REQUESTCODE_CROP);
	
	}
	
	protected void doCropPhoto(Bitmap bitmap) {
		  Intent intent = getCropImageIntent();
		  intent.putExtra("data", bitmap);
		  startActivityForResult(intent, REQUESTCODE_CROP);
		
	}
  
   public static Intent getCropImageIntent() {
	    Intent intent = new Intent("com.android.camera.action.CROP");
	    intent.putExtra("crop", "true");
	    intent.putExtra("aspectX", 1);
	    intent.putExtra("aspectY", 1);
	    intent.putExtra("outputX", 200);
	    intent.putExtra("outputY", 200);
	    intent.putExtra("return-data", true);
	    
	    return intent;
	}
}
