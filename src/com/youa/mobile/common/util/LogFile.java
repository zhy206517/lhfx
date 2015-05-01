package com.youa.mobile.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

public class LogFile {

	private static LogFile logFile = null;
	private static String CACHE_PATH = null;
	private static String SERVER_PUSH_LOG = "server_push_log"; 
	ArrayList<String> list = new ArrayList<String>();
	
	private LogFile(){
	
	}
	
	public static LogFile getInstance(Context context){
		if(logFile == null){
			logFile = new LogFile();
			CACHE_PATH = context.getCacheDir().getPath();
		}
		return logFile;
	}
	
	public synchronized void writeOneLine(String strLine){
		
//		readArrayList();
//		
//		if(list.size()>=100){
//			list.remove(0);
//			list.add(strLine);
//		}else{
//			list.add(strLine);
//		}
//		
//		writeArrayList(list);
	}
	
	public synchronized String readWholeFile(){
		
//		StringBuffer strBuffer = new StringBuffer();
//		
//		try {
//			
//			FileReader fr = new FileReader(new File(CACHE_PATH+File.separator+SERVER_PUSH_LOG));
//			BufferedReader br = new BufferedReader(fr);
//			
//			String strLine = null;
//			while((strLine=br.readLine())!=null){
//				strBuffer.append(strLine+"\n");
//			}
//			br.close();
//			fr.close();
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e){
//			
//		}
//		
//		return strBuffer.toString();
		return "";
	}
	
	private synchronized ArrayList<String> readArrayList(){
		list.clear();
		
		try {
			
			FileReader fr = new FileReader(new File(CACHE_PATH+File.separator+SERVER_PUSH_LOG));
			BufferedReader br = new BufferedReader(fr);
			
			String strLine = null;
			while((strLine=br.readLine())!=null){
				list.add(strLine);
			}
			br.close();
			fr.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){
			
		}
		
		return list;
	}
	
	private synchronized void writeArrayList(ArrayList<String> list){
		try{
			FileWriter fw = new FileWriter(new File(CACHE_PATH+File.separator+SERVER_PUSH_LOG), false);
			
			for(int i=0; i<list.size(); i++){
				fw.write(list.get(i)+"\n");
			}
			fw.close();
			
		}catch( Exception e){
			
		}
	}
}
