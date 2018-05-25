package com.common.lib.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {

    private static Toast toast = null;

    private static Handler mHandler = null;

    public static int mDuration = Toast.LENGTH_LONG;

    public static String mMsg = null;

    private static void initToast(Context mContext) {
        if (mContext == null)
            return;
        final Context context = mContext.getApplicationContext();
        if (toast == null) {
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (context != null) {
                        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private static void show(final int duration, final String msg) {
        mDuration = duration;
        mMsg = msg;
        mHandler.removeCallbacks(showRunnable);
        mHandler.postDelayed(showRunnable, 100);
    }

    public static Runnable showRunnable = new Runnable() {

        @Override
        public void run() {
            if (toast != null) {
                toast.setDuration(mDuration);
                toast.setText(mMsg);
                toast.show();
            }
        }
    };

    public static void showShortToast(Context context, int resID) {
        initToast(context);
        show(Toast.LENGTH_SHORT, context.getString(resID));
    }

    public static void showShortToast(Context context, String msg) {
        initToast(context);
        show(Toast.LENGTH_SHORT, msg);
    }

    public static void showLongToast(Context context, int resID) {
        initToast(context);
        show(Toast.LENGTH_LONG, context.getString(resID));
    }

    public static void showLongToast(Context context, String msg) {
        initToast(context);
        show(Toast.LENGTH_LONG, msg);
    }

//	private static void toastShow(final String content) {
//		initToast(context);
//		show(Toast.LENGTH_LONG, content);
//	}
//
//	public static void toast(String content) {
//		toastShow(content);
//	}
//
//	public static void toast(int resId) {
//		toastShow(FinApp.getInstance().getString(resId));
//	}

}
