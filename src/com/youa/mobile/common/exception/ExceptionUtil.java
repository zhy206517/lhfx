package com.youa.mobile.common.exception;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.youa.mobile.R;
import com.youa.mobile.YoumentEvent;
import com.youa.mobile.common.manager.SavePathManager;
import com.youa.mobile.common.util.LogFile;

public class ExceptionUtil {
	
	public interface CheckExceptionListener {
		public static final int RESULT_OK = 1;
		public static final int RESULT_CANCEL = 2;
		public static final int RESULT_NOERROR = 3;
		public void onResult(int result);
	}
	
	public static void initErrorHandler(final Context context) {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(){
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {	
				System.out.println("uncaughtException");
				//保存文件
				ex.printStackTrace();
				try {
					File logFile = SavePathManager.getLogFile(context);
					FileOutputStream outStream = new FileOutputStream(logFile, true);
					PrintStream out = new PrintStream(outStream);
					ex.printStackTrace(out);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				System.exit(0);
				//设置定时
				Context appContext = context.getApplicationContext();
				Intent mainintent = new Intent("com.youa.mobile.restart");
				PendingIntent intent = PendingIntent.getBroadcast(
						context,
						0, 
						mainintent,
						mainintent.getFlags());
				
//				PendingIntent intent = PendingIntent.getActivity(
//						context.getApplicationContext(), 
//						0,
//						mainintent,
//						mainintent.getFlags());
				AlarmManager mgr = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1500, intent);
				//退出
				System.exit(2);
//				try {
//					Toast.makeText(context, R.string.common_fatal_error_content, Toast.LENGTH_LONG).show();
//				} catch(Exception e) {
//					e.printStackTrace();
//				}
//				thread.notifyAll();
//				thread.interrupt();
//				System.out.println("uncaughtException - end");
//				new Handler().post(new Runnable(){
//					public void run() {
//						System.out.println("########################################################################uncaughtException");
//						Context appContext = context.getApplicationContext();
//						Intent intent = new Intent(appContext, MainPage.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						appContext.startActivity(intent);
//					}
//				});				
			}
		});
	}
	
	//检查错误报告
	public static void checkException(
			final Activity context, 
			final CheckExceptionListener checkExceptionListener)
	{
		final String mailText = RecordException.getLastException(context);
		if(mailText==null || mailText.length()==0)  {
			checkExceptionListener.onResult(CheckExceptionListener.RESULT_NOERROR);
			return;
		}
		MobclickAgent.onEvent(context, YoumentEvent.EVENT_HAS_ERROR);
		System.out.println("#######################################################################has error");
		System.out.println(">>:" + mailText + ":<<");
		System.out.println("#######################################################################error end");
		new AlertDialog.Builder(context)
		            .setTitle(R.string.common_fatal_error_title)
		            .setMessage(R.string.common_fatal_error_content)
		            .setPositiveButton(
	            		R.string.common_know, 
	            		new Dialog.OnClickListener(){
							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								String versionName = "";
								PackageManager manager = context.getPackageManager();
								try { 
								   PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
								   versionName = info.versionName; 
								} catch (NameNotFoundException e) {
								}
								//发送mail
								sendException(
										context, 
										mailText, 
										versionName
										);
								checkExceptionListener.onResult(CheckExceptionListener.RESULT_OK);
							}
	            		}).show();
	}
	
	private static void sendException(
			final Activity context, 
			final String mailText, 
			final String versionName) {
		//发送错误报告
		new Thread() {
			public void run() {
				try {
					String sdkVer = android.os.Build.VERSION.SDK.toLowerCase();
					if(sdkVer==null)
						sdkVer = "unknown";
					String reportText = "exception lehuo: "+ versionName + " sdk "+sdkVer + "\r\n";	
					reportText = reportText + mailText;
					System.out.println("reportText:" + reportText);
					
					LogFile logFile = LogFile.getInstance(context);
					Log.i("BackDoor", "send log file: "+logFile.readWholeFile());
					reportText+="\r\n"+logFile.readWholeFile();
					
					ExceptionRequestManager exceptionRequestManager = ExceptionRequestManager.getInstance();
					exceptionRequestManager.uploadError(context, reportText);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

		//删除文件
		File logFile = SavePathManager.getLogFile(context);
		logFile.delete();
	}
}
