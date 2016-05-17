package com.rainnka.zhkunews;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by rainnka on 2016/3/3 10:36
 * Project name is ZHKUNews
 */
public class LengthTransitionUtility {

	/*
	* 获取statusBar的高度
	* */
	public static int getStatusBarHeight(AppCompatActivity appCompatActivity) {
		int resId = appCompatActivity.getApplicationContext().getResources().getIdentifier
				("status_bar_height", "dimen",
						"android");
		if (resId > 0) {
			return appCompatActivity.getApplicationContext().getResources().getDimensionPixelSize
					(resId);
		}
		return 0;
	}

	/*
	* 使ListView适应ScrollView
	* */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/*
	* 将px值转换为dip或dp值，保证尺寸大小不变
	*
	* @param pxValue
	* @param scale（DisplayMetrics类中属性density）
	* @return
	* */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/*
	* 将dip值转换为px值，保证尺寸大小不变
	*
	* @param dipValue
	* @param scale（DisplayMetrics类中属性density）
	* @return
	* */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/*
	* 将px值转换为sp值，保证字体大小不变
	*
	* @param pxValue
	* @param scale（DisplayMetrics类中属性density）
	* @return
	* */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/*
	* 将sp值转换为px值， 保证字体大小不变
	*
	* @param spValue
	* @param scale（DisplayMetrics类中属性density）
	* @return
	* */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
}
