package com.rainnka.zhkunews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by rainnka on 2016/5/12 21:55
 * Project name is ZHKUNews
 */
public class HomeActivityViewPagerAdapter extends PagerAdapter {

	List<View> viewList;
	List<ZhiHuNewsItemInfo> zhiHuNewsTopItemInfoList;
	public WeakReference<AppCompatActivity> appCompatActivityWeakReference;
	public AppCompatActivity appCompatActivity;

	public HomeActivityViewPagerAdapter(AppCompatActivity appCompatActivity, List<View> viewList) {
		this.appCompatActivityWeakReference = new WeakReference<>(appCompatActivity);
		this.appCompatActivity = this.appCompatActivityWeakReference.get();
		this.viewList = viewList;
	}

	public void setZhiHuNewsTopItemInfoList(List<ZhiHuNewsItemInfo> zhiHuNewsTopItemInfoList) {
		this.zhiHuNewsTopItemInfoList = zhiHuNewsTopItemInfoList;
	}

	public void updateViewImage() {
		for (int i = 0; i < zhiHuNewsTopItemInfoList.size() + 2; i++) {
			ImageView imageView = (ImageView) viewList.get(i).findViewById(R.id
					.homeActivity_Content_ViewPager_CustomItem_ImageView);
			if (i == 0) {
				Glide.with(appCompatActivity)
						.load(zhiHuNewsTopItemInfoList.get(zhiHuNewsTopItemInfoList.size() - 1).image)
						.into(imageView);
				Log.i("ZRH", "更新 i==0 图片");
			} else if (i == zhiHuNewsTopItemInfoList.size() + 1) {
				Glide.with(appCompatActivity)
						.load(zhiHuNewsTopItemInfoList.get(0).image)
						.into(imageView);
				Log.i("ZRH", "更新 i==homeActivity.zhiHuNewsTopItemInfoList.size() + 1 图片");
			} else {
				Glide.with(appCompatActivity)
						.load(zhiHuNewsTopItemInfoList.get(i - 1).image)
						.into(imageView);
				Log.i("ZRH", "更新图片");
			}
		}
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = viewList.get(position);

		/*
		* 加载图片
		* */
		//		ImageView imageView = (ImageView) view.findViewById(R.id
		//				.homeActivity_Content_ViewPager_CustomItem_ImageView);
		//		if (position == 0) {
		//			Glide.with(appCompatActivity)
		//					.load(zhiHuNewsTopItemInfoList.get(zhiHuNewsTopItemInfoList.size() - 1).image)
		//					.into(imageView);
		//			Log.i("ZRH", "加载i==0图片: " + zhiHuNewsTopItemInfoList.get(zhiHuNewsTopItemInfoList.size() - 1).image);
		//		} else if (position == zhiHuNewsTopItemInfoList.size() + 1) {
		//			Glide.with(appCompatActivity)
		//					.load(zhiHuNewsTopItemInfoList.get(0).image)
		//					.into(imageView);
		//			Log.i("ZRH", "加载i==homeActivity.zhiHuNewsTopItemInfoList.size() + 1 图片: " + zhiHuNewsTopItemInfoList.get(0).image);
		//		} else {
		//			Glide.with(appCompatActivity)
		//					.load(zhiHuNewsTopItemInfoList.get(position - 1).image)
		//					.into(imageView);
		//			Log.i("ZRH", "加载图片: " + zhiHuNewsTopItemInfoList.get(position - 1).image);
		//		}

		/*
		* 每个子view添加点击事件
		* */
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//				Snackbar.make(v, "test click", Snackbar.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setAction(HomeActivity.INTENT_TO_NEWS_KEY);
				Bundle bundle = new Bundle();
				bundle.putSerializable(HomeActivity.SER_KEY, zhiHuNewsTopItemInfoList.get
						(position - 1));
				intent.putExtras(bundle);
				appCompatActivity.startActivity(intent);
			}
		});

		/*
		* 重写子view的触控事件
		* */
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						return false;
				}
				return false;
			}
		});
		container.addView(viewList.get(position));
		return viewList.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position));
	}

}
