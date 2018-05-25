package com.common.lib.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class SdcardUtils
{

	/**
	 * 检查是否存在SDCard
	 * 
	 * @return hasSDCard
	 */
	public static boolean checkSDCard()
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * 获取sdcard路径
	 * 
	 * @return
	 */
	public static String getSDCardPathWithFileSeparators()
	{
		return Environment.getExternalStorageDirectory().toString() + File.separator;
	}

	public static long getAvailableStore(String filePath)
	{
		StatFs statFs = new StatFs(filePath);
		long blocSize = statFs.getBlockSize();
		long availaBlock = statFs.getAvailableBlocks();
		long availableSpare = availaBlock * blocSize;
		return availableSpare;
	}
	
	/**
	 * 
	 * @return
	 */
	private static final long NOSPACE_THRESHOLD = 1000;
	public static boolean isSdcardHasSpareSpace(){
		if(checkSDCard()){
			long availableSize = getAvailableStore(getSDCardPathWithFileSeparators());
			if(availableSize<NOSPACE_THRESHOLD){
				return false;
			}else{
				return true;
			}
		}else{
			return false;
		}
	}
	
	

}
