package com.youa.mobile.common.manager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

public class NetworkStatus {
	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}
		NetworkInfo[] netinfo = cm.getAllNetworkInfo();
		if (netinfo == null) {
			return false;
		}
		for (int i = 0; i < netinfo.length; i++) {
			if (netinfo[i] != null && netinfo[i].isConnected()) {
				return true;
			}
		}
		return false;
	}
	//获取IP
	public static String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("NetworkStatus", ex.toString());
	    }
	    return null;
	}
	
	public static NetworkInfo getNetWorkInfo(Context ctx){
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetinfo = cm.getActiveNetworkInfo();
		return activeNetinfo;
	}
	
	
	public static boolean isCmWap(Context ctx){
		NetworkInfo info = getNetWorkInfo(ctx);
		if(info != null && info.getType() == ConnectivityManager.TYPE_MOBILE){
			return true;
		}
		return false ;
	}
	
//	public static void getNetWorkInfo(Context ctx){
//		ConnectivityManager connectivity = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);  
//        if (connectivity != null) {   
//            // 获取网络连接管理的对象  
//            NetworkInfo info = connectivity.getActiveNetworkInfo();  
//
//            if (info != null && info.isConnected()) {  
//                // 判断当前网络是否已经连接  
//                if (info.getState() == NetworkInfo.State.CONNECTED) {  
//                    if(info.getTypeName().equals("WIFI")){  
//                           
//                    }else{   
//                        Uri uri = Uri.parse("content://telephony/carriers/preferapn");  
//                        Cursor cr = ctx.getContentResolver().query(uri, null,null, null, null);  
//                        while (cr != null && cr.moveToNext()) {  
//                              // APN id  
//                              @SuppressWarnings("unused")  
//                              String id = cr.getString(cr.getColumnIndex("_id"));  
//                              // APN name  
//                              @SuppressWarnings("unused")  
//                              String apn = cr.getString(cr.getColumnIndex("apn"));  
//                              // do other things...  
//                              String strProxy = cr.getString(cr.getColumnIndex("proxy"));  
//                              String strPort = cr.getString(cr.getColumnIndex("port"));  
//                              if(strProxy != null && !"".equals(strProxy)){  
//                                  Config.host = strProxy;  
//                                  Config.port = Integer.valueOf(strPort);  
//                              }  
//                                
//                         }  
//                    }   
//                }  
//            } 
//		
//	}
	
}
