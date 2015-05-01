package com.youa.mobile.common.exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

import com.youa.mobile.common.manager.SavePathManager;

public class RecordException {
	public static String getLastException(Context appCtx)
	{
		File fileLog = SavePathManager.getLogFile(appCtx);
		if(fileLog.exists() && fileLog.length() > 0)
		{
			try {
				FileInputStream fehlogI = new FileInputStream(fileLog);
				byte[] buffer = new byte[(int) fileLog.length()];
				fehlogI.read(buffer);
				String log = new String(buffer);
				fileLog.delete();
				return log;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static void setLastException(Context appCtx, String exception)
	{
		try {
			File feihong_log = SavePathManager.getLogFile(appCtx);
			FileOutputStream fehlogO = new FileOutputStream(feihong_log);
			fehlogO.write(exception.getBytes());
			fehlogO.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
