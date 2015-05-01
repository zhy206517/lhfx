package com.youa.mobile.common.base;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.youa.mobile.R;
import com.youa.mobile.login.auth.BaseAuthPage;
import com.youa.mobile.login.auth.BaseToken;
import com.youa.mobile.login.auth.SupportSite;
import com.youa.mobile.login.util.LoginUtil;

public abstract class BaseSyncPage extends BaseAuthPage {
	protected View mBindSinaParentView;
	protected View mBindQQParentView;
	protected View mBindRenrenParentView;
	
	protected CheckBox mBindSinaBox;
	protected CheckBox mBindQQBox;
	protected CheckBox mBindRenrenBox;
	
	protected ProgressBar loadProgressBar;
	protected List<BaseToken> tokens = new ArrayList<BaseToken>();
	
	public boolean isSyncPage = false;
	
	@Override
	public void onAuthResult(BaseToken tokenData) {
		updateAllSyncBoxStatus();
	}
	@Override
	protected void onResume() {
		super.onResume();
		updateAllSyncBoxStatus();
	}
	
	protected void initSyncBoxCheckListener(OnClickListener onClickListener) {
		mBindSinaBox.setOnClickListener(onClickListener);
		mBindRenrenBox.setOnClickListener(onClickListener);
		mBindQQBox.setOnClickListener(onClickListener);
	}
	
	protected void openThirdAuthPageOrUnbind(int btnId, boolean clearStoreToken) {
		View v = findViewById(btnId);
		BaseToken token = (BaseToken) v.getTag();
		if (token == null)
			return;
		SupportSite site = token.site;
		String thirdUid = token.userid;
		boolean canAuth = (thirdUid == null || "".equals(thirdUid) || token.isExpired(BaseSyncPage.this));
		if (canAuth) {	// 未授权
			LoginUtil.openThirdAuthPage(BaseSyncPage.this, site);
		} else {		//已授权
			if(clearStoreToken){
				token.clearToken(getApplicationContext());
			}else{
				if(!token.isSync){
					token.isSync = true;
					token.saveToken(getApplicationContext());
				}else{
					token.isSync = false;
					token.saveToken(getApplicationContext());
				}
			}
			updateAllSyncBoxStatus();
		}
	}
	
	private void hideOrShowLoadingView(final int showOrHidn) {
		if(loadProgressBar != null){
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					loadProgressBar.setVisibility(showOrHidn == View.VISIBLE ? View.VISIBLE : View.GONE);
				}
			});
		}
	}
	
	protected void updateAllSyncBoxStatus(){
		hideOrShowLoadingView(View.VISIBLE);
		tokens = BaseToken.getAllToken(BaseSyncPage.this);
		if(tokens == null || tokens.size() == 0){
			return;
		}
		CheckBox box = null;
		for (BaseToken token : tokens) {
			if(token != null){
				switch (token.site) {
				case QQ:
					box = mBindQQBox;
					break;
				case SINA:
					box = mBindSinaBox;
					break;
				case RENREN:
					box = mBindRenrenBox;
					break;
				default:
					break;
				}
				if(box != null && token != null){
					boolean isExpired = token.isExpired(BaseSyncPage.this);
					boolean isBinded = token.isBinded(getApplicationContext());
					box.setChecked(isBinded && !isExpired && (isSyncPage ? token.isSync : true));// && 
					box.setTag(token);
					View parentView = null;
					switch (token.site) {
					case QQ:
						parentView = mBindQQParentView;
						break;
					case SINA:
						parentView = mBindSinaParentView;
						break;
					case RENREN:
						parentView = mBindRenrenParentView;
						break;
					default:
						break;
					}
					
					if(parentView != null){
						TextView expiredView = (TextView)parentView.findViewById(R.id.is_expired);
						if(expiredView != null && isBinded){
							if(isExpired){
								expiredView.setVisibility(View.VISIBLE);
								expiredView.setText(R.string.third_bind_expried);
							}else{
								expiredView.setText("");
								expiredView.setVisibility(View.GONE);
							}
						}
					}
				}	
			}
		}
		hideOrShowLoadingView(View.GONE);
	}
}
