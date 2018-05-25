package com.common.lib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Vic on 16/9/9.
 */
public class NetworkUtil {
    public NetworkUtil() {
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if(info != null) {
            for(int i = 0; i < info.length; ++i) {
                if(info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        if(context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if(context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(1);
            if(mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    public static boolean isMobileConnected(Context context) {
        if(context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(0);
            if(mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    public static int getConnectedType(Context context) {
        if(context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }

        return -1;
    }

    public static NetworkUtil.netType getAPNType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo == null) {
            return NetworkUtil.netType.noneNet;
        } else {
            int nType = networkInfo.getType();
            return nType == 0?(networkInfo.getExtraInfo().toLowerCase().equals("cmnet")?NetworkUtil.netType.CMNET:NetworkUtil.netType.CMWAP):(nType == 1?NetworkUtil.netType.wifi:NetworkUtil.netType.noneNet);
        }
    }

    public static int getNetWorkType(Context context) {
        if(context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                switch(mNetworkInfo.getType()) {
                    case 0:
                        return 0;
                    case 1:
                        return 1;
                    default:
                        return 100;
                }
            }
        }

        return 100;
    }

    public static enum netType {
        wifi,
        CMNET,
        CMWAP,
        noneNet;

        private netType() {
        }
    }
}
