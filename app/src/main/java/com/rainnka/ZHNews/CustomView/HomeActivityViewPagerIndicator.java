package com.rainnka.ZHNews.CustomView;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.LengthTransitionUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainnka on 2016/5/13 13:28
 * Project name is ZHKUNews
 */
public class HomeActivityViewPagerIndicator extends LinearLayout {

	public List<View> viewList;
	public int VirtualSize;
	public TextView textView;

	public HomeActivityViewPagerIndicator(Context context) {
		super(context);
	}

	public HomeActivityViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HomeActivityViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setRecyclerViewIndicatorAttribute(int bannerSize, AppCompatActivity homeActivity) {
		VirtualSize = bannerSize + 2;
		viewList = new ArrayList<>();
		LinearLayout.LayoutParams layoutParamsNormal = new LinearLayout.LayoutParams
				(LengthTransitionUtility.dip2px(homeActivity, 16), ViewGroup
						.LayoutParams.MATCH_PARENT);
		layoutParamsNormal.setMargins(0, 0, LengthTransitionUtility.dip2px(homeActivity, 8), 0);
		LinearLayout.LayoutParams layoutParamsEnd = new LinearLayout.LayoutParams
				(LengthTransitionUtility.dip2px(homeActivity, 16), ViewGroup
						.LayoutParams.MATCH_PARENT);
		layoutParamsEnd.setMargins(0, 0, 0, 0);
		for (int i = 0; i < bannerSize; i++) {
			textView = new TextView(homeActivity);
			textView.setGravity(Gravity.CENTER);
			if (i != bannerSize - 1) {
				textView.setLayoutParams(layoutParamsNormal);
			} else if (bannerSize == 1) {
				textView.setLayoutParams(layoutParamsEnd);
			} else {
				textView.setLayoutParams(layoutParamsEnd);
			}
			addView(textView);
			viewList.add(textView);
		}
	}

	public void setColorForStart() {
		//		if (viewList.get(0) != null) {
		//			viewList.get(0).setBackgroundColor(Color.WHITE);
		//			viewList.get(1).setBackgroundColor(Color.GRAY);
		//			viewList.get(2).setBackgroundColor(Color.GRAY);
		//		}
		for (int i = 0; i < viewList.size(); i++) {
			if (i == 0) {
				viewList.get(i).setBackgroundResource(R.color.homeActivityBannerIndicatorSelected);
			} else {
				viewList.get(i).setBackgroundResource(R.color.homeActivityBannerIndicatorUnSelected);
			}
		}
	}

	public void changeColorForStatus(int position) {
		if (position == 0) {
			viewList.get(viewList.size() - 1).setBackgroundResource(R.color.homeActivityBannerIndicatorSelected);
			viewList.get(0).setBackgroundResource(R.color.homeActivityBannerIndicatorUnSelected);
		} else if (position == VirtualSize - 1) {
			viewList.get(0).setBackgroundResource(R.color.homeActivityBannerIndicatorSelected);
			viewList.get(viewList.size() - 1).setBackgroundResource(R.color.homeActivityBannerIndicatorUnSelected);
		} else {
			if (position == 1) {
				viewList.get(0).setBackgroundResource(R.color.homeActivityBannerIndicatorSelected);
				viewList.get(1).setBackgroundResource(R.color.homeActivityBannerIndicatorUnSelected);
				viewList.get(viewList.size() - 1).setBackgroundResource(R.color.homeActivityBannerIndicatorUnSelected);
			} else if (position == VirtualSize - 2) {
				viewList.get(viewList.size() - 1).setBackgroundResource(R.color.homeActivityBannerIndicatorSelected);
				viewList.get(viewList.size() - 2).setBackgroundResource(R.color.homeActivityBannerIndicatorUnSelected);
				viewList.get(0).setBackgroundResource(R.color.homeActivityBannerIndicatorUnSelected);
			} else {
				viewList.get(position - 1).setBackgroundResource(R.color.homeActivityBannerIndicatorSelected);
				viewList.get(position - 2).setBackgroundResource(R.color.homeActivityBannerIndicatorUnSelected);
				viewList.get(position).setBackgroundResource(R.color.homeActivityBannerIndicatorUnSelected);
			}
		}
	}
}
