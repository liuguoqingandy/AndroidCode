package com.common.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;


/**
 * pixel util
 *
 */
public class DipPixUtil {

	/**
	 * 根据手机的分辨率从dp 的单位 转成为px(像素)
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从px(像素) 的单位 转成为dp
	 */
	public static int px2dip(Activity context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
/*		Log.d("lixm", "scale = " + scale);
		
		Display mDisplay = context.getWindowManager().getDefaultDisplay();
		int W = mDisplay.getWidth();
		int H = mDisplay.getHeight();
		Log.d("lixm", "Width = " + W);
		Log.d("lixm", "Height = " + H);*/
		
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */ 
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (pxValue / fontScale + 0.5f); 
    } 
   
    /**
     * 将sp值转换为px值，保证文字大小不变
     * 
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */ 
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f); 
    } 

	public static int getDpi(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi;
	}
	
	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static class DisplayRect {
		private int width;
		
		private int height;
		
		public DisplayRect(int width, int height){
			this.width = width;
			this.height = height;
		}
		
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			this.height = height;
		}
		
	}
	
	public static DisplayRect getWindowDisplay(Context context) {
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayRect rect = new DisplayRect(display.getWidth(), display.getHeight());
		return rect;
	}


}
