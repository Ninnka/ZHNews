package com.rainnka.zhkunews;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by hennzr on 2016/5/4 16:02
 * Project name is Other
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	public WeakReference<AppCompatActivity> navigationViewActivityWeakReference;
	public AppCompatActivity navigationViewActivity;
	public List<String> dataList;
	protected LayoutInflater layoutInflater;

	public RecyclerViewAdapter(AppCompatActivity navigationViewActivity) {
		navigationViewActivityWeakReference = new WeakReference<>(navigationViewActivity);
		this.navigationViewActivity = navigationViewActivityWeakReference.get();
		layoutInflater = LayoutInflater.from(this.navigationViewActivity);
	}

	public void setDataList(List<String> dataList) {
		this.dataList = dataList;
	}

	public void addDataToFirst(List<String> expandaList) {
		this.dataList.addAll(0, expandaList);
		notifyItemRangeInserted(0, expandaList.size());
	}

	public void addDataToLast(List<String> expandaList) {
		this.dataList.addAll(this.dataList.size(), expandaList);
		notifyItemRangeInserted(this.dataList.size(), expandaList.size());
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new RecyclerViewHolder(layoutInflater.inflate(R.layout.recyclerviewcustomitem,
				parent, false));
//		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof RecyclerViewHolder) {
			((RecyclerViewHolder) holder).textView.setText(dataList.get(position));
		}
	}

	@Override
	public int getItemCount() {
		Log.i("ZRH",""+dataList.size());
//		return 0;
		return dataList.size();
	}


	public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements
			View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

		public TextView textView;

		public RecyclerViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.recycler_custom_tv);
			itemView.setOnCreateContextMenuListener(this);

		}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
			menu.add(0, 1, Menu.NONE, "1").setOnMenuItemClickListener(this);
			menu.add(0, 1, Menu.NONE, "2").setOnMenuItemClickListener(this);

		}

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			Snackbar.make(itemView, "Id: " + "  Position: ", Snackbar.LENGTH_SHORT).show();
			return true;
		}
	}

}
