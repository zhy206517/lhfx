package com.youa.mobile.common.util.picture;

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.youa.mobile.common.manager.SavePathManager;


public class PickImage {
    public static final int REQUEST_CODE_CAMERA = 131;
	public static final int REQEUST_CODE_PHOTO_PICK = 132;
//	private File mCurrentPhotoFile;
	private long outTime = -1;
	private File mSelectedFile;
	private boolean needCrop = false;
	private static PickImage pickImage = new PickImage();
	
	public static PickImage getInstance() {
		return pickImage;
	}

	public File getSelectedFile() {
		return mSelectedFile;
	}

//	/**
//     * Pick a specific photo to be added under the currently selected tab.
//     */
//    public boolean doPickPhotoAction(Context ctx) {
//        createPickPhotoDialog(ctx).show();
//        return true;
//    }

//    /**
//     * Creates a dialog offering two options: take a photo or pick a photo from the gallery.
//     */
//    private Dialog createPickPhotoDialog(final Context context) {
//        // Wrap our context to inflate list items using correct theme
//        final Context dialogContext = new ContextThemeWrapper(context,
//                android.R.style.Theme_Light);
//
//        String[] choices;
//        choices = context.getResources().getStringArray(R.array.chatroom_pick_pic_options);
//        final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
//                android.R.layout.simple_list_item_1, choices);
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
//        builder.setTitle(R.string.chatroom_choose_photo);
//        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();                
//                switch(which) {
//                    case 0://选图
//                    	mSelectedFile = getTempFile();
//                        doPickPhotoFromGallery(context, false);
//                        break;
//                    case 1://照相
//                    	mSelectedFile = getTempFile();
//                        doTakePhotoFromCamera(context, false);
//                        break;
//                }
//            }
//        });
//        return builder.create();
//    }

    public File getTempFile() {
    	String path = SavePathManager.getImagePath();
    	String file = path + "/" + new Date().getTime();
    	mSelectedFile = new File(file);
    	return mSelectedFile;
    }
    
    public void doTakePhotoFromCamera(Context ctx, boolean needCrop) {
        try {
            // Launch camera to take photo for selected contact        	
//        	if(mSelectedFile == null) {
        		mSelectedFile = getTempFile();
//        	}
        	if(needCrop)
        	{	
	            final Intent intent = getTakePickIntent(mSelectedFile);
	            ((Activity)ctx).startActivityForResult(intent, REQUEST_CODE_CAMERA);
        	}else
        	{
	            final Intent intent = getTakePickIntent(mSelectedFile);
	            ((Activity)ctx).startActivityForResult(intent, REQEUST_CODE_PHOTO_PICK);
        	}
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx.getApplicationContext(), "没有找到照相应用", Toast.LENGTH_LONG).show();
        }
    }
    
    
    /**
     * Constructs an intent for capturing a photo and storing it in a temporary file.
     */
    private static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG);
        return intent;
    }

//    /**
//     * Sends a newly acquired photo to Gallery for cropping
//     */
//    public void doCropPhoto(Context ctx) {
//        try {
//
//            // Add the image to the media store
////            MediaScannerConnection.scanFile(
////                    this,
////                    new String[] { f.getAbsolutePath() },
////                    new String[] { null },
////                    null);
//
//            // Launch gallery to crop the photo
//        	if(mCurrentPhotoFile!=null)
//        	{
//        		final Intent intent = getCropImageIntent(Uri.fromFile(mCurrentPhotoFile));
//                ((Activity)ctx).startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
//        	}
//        } catch (Exception e) {
//            //Log.e(TAG, "Cannot crop image", e);
//            Toast.makeText(ctx, "没有找到图库应用", Toast.LENGTH_LONG).show();
//        }
//    }

    /**
     * Constructs an intent for image cropping.
     */
    private Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        if(needCrop)
        {
        	addCropOptions(intent);
        }
        return intent;
    }
    
    private void addCropOptions(Intent intent)
    {
    	intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG);
        intent.putExtra("crop", "true");
        intent.putExtra("scale", false);
        intent.putExtra("scaleUpIfNeeded", false);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mSelectedFile));
    }
    
    /**
     * Launches Gallery to pick a photo.
     */
    public void doPickPhotoFromGallery(Context ctx, boolean isNeedCrop) {
        try {
            // Launch picker to choose photo for selected contact
//        	if(mSelectedFile == null) {
        		mSelectedFile = getTempFile();
//        	}
            final Intent intent = getPhotoPickIntent(isNeedCrop);
            ((Activity)ctx).startActivityForResult(intent, REQEUST_CODE_PHOTO_PICK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx.getApplicationContext(), "没有找到图库应用", Toast.LENGTH_LONG).show();
        }
    }
    
//    public void clearCache()
//    {
//    	if(mCurrentPhotoFile!=null)
//    	{
//    		try
//    		{
//    			mCurrentPhotoFile.delete();
//    		}catch(SecurityException e){}
//    		mCurrentPhotoFile = null;
//    	}
//    }

    /**
     * Constructs an intent for picking a photo from Gallery, cropping it and returning the bitmap.
     */
    private Intent getPhotoPickIntent(boolean isNeedCrop) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        if(needCrop)
        {
        	addCropOptions(intent);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mSelectedFile));
        }
        return intent;
    }
    
//    protected void onActivityResult(
//    		final Context context,
//			int requestCode, 
//			int resultCode, 
//			final Intent data) {
////		super.onActivityResult(requestCode, resultCode, data);
//		if(resultCode!=Activity.RESULT_OK)
//			return;
////		File imageFile;
//		switch(requestCode)
//		{
//			//接收选图片结果
//			case PickImage.REQEUST_CODE_PHOTO_PICK:
//				new Thread() {
//					public void run() {
//						PickImage pickImage = PickImage.getInstance();
//						File imageFile = pickImage.getSelectedFile();
//						if(!imageFile.exists() && data!=null)
//						{
//		//					//如果是图库类应用，如果Extra_Output没起作用，应该返回文件的Uri
//							Uri fileUri = data.getData();
//							if(fileUri!=null)
//							{
//								try {
//									FileUtil.outputToFile(context, fileUri, imageFile);
//								} catch (IOException e) {
//									e.printStackTrace();
//									Toast.makeText(
//											context,
//											R.string.viewpic_error_cannot_create_file,
//											Toast.LENGTH_LONG).show();
//								}					
//							}
//						}
//						System.out.println("imageFile:" + imageFile.getAbsolutePath());
////						sendImageMsg(imageFile.getAbsolutePath());
//					}
//				}.start();
//				break;
//			case PickImage.REQUEST_CODE_CAMERA:
////			//不然就是照相了，照相没放到指定位置的话，就是直接把bmp传回来了，一样，自己存文件
//				new Thread() {
//					public void run() {
//						PickImage pickImage = PickImage.getInstance();
//						File imageFile = pickImage.getSelectedFile();
//						if(!imageFile.exists() && data.getExtras()!=null)
//						{
//							Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
//							if(cameraBitmap!=null)
//							{
//								try {
//									imageFile = new File(SavePathManager.getImagePath() + new Date().getTime());
//									cameraBitmap.compress(Bitmap.CompressFormat.PNG, 100,new FileOutputStream(imageFile));
//									
//								} catch (IOException e) {
//									e.printStackTrace();
//									Toast.makeText(
//											context,
//											R.string.viewpic_error_cannot_create_file,
//											Toast.LENGTH_LONG).show();
//								}	
//							}
//							
//						}
////						sendImageMsg(imageFile.getAbsolutePath());
//					}
//				}.start();
//				break;
//				
//		}
//    }
    
//    private void doPickPhoto(
//    		final Context context, 
//    		final boolean isNeedCrop) {
////		Context dialogContext = new ContextThemeWrapper(context, android.R.style.Theme_Light);
//		
//		//String [] choices = context.getResources().getStringArray(R.array.account_pick_photo_options);
//		//ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1, choices);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
//        
//        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//            	
//                dialog.dismiss();
//                
//                switch (which) {
//	                case 0 : {
//	                    doPickPhotoFromGallery(context, isNeedCrop);
//	                    break;
//	                }
//	                
//	                case 1 : {
//	                	doTakePhotoFromCamera(context, isNeedCrop);	                    
//	                    break;
//	                }
//                }
//            }
//        });
//        
//	    builder.setNegativeButton(R.string.account_back_text, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//	    
//        builder.create().show();
//	}
}
