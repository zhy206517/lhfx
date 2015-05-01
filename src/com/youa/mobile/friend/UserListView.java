package com.youa.mobile.friend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youa.mobile.LehuoIntent;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BaseListView;
import com.youa.mobile.common.manager.ApplicationManager;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.friend.UserListView.SuperPeopleHolder;
import com.youa.mobile.friend.friendsearch.FriendSearchResultActivity;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.information.action.AddCancelAttentAction;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.login.LoginPage;
import com.youa.mobile.ui.base.BaseHolder;

public class UserListView extends
		BaseListView<SuperPeopleHolder, List<SuperPeopleData>> {
	private LayoutInflater mInflater;
	private Context mContext;
	private Handler mHandler;

	public class SuperPeopleHolder extends BaseHolder {
		public ImageView headImage;
		public TextView text;
		public ImageButton button;
		public TextView signature;
		public RelativeLayout addFriendArea;
		public RelativeLayout superPeopleArea;
	}

	public UserListView(ListView listView, View header, View footer,
			Handler handler) {
		super(listView, header, footer);
		mInflater = LayoutInflater.from(listView.getContext());
		mContext = listView.getContext();
		mHandler = handler;
	}

	private float x, y;

	@Override
	protected View createTemplateView(int pos) {
		RelativeLayout linear = (RelativeLayout) mInflater.inflate(
				R.layout.life_super_people_list_item, null);
		return linear;
	}

	@Override
	protected SuperPeopleHolder getHolder(View convertView, int pos) {
		SuperPeopleHolder holder = new SuperPeopleHolder();
		holder.headImage = (ImageView) convertView
				.findViewById(R.id.head_image);
		holder.text = (TextView) convertView.findViewById(R.id.name);
		holder.button = (ImageButton) convertView
				.findViewById(R.id.pay_attention_to);
		holder.signature = (TextView) convertView.findViewById(R.id.signature);
		holder.addFriendArea = (RelativeLayout) convertView
				.findViewById(R.id.friend_addfriend);
		holder.superPeopleArea = (RelativeLayout) convertView
				.findViewById(R.id.super_people_item);
		return holder;
	}

	@Override
	protected void setDataWithHolder(final SuperPeopleHolder holder,
			int position, boolean isMoving) {
		final SuperPeopleData data = mDataList.get(position);
		if (position == 0 && TextUtils.isEmpty(data.getUserId())) {
			holder.addFriendArea.setVisibility(View.VISIBLE);
			holder.superPeopleArea.setVisibility(View.GONE);
			return;
		}
		holder.addFriendArea.setVisibility(View.GONE);
		holder.superPeopleArea.setVisibility(View.VISIBLE);
		String username = data.getUserName();
		String imgId = data.getUserImage();
		holder.text.setText(username);
		holder.signature.setText("");
		if (!TextUtils.isEmpty(data.getSignature())) {
			holder.signature.setText(data.getSignature());
		}
		if (!InputUtil.isEmpty(imgId)) {
			ImageUtil.setHeaderImageView(getContext(), holder.headImage, imgId,
					R.drawable.head_men);
		} else {
			holder.headImage
					.setImageResource(("1".equals(data.getSex())) ? R.drawable.head_men
							: R.drawable.head_women);
		}
		final boolean hasGuanZhu = data.isPayAttentionTo();
		holder.button.setFocusable(false);
		if (data.getRelation() || data.isRecommendSuperPeople) {
			if (hasGuanZhu) {
				holder.button
						.setBackgroundResource(R.drawable.bt_on_switch_bg_seletor);
				holder.button.setImageResource(R.drawable.image_on_switch);
			} else {
				holder.button
						.setBackgroundResource(R.drawable.bt_off_switch_bg_seletor);
				holder.button.setImageResource(R.drawable.image_off_switch);
			}
			final String operateType = hasGuanZhu ? AddCancelAttentAction.TYPE_CANCEL
					: AddCancelAttentAction.TYPE_ADD;
			holder.button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!ApplicationManager.getInstance().isLogin()) {
						Intent i = new Intent(getContext(), LoginPage.class);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						getContext().startActivity(i);
						return;
					}

					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put(AddCancelAttentAction.KEY_FOLLOW_UID,
							data.getUserId());
					paramMap.put(AddCancelAttentAction.KEY_OPERATE_TYPE,
							operateType);
					if (!hasGuanZhu) {
						paramMap.put(AddCancelAttentAction.KEY_ADD_IMAGEID,
								data.getUserImage());
						paramMap.put(AddCancelAttentAction.KEY_ADD_SEXINT,
								data.getSex());
						paramMap.put(AddCancelAttentAction.KEY_ADD_UNAME,
								data.getUserName());
					}
					ActionController.post(getContext(),
							AddCancelAttentAction.class, paramMap,
							new AddCancelAttentAction.IOperateResult() {
								@Override
								public void onEnd(final boolean flag) {
									mHandler.post(new Runnable() {
										public void run() {
											data.setPayAttentionTo(!hasGuanZhu);
											getAdapter().notifyDataSetChanged();
											Toast.makeText(
													getContext(),
													hasGuanZhu ? R.string.life_guanzhu_not_succ
															: R.string.life_guanzhu_succ,
													Toast.LENGTH_SHORT).show();
											getContext()
													.sendBroadcast(
															new Intent(
																	LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE));
										}
									});

								}

								@Override
								public void onStart() {
								}

								@Override
								public void onFail(final int resourceID) {
									mHandler.post(new Runnable() {
										public void run() {
											Toast.makeText(
													getContext(),
													getContext().getString(
															resourceID),
													Toast.LENGTH_SHORT).show();
										}
									});

								}
							}, true);
				}

			});
		} else {
			holder.button.setBackgroundResource(0);
			holder.button.setImageResource(R.drawable.more_right_icon);
			holder.button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (data != null) {
						Intent intent = new Intent(getContext(),
								PersonnalInforPage.class);
						intent.putExtra(PersonnalInforPage.KEY_USER_ID,
								data.getUserId());
						intent.putExtra(PersonnalInforPage.KEY_USER_NAME,
								data.getUserName());
						getContext().startActivity(intent);
					}

				}
			});
		}
	}

	@Override
	protected void treateStopEvent(SuperPeopleHolder holder, int position) {
	}
}
