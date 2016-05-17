package com.rainnka.zhkunews;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by rainnka on 2016/5/13 11:01
 * Project name is ZHKUNews
 */
public class HomeActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

	public WeakReference<HomeActivity> homeActivityWeakReference;
	public HomeActivity homeActivity;
	public LayoutInflater layoutInflater;
	public List<HomeActivityRecyclerViewItemInfo> itemInfoList;

	public HomeActivityRecyclerViewAdapterCallback homeActivityRecyclerViewAdapterCallback;

//	public Boolean LOAD_STATUS_FIRST = true;

	public enum ITEM_TYPE {
		ITEM_DATE,
		ITEM_CONTENT
	}

	public int[] iteminfotype;

	public onRecyclerViewItemClickListener onRecyclerViewItemClickListener;

	public onRecyclerViewItemLongClickListener onRecyclerViewItemLongClickListener;

	public interface HomeActivityRecyclerViewAdapterCallback {
		void refreshOldNews();
	}

	public interface onRecyclerViewItemClickListener {
		void onItemClick(String token, HomeActivityRecyclerViewItemInfo
				homeActivityRecyclerViewItemInfo);
	}

	public interface onRecyclerViewItemLongClickListener {
		void onItemLongClick();
	}

	public void setHomeActivityRecyclerViewAdapterCallback
			(HomeActivityRecyclerViewAdapterCallback homeActivityRecyclerViewAdapterCallback) {
		this.homeActivityRecyclerViewAdapterCallback = homeActivityRecyclerViewAdapterCallback;
	}

	public void setOnRecyclerViewItemClickListener(HomeActivityRecyclerViewAdapter
														   .onRecyclerViewItemClickListener
														   onRecyclerViewItemClickListener) {
		this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
	}

	public void setOnRecyclerViewItemLongClickListener(HomeActivityRecyclerViewAdapter
															   .onRecyclerViewItemLongClickListener
															   onRecyclerViewItemLongClickListener) {
		this.onRecyclerViewItemLongClickListener = onRecyclerViewItemLongClickListener;
	}

	public HomeActivityRecyclerViewAdapter(HomeActivity homeActivity) {
		this.homeActivityWeakReference = new WeakReference<>(homeActivity);
		this.homeActivity = this.homeActivityWeakReference.get();
		layoutInflater = LayoutInflater.from(this.homeActivity);
		this.iteminfotype = this.homeActivity.getApplicationContext().getResources().getIntArray(R
				.array.recyclerview_iteminfo_type);
	}

	public void setItemInfoList(List<HomeActivityRecyclerViewItemInfo> itemInfoList) {
		this.itemInfoList = itemInfoList;
	}

	public void addItemIntoFirst(List<HomeActivityRecyclerViewItemInfo> newItemInfoList) {
		this.itemInfoList.addAll(0, newItemInfoList);
		notifyItemRangeInserted(0, newItemInfoList.size());
	}

	public void addItemIntoLast(List<HomeActivityRecyclerViewItemInfo> newitemInfoList) {
		this.itemInfoList.addAll(this.itemInfoList.size(), newitemInfoList);
		notifyItemRangeInserted(this.itemInfoList.size(), newitemInfoList.size());
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == ITEM_TYPE.ITEM_DATE.ordinal()) {
			return new RecyclerViewDateViewHolder(layoutInflater.inflate(R.layout
					.home_activity_content_recyclerview_item_date, parent, false));
		} else if (viewType == ITEM_TYPE.ITEM_CONTENT.ordinal()) {
			return new RecyclerViewContentViewHolder(layoutInflater.inflate(R.layout
					.home_activity_content_recyclerview_item_content, parent, false));
		}
		Log.i("ZRH","onCreateViewHolder return null");
		return null;
//		return new RecyclerViewContentViewHolder(layoutInflater.inflate(R.layout
//				.home_activity_content_recyclerview_item_content, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
		if (holder instanceof RecyclerViewContentViewHolder) {
			RecyclerViewContentViewHolder recyclerViewContentViewHolder =
					(RecyclerViewContentViewHolder) holder;
			/*
			* 添加点击事件
			* */
			recyclerViewContentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onRecyclerViewItemClickListener != null) {
						onRecyclerViewItemClickListener.onItemClick(itemInfoList.get(position)
								.token, itemInfoList.get(position));
					}
				}
			});
			Log.i("ZRH","test: "+1);
			HomeActivityRecyclerViewItemInfo homeActivityRecyclerViewItemInfo = itemInfoList.get
					(position);
			recyclerViewContentViewHolder.content_tv.setText(homeActivityRecyclerViewItemInfo
					.title);
			Glide.with(homeActivity)
					.load(homeActivityRecyclerViewItemInfo.pictureID)
					.placeholder(R.drawable.placeholder)
					.skipMemoryCache(true)
					.crossFade(500)
					.fitCenter()
					.into(recyclerViewContentViewHolder.picture_iv);
		} else if (holder instanceof RecyclerViewDateViewHolder) {
			RecyclerViewDateViewHolder recyclerViewDateViewHolder = (RecyclerViewDateViewHolder)
					holder;
			HomeActivityRecyclerViewItemInfo homeActivityRecyclerViewItemInfo = itemInfoList.get
					(position);
			recyclerViewDateViewHolder.date_tv.setText("xx月xx日 星期x");
		}

		if (position == itemInfoList.size() - 1) {
			if (homeActivityRecyclerViewAdapterCallback != null) {
				homeActivityRecyclerViewAdapterCallback.refreshOldNews();
			}
		}

		/*
		* 测试用
		* */
//		RecyclerViewContentViewHolder recyclerViewContentViewHolder =
//				(RecyclerViewContentViewHolder) holder;
//		HomeActivityRecyclerViewItemInfo homeActivityRecyclerViewItemInfo = itemInfoList.get
//				(position);
//		recyclerViewContentViewHolder.content_tv.setText(homeActivityRecyclerViewItemInfo
//				.title);
//		Glide.with(homeActivity)
//				.load(homeActivityRecyclerViewItemInfo.pictureID)
//				.placeholder(R.drawable.placeholder)
//				.skipMemoryCache(true)
//				.crossFade(500)
//				.fitCenter()
//				.into(recyclerViewContentViewHolder.picture_iv);

	}

	@Override
	public int getItemViewType(int position) {
		int return_type = -1;
		HomeActivityRecyclerViewItemInfo recyclerViewItemInf = itemInfoList.get(position);
		int item_type = recyclerViewItemInf.type;
		if (item_type == iteminfotype[0]) {
			return_type = ITEM_TYPE.ITEM_DATE.ordinal();
		}
		if (item_type == iteminfotype[1]) {
			return_type = ITEM_TYPE.ITEM_CONTENT.ordinal();
		}
		return return_type;
//		return super.getItemViewType(position);
	}

	@Override
	public int getItemCount() {
//		Log.i("ZRH",itemInfoList.size()+"");
//		return 0;
		return itemInfoList.size();
	}

	/*
	* 内部类--content的viewholder
	* */
	static class RecyclerViewContentViewHolder extends RecyclerView.ViewHolder {

		TextView content_tv;
		ImageView picture_iv;

		public RecyclerViewContentViewHolder(View itemView) {
			super(itemView);
			picture_iv = (ImageView) itemView.findViewById(R.id
					.home_activity_recyclerview_item_content_picture_iv);
			content_tv = (TextView) itemView.findViewById(R.id
					.home_activity_recyclerview_item_content_content_tv);
		}
	}

	/*
	* 内部类--date的viewholder
	* */
	public static class RecyclerViewDateViewHolder extends RecyclerView.ViewHolder {

		TextView date_tv;

		public RecyclerViewDateViewHolder(View itemView) {
			super(itemView);
			date_tv = (TextView) itemView.findViewById(R.id
					.home_activity_content_recyclerview_item_date_date_tv);
		}
	}
}
