package com.youa.mobile.common.util;

import java.util.Date;

import android.content.Context;

import com.youa.mobile.R;

public class DateUtil {
	public static String getAbbreTime(Context context, long time)
	{
		String ret = "";
		long nowMil = new Date().getTime();
		long dateMil = time;
		Date date = new Date(time);
		if(nowMil - dateMil < 3600*1000)
		{
			int secDif = (int) ((nowMil-dateMil)/1000);
			int minDif = secDif/60;
			if(minDif>0)
				ret = minDif + context.getString(R.string.common_time_minite_before);
			else 
			{
				if(secDif<0)
					secDif = 0;
				ret = secDif + context.getString(R.string.common_time_second_before);
			}
			return ret;
		}
		int hour = date.getHours();
		int min = date.getMinutes();
		Date tmpDate = new Date(date.getTime());
		tmpDate.setHours(0);
		tmpDate.setMinutes(0);
		tmpDate.setSeconds(0);
		long nowDay = (nowMil/1000-tmpDate.getTime()/1000)/(3600*24);
		if(nowDay>2)
		{
			String month = context.getString(R.string.common_time_month);
			String day = context.getString(R.string.common_time_day);
			ret = (date.getMonth()+1)+ month +date.getDate() + day;
		}else if(nowDay==2)
		{
			ret = context.getString(R.string.common_time_before_yesterday);
		}else if(nowDay== 1)
		{
			ret = context.getString(R.string.common_time_yesterday);
		}else
		{
			ret = context.getString(R.string.common_time_today);
		}
		ret = ret + " "+hour+":";
		if(min<10)
			ret = ret +"0"+ min;
		else
			ret = ret + min;
		return ret;
	}
	public static String getDetailTime(Context context, long time)
	{
		String ret = "";
		long nowMil = new Date().getTime();
		Date date = new Date(time);
		int hour = date.getHours();
		int min = date.getMinutes();
		Date tmpDate = new Date(date.getTime());
		tmpDate.setHours(0);
		tmpDate.setMinutes(0);
		tmpDate.setSeconds(0);
		long nowDay = (nowMil/1000-tmpDate.getTime()/1000)/(3600*24);
		if(nowDay>2)
		{
			String month = context.getString(R.string.common_time_month);
			String day = context.getString(R.string.common_time_day);
			ret = (date.getMonth()+1)+month+date.getDate()+day;
		}else if(nowDay==2)
		{
			ret = context.getString(R.string.common_time_before_yesterday);
		}else if(nowDay== 1)
		{
			ret = context.getString(R.string.common_time_yesterday);
		}else
		{
			ret = context.getString(R.string.common_time_today);
		}
		ret = ret + " "+hour+":";
		if(min<10)
			ret = ret +"0"+ min;
		else
			ret = ret + min;
		return ret;
	}
	
	public static String getDuration(Context context, long time) {
		StringBuffer showTextBuf = new StringBuffer();		
		if(time < 1000) {
			showTextBuf.append((float)(time/10)/100);//保留2位小数
			showTextBuf.append("''");
		} else {
			int secords = (int)(time/1000 % 60);
			int minutes = (int)(time/(1000*60) % 60);
			if(minutes > 0) {
				showTextBuf.append(minutes + "'");
			}
			if(secords > 0) {
				showTextBuf.append(secords + "''");
			}
		}	
		return showTextBuf.toString();
	}
}
