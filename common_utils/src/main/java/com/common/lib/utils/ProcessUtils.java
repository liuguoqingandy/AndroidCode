package com.common.lib.utils;

import android.app.ActivityManager;
import android.content.Context;

public class ProcessUtils {
	public static boolean isSameProcess(Context context, String processName) {
		try {
			int pid = android.os.Process.myPid();
			ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
				if (appProcess.pid == pid) {
					if (appProcess.processName.equalsIgnoreCase(processName)) {
						return true;
					}
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
