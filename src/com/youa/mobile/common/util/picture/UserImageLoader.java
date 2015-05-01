package com.youa.mobile.common.util.picture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.common.http.HttpManager;
import com.youa.mobile.common.http.netsynchronized.ImageEffect;
import com.youa.mobile.common.manager.SavePathManager;

public class UserImageLoader {
	private static final boolean IS_DEBUG = false;
	private static final String TAG = "UserImageLoader";

	private Map<String, SoftReference<Bitmap>> imageCache;
	private Map<String, Object> downloadings;
	private Context lastToastContext;
	private long lastToastTime;

	private static UserImageLoader instance = null;

	public synchronized static UserImageLoader getInstance() {
		if (instance == null)
			instance = new UserImageLoader();
		return instance;
	}

	private UserImageLoader() {
		imageCache = Collections
				.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());
		downloadings = new HashMap<String, Object>();
	}

	public void loadDrawable(Context context, String imageUrl,
			OnImageLoadListener onImageLoadListener) {
		loadDrawable(context, imageUrl, onImageLoadListener, null, true, false,
				true, 0, 0, 0);
	}

	public void loadDrawable(Context context, String imageUrl,
			Drawable defaultDrawable, OnImageLoadListener onImageLoadListener) {
		loadDrawable(context, imageUrl, onImageLoadListener, defaultDrawable,
				true, false, true, 0, 0, 0);
	}

	public void loadDrawable(Context context, String imageUrl,
			Drawable defaultDrawable, int roundCorner,
			OnImageLoadListener onImageLoadListener) {
		loadDrawable(context, imageUrl, onImageLoadListener, defaultDrawable,
				true, false, true, roundCorner, 0, 0);
	}

	public void loadDrawable(Context context, String imageUrl,
			Drawable defaultDrawable, int roundCorner, int width, int height,
			OnImageLoadListener onImageLoadListener, boolean isHasCache) {
		loadDrawable(context, imageUrl, onImageLoadListener, defaultDrawable,
				true, false, isHasCache, roundCorner, width, height);
	}

	public void loadDrawable(Context context, String imageUrl,
			Drawable defaultDrawable, OnImageLoadListener onImageLoadListener, boolean isUseCache) {
		loadDrawable(context, imageUrl, onImageLoadListener, null, true, false,
				isUseCache, 0, 0, 0);
	}

	public void loadDrawable(final Context context, final String imagePath,
			final boolean isURL, final boolean isUseCache, final int width,
			final int height, final OnImageLoadListener onImageLoadListener) {
		if (IS_DEBUG) {
			Log.d(TAG, "loadDrawable");
		}
		boolean isHasCache = false;
		if (isUseCache && imageCache.containsKey(imagePath)) {
			if (IS_DEBUG) {
				Log.d(TAG, "load from cache");
			}
			SoftReference<Bitmap> bitmapObj = imageCache.get(imagePath);
			Bitmap bitmap = bitmapObj.get();
			if (bitmap != null) {
				isHasCache = true;
				onImageLoadListener.onImageLoaded(bitmap);
			}
		}
		if (!isHasCache) {
			new Thread() {
				@Override
				public void run() {
					Bitmap bitmap = null;
					if (isURL) {
						bitmap = loadBitmap(context, imagePath, null);
					} else {
						bitmap = ImageUtil.decode(imagePath);
					}
					if (bitmap != null) {
						if (isUseCache) {
							imageCache.put(imagePath,
									new SoftReference<Bitmap>(bitmap));
						}
						onImageLoadListener.onImageLoaded(bitmap);
					}
					setResult(onImageLoadListener, null, bitmap);
				}
			}.start();
		}
	}

	private void loadDrawable(final Context context, final String imagePath,
			final OnImageLoadListener onImageLoadListener,
			final Drawable defaultDrawable, final boolean isURL,
			final boolean isCreateThumb, final boolean isUseCache,
			final int roundCorner, final int width, final int height) {
		if (IS_DEBUG) {
			Log.d(TAG, "loadDrawable");
		}
		if (imagePath == null || "".equals(imagePath)) {
			if (defaultDrawable != null) {
				onImageLoadListener.onImageLoaded(defaultDrawable, imagePath);
			}
			return;
		}
		boolean isHasCache = false;
		if (isUseCache && imageCache.containsKey(imagePath)) {
			if (IS_DEBUG) {
				Log.d(TAG, "load from cache");
			}
			SoftReference<Bitmap> bitmapObj = imageCache.get(imagePath);
			Bitmap bitmap = bitmapObj.get();
			if(bitmap != null && bitmap.isRecycled()) {
				imageCache.remove(imagePath);
				bitmap = null;
			}
			if (bitmap != null) {
				isHasCache = true;
				if (context != null) {
					onImageLoadListener.onImageLoaded(new BitmapDrawable(
							context.getResources(), bitmap), imagePath);
				} else {
					onImageLoadListener.onImageLoaded(
							new BitmapDrawable(bitmap), imagePath);
				}
			}
		}
		if (!isHasCache) {
			final Handler handler = new Handler();
			new Thread() {
				@Override
				public void run() {
					Bitmap bitmap = null;
					if (isURL) {
						bitmap = loadBitmap(context, imagePath, handler);
					} else {
						bitmap = ImageUtil.decode(imagePath);
					}
					Drawable drawable = null;
					if (bitmap != null) {
						if (width > 0 && height > 0) {
							bitmap = Bitmap.createScaledBitmap(bitmap, width,
									height, true);
						}
						if (roundCorner > 0) {
							bitmap = ImageEffect.createCornorBitmap(bitmap,
									roundCorner);
						}

						if (isUseCache) {
							imageCache.put(imagePath,
									new SoftReference<Bitmap>(bitmap));
						}
						if (context != null) {
							drawable = new BitmapDrawable(
									context.getResources(), bitmap);
						} else {
							drawable = new BitmapDrawable(bitmap);
						}
					}
					setResult(drawable, defaultDrawable, onImageLoadListener,
							handler, imagePath);
				}
			}.start();
		}

	}

	private Bitmap loadBitmap(Context context, String imageUrl, Handler handler) {
		if (IS_DEBUG) {
			Log.d(TAG, "loadDrawableNoCache");
		}
		if (imageUrl == null || "".equals(imageUrl)) {
			return null;
		}

		Bitmap exists = null;
		exists = ImageUtil.decode(SavePathManager.changeURLToPath(imageUrl));
		if (exists != null) {
			if (IS_DEBUG) {
				Log.d(TAG, "load from file");
			}
			// 从文件读取成功
			// exists = ImageEffect.createCornorBitmap(exists,8);
		} else {
			// 从网络读取

			if (IS_DEBUG) {
				Log.d(TAG, "load from net");
			}
			exists = loadBitmapFromUrl(context, imageUrl, handler);
		}

		return exists;
	}

	private void setResult(Drawable drawable, Drawable defaultDrawable,
			final OnImageLoadListener onImageLoadListener, Handler handler,
			final String imageUrl) {
		if (drawable == null) {
			drawable = defaultDrawable;
		}
		if (drawable == null) {
			return;
		}
		final Drawable resultDrawable = drawable;
		handler.post(new Runnable() {
			public void run() {
				onImageLoadListener.onImageLoaded(resultDrawable, imageUrl);
			}
		});
	}

	private void setResult(final OnImageLoadListener onImageLoadListener,
			Handler handler, final Bitmap bitmap) {
		if (bitmap == null) {
			return;
		}
		onImageLoadListener.onImageLoaded(bitmap);
		;
	}

	private Bitmap loadBitmapFromUrl(final Context context, String url,
			Handler handler) {
		synchronized (downloadings) {
			Object synObj = downloadings.get(url);
			if (synObj != null) {
				synchronized (synObj) {
					try {
						synObj.wait();

						// SoftReference<Bitmap> bitmapObj =
						// imageCache.get(url);
						Bitmap exists = ImageUtil.decode(SavePathManager
								.changeURLToPath(url));
						if (exists != null) {
							return exists;
						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				downloadings.put(url, url);
			}
		}

		URL m;
		InputStream i = null;
		FileOutputStream imageFile = null;
		String output = SavePathManager.changeURLToPath(url);
		// System.out.println("output:" + output);
		boolean success = true;
		try {
			// m = new URL(url);
			// i = (InputStream) m.getContent();
			// HttpURLConnection urlConn = (HttpURLConnection)
			// m.openConnection();// 创建一个Http连接
			// i = urlConn.getInputStream();// 使用IO流读取数据
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("imname", url);
			HttpManager.addCommonParam("_getimage", paramMap, context);
			i = HttpManager.execPostStatic("_getimage", paramMap, context);
			byte[] buffer = new byte[1024];
			imageFile = new FileOutputStream(output);
			int downsize = 0;
			while (true) {
				int count = i.read(buffer);
				if (count < 0) {
					break;
				} else {
					downsize += count;
					// System.out.println("downsize:" + downsize);
					imageFile.write(buffer, 0, count);
				}
			}
			// System.out.println(">>downsize:" + downsize);
			i.close();
			imageFile.close();
		} catch (SocketException e1) {
			e1.printStackTrace();
			success = false;
		} catch (SocketTimeoutException e1) {
			e1.printStackTrace();
			success = false;
		} catch (ConnectTimeoutException e1) {
			e1.printStackTrace();
			success = false;
		} catch (IOException e1) {
			e1.printStackTrace();
			// System.out.println("context != lastToastContet:" + (context !=
			// lastToastContext));
			// System.out.println("context != lastToastContet:" +
			// (System.currentTimeMillis() - lastToastTime));
			if (context != lastToastContext
					|| (System.currentTimeMillis() - lastToastTime) > 30000) {
				// 当切换页面或时间大于30秒
				lastToastContext = context;
				lastToastTime = System.currentTimeMillis();
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(context, R.string.common_error_nospace,
								Toast.LENGTH_LONG).show();
					}
				});
			}
			success = false;
		} catch (Exception e1) {
			e1.printStackTrace();
			success = false;
		}
		if (i != null) {
			try {
				i.close();
			} catch (IOException e) {
			}
		}
		if (imageFile != null) {
			try {
				imageFile.close();
			} catch (IOException e) {
			}
		}
		Bitmap exists = null;
		if (success) {

			exists = ImageUtil.decode(output);
			if (exists != null) {
				// exists = ImageEffect.createCornorBitmap(exists,8);
			} else {
				new File(output).delete();
			}
		}

		Object synObj = downloadings.remove(url);
		if (synObj != null) {
			synchronized (synObj) {
				synObj.notifyAll();
			}
		}
		return exists;
	}

	public interface OnImageLoadListener {
		public void onImageLoaded(Drawable imageDrawable, String imageUrl);

		public void onImageLoaded(Bitmap bitmap);
	}

	
}
