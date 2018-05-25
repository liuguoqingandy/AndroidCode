package com.common.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class InputMethodUtil {
	public static void showInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			if (!imm.showSoftInput(view, 0)) {
				Logger.d("showInputMethod", "Failed to show soft input method.");
			}
		}
	}

	public static void hideInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static void hideInputMethod(Activity context) {
		View view = context.getCurrentFocus();
		hideInputMethod(view);
	}
}
