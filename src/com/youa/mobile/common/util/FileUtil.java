package com.youa.mobile.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.net.Uri;

public class FileUtil {
	public static void outputToFile(
			Context context,
			Uri fileUri,
			File filename) throws IOException{
			InputStream is = context.getContentResolver().openInputStream(fileUri);
			OutputStream os = new FileOutputStream(filename);
			byte[] buffer = new byte[1024*256];
			while(true)
			{
				int count = is.read(buffer);
				if(count<0)
					break;
				os.write(buffer, 0, count);
			}
			os.close();
			is.close();
	}
	
	public static long getSize(File pathFile) {
		long size = 0;
		if(pathFile.exists()) {
			if(pathFile.isDirectory()) {
				File[] fileList = pathFile.listFiles();
				for(File file:fileList) {
					size += getSize(file);
				}
				
			} else {
				size += pathFile.length();
			}
		}
		return size;
	}
	
	public static String getSizeText(long size) {
		int b = (int)(size % 1024);
		int k = (int)(size / 1024);
		int m = (int)(size / (1024*1024));
		
		StringBuffer textBuf = new StringBuffer();
		if (m > 0) {
			textBuf.append(m);
			k = k/100;
			if(k > 10) {//最大到.9
				k = 9;
			}
			if(k>0) {
				textBuf.append(".").append(k);
			}
			textBuf.append("M");
		} else if(k > 0) {
			textBuf.append(k);
			b = b/100;
			if(b > 10) {//最大到.9
				b = 9;
			}
			if(b>0) {
				textBuf.append(".").append(b);
			}
			textBuf.append("K");
		} else {
			textBuf.append(b).append("Byte");;
		}
		return textBuf.toString();
	}
}
