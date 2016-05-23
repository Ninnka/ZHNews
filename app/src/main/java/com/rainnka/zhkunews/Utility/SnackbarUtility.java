package com.rainnka.zhkunews.Utility;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.rainnka.zhkunews.R;

/**
 * Created by rainnka on 2016/5/20 0:15
 * Project name is ZHKUNews
 */
public class SnackbarUtility {

	public static Snackbar getSnackbarLight(View view, String string, int time_length) {
		Snackbar snackbar = Snackbar.make(view, string, time_length);
		snackbar.getView().setBackgroundColor(Color.argb(220, 233, 233, 233));
		((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.GRAY);
		snackbar.show();
		return snackbar;
	}

	public static Snackbar getSnackbarDefault(View view, String string, int time_length) {
		Snackbar snackbar = Snackbar.make(view, string, time_length);
		return snackbar;
	}

}
