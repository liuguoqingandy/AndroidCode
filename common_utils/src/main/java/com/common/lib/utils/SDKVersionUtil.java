package com.common.lib.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

public class SDKVersionUtil {

	//private static final String TAG = Utility.class.getSimpleName();
	public static final int ECLAIR_MR1 = 7; 
	public static final int FROYO = 8;
	public static final int GINGERBREAD = 9;
	public static final int GINGERBREAD_MR1 = 10;
	public static final int HONEYCOMB = 11;
	public static final int HONEYCOMB_MR1 = 12;
	public static final int HONEYCOMB_MR2 = 13;
	public static final int ICE_CREAM_SANDWICH = 14;
	public static final int ICE_CREAM_SANDWICH_MR1 = 15;
	public static final int KITKAT = Build.VERSION_CODES.KITKAT;
	public static final int JELLY_BEAN = 16;
	public static final int Android_N = 24;

	/**
	 * SdkVersion7 - 2.1
	 * @return
	 */
    public static boolean hasECLAIR_MR1() {
        return Build.VERSION.SDK_INT >= ECLAIR_MR1;
    }
	
    /**
	 * SdkVersion8 - 2.2
	 * @return
	 */
    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= FROYO;
    }

    /**
	 * SdkVersion9 - 2.3
	 * @return
	 */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= GINGERBREAD;
    }
    
    /**
	 * SdkVersion11 - 3.0
	 * @return
	 */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= HONEYCOMB;
    }
    
    /**
	 * SdkVersion12 - 3.1
	 * @return
	 */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= HONEYCOMB_MR1;
    }
    
    /**
	 * SdkVersion13 - 3.2
	 * @return
	 */ 
    public static boolean hasHoneycombMR2() {
        return Build.VERSION.SDK_INT >= HONEYCOMB_MR2;
    }
    
    /**
	 * SdkVersion14 - 4.0
	 * @return
	 */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH;
    }
    
    /**
	 * SdkVersion16 - 4.1
	 * @return
	 */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= JELLY_BEAN;
    }

    /**
     * SdkVersion19 - 4.4
     * @return
     */
    public static boolean hasKITKAT() {
        return Build.VERSION.SDK_INT >= KITKAT;
    }


    /**
     * SdkVersion24 - 7.0
     * @return
     */
    public static boolean hasN() {
        return Build.VERSION.SDK_INT >= Android_N;
    }
    
    /**
     *  judge is or not  tablet
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    
    /**
     *  is 3.0 later os tablet
     * @param context
     * @return
     */
    public static boolean isHoneycombTablet(Context context) {
        return hasHoneycomb() && isTablet(context);
    }

}
