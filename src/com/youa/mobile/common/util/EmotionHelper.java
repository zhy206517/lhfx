package com.youa.mobile.common.util;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.text.Html.ImageGetter;

import com.youa.mobile.R;
import com.youa.mobile.common.manager.ApplicationManager;

public class EmotionHelper{
	public static final String[] emoChars = new String[] {
		"[饭]",//1
		"[哈哈]",//2
		"[差劲]",//3
		"[吃惊]",//4
		"[臭美]",//5
		"[流泪]",//6
		"[便便]",//7
		"[开心]",//8
		"[调皮]",//9
		"[蓝色妖姬]",//10
		"[猥琐]",//11
		"[怒]",//12
		"[礼物]",//13
		"[钱包]",//14
		"[心碎]",//15
		"[心动]",//16
		"[思考]",//17
		"[可怜]",//18
		"[胜利]",//19
		"[可爱]",//20
		"[咖啡]",//21
		"[抱拳]",//22
		"[爽]",//23
		"[衰]",//24
		"[生日快乐]",//25
		"[喜欢]",//26
		"[相机]",//27
		"[委屈]",//28
		"[偷笑]",//29
		"[微笑]",//30
		"[问号]",//31
		"[太阳]",//32
		"[握手]",//33
		"[挖鼻屎]",//34
		"[嘻嘻]",//35
		"[no]",//36
		"[汗]",//37
		"[夜晚]",//38
		"[好]",//39
		"[凋谢]",//40
		"[寒]",//41
		"[冤枉啊]",//42
		"[红包]",//43
		"[ok]"//44
	};
	private static String[] EncodedEmoChars = null;
	private static final int[] emoImgs = new int[] { 
		R.drawable.input_face_fanfan,//1-5
	    R.drawable.input_face_haha,
	    R.drawable.input_face_chajin,
	    R.drawable.input_face_chijing,
	    R.drawable.input_face_choumei,
	    
	    R.drawable.input_face_liulei,//6-10
	    R.drawable.input_face_bianbian,
	    R.drawable.input_face_kaixin,
	    R.drawable.input_face_tiaopi,
	    R.drawable.input_face_lanseyaoji,

	    R.drawable.input_face_weisuo,//11-15
	    R.drawable.input_face_nu,
	    R.drawable.input_face_liwu,
	    R.drawable.input_face_qianbao,
	    R.drawable.input_face_xinsui,

	   
	    R.drawable.input_face_xindong,//16-20
	    R.drawable.input_face_sikao,
	    R.drawable.input_face_kelian,
	    R.drawable.input_face_shengli,
	    R.drawable.input_face_keai,

	    
	    R.drawable.input_face_kafei,//21-25
	    R.drawable.input_face_baoquan,
	    R.drawable.input_face_shuang,
	    R.drawable.input_face_shuai,
	    R.drawable.input_face_srkl,
	    
	    R.drawable.input_face_xihuan,//26-30
	    R.drawable.input_face_xiangji,
	    R.drawable.input_face_weiqu,
	    R.drawable.input_face_touxiao,
	    R.drawable.input_face_weixiao,
	    
	    
	    R.drawable.input_face_wenhao,//31-35
	    R.drawable.input_face_taiyang,
	    R.drawable.input_face_woshou,
	    R.drawable.input_face_wbs,
	    R.drawable.input_face_xixi,
	    
	    R.drawable.input_face_no,//36-40
	    R.drawable.input_face_han,
	    R.drawable.input_face_yewan,
	    R.drawable.input_face_hao,
	    R.drawable.input_face_diaoxie,
	    
	    R.drawable.input_face_hanleng,//41-45
	    R.drawable.input_face_yuanwang,
	    R.drawable.input_face_hongbao,
	    R.drawable.input_face_ok
		};

	private static ImageGetter mImageGetter;
	public static int getEmoCount() {
		return emoChars.length;
	}

	public EmotionHelper(Context appCtx) {
	}


	public static String getEmoChar(int arg0) {
		if (arg0 >= emoChars.length) {
			return null;
		}
		return emoChars[arg0];
	}

	public static int getEmoImg(int arg0) {
		if (arg0 >= emoImgs.length) {
			return 0;
		}
		return emoImgs[arg0];
	}

	public static int getEmoImgByChar(String str) {
		for(int i = 0; i < emoChars.length; i++) {
			if(emoChars[i].equals(str)){
				return emoImgs[i];
			}
		}
		return -1;
	}

	public static String genHTMLText(String msgText) {
		if(msgText == null) {
			return "";
		}
		msgText = filterHtml(msgText);
		if(EncodedEmoChars == null)
		{
			EncodedEmoChars = new String[emoChars.length];
			for(int i=0;i<emoChars.length;i++)
			{
				EncodedEmoChars[i] = filterHtml(emoChars[i]);
			}
		}
		if(EncodedEmoChars == null)
		{
			for (int i = 0; i < emoChars.length; i++) {
				msgText = msgText.replace(emoChars[i], "<img src=\"" + i + "\"/>");
			}
		}else
		{
			for (int i = 0; i < EncodedEmoChars.length; i++) {
				msgText = msgText.replace(EncodedEmoChars[i], "<img src=\"" + i + "\"/>");
			}
		}
		return msgText;
	}
	
	public static String genMessageText(String htmlText)
	{
		for (int i = 0; i < emoChars.length; i++) {
			htmlText = htmlText.replace("<img src=\"" + i + "\"/>", emoChars[i]);
		}
		return htmlText;
	}
    //private static final Pattern htmlPattern = Pattern.compile("<[\\sa-zA-Z/]+?>", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
    //private static final Pattern spacePattern = Pattern.compile("\\s{2,}", Pattern.MULTILINE);
    private static final String[] htmlEscape = {"&ensp;", "&emsp;", "&nbsp;", "&lt;", "&gt;", "&quot;"};
	private static final String[] htmlUnescape = {" ", " ", " ", "<", ">", "\""};
    protected static String filterHtml(String str) {
    	if (str == null || str.length() == 0)
    		return str;
    	
    	String nohtmlStr = str;//htmlPattern.matcher(str).replaceAll(" ");
    	    	
    	for (int i = 0; i < htmlEscape.length; i++){
    		nohtmlStr = nohtmlStr.replace(htmlUnescape[i], htmlEscape[i]);
    	}
    	
    	String onespaceStr = nohtmlStr;//spacePattern.matcher(nohtmlStr).replaceAll(" ");
    	return onespaceStr;
	}
    
    public static ImageGetter getImageGetter(
    		final Context context, final int size) {
//    	if(mImageGetter == null) {
//    		final Context appContext= context.getApplicationContext();
    		final float density = ApplicationManager.getInstance().getDensity();
    		ImageGetter mImageGetter = new ImageGetter() {//表情处理			
    			@Override
    			public Drawable getDrawable(String source) {    				
    				int rid = 0;
    				try
    				{
    					rid = EmotionHelper.getEmoImg(Integer.parseInt(source));
    				}catch(NumberFormatException e){e.printStackTrace();}
    				Drawable d = null;
    				try
    				{
    					d = context.getResources().getDrawable(rid);
    				}catch(NotFoundException e){e.printStackTrace();}
    				if(d!=null)
    				{
    					d.setBounds(0, 0, (int) (size*density), (int) (size*density));
    				}
//    				System.out.println("#################getDrawable:" + d);
    				return d;
    			}
    		};
//    	}
    	return mImageGetter;
    }
    
    public static Spanned parseToImageText(Context context, String text, int size) {
    	String htmlText = EmotionHelper.genHTMLText(text);
    	ImageGetter imageGetter = EmotionHelper.getImageGetter(context, size);
    	Spanned resultText = Html.fromHtml(htmlText, imageGetter, null);
    	return resultText;
    }
   
}
