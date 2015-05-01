package com.youa.mobile.common.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.login.LoginPage;

public class BasePage extends Activity {
	protected static String TAG = "";
	protected View mProcessView;
	protected View mLoadView;
	protected int mIconSuccResId = R.drawable.toast_succ;
	protected int mIconFailResId = R.drawable.toast_fail;
	public static List<Activity> activityList = new ArrayList<Activity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		ApplicationManager.getInstance().init(this);
		activityList.add(this);
	}

	protected static void exitClient(Context context) {
		// 关闭所有Activity
		for (int i = 0; i < activityList.size(); i++) {
			if (null != activityList.get(i)) {
				activityList.get(i).finish();
			}
		}
		activityList.clear();
	}

	private void popActivity() {
		if (activityList == null || activityList.size() < 1) {
			return;
		}
		for (int i = 0; i < activityList.size(); i++) {
			if (activityList.get(i) == this) {
				activityList.remove(i);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onDestroy() {
		popActivity();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	protected Handler mHandler = new Handler();

	protected void showToast(final Context context, final int resourceID) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context.getApplicationContext(), getString(resourceID), Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void showToastWithIcon(final int resourceID, final int iconId) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(getApplicationContext(), getString(resourceID), Toast.LENGTH_SHORT);
				LinearLayout toastView = (LinearLayout) toast.getView();
				toastView.setGravity(Gravity.CENTER);
				toastView.setOrientation(LinearLayout.HORIZONTAL);
				ImageView iconView = new ImageView(getApplicationContext());
				iconView.setImageResource(iconId);
				toastView.addView(iconView, 0);
				toast.show();
			}
		});
	}

	protected void showToast(final int resourceID) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), getString(resourceID), Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected ProgressDialog mProgressDialog;

	protected void showProgressDialog(final Context context, final int title, final int tip) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressDialog = ProgressDialog.show(context, getResources().getString(title), getResources().getString(tip), true, true);
			}
		});
	}

	protected void showProgressView() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mProcessView.setVisibility(View.VISIBLE);
				if (mLoadView != null) {
					mLoadView.setVisibility(View.GONE);
				}
			}
		});
	}

	protected void hiddenProgressView() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mProcessView.setVisibility(View.GONE);
				if (mLoadView != null) {
					mLoadView.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	protected void hideProgressDialog() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (null != mProgressDialog && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		});
	}

	protected boolean checkInput(final TextView textView, int lblStringRes) {
		String text = textView.getText().toString();
		if (text == null || "".equals(text)) {
			showInputPrompt(lblStringRes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					textView.requestFocus();
				}
			});
			return false;
		}
		return true;
	}

	protected boolean checkInput(final RadioGroup radioGroup, int lblStringRes) {
		int checkId = radioGroup.getCheckedRadioButtonId();
		if (checkId == -1) {
			showInputPrompt(lblStringRes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					radioGroup.getChildAt(0).requestFocus();
				}
			});
			return false;
		}
		return true;
	}

	private void showInputPrompt(final int lblStringRes, DialogInterface.OnClickListener onClickListener) {
		String paramtxt = getResources().getString(lblStringRes);
		String showText = getResources().getString(R.string.common_error_input_null, paramtxt);
		new AlertDialog.Builder(this).setTitle(R.string.common_error_lbl).setMessage(showText).setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton(R.string.common_ok, onClickListener

				).show();
	}

	protected boolean checkLength(final TextView textView, int lblStringRes, int maxlength) {
		String text = textView.getText().toString();
		if (text != null && text.length() >= maxlength) {
			showInputPrompt(lblStringRes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					textView.requestFocus();
				}
			});
			return false;
		}
		return true;
	}

	protected void startLoginActivity() {
		startActivity(new Intent(this, LoginPage.class));
	}

}
