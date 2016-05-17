package com.rainnka.zhkunews;

import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by rainnka on 2016/5/12 21:55
 * Project name is ZHKUNews
 */
public class HomeActivityViewPagerAdapter extends PagerAdapter {

	List<View> viewList;

	public HomeActivityViewPagerAdapter(List<View> viewList) {
		this.viewList = viewList;
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
	public Object instantiateItem(ViewGroup container, int position) {
		View view = viewList.get(position);
		/*
		* 每个子view添加点击事件
		* */
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Snackbar.make(v,"test click",Snackbar.LENGTH_SHORT).show();
			}
		});
		/*
		* 重写子view的触控事件
		* */
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
//					case MotionEvent.ACTION_DOWN:
//						Snackbar.make(v,"test click",Snackbar.LENGTH_SHORT).show();
//						return false;
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
