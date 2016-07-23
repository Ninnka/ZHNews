package com.rainnka.ZHNews.Callback_Listener;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by rainnka on 2016/5/21 17:31
 * Project name is ZHKUNews
 */
public abstract class onSHPActRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

	private RecyclerView recyclerView;
	private GestureDetectorCompat gestureDetectorCompat;

	public onSHPActRecyclerItemClickListener(RecyclerView recyclerView) {
		this.recyclerView = recyclerView;
		gestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext().getApplicationContext(), new
				ItemTouchHelperGestureListener());
	}

	public abstract void onItemClick(RecyclerView.ViewHolder viewHolder);

	public abstract void onItemLongClick(RecyclerView.ViewHolder viewHolder);

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
				onItemClick(viewHolder);
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
			if (childView != null) {
				RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(childView);
				onItemLongClick(viewHolder);
			}
		}
	}
}
