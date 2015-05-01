package com.youa.mobile.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.youa.mobile.ExitPage;
import com.youa.mobile.LehoTabActivity;
import com.youa.mobile.LehuoIntent;
import com.youa.mobile.MainActivity;
import com.youa.mobile.R;
import com.youa.mobile.common.base.ActionController;
import com.youa.mobile.common.base.BasePage;
import com.youa.mobile.common.data.UserData;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.information.PersonnalInforPage;
import com.youa.mobile.information.action.AddCancelAttentAction;
import com.youa.mobile.input.util.InputUtil;
import com.youa.mobile.life.action.RequestFindSuperPeopleAction;
import com.youa.mobile.life.action.SuperPeopleAction;
import com.youa.mobile.life.action.SuperPeopleAction.RequestType;
import com.youa.mobile.life.data.SuperPeopleClassify;
import com.youa.mobile.life.data.SuperPeopleData;
import com.youa.mobile.login.auth.SupportSite;

public class SuperPeopleAllPage extends BasePage implements OnItemClickListener {

	private class ItemData {
		String mTitle;
		SuperPeopleData mPeopleData;
		boolean mIsTitleItem;

		public ItemData(String title, SuperPeopleData peopleData) {
			mTitle = title;
			mPeopleData = peopleData;
			mIsTitleItem = false;
		}

		public ItemData(String title) {
			mTitle = title;
			mIsTitleItem = true;
		}
	}

	private View mNextButton;
	private ListView mListView;

	private ArrayAdapter<ItemData> mAdapter;

	private List<SuperPeopleClassify> mPeopleCatList;
	private List<ItemData> mPeopleList;

	private int mCurrentCatRequestId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.life_super_people);
		setupViews();
		attionLeho();
		setupData();
	}

	private void attionLeho() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Intent i = getIntent();
				String userid = i.getStringExtra("thirdUid");
				SupportSite site = (SupportSite) i
						.getSerializableExtra("thirdType");
				if (userid != null && !"".equals(userid) && site != null
						&& site == SupportSite.SINA) {
					showAttentLehoDialog();
				}
			}
		});
	}

	private void showAttentLehoDialog() {
		new AlertDialog.Builder(SuperPeopleAllPage.this)
				.setTitle("提示")
				.setMessage(R.string.atten_leho_text)
				.setPositiveButton(R.string.atten_leho_attent_lable,
						new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Weibo weibo = Weibo.getInstance();
								try {
									weibo.follow(SuperPeopleAllPage.this,
											"2554199231");// 2554199231为爱乐活手机版
															// 官方微薄的uid；
								} catch (WeiboException e) {
									e.printStackTrace();
								}
							}
						})
				.setNeutralButton(R.string.common_cancel,
						new Dialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
	}

	private void setupViews() {
		findViewById(R.id.back).setVisibility(View.GONE);
		((TextView) findViewById(R.id.title_id))
				.setText(R.string.life_add_super_people);
		mNextButton = findViewById(R.id.next_btn);
		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SuperPeopleAllPage.this,
						LehoTabActivity.class));
				finish();
			}
		});

		mProcessView = findViewById(R.id.progressBar);
		mListView = (ListView) findViewById(R.id.list);

		mPeopleCatList = new ArrayList<SuperPeopleClassify>();
		mPeopleList = new ArrayList<ItemData>();

		mAdapter = new PeopleListAdapter(this, mPeopleList);
		mAdapter.setNotifyOnChange(false);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	private void setupData() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SuperPeopleAction.REQUEST_TYPE, RequestType.GET_CLASS);
		ActionController.post(this, SuperPeopleAction.class, params,
				new SuperPeopleAction.SuperPeopleResultListener() {

					@Override
					public void onStart() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mProcessView.setVisibility(View.VISIBLE);
							}
						});
					}

					@Override
					public void onFinish(final List<SuperPeopleClassify> data) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mPeopleCatList.clear();
								mPeopleCatList.addAll(data);

								requestPeopleList();
							}
						});

					}

					@Override
					public void onFail(final int resourceID) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								showToast(SuperPeopleAllPage.this, resourceID);
								mProcessView.setVisibility(View.GONE);
							}
						});
					}
				}, true);
	}

	private void requestPeopleList() {
		if (mCurrentCatRequestId >= mPeopleCatList.size())
			return;

		SuperPeopleClassify peopleCat = mPeopleCatList
				.get(mCurrentCatRequestId);
		String key = peopleCat.name;
		if (TextUtils.isEmpty(key)) {
			mCurrentCatRequestId++;
			requestPeopleList();
			return;
		}

		Map<String, Object> para = new HashMap<String, Object>();
		para.put("key", key);
		ActionController
				.post(this,
						RequestFindSuperPeopleAction.class,
						para,
						new RequestFindSuperPeopleAction.ISearchSuperPeopleResultListener() {

							@Override
							public void onFinish(List<SuperPeopleData> list) {
								if (list != null) {
									if (mProcessView.getVisibility() == View.VISIBLE
											&& list.size() > 0) {
										hiddenProgressView();
									}

									String catName = mPeopleCatList
											.get(mCurrentCatRequestId).name;
									mPeopleList.add(new ItemData(catName));
									for (SuperPeopleData peopleData : list) {
										mPeopleList.add(new ItemData(catName,
												peopleData));
									}

									mHandler.post(new Runnable() {
										@Override
										public void run() {
											mAdapter.notifyDataSetChanged();
										}
									});
								}

								mCurrentCatRequestId++;
								requestPeopleList();
							}

							@Override
							public void onFail(int resourceID) {
								mCurrentCatRequestId++;
								requestPeopleList();
							}

							@Override
							public void onStart() {
								// none
							}
						}, true);
	}

	// ('hot_recommend', // 热门推荐
	// 'share_cate', // 乐享美食
	// 'play_group', // 玩乐帮派
	// 'city_beauty', // 都市丽人
	// 'mother_baby', // 辣妈萌宝
	// 'roman_mary', // 浪漫婚恋
	// 'happy_house' // 幸福居家
	// );
	public String getSuperKey(String Value) {
		String key = Value;
		if ("hot_recommend".equals(Value)) {
			key = "热门推荐";
		} else if ("share_cate".equals(Value)) {
			key = "乐享美食";
		} else if ("play_group".equals(Value)) {
			key = "玩乐帮派";
		} else if ("city_beauty".equals(Value)) {
			key = "都市丽人";
		} else if ("mother_baby".equals(Value)) {
			key = "辣妈萌宝";
		} else if ("roman_mary".equals(Value)) {
			key = "浪漫婚恋";
		} else if ("happy_house".equals(Value)) {
			key = "幸福居家";
		}
		return key;
	}

	private static class ViewHolder {
		public ImageView headImage;
		public TextView text;
		public ImageButton button;
		public TextView signature;
	}

	private class PeopleListAdapter extends ArrayAdapter<ItemData> {
		private LayoutInflater inflater;

		public PeopleListAdapter(Context context, List<ItemData> items) {
			super(context, 0, items);
			inflater = LayoutInflater.from(context);
		}

		public void updateView(SuperPeopleData data, boolean isGuanzhu) {
			data.setPayAttentionTo(isGuanzhu);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					notifyDataSetChanged();
				}
			});
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemData itemData = getItem(position);

			ViewHolder holder = null;
			if (!itemData.mIsTitleItem) {
				if (convertView != null
						&& convertView.getId() == R.id.super_people_item) {
					holder = (ViewHolder) convertView.getTag();
				} else {
					holder = new ViewHolder();
					convertView = inflater.inflate(
							R.layout.life_super_people_list_item, null);
					convertView.findViewById(R.id.friend_addfriend).setVisibility(View.GONE);
					holder.headImage = (ImageView) convertView
							.findViewById(R.id.head_image);
					holder.text = (TextView) convertView
							.findViewById(R.id.name);
					holder.button = (ImageButton) convertView
							.findViewById(R.id.pay_attention_to);
					holder.signature = (TextView) convertView
							.findViewById(R.id.signature);
					convertView.setTag(holder);
				}

				final SuperPeopleData data = itemData.mPeopleData;

				holder.text.setText(data.getUserName());

				if (TextUtils.isEmpty(data.getSignature())) {
					// TODO
					holder.signature.setText(getSuperKey(itemData.mTitle));
				} else {
					holder.signature.setText(data.getSignature());
				}

				String imgId = data.getUserImage();
				if (!InputUtil.isEmpty(imgId)) {
					ImageUtil.setHeaderImageView(getContext(),
							holder.headImage, imgId, R.drawable.head_men);
				} else {
					holder.headImage
							.setImageResource(("1".equals(data.getSex())) ? R.drawable.head_men
									: R.drawable.head_women);
				}

				final boolean hasGuanZhu = data.isPayAttentionTo();
				holder.button.setFocusable(false);
				if (hasGuanZhu) {
					holder.button
							.setBackgroundResource(R.drawable.bt_on_switch_bg_seletor);
					// TODO
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
						ActionController.post(SuperPeopleAllPage.this,
								AddCancelAttentAction.class, paramMap,
								new AddCancelAttentAction.IOperateResult() {
									@Override
									public void onEnd(final boolean flag) {
										hiddenProgressView();
										updateView(data, !hasGuanZhu);
										showToast(hasGuanZhu ? R.string.life_guanzhu_not_succ
												: R.string.life_guanzhu_succ);
										sendBroadcast(new Intent(
												LehuoIntent.ACTION_USERCOUNT_NEEDUPDATE));
									}

									@Override
									public void onStart() {
										showProgressView();
									}

									@Override
									public void onFail(int resourceID) {
										hiddenProgressView();
										showToast(resourceID);
									}
								}, true);
					}

				});
			} else {
				if (convertView != null
						&& convertView.getId() == R.id.super_people_title_item) {
					holder = (ViewHolder) convertView.getTag();
				} else {
					holder = new ViewHolder();
					convertView = inflater.inflate(
							R.layout.life_super_people_title_list_item, null);
					holder.text = (TextView) convertView
							.findViewById(R.id.name);
					convertView.setTag(holder);
				}

				holder.text.setText(getSuperKey(itemData.mTitle));
			}

			return convertView;
		}
	}

	// -------------------------------------------------------------------------------------
	public void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CLIENT_QUIT && resultCode == RESULT_OK) {
			finish();
		}
	}

	public static final int REQUEST_CODE_CLIENT_QUIT = 100;

	protected void showQuitPage() {
		Intent intent = new Intent(this, ExitPage.class);
		startActivityForResult(intent, REQUEST_CODE_CLIENT_QUIT);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {// !ApplicationManager.getInstance().isLogin()&&
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				showQuitPage();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		ItemData itemData = mPeopleList.get(position);
		if (!itemData.mIsTitleItem) {
			Intent intent = new Intent(this, PersonnalInforPage.class);
			intent.putExtra(PersonnalInforPage.KEY_USER_ID,
					itemData.mPeopleData.getUserId());
			intent.putExtra(PersonnalInforPage.KEY_USER_NAME,
					itemData.mPeopleData.getUserName());
			startActivity(intent);
		}
	}

}
