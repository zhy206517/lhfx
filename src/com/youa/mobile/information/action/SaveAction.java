package com.youa.mobile.information.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.information.manager.PersonalInfoManager;
import com.youa.mobile.information.action.SaveAction.ISaveResultListener;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;

public class SaveAction extends BaseAction<ISaveResultListener> {
	public static final String KEY_UID = "uid";
	public static final String KEY_USERNAME = "";
	public static final String KEY_NICKNAME = "username";
	public static final String KEY_SEX = "sex";
	public static final String KEY_PROVINCE = "province";
	public static final String KEY_CITY = "city";
	public static final String KEY_COUNTIES = "counties";
	public static final String KEY_BIRTHDAYYEAR = "birth_year";
	public static final String KEY_BIRTHDAYMONTH = "birth_month";
	public static final String KEY_BIRTHDAYDAY = "birth_day";
	public static final String KEY_INTRUDUCE = "signature";
	public static final String KEY_HEADERIMAGEPATH="path";
	

	public interface ISaveResultListener extends IResultListener, IFailListener {
		public void onStart();

		public void onEnd();
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISaveResultListener resultListener) throws Exception {
		resultListener.onStart();
		String uid = (String) params.get(KEY_UID);
		String username = (String) params.get(KEY_UID);
		String nickname = (String) params.get(KEY_USERNAME);
		String sex = (String) params.get(KEY_SEX);
		String province = (String) params.get(KEY_PROVINCE);
		String city = (String) params.get(KEY_CITY);
		String counties = (String) params.get(KEY_COUNTIES);
		String birthdayYear = (String) params.get(KEY_BIRTHDAYYEAR);
		String birthdayMonth = (String) params.get(KEY_BIRTHDAYMONTH);
		String birthdayDay = (String) params.get(KEY_BIRTHDAYDAY);
		String intruduce = (String) params.get(KEY_INTRUDUCE);
		String imagePath = (String) params.get(KEY_HEADERIMAGEPATH);
		new PersonalInfoManager().saveManager(
				context,
				uid, 
				username, 
				nickname, 
				sex,
				province, 
				city,
				counties,
				birthdayYear, 
				birthdayMonth, 
				birthdayDay, 
				intruduce, 
				imagePath);
		resultListener.onEnd();
	}
}
