package com.youa.mobile.common.widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.common.http.HttpManager;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.manager.SavePathManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.common.util.picture.UserImageLoader;
import com.youa.mobile.common.util.picture.UserImageLoader.OnImageLoadListener;
import com.youa.mobile.content.ContentOriginActivity;
import com.youa.mobile.content.ContentTranspondActivity;
import com.youa.mobile.friend.data.HomeData;

public class FlowView extends ImageView implements View.OnClickListener,
		View.OnLongClickListener {

	public static int THREAD_COUNT = 4;

	private static ThreadPoolExecutor mThreadPoolExecutor = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(THREAD_COUNT);

	static {
		mThreadPoolExecutor.setThreadFactory(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setPriority(Thread.NORM_PRIORITY);
				return thread;
			}
		});
	}

	private FlowTag flowTag;
	private Context context;
	private int columnIndex;// 图片属于第几列
	private int rowIndex;// 图片属于第几行
	private Handler viewHandler;
	private SoftReference<Bitmap> mBitmapObj;
	private String imagePath;
	private boolean isRecycle ;

	private static long lastToastTime;

	public FlowView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
		Init();
	}

	public FlowView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
		Init();
	}

	public FlowView(Context c) {
		super(c);
		this.context = c;
		Init();
	}

	private void Init() {
		setOnClickListener(this);
		this.setOnLongClickListener(this);
		setAdjustViewBounds(true);

	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	@Override
	public void onClick(View v) {
		if (flowTag != null) {
			HomeData data = flowTag.getData();
			Bundle bundle = new Bundle();
			Class c = null;
			// --------------------
			if ("0".equals(data.PublicUser.feedType)) {
				c = ContentOriginActivity.class;
				bundle.putString(ContentOriginActivity.ORIGIN_FEED_ID,
						data.PublicUser.postId);// 源动态id
			} else {
				c = ContentTranspondActivity.class;
				bundle.putString(ContentTranspondActivity.TRANSPOND_FEED_ID,
						data.PublicUser.postId);// 转发动态id
			}
			// --------------------
			Intent intent = new Intent();
			intent.putExtras(bundle);
			intent.setClass(context, c);
			context.startActivity(intent);

		}

	}

	// public void onDraw(Canvas canvas) {
	// super.onDraw(canvas);
	// }

	/**
	 * 加载图片
	 */
	public void LoadImage() {
//		Log.d("TAG", ">>>>>>>>>>>>>>>>>LoadImage");
		if (getFlowTag() != null) {
			// new LoadImageThread().start();
//			Log.d("TAG", ">>>>>>>>>>>>>>>>>LoadImage1");
			mThreadPoolExecutor.execute(new LoadImageThread());
		}
	}

	/**
	 * 重新加载图片
	 */
	public void Reload() {
//		Log.d("TAG", ">>>>>>>>>>>>>>>>>Reload");
		if ((mBitmapObj == null || mBitmapObj.get() == null)
				&& getFlowTag() != null) {
//			Log.d("TAG", ">>>>>>>>>>>>>>>>>Reload1");
			// new ReloadImageThread().start();
			mThreadPoolExecutor.execute(new ReloadImageThread());
		}
	}

	/**
	 * 回收内存
	 */
	public void recycle() {
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				setImageBitmap(null);
			}
		});
		if ((mBitmapObj == null || mBitmapObj.get() == null))
			return;
		isRecycle=true;
		mBitmapObj.get().recycle();
		mBitmapObj = null;
		// this.mBitmap.recycle();
		// this.mBitmap = null;
	}

	public FlowTag getFlowTag() {
		return flowTag;
	}

	public void setFlowTag(FlowTag flowTag) {
		this.flowTag = flowTag;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Handler getViewHandler() {
		return viewHandler;
	}

	public FlowView setViewHandler(Handler viewHandler) {
		this.viewHandler = viewHandler;
		return this;
	}

	class ReloadImageThread extends Thread {
		@Override
		public void run() {
			if (flowTag != null) {
				try {
//					BitmapFactory.decodeFile(pathName, opts)
//					Log.d("FlowView", "imagePath:" + imagePath);
//					File file = new File(imagePath);
//					FileInputStream fis = new FileInputStream(file);
//					ImageUtil.decode(imagePath);
//					BitmapFactory.decodeStream(is);
//					final Bitmap mBitmap = BitmapFactory.decodeFile(imagePath);
					final Bitmap mBitmap = ImageUtil.decode(imagePath);
//					int width = ApplicationManager.getInstance().getWidth();
//					final Bitmap mBitmap;
//					if(width > 480) {
//						mBitmap = ImageUtil.decode(imagePath, 220, 440);
//					} else {
//						mBitmap = ImageUtil.decode(imagePath, 110, 220);
//					}
					
					mBitmapObj = new SoftReference<Bitmap>(mBitmap);
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							if (mBitmap != null) {
								setImageBitmap(mBitmap);
							}
						}
					});
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
		}
	}

	class LoadImageThread extends Thread {
		LoadImageThread() {
		}

		public void run() {
			if (flowTag != null) {
				String url = null;
				if(flowTag.hasPic){
					url = ImageUtil.getImageURL(
							flowTag.getUser().contentImg[0].img_content_id,
							flowTag.imageWidth, 0);
				}else{
					url = ImageUtil.getImageURL(
							flowTag.getUser().contentImg[0].img_content_id,
							flowTag.imageWidth, flowTag.imageHeight);
				}
				imagePath = SavePathManager.changeURLToPath(url);
				final Bitmap mBitmap = loadBitmapFromUrl(context,
						getViewHandler(), url);
				if(isRecycle) {
					return;
				}
				mBitmapObj = new SoftReference<Bitmap>(mBitmap);
				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
						if (mBitmap != null) {// 此处在线程过多时可能为null
						// int width = mBitmap.getWidth();// 获取真实宽高
						// int height = mBitmap.getHeight();
						// LayoutParams lp = getLayoutParams();
						// int layoutHeight = (height * flowTag.getItemWidth())
						// / width;// 调整高度
						// if (lp == null) {
						// lp = new LayoutParams(flowTag.getItemWidth(),
						// layoutHeight);
						// }
						// // setLayoutParams(lp);
							if(!isRecycle){
								setImageBitmap(mBitmap);
							}else{
								if(mBitmap!=null){
									mBitmap.recycle();
								}
							}
						}
					}
				});
			}
		}
	}

	public static Bitmap loadBitmapFromUrl(final Context context,
			Handler handler, String url) {
//		Log.d("TAG", ">>>>>>>>>>>>>>>>>loadBitmapFromUrl");
		Bitmap exists = null;
		URL m;
		InputStream i = null;
		FileOutputStream imageFile = null;
		String output = SavePathManager.changeURLToPath(url);
		boolean success = true;
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("imname", url);
			HttpManager.addCommonParam("_getimage", paramMap, context);
			i = HttpManager.execPostStatic("_getimage", paramMap, context);
			byte[] buffer = new byte[1024];
			imageFile = new FileOutputStream(output);
			while (true) {
				int count = i.read(buffer);
				if (count < 0) {
					break;
				} else {
					imageFile.write(buffer, 0, count);
				}
			}
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
			// if ((System.currentTimeMillis() - lastToastTime) > 30000) {
			// // 当切换页面或时间大于30秒
			// lastToastTime = System.currentTimeMillis();
			// handler.post(new Runnable() {
			// public void run() {
			// Toast.makeText(context, R.string.common_error_nospace,
			// Toast.LENGTH_LONG).show();
			// }
			// });
			// }
			success = false;
		} catch (Exception e1) {
			e1.printStackTrace();
			success = false;
		} catch (java.lang.OutOfMemoryError e1) {
			success = false;
			return null;
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
		if (success) {
			try {
				exists = ImageUtil.decode(output);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
			if (exists != null) {
				// exists = ImageEffect.createCornorBitmap(exists,8);
			} else {
				new File(output).delete();
			}
		}
		return exists;
	}
}
