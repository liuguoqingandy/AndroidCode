package com.common.lib.utils;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Vic on 16/8/11.
 */
public class DisplayRect {

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

    public static DisplayRect getDiaplayRect(Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayRect rect = new DisplayRect(display.getWidth(), display.getHeight());
        return rect;
    }

}
