package com.youa.mobile.common.http.netsynchronized;

public abstract class DataUDObject {
	long mFileSize;
	long processed;
	String tag;
	boolean canceled = false;
	public String getTag() {
		return tag;
	}
	public int getProgress() {
		if(mFileSize>0)
			return (int) (processed*100/mFileSize);
		return 0;
	}
	public String getRemain() {
		float remain = mFileSize - processed;
		String unit = "B";
		if(remain>102.4f)
		{
			remain /= 1024f;
			unit = "KB";
			if(remain>102.4f)
			{
				remain /= 1024f;
				unit = "MB";				
			}
		}
		float remainInt = (int)(remain *10)/10f;
		return remainInt+" "+unit;
	}
	public void cancel() {
		canceled = true;
	}

}
