package com.youa.mobile.common.http.netsynchronized;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.youa.mobile.DebugMode;
import com.youa.mobile.R;
import com.youa.mobile.common.exception.MessageException;
import com.youa.mobile.common.http.HttpManager;
import com.youa.mobile.common.http.HttpRes;
import com.youa.mobile.common.util.picture.ImageUtil;
import com.youa.mobile.parser.JsonObject;

public class FileUploader extends DataUploader {
	private final String TAG = "PicUploader";
	private boolean IS_DEBUG = DebugMode.debug;
//	private File mFile;
	private String mFileName;
	private boolean mIsNeedCrop;
	private String mTargetFileName;
	public FileUploader(String fileName, boolean isNeedCrop) throws FileNotFoundException {
//		mFile = new File(fileName);
		mFileName = fileName;
		mIsNeedCrop = isNeedCrop;
//		this.mFileSize = mFile.length();
	}

//	UploadInputStream input = null;
	@Override
	protected void onUpload(FileUploadListener uploadListener, Context context) {
		try {
			if(canceled) {
				if(uploadListener != null) {
					uploadListener.onFailed(FileUploadListener.FAIL_CANCEL);
				}
				Log.i(TAG, "canceled");
				return;
			} else {
				if(IS_DEBUG) {
					Log.d(TAG, "start updateLoad");
				}
				uploaderCache.put(tag, this);
				String imageid = uploadFile(context, mFileName, mIsNeedCrop);
//				try{
//					input.close();
//				} catch (IOException e) {
//					e.printStackTrace();			
//				}
				uploaderCache.remove(tag);
				((FileUploadListener)uploadListener).onFinished(imageid);
				if(IS_DEBUG) {
					Log.d(TAG, "end updateLoad");
				}
			}			
	        
			Log.i(TAG, "upload finished");
		} catch (Exception e) {
			e.printStackTrace();
			if(uploadListener != null) {
				uploadListener.onFailed(FileUploadListener.FAIL_ERROR);
			}
		} 
	}
	public String getTargetPath(){
		return mTargetFileName;
	}
	 /* 上传文件至Server的方法 */
    private String uploadFile(Context context, String fileName, boolean isNeedcrop) throws MessageException
    {
    	File upLoadFile;
    	try {
//    		File oldFile = new File(fileName);
//    		Log.d(TAG, "new-fileName:" + fileName);
//    		Log.d(TAG, "old-filesize:" + oldFile.length());
			fileName = ImageUtil.decodeToFile(fileName, 600, 1024);//600 最大宽度，1024最大高度
			mTargetFileName=fileName;
			upLoadFile  = new File(fileName);
//			Log.d(TAG, "new-fileName:" + fileName);
//    		Log.d(TAG, "new-filesize:" + newFile.length());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new MessageException("", R.string.common_error_filenotfound);
		}
    	Map<String, File> fileparamMap = new HashMap<String, File>();//"/sdcard/LeHuoStore/313d852fcb2a2734c9569b15--50-50-1"
    	fileparamMap.put("imgblob", upLoadFile);    	
    	Map<String, String> paramMap = new HashMap<String, String>();
    	if(isNeedcrop) {
    		paramMap.put("needcrop", "1");
    	}
    	HttpManager httpManager = new HttpManager();
		HttpRes result = httpManager.postFile("_upimg", paramMap, fileparamMap, context);
		JsonObject jsonObject = result.getJSONObject().getJsonObject("data");
		String imageId = jsonObject.getString("rpcret");
    	return imageId;
    }
    
    private String fileToString(String filename) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder builder = new StringBuilder();
        String line;    

        // For every line in the file, append it to the string builder
        while((line = reader.readLine()) != null)
        {
            builder.append(line);
        }

        return builder.toString();
    }


}
