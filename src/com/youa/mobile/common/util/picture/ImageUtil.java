package com.youa.mobile.common.util.picture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.youa.mobile.DebugMode;
import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.SavePathManager;
import com.youa.mobile.common.params.ServerAddressUtil;
import com.youa.mobile.friend.data.HomeData;
import com.youa.mobile.friend.data.User;
import com.youa.mobile.login.auth.ShareUtil;

public class ImageUtil {
	public static final String TAG = "ImageUtil";

	public static final int HEADER_SIZE_LARGE = 120;// 60
	public static final int HEADER_SIZE_BIG = 80;// 40
	public static final int HEADER_SIZE_SMALL = 40;
	public static final int FEED_SIZE_BIG = 120;
	public static final int FEED_SIZE_SMALL = 80;
	public static final int CONTENT_SIZE_BIG = 450;// 432
	public static final int CONTENT_SIZE_SMALL = 300;// 284
	public static final int CONTENT_SIZE_LARGE = 432;// 432
	public static final int CONTENT_SIZE_LITTLE = 284;// 284

	public static final int WATERFALL_SIZE_W = 220;
	public static final int WATERFALL_SIZE_2H = 360;
	public static final int WATERFALL_SIZE_H = 180;

	// private static Map<String, Object> downloadings = new HashMap<String,
	// Object>();

	public static String getImageURL(String imageid, int width, int height) {
		if (imageid == null || "".equals(imageid) || "0".equals(imageid)) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		// sb.append(ServerAddressUtil.FILE_SERVER);
		sb.append(imageid);
		sb.append("--");
		sb.append(width);
		sb.append("-");
		if (height != 0) {
			sb.append(height);
		}
		sb.append("-1");
		return sb.toString();
		// return
		// "http://10.38.50.33:8645/img_new/56945f1ef0126ebc11d8f76e--50-50-1";
	}

	public static void setHeaderImageView(Context context,
			final ImageView imageView, String imageId, int defaultImageRes) {
		int roundCorner;
		int width;
		if (ApplicationManager.getInstance().getDensityDpi() < 240) {//
			roundCorner = 6;
			width = ImageUtil.HEADER_SIZE_BIG;
		} else {
			roundCorner = 8;
			width = ImageUtil.HEADER_SIZE_LARGE;
		}

		String url = getImageURL(imageId, width, width);
		if (DebugMode.debug) {
			Log.d(TAG, "url:" + url);
		}

		setImageViewPri(context, imageView, url, 0, 0, defaultImageRes,
				roundCorner, width, width, true);
	}

	public static void setImageView(Context context, final ImageView imageView,
			String imageId, int width, int height, int defaultImageRes) {
		setImageView(context, imageView, imageId, width, height,
				defaultImageRes, 0);
	}

	public static String getImageURL(String imageid) {
		if (imageid == null) {
			return null;
		} else if ("".equals(imageid)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(ServerAddressUtil.FILE_SERVER_ORIGINAL);
		sb.append(imageid);
		return sb.toString();
		// return
		// "http://10.38.50.33:8645/img_new/56945f1ef0126ebc11d8f76e--50-50-1";
	}

	public static void setImageView(Context context, final ImageView imageView,
			String imageId, int width, int height, int defaultImageRes,
			int roundCorner) {
		String url = getImageURL(imageId, width, height);
		if (DebugMode.debug) {
			Log.d(TAG, "url:" + url);
		}
		setImageViewPri(context, imageView, url, 0, 0, defaultImageRes,
				roundCorner);
	}

	public static void setImageView(Context context, final ImageView imageView,
			String imageId, int width, int height, int viewWidthOffset,
			int viewHeightOffset, int defaultImageRes) {

		String url = getImageURL(imageId, width, height);
		if (DebugMode.debug) {
			Log.d(TAG, "url:" + url);
		}
		setImageViewPri(context, imageView, url, viewWidthOffset,
				viewHeightOffset, defaultImageRes, 0);
	}

	public static void setImageView(Context context, final ImageView imageView,
			String imageId, int width, int height, float angle, View loadingView) {

		String url = getImageURL(imageId, width, height);
		Log.d(TAG, "url:" + url);
		setImageView(context, imageView, url, angle, loadingView, true);
	}

	public static void setImageView(Context context, final ImageView imageView,
			String imageId, int width, int height, float angle,
			View loadingView, boolean isUserCache) {

		String url = getImageURL(imageId, width, height);
		Log.d(TAG, "url:" + url);
		setImageView(context, imageView, url, angle, loadingView, isUserCache);
	}

	public static void setImageViewOriginal(Context context,
			final ImageView imageView, String imageId, int defaultRes) {

		String url = getImageURL(imageId);
		Log.d(TAG, "url:" + url);
		setImageViewPri(context, imageView, url, 0, 0, defaultRes, 0);
	}

	public static void setImageViewOriginal(Context context,
			final ImageView imageView, String imageId, View loadingView) {

		String url = getImageURL(imageId);
		Log.d(TAG, "url:" + url);
		setImageView(context, imageView, url, 0, loadingView, true);
	}

	public static void setImageViewOriginal(Context context,
			final ImageView imageView, String imageId) {

		String url = getImageURL(imageId);
		setImageViewPri(context, imageView, url, 0, 0, R.drawable.feed_img_bg,
				0);
	}

	private static void setImageViewPri(Context context,
			final ImageView imageView, String url, final int viewWidthOffset,
			final int viewHeightOffset, int defaultImageRes, int roundCorner) {
		setImageViewPri(context, imageView, url, viewWidthOffset,
				viewHeightOffset, defaultImageRes, roundCorner, 0, 0, true);
	}

	private static void setImageViewPri(Context context,
			final ImageView imageView, String url, final int viewWidthOffset,
			final int viewHeightOffset, int defaultImageRes, int roundCorner,
			int width, int height, boolean isUserCache) {
		if (defaultImageRes != -1) {
			// defaultImageRes = R.drawable.img_bg;
			imageView.setImageResource(defaultImageRes);
		}
		imageView.setTag("");
		if (url == null || url.equals("")) {
			imageView.setImageResource(defaultImageRes);
			return;
		}
		Drawable defaultDrawable = null;
		if (defaultImageRes != -1) {
			defaultDrawable = context.getResources().getDrawable(
					defaultImageRes);
		}
		imageView.setTag(url);
		UserImageLoader.getInstance().loadDrawable(context, url,
				defaultDrawable, roundCorner, width, height,
				new UserImageLoader.OnImageLoadListener() {
					@Override
					public void onImageLoaded(Drawable imageDrawable,
							String imageUrl) {
						String curentUrl = (String) imageView.getTag();
						if (curentUrl.equals(imageUrl)) {
							imageView.setImageDrawable(imageDrawable);
						}
						imageView.setTag("");
						// if (viewWidthOffset != 0 || viewHeightOffset != 0) {
						// // imageView.getd
						// System.out.println("viewWidthOffset:"
						// + viewWidthOffset);
						// System.out.println("viewHeightOffset:"
						// + viewHeightOffset);
						// System.out.println(imageDrawable
						// .getIntrinsicWidth()
						// + "====="
						// + imageDrawable.getIntrinsicHeight());
						// LayoutParams lp = imageView.getLayoutParams();
						// if (lp != null) {
						// lp.width = viewWidthOffset
						// + imageDrawable.getIntrinsicWidth();
						// lp.height = viewHeightOffset
						// + imageDrawable.getIntrinsicHeight();
						// ;
						// }
						// }
					}

					@Override
					public void onImageLoaded(Bitmap bitmap) {
						// TODO Auto-generated method stub

					}

				}, isUserCache);
	}

	private static void setImageView(Context context,
			final ImageView imageView, String url, final float angle,
			final View view, boolean isUserCache) {
		if (null != view) {
			view.setVisibility(View.VISIBLE);
		}
		imageView.setTag("");
		if (url == null || url.equals("")) {
			return;
		}
		Drawable defaultDrawable = null;
		imageView.setTag(url);
		UserImageLoader.getInstance().loadDrawable(context, url,
				defaultDrawable, new UserImageLoader.OnImageLoadListener() {
					@Override
					public void onImageLoaded(Drawable imageDrawable,
							String imageUrl) {
						String curentUrl = (String) imageView.getTag();
						if (curentUrl != null && curentUrl.equals(imageUrl)) {
							if (angle != 0) {
								Bitmap bitmap = ((BitmapDrawable) imageDrawable)
										.getBitmap();
								bitmap = setAngle(bitmap, angle);
								imageDrawable = new BitmapDrawable(bitmap);
							}
							imageView.setImageDrawable(imageDrawable);
							view.setVisibility(View.GONE);
						}
						imageView.setTag(null);
					}

					@Override
					public void onImageLoaded(Bitmap bitmap) {
						// TODO Auto-generated method stub

					}
				}, isUserCache);
	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap decode(String path) {
		return decode(path, 0);
	}

	public static Bitmap decode(String path, float angle) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 设置inJustDecodeBounds为true后，decodeFile并不分配空间
		// options.inTempStorage = new byte[16*1024];
		BitmapFactory.decodeFile(path, options);
		/*
		 * DisplayMetrics dm = new DisplayMetrics();
		 * ((Activity)context).getWindowManager
		 * ().getDefaultDisplay().getMetrics(dm);
		 */
		options.inSampleSize = computeSampleSize(options, -1,
				ApplicationManager.getInstance().getHeight()
						* ApplicationManager.getInstance().getWidth());
		options.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(path, options);
			bitmap = setAngle(bitmap, angle);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static Bitmap decode(String path, int maxWidth, int maxHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 设置inJustDecodeBounds为true后，decodeFile并不分配空间
		// options.inTempStorage = new byte[16*1024];
		BitmapFactory.decodeFile(path, options);
		/*
		 * DisplayMetrics dm = new DisplayMetrics();
		 * ((Activity)context).getWindowManager
		 * ().getDefaultDisplay().getMetrics(dm);
		 */
		options.inSampleSize = computeSampleSize(options, -1, maxWidth
				* maxHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(path, options);
			bitmap = setAngle(bitmap, 0);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static Bitmap setAngle(Bitmap bitmap, float angle) {
		if (angle != 0 && null != bitmap) {
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}
		return bitmap;
	}

	public static String decodeToFile(String path) throws FileNotFoundException {
		return decodeToFile(path, ApplicationManager.getInstance().getWidth(),
				ApplicationManager.getInstance().getHeight());// 600，最大宽度，1024最大高度
	}

	public static String decodeToFile(String path, int maxWidth, int maxHeight)
			throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 设置inJustDecodeBounds为true后，decodeFile并不分配空间
		// options.inTempStorage = new byte[16*1024];
		BitmapFactory.decodeFile(path, options);
		/*
		 * DisplayMetrics dm = new DisplayMetrics();
		 * ((Activity)context).getWindowManager
		 * ().getDefaultDisplay().getMetrics(dm);
		 */
		options.inSampleSize = computeSampleSize(options, -1, maxWidth
				* maxHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(path, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		String tmppath = SavePathManager.getImageFilePath("temp/");
		File fileDir = new File(tmppath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String targetPath = tmppath + System.currentTimeMillis();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(
				targetPath));
		bitmap.recycle();
		return targetPath;
	}

	public static String decodeToFile(String path, int maxWidth, int maxHeight,
			int rotate) throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 设置inJustDecodeBounds为true后，decodeFile并不分配空间
		// options.inTempStorage = new byte[16*1024];
		BitmapFactory.decodeFile(path, options);
		/*
		 * DisplayMetrics dm = new DisplayMetrics();
		 * ((Activity)context).getWindowManager
		 * ().getDefaultDisplay().getMetrics(dm);
		 */
		options.inSampleSize = computeSampleSize(options, -1, maxWidth
				* maxHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(path, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		String tmppath = SavePathManager.getImageFilePath("temp/");
		File fileDir = new File(tmppath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		if (rotate != 0) {
			Matrix matrix = new Matrix();
			matrix.setRotate(rotate);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, false);
		}

		String targetPath = tmppath + System.currentTimeMillis();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(
				targetPath));
		bitmap.recycle();
		return targetPath;
	}

	public static String decodeToFile(ContentResolver contentResolver, Uri uri,
			int maxWidth, int maxHeight) throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 设置inJustDecodeBounds为true后，decodeFile并不分配空间
		// options.inTempStorage = new byte[16*1024];
		BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null,
				options);
		/*
		 * DisplayMetrics dm = new DisplayMetrics();
		 * ((Activity)context).getWindowManager
		 * ().getDefaultDisplay().getMetrics(dm);
		 */
		options.inSampleSize = computeSampleSize(options, -1, maxWidth
				* maxHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(
					contentResolver.openInputStream(uri), null, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		String tmppath = SavePathManager.getImageFilePath("temp/");
		File fileDir = new File(tmppath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String targetPath = tmppath + System.currentTimeMillis();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(
				targetPath));
		bitmap.recycle();
		return targetPath;
	}

	public static String decodeToFile(byte[] data, int maxWidth, int maxHeight)
			throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 设置inJustDecodeBounds为true后，decodeFile并不分配空间
		// options.inTempStorage = new byte[16*1024];
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		/*
		 * DisplayMetrics dm = new DisplayMetrics();
		 * ((Activity)context).getWindowManager
		 * ().getDefaultDisplay().getMetrics(dm);
		 */
		options.inSampleSize = computeSampleSize(options, -1, maxWidth
				* maxHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		String tmppath = SavePathManager.getImageFilePath("temp/");
		File fileDir = new File(tmppath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String targetPath = tmppath + System.currentTimeMillis();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(
				targetPath));
		bitmap.recycle();
		return targetPath;
	}

	public static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
			Log.e(TAG, "cannot read exif", ex);
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				// We only recognize a subset of orientation tag values.
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			}
		}
		return degree;
	}

	public static byte[] getThumbDataByteArray(Context context, User user,
			int width, int height, boolean needRecycle) {
		String imagePath = ShareUtil.getImgs(user, false, "", true);
		Bitmap bmp = null;
		try {
			bmp = ImageUtil.decode(imagePath);
			if (bmp == null) {
				bmp = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.icon);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, width, height, true);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		thumbBmp.compress(CompressFormat.JPEG, 80, output);
		if (needRecycle) {
			thumbBmp.recycle();
			bmp.recycle();
		}
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}