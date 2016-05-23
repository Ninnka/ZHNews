package com.rainnka.zhkunews.Callback_Listener;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.rainnka.zhkunews.Adapter.ItemTouchHelperAdapter;

/**
 * Created by rainnka on 2016/5/21 17:08
 * Project name is ZHKUNews
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

	private ItemTouchHelperAdapter itemTouchHelperAdapter;

	public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter itemTouchHelperAdapter) {
		this.itemTouchHelperAdapter = itemTouchHelperAdapter;
	}

	@Override
	public boolean isLongPressDragEnabled() {
		return false;
	}

	@Override
	public boolean isItemViewSwipeEnabled() {
		return false;
	}

	@Override
	public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		int dragFlags = 0;
		int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
		return makeMovementFlags(dragFlags, swipeFlags);
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return false;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		if (itemTouchHelperAdapter != null) {
			itemTouchHelperAdapter.onItemDismiss(viewHolder.getAdapterPosition());
		}
	}

}
