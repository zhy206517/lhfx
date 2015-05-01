package com.youa.mobile.login.auth;

import java.io.Serializable;

import com.youa.mobile.R;

import android.content.Context;
import android.text.TextUtils;

/**
	 * SNS站点类型 目前暂支持  sina qq renren。
	 * @author LiQingJun
	 *
	 */
	public enum SupportSite implements Serializable {
		BAIDU("baidu"), SINA("sina"), QQ("qq"), RENREN("renren"),WEIXIN("weixin");
		private String value;

		SupportSite(String siteTag) {
			this.value = siteTag;
		}

		public String getSiteTag() {
			return value;
		}
		public static SupportSite parseSite(String value){
			if(TextUtils.isEmpty(value)){
				return null;
			}
			if("baidu".equals(value)){
				return BAIDU;
			}else if("sina".equals(value)){
				return SINA;
			}else if("renren".equals(value)){
				return RENREN;
			}else if("qq".equals(value)){
				return QQ;
			}else if("weixin".equals(value)){
				return WEIXIN;
			}
			return null;
		}
		
		public String getSiteName(Context c){
			int resId = -1;
			switch (this) {
			case BAIDU:
				resId = R.string.sns_baidu;
				break;
			case RENREN:
				resId = R.string.sns_renren;
				break;
			case QQ:
				resId = R.string.sns_qqzone;
				break;
			case SINA:
				resId = R.string.sns_sina;
				break;
			default:
				break;
			}
			if(resId > 0){
				return c.getApplicationContext().getString(resId);
			}
			return "";
		}
	}