package com.rainnka.zhkunews;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rainnka on 2016/5/17 14:11
 * Project name is ZHKUNews
 */
public class BaseActivity extends AppCompatActivity {

	private View statusBarInstead;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT <= 19 && Build.VERSION.SDK_INT >= 14) {
			ViewGroup firstChildView = (ViewGroup) ((ViewGroup) getWindow().getDecorView()).getChildAt(0);
			statusBarInstead = new View(this);

			/*
			* 创建statusBarInstead用的布局参数
			* */
			ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup
					.LayoutParams.MATCH_PARENT, getStatusBarHeight());

			/*
			* 默认颜色透明
			* 可以自定义
			* */
			statusBarInstead.setBackgroundColor(Color.BLUE);

			/*
			* 在第一个位置填充上statusBarInstead
			* */
			firstChildView.addView(statusBarInstead, 0, layoutParams);

		}
	}

	private int getStatusBarHeight() {
		int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resId > 0) {
			return getResources().getDimensionPixelSize(resId);
		}
		return 0;
	}

	protected void setStatusBarInsteadColor(int colorResourceID) {
		statusBarInstead.setBackgroundResource(colorResourceID);
	}
}
