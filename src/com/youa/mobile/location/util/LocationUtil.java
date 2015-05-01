package com.youa.mobile.location.util;

import com.youa.mobile.SystemConfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

public class LocationUtil {
	
	public EarthPoint getCurLocation(Context c){
		SharedPreferences sp = c.getSharedPreferences(
				SystemConfig.XML_FILE_LOCATION_GUIDE,
				Context.MODE_WORLD_READABLE);
		String placeX = sp.getString(SystemConfig.KEY_PLACE_X, "");
		String placeY = sp.getString(SystemConfig.KEY_PLACE_Y, "");
		EarthPoint ep = new EarthPoint();
		if(!TextUtils.isEmpty(placeY) && !TextUtils.isEmpty(placeX)){
			try {
				ep.latitude = Double.parseDouble(placeY);
				ep.longitude = Double.parseDouble(placeX);
			} catch (NumberFormatException e) {
//				Toast.makeText(context, text, duration)
				e.printStackTrace();
			}
		}
		return ep;
	}
	
	public double getDistance(EarthPoint ep1, EarthPoint ep2) {
		double R = 6378.137;
		double dLat = (ep2.latitude - ep1.latitude) * Math.PI / 180;
		double dLon = (ep2.longitude - ep1.longitude) * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(ep1.latitude * Math.PI / 180)
				* Math.cos(ep2.latitude * Math.PI / 180) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R;
		return distance;
	}
	
	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI= 6.28318530712; // 2*PI
	static double DEF_PI180= 0.01745329252; // PI/180.0
	static double DEF_R =6370693.5; // radius of earth
	public double GetShortDistance(double lon1, double lat1, double lon2, double lat2)
	{
		double ew1, ns1, ew2, ns2;
		double dx, dy, dew;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 经度差
		dew = ew1 - ew2;
		// 若跨东经和西经180 度，进行调整
		if (dew > DEF_PI)
		dew = DEF_2PI - dew;
		else if (dew < -DEF_PI)
		dew = DEF_2PI + dew;
		dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
		dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
		// 勾股定理求斜边长
		distance = Math.sqrt(dx * dx + dy * dy);
		return distance;
	}
	
	public double GetLongDistance(double lon1, double lat1, double lon2, double lat2)
	{
		double ew1, ns1, ew2, ns2;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 求大圆劣弧与球心所夹的角(弧度)
		distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);
		// 调整到[-1..1]范围内，避免溢出
		if (distance > 1.0)
		     distance = 1.0;
		else if (distance < -1.0)
		      distance = -1.0;
		// 求大圆劣弧长度
		distance = DEF_R * Math.acos(distance);
		return distance;
	}
	
	public double getCurFarByEarthPoint(Context c, EarthPoint ep){
		EarthPoint curPoint = getCurLocation(c);
		//return getDistance(curPoint, ep);
		return GetLongDistance(curPoint.longitude/1e6, curPoint.latitude/1e6, ep.longitude/1e6, ep.latitude/1e6);
	}
	
	public class EarthPoint{
		public double latitude;
		public double longitude;

		public EarthPoint(double latitude, double longitude) {
			this();
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
//		public EarthPoint(String latitude, String longitude) {
//			this(Double.parseDouble(latitude), Double.parseDouble(longitude));
//		}
		
		public EarthPoint() {
			super();
		}
	}
}
