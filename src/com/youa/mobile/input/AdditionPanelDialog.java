package com.youa.mobile.input;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;

//选表情,不解释,简单
public class AdditionPanelDialog implements OnItemClickListener {
	private EmoSelected callback;
	private Context appCtx;
	private GridView mGridView;
	private Handler handler = new Handler();
	private Dialog dialog;
//	private static final int dif = 0;
	private static final int HEIGHT = 210;
	public static final int SHOW_BELOW = 1;
	public static final int SHOW_UPON = 0;
	public interface EmoSelected
	{
		void onSelected(int which);
	}
	public AdditionPanelDialog(EmoSelected callback)
	{
		this.callback = callback;
	}
	//关键是定位
	private void createDialog(Dialog dialog, View view, Context context) {
		DisplayMetrics dm = new DisplayMetrics(); 
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm); 
		appCtx = context.getApplicationContext();		
		initUI(view);
		dialog.getWindow().setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
	}
	
	public void showDialog(final Activity context, View relativeView) {
	    DisplayMetrics dm = new DisplayMetrics(); 
//	    context.getWindowManager().getDefaultDisplay().getMetrics(dm); 
//		final float density = dm.density;
		//为了使用theme创建dialog
		Context dialogContext = new ContextThemeWrapper(context,R.style.Style_AboutDialog);
		Builder builder = new AlertDialog.Builder(dialogContext);
		LayoutInflater inflater = LayoutInflater.from(dialogContext);
		View view = inflater.inflate(R.layout.input_addition_panel, null);		
//		builder.setView(view);
		dialog = builder.create();
//		dialog = new Dialog(dialogContext, R.style.Style_NormalDialog);
		
		ApplicationManager appManager = ApplicationManager.getInstance();
		WindowManager m = context.getWindowManager();
		Display d = m.getDefaultDisplay();
		dialog.show();		
		WindowManager.LayoutParams p =dialog.getWindow().getAttributes();  //获取对话框当前的参数值
		p.height = (int) (appManager.getHeight() * 0.7);//0.96);   //高度设置为屏幕的0.6
		p.width = (int) (appManager.getWidth() * 0.99);    //宽度设置为屏幕的0.95
		dialog.getWindow().setAttributes(p);
	
		createDialog(dialog, view, context);
		
//		int topPosition = relativeView.getTop();
//		int bottomPosition;
//		//计算顶部坐标
//		View tempView = relativeView;
//		while(true)
//		{
//			try
//			{
//				View parentView = (View) tempView.getParent();
//				if(parentView == null || parentView == tempView)
//				{
//					break;
//				}
//				topPosition += parentView.getTop();
//				tempView = parentView;
//			}catch(ClassCastException e)
//			{
//				break;
//			}
//		}
//		//计算底部坐标
//		int offsetHeight = relativeView.getHeight();
//		bottomPosition = topPosition + offsetHeight;
////		int actTop = actHeight-relativePostionView.getHeight()-offsetHeight;
//		final int relativeViewTop = topPosition;
//		final int relativeViewBottom = bottomPosition;
//		handler.postDelayed(new Runnable(){
//			@Override
//			public void run() {
//				//定位
//				//adjustDialog(dialog, context, relativeViewTop, relativeViewBottom);
//			}
//		}, 50);
	}
	
//	//关键是定位
//	private void adjustDialog(Dialog dialog, Context context, int topPosition, int bottomPosition) {
//		DisplayMetrics dm = new DisplayMetrics(); 
//		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm); 
//		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//		int screenH = dm.heightPixels;
//		float density = ApplicationManager.getInstance().getDensity();
//		
//		int height = (int)(HEIGHT * density);
//		if((screenH - topPosition) < height)
//		{
//			int dif = (int)(25 * density);//计算之后有误差
//			lp.y = topPosition - height - dif;
//			lp.height = height;
//		}else
//		{
//			int bottomHeight = screenH-bottomPosition;
//			lp.y = bottomPosition;			
//			lp.height = bottomHeight;
//		}
//		System.out.println("screenH:" + screenH);
//		System.out.println("topPosition:" + topPosition);
//		System.out.println("bottomPosition:" + bottomPosition);
//		System.out.println("height:" + height);
//		System.out.println("width:" + lp.width);
//		System.out.println("x:" + lp.x);
//		System.out.println("y:" + lp.y);
//		//获取窗口layout的参数
//		//关键是top,其他都固定的
//		//空白区域alpha
//		
//		lp.dimAmount = 1f;
//		//dialog本身alpha
//		lp.alpha = 1f;
//		//宽度fill_parent
//		lp.width = WindowManager.LayoutParams.FILL_PARENT;
//
//		//只有gravity为top,y参数才work
//		lp.gravity = Gravity.TOP;
//		dialog.getWindow().setAttributes(lp);
//		dialog.setCanceledOnTouchOutside(true);
////		if(isUp)
////		{
////			dialog.findViewById(R.id.AddPanelFrame).setBackgroundResource(R.drawable.pick_emo_up_background);
////			dialog.findViewById(R.id.AddPanelShader).setVisibility(View.VISIBLE);
////			return SHOW_UPON;
////		}
////		else
////		{
////			dialog.findViewById(R.id.AddPanelFrame).setBackgroundResource(R.drawable.pick_emo_below_background);
////			dialog.findViewById(R.id.AddPanelShader).setVisibility(View.GONE);
////			return SHOW_BELOW;
////		}
//	}


	private void initUI(View view) {
		mGridView = (GridView) view.findViewById(R.id.emoGrid);
		mGridView.setAdapter(new EmoAdapter(appCtx));
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String emoChar = (String) arg0.getItemAtPosition(arg2);
		if(emoChar != null && emoChar.length()>0)
		{
			callback.onSelected(arg2);
			dialog.dismiss();
			dialog = null;
		}
	}


}
