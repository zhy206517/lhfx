package com.youa.mobile.information.action;

import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IFailListener;
import com.youa.mobile.common.base.IAction.IResultListener;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.information.action.AddCancelAttentAction.IOperateResult;
import com.youa.mobile.information.data.PersonalInformationData;
import com.youa.mobile.information.manager.PersonalInfoManager;

public class AddCancelAttentAction extends BaseAction <IOperateResult>{

	public static final String KEY_FOLLOW_UID = "uid";
	public static final String KEY_ADD_UNAME = "uname";
	public static final String KEY_ADD_IMAGEID = "imageid";
	public static final String KEY_ADD_SEXINT = "sexint";
	public static final String KEY_OPERATE_TYPE = "type";
	public static final String TYPE_ADD = "add";
	public static final String TYPE_CANCEL = "cancel";
	
	public interface IOperateResult extends IResultListener, IFailListener {
		void onStart();
		void onEnd(boolean flag);
	}
	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			IOperateResult resultListener) throws Exception {
		resultListener.onStart();
		String uid = ApplicationManager.getInstance().getUserId();
		String followUID = (String)params.get(KEY_FOLLOW_UID);
		String type = (String)params.get(KEY_OPERATE_TYPE);
		String uname = (String)params.get(KEY_ADD_UNAME);
		String imageId = (String)params.get(KEY_ADD_IMAGEID);
		String sexInt = (String)params.get(KEY_ADD_SEXINT);
		if(type == null) {
			throw new IllegalArgumentException("please set type");
		}
		boolean isAdd;
		if(type.equals(TYPE_ADD)) {
			isAdd = true;
		} else {
			isAdd = false;
		}
		try {
			if(isAdd) {
				new PersonalInfoManager().addAttent(
						context, 
						uid, 
						followUID,
						uname,
						imageId,
						sexInt);
			} else {
				new PersonalInfoManager().cancelAttent(context, uid, followUID);
			}
			
			resultListener.onEnd(isAdd);
		} catch (MessageException e) {
			String errorCode = e.getErrCode();
			if("jt.follow.followed".equals(errorCode)
					|| "jt.follow.u_followed".equals(errorCode)) {
				e.setResID(R.string.information_error_followed);
			}
			
			throw e;
		}
		
	}
}
