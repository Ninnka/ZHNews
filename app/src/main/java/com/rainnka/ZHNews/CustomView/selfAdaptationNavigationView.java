package com.rainnka.ZHNews.CustomView;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;

import com.rainnka.ZHNews.Utility.ScreenToolUtility;

/**
 * Created by rainnka on 2016/7/31 14:47
 * Project name is ZHKUNews
 */
public class selfAdaptationNavigationView extends NavigationView {

	public selfAdaptationNavigationView(Context context) {
		super(context);
	}

	public selfAdaptationNavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public selfAdaptationNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec - ScreenToolUtility.getCurrentNavigationBarSize
				(getContext()).y);
	}
}
