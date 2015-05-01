package com.youa.mobile.common.util;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.youa.mobile.R;
import com.youa.mobile.common.http.UpgradeHttpManager;
import com.youa.mobile.common.http.netsynchronized.FileDownloader;
import com.youa.mobile.common.manager.SavePathManager;
import com.youa.mobile.ui.base.LehoProgressDialog;

public class UpgradeUtil {
	private static final String TAG = "tag";
	private static boolean isNotification = false;
	public static boolean isUpgrade = true;
	public static boolean isForceUpgrade = false;
	static LehoProgressDialog mProgressDialog;
	static Handler mProgressHandler;

	public static boolean checkForceUpgrade(final Context context) {
		UpgradeHttpManager upgradeManager = UpgradeHttpManager.getInstance();
		upgradeManager.request();
		final int minVersion = upgradeManager.getMinVersionCode();
		PackageInfo info;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
		int curVersion = info.versionCode;
		;
		Log.d(TAG, "minVersion:" + minVersion);
		Log.d(TAG, "curVersion:" + curVersion);
		if (minVersion > curVersion) {
			isForceUpgrade = true;
		} else {
			isForceUpgrade = false;
		}
		return isForceUpgrade;
	}

	public static void checkUpgrade(final Context context) {
		checkUpgrade(context, false, false);
	}

	public static void checkUpgrade(final Context context,
			final boolean isToast, final boolean isManual) {
		final Handler handler = new Handler();
		new Thread() {
			public void run() {
				UpgradeHttpManager upgradeManager = UpgradeHttpManager
						.getInstance();
				upgradeManager.request();
				final String apkUrl = upgradeManager.getApkURL();
				final String apkDescription = upgradeManager.getDescription();
				final String apkName = upgradeManager.getVersionName();
				Log.d(TAG, "apkUrl:" + apkUrl);
				Log.d(TAG, "apkDescription:" + apkDescription);
				Log.d(TAG, "apkName:" + apkName);
				if (apkUrl != null && apkUrl.length() > 0) {
					if (!isManual) {
						if (!isUpgrade) {
							return;
						}
						isUpgrade = false;
					}
					handler.post(new Runnable() {
						public void run() {
							try {
								popDialog(context, apkUrl, apkDescription,
										apkName);
							} catch (BadTokenException e) {
								e.printStackTrace();
							}
						}
					});
				} else if (isToast) {
					handler.post(new Runnable() {
						public void run() {
							Toast.makeText(context,
									R.string.common_upgrade_hasnot_version,
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}.start();
	}

	private static void popDialog(final Context context, final String apkUrl,
			final String apkDescription, final String apkName) {
		String title = context
				.getString(R.string.common_upgrade_title, apkName);
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(apkDescription)
				.setPositiveButton(R.string.common_ok,
						new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								downLoad(context, apkUrl, apkName);
							}
						})
				.setNeutralButton(R.string.common_cancel,
						new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
	}

	public static void popForceUpgradeDialog(final Context context) {
		UpgradeHttpManager upgradeManager = UpgradeHttpManager.getInstance();
		final String apkUrl = upgradeManager.getApkURL();
		final String apkName = upgradeManager.getVersionName();
		final String apkDescription = upgradeManager.getDescription();
		String title = context
				.getString(R.string.common_upgrade_title, apkName);
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(
						context.getString(R.string.common_force_upgrade_title)
								+ apkDescription)
				.setPositiveButton(R.string.common_force_upgrade_button_exit,
						new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								((Activity) context).finish();
							}
						})
				.setNeutralButton(R.string.common_force_upgrade_button_ok,
						new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								popUpgradeProgressDialog(context);
								downLoad(context, apkUrl, apkName);
							}
						}).setCancelable(false).show();
	}

	public static void popForceUpgradeErrorDialog(final Context context) {
		UpgradeHttpManager upgradeManager = UpgradeHttpManager.getInstance();
		final String apkUrl = upgradeManager.getApkURL();
		final String apkName = upgradeManager.getVersionName();
		String title = context
				.getString(R.string.common_upgrade_title, apkName);
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage("下载出错")
				.setPositiveButton(R.string.common_force_upgrade_button_exit,
						new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								((Activity) context).finish();
							}
						})
				.setNeutralButton(R.string.common_force_upgrade_button_retry,
						new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								popUpgradeProgressDialog(context);
								downLoad(context, apkUrl, apkName);
							}
						}).setCancelable(false).show();
	}

	public static void popUpgradeProgressDialog(final Context context) {
		UpgradeHttpManager upgradeManager = UpgradeHttpManager.getInstance();
		final String apkName = upgradeManager.getVersionName();
		String title = context.getString(
				R.string.common_upgrade_downloading_title, apkName);
			mProgressHandler = new Handler();
			mProgressDialog = new LehoProgressDialog(context);
			mProgressDialog.setTitle(title);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgress(0);
			mProgressDialog.setMessage("已经下载");
			mProgressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							((Activity) context).finish();

						}
					});
			mProgressDialog.setCancelable(false);
			mProgressDialog.setButton(context
					.getText(R.string.common_force_upgrade_button_background),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							((Activity) context).moveTaskToBack(true);
						}
					});
		// mProgressDialog.setButton2(context.getText(R.string.common_force_upgrade_button_cancel),
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// ((Activity)context).finish();
		// }
		// });
	}

	private static void downLoad(final Context context, final String apkUrl,
			final String apkName) {
		final Handler handler = new Handler();
		String fileName = SavePathManager.getSDPath() + "/LeHuoStore/"
				+ apkName + ".apk";
		// /LeHuoStore/down/
		FileDownloader fileDownloader = new FileDownloader(apkUrl, fileName);
		fileDownloader.startDownload(new FileDownloader.DownloaderListener() {
			@Override
			public void onFailed(int reason) {
				System.out.println("onFailed:");
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (mProgressDialog != null) {
							mProgressDialog.dismiss();
						}
						popForceUpgradeErrorDialog(context);
						Toast.makeText(context,
								R.string.common_upgrade_downloadfile,
								Toast.LENGTH_SHORT).show();
					}
				});
				isNotification = false;
			}

			@Override
			public void onFinished(String file) {
				System.out.println("onFinished:");
				startInstall(context, file);
				notificationDownloadFinish(context, file, apkName);
				if (mProgressDialog != null) {
					mProgressDialog.cancel();
				}
				isNotification = false;
			}

			@Override
			public void onProgressChanged(long progress, long fileSize) {
				if (!isNotification) {
					final int pre = (int) (((float) progress / (float) fileSize) * 100);
					notificationDownloading(context, apkName, pre);
					if (mProgressDialog != null) {
						mProgressHandler.post(new Runnable() {
							@Override
							public void run() {
								if (mProgressDialog.isShowing()) {
									mProgressDialog.setProgress(pre);
								} else {
									mProgressDialog.show();
									mProgressDialog.setProgress(0);
								}
							}
						});
					}
				}
			}
		}, true);
	}

	public static void startInstall(Context context, String fileName) {
		Log.i(TAG, "startInstall-fileName:" + fileName);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		File installFile = new File(fileName);
		Uri uri = Uri.fromFile(installFile);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	private static void notificationDownloading(Context context,
			String apkVersionName, int progress) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String downloadingStr = context
				.getString(R.string.common_upgrade_downloading);
		Notification n = new Notification(R.drawable.notification_icon,
				downloadingStr, System.currentTimeMillis());// 实例化Notification
		n.contentView = new RemoteViews(context.getApplicationContext()
				.getPackageName(), R.layout.download_notification);
		n.contentView.setTextViewText(R.id.down_tv, apkVersionName + "("
				+ downloadingStr + ")");
		n.contentView.setProgressBar(R.id.pb, 100, progress, false);
		// 显示在“正在进行中”
		// n.flags = Notification.FLAG_ONGOING_EVENT;
		n.flags |= Notification.FLAG_AUTO_CANCEL; // 自动终止

		// 通过启动一个Intent让系统来帮你安装新的APK
		Intent intent = new Intent();
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		/* n.setLatestEventInfo(context,apkVersionName, "", pi); */
		n.contentIntent = pi;
		// 设置事件信息，显示在拉开的里面
		// 发出通知

		nm.notify(R.string.app_name, n);
	}

	private static void notificationDownloadFinish(Context context,
			String fileName, String apkVersionName) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification(R.drawable.notification_icon,
				apkVersionName, System.currentTimeMillis());// 实例化Notification
		n.contentView = new RemoteViews(context.getApplicationContext()
				.getPackageName(), R.layout.download_notification);
		// 显示在“正在进行中”
		// n.flags = Notification.FLAG_ONGOING_EVENT;
		n.flags |= Notification.FLAG_AUTO_CANCEL; // 自动终止
		// n.vibrate = new long[]{300};

		// 通过启动一个Intent让系统来帮你安装新的APK
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(fileName)),
				"application/vnd.android.package-archive");
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		String content = context
				.getString(R.string.common_upgrade_install_hint);
		n.contentView
				.setViewVisibility(R.id.status_progress_wrapper, View.GONE);
		n.contentView.setTextViewText(R.id.down_tv, apkVersionName + "("
				+ content + ")");
		//
		/* n.setLatestEventInfo(context,apkVersionName, content, pi); */
		n.contentIntent = pi;
		// 设置事件信息，显示在拉开的里面
		// 发出通知

		nm.notify(R.string.app_name, n);
	}
}
