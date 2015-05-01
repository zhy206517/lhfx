package com.youa.mobile.common.util.pinyin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.youa.mobile.R;

public class Pinyin {
	static String pinyin[] = null;
	static char indexes[] = null;
	static Map<Integer, String> xing_map = null;
//	private static boolean initialed = false;
	public static void init(Context appCtx) throws IOException{
		if(pinyin != null
				&& indexes != null
				&& xing_map != null)
			return;
		InputStream in = appCtx.getResources().openRawResource(R.raw.pinyin_table);
//		InputStream in = null;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String s;
		s = br.readLine();
		pinyin = s.split(" ");
		s = br.readLine();
		int length = Integer.parseInt(s);
		indexes = new char[length];
		br.read(indexes, 0, length);
		br.close();
		char xing[] = { '单', '查', '朴', '尉', '仇', '重', '秘', '区', '折', '冼', '解' };
		String xing_pinyin[] = { "shan", "zha", "piao", "yu", "qiu", "chong", "bi",
				"ou", "she", "xian", "xie" };
		xing_map = new HashMap<Integer, String>();
		for (int i = 0; i < xing.length; i++){
			xing_map.put((int)xing[i], xing_pinyin[i]);
		}
//		initialed = true;
	}
	
	/**
	 * if c is chinese, return it's pinyin, else return ""
	 * @param c
	 * @return if c is chinese, return it's pinyin, else return ""
	 */
	static String getCN(char c){
		if (c == 0x3007){
			return "ling";
		}
		
		c -= 0x4E00;
		if (c >= 0 && c < indexes.length){
			return pinyin[indexes[c]];
		}else{
			return "";
		}
	}
	
	/**
	 * String s to chinese pinyin string. english keep the same
	 * @param s
	 * @return
	 */
	public static String getCN(String s){
		s = s.trim();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			String cn = null;
			if (i == 0){
				cn = xing_map.get((int)c);
			}
			if (cn == null){
				cn = getCN(c);
			}
			if (cn.length() == 0) {
				sb.append(c);
			} else {
				sb.append(cn);
			}
		}
		return sb.toString();
	}
	
	/**
	 * String s to chinese pinyin first-char string. english keep the same
	 * @param s
	 * @return
	 */
	public static String getShortCN(String s){
		s = s.trim();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			String cn = null;
			if (i == 0){
				cn = xing_map.get((int)c);
			}
			
			if (cn == null){
				cn = getCN(c);
			}
			
			if (cn.length() == 0) {
				sb.append(c);
			} else {
				sb.append(cn.charAt(0));
			}
		}
		return sb.toString();
	}
	
	public static void release(){
		pinyin = null;
		indexes = null;
//		initialed = false;
	}
}
