package com.rainnka.zhkunews.Callback_Listener;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by rainnka on 2016/5/20 23:18
 * Project name is ZHKUNews
 */
public abstract class onHActRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

	private GestureDetectorCompat gestureDetectorCompat;
	private RecyclerView recyclerView;

	public onHActRecyclerItemClickListener(RecyclerView recyclerView) {
		this.recyclerView = recyclerView;
		this.gestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), new
				ItemTouchHelperGestureListener());
	}

	public abstract void onItemClickListener(RecyclerView.ViewHolder viewHolder);

	public abstract void onItemLongClickListener(RecyclerView.ViewHolder viewHolder);

	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
		gestureDetectorCompat.onTouchEvent(e);
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {
		gestureDetectorCompat.onTouchEvent(e);
	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

	}

	public class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
			if (childView != null) {
				RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(childView);
				onItemClickListener(viewHolder);
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
			if (childView != null) {
				RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(childView);
				onItemLongClickListener(viewHolder);
			}
		}
	}
}
