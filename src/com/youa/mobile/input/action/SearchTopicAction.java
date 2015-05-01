package com.youa.mobile.input.action;

import java.util.Map;
import android.content.Context;
import com.youa.mobile.common.base.BaseAction;
import com.youa.mobile.common.base.IAction.IResultListener;
//import com.youa.mobile.home.data.PopThemeInfo;
//import com.youa.mobile.home.manager.HomeManager;
import com.youa.mobile.input.action.SearchTopicAction.ISearchTopicListener;

public class SearchTopicAction extends BaseAction<ISearchTopicListener> {

	public interface ISearchTopicListener extends IResultListener {
		public void onStart();
		//-----------
//		public void onFinish(List<PopThemeInfo> data);
		//-----------
		//void onFail(int resourceID);
	}

	@Override
	protected void onExecute(Context context, Map<String, Object> params,
			ISearchTopicListener callback) throws Exception {
		//-----------
//		List<PopThemeInfo> data = new HomeManager().requestThemeCollectionList(context, 0, 100);
//		callback.onStart();
		//-----------
//		if(data==null){
//			data = new ArrayList<PopThemeInfo>(0);
//			
//			for(int i=0; i < 6; i++){
//				PopThemeInfo info = new PopThemeInfo();
//				info.name = "test"+i;
//				info.sUid = i+"";
//				data.add(info);
//				if(i == 0){
//					PopThemeInfo info1 = new PopThemeInfo();
//					info1.name = "test00";
//					info1.sUid = "00";
//					data.add(info1);
//					PopThemeInfo info2 = new PopThemeInfo();
//					info2.name = "test000";
//					info2.sUid = "000";
//					data.add(info2);
//				}
//			}
//		}
		//-----------
//		callback.onFinish(data);
		//-----------
	}

}
