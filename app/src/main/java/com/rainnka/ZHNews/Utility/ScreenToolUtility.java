package com.rainnka.ZHNews.Utility;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by rainnka on 2016/7/31 14:48
 * Project name is ZHKUNews
 */
public class ScreenToolUtility {

	/*
	* 获取 statusBar 的高度
	* */
	public static int getStatusBarHeightValue(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result; // in px
	}

	/*
	* 获取navigationBar的高度
	* */
	public static int getNavigationBarHeightValue(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result; // in px
	}

	/*
	* 获取当前那navigationBar的可见大小
	* */
	public static Point getCurrentNavigationBarSize(Context context) {
		Point appUsableSize = getAppUsableScreenSize(context);
		Point realScreenSize = getRealScreenSize(context);

		// navigation bar on the right
//		if (appUsableSize.x < realScreenSize.x) {
//			return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
//		}

		// navigation bar at the bottom
		if (appUsableSize.y < realScreenSize.y) {
			return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
		}

		// navigation bar is not present
		return new Point();
	}

	/*
	* 获取屏幕真实大小
	* */
	private static Point getRealScreenSize(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	/*
	* 获取App中不可用部分的屏幕大小
	* */
	private static Point getAppUsableScreenSize(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();

		if (Build.VERSION.SDK_INT >= 17) {
			display.getRealSize(size);
		} else if (Build.VERSION.SDK_INT >= 14) {
			try {
				size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
				size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
			} catch (Exception e) {
				size = new Point(0, 0);
			}
		}
		return size;
	}

}
