package com.rainnka.zhkunews;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by rainnka on 2016/5/13 11:01
 * Project name is ZHKUNews
 */
public class HomeActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	public WeakReference<HomeActivity> homeActivityWeakReference;
	public HomeActivity homeActivity;
	public LayoutInflater layoutInflater;
	public List<ZhiHuNewsItemInfo> zhiHuNewsItemInfoList;

	public HomeActivityRecyclerViewAdapterCallback homeActivityRecyclerViewAdapterCallback;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
	String cd;

	//	public Boolean LOAD_STATUS_FIRST = true;

	public enum ITEM_TYPE {
		ITEM_DATE,
		ITEM_CONTENT
	}

	//	public int[] iteminfotype;

	public onRecyclerViewItemClickListener onRecyclerViewItemClickListener;

	public onRecyclerViewItemLongClickListener onRecyclerViewItemLongClickListener;

	public interface HomeActivityRecyclerViewAdapterCallback {
		void refreshOldNews();
	}

	public interface onRecyclerViewItemClickListener {
		void onItemClick(ZhiHuNewsItemInfo zhiHuNewsItemInfo);
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
		cd = simpleDateFormat.format(new Date());
		//		this.iteminfotype = this.homeActivity.getApplicationContext().getResources().getIntArray(R
		//				.array.recyclerview_iteminfo_type);
		//		Log.i("ZRH",this.iteminfotype[0]+"\n"+this.iteminfotype[1]);
	}

	public void setZhiHuNewsItemInfoList(List<ZhiHuNewsItemInfo> zhiHuNewsItemInfoList) {
		this.zhiHuNewsItemInfoList = zhiHuNewsItemInfoList;
	}

	public void addItemIntoFirst(List<ZhiHuNewsItemInfo> newItemInfoList) {
		this.zhiHuNewsItemInfoList.addAll(1, newItemInfoList);
		notifyItemRangeInserted(1, newItemInfoList.size());
	}

	public void addItemIntoLast(List<ZhiHuNewsItemInfo> newitemInfoList) {
		this.zhiHuNewsItemInfoList.addAll(this.zhiHuNewsItemInfoList.size(), newitemInfoList);
		notifyItemRangeInserted(this.zhiHuNewsItemInfoList.size(), newitemInfoList.size());
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == 0) {
			return new RecyclerViewDateViewHolder(layoutInflater.inflate(R.layout
					.home_activity_content_recyclerview_item_date, parent, false));
		} else if (viewType == 1) {
			return new RecyclerViewContentViewHolder(layoutInflater.inflate(R.layout
					.home_activity_content_recyclerview_item_content, parent, false));
		}
		//		Log.i("ZRH", "onCreateViewHolder return null");
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
						onRecyclerViewItemClickListener.onItemClick(zhiHuNewsItemInfoList.get(position));
					}
				}
			});
			//			Log.i("ZRH", "test: " + 1);
			ZhiHuNewsItemInfo zhiHuNewsItemInfo = zhiHuNewsItemInfoList.get(position);
			//			Log.i("ZRH", "zhiHuNewsItemInfo.title: " + zhiHuNewsItemInfo.title);
			recyclerViewContentViewHolder.content_tv.setText(zhiHuNewsItemInfo.title);
			Glide.with(homeActivity)
					.load(zhiHuNewsItemInfo.images.get(0))
					.placeholder(R.drawable.placeholder)
					.skipMemoryCache(true)
					.crossFade(500)
					.fitCenter()
					.into(recyclerViewContentViewHolder.picture_iv);
		} else if (holder instanceof RecyclerViewDateViewHolder) {
			RecyclerViewDateViewHolder recyclerViewDateViewHolder = (RecyclerViewDateViewHolder)
					holder;
			ZhiHuNewsItemInfo zhiHuNewsItemInfo = zhiHuNewsItemInfoList.get(position);

			if (String.valueOf(zhiHuNewsItemInfo.date_cus).equals(cd)) {
				//				Log.i("ZRH", "cd: " + cd);
				//				Log.i("ZRH", "zhiHuNewsItemInfo.date_cus: "+String.valueOf(zhiHuNewsItemInfo.date_cus));
				recyclerViewDateViewHolder.date_tv.setText("今日新闻");
			} else {
				String date_month = String.valueOf(zhiHuNewsItemInfo.date_cus).substring(4, 6);
				String date_day = String.valueOf(zhiHuNewsItemInfo.date_cus).substring(6, 8);
				String date_month_day = date_month + "月" + date_day + "日";
				recyclerViewDateViewHolder.date_tv.setText(date_month_day);
			}
		}

		if (position == zhiHuNewsItemInfoList.size() - 1) {
			if (homeActivityRecyclerViewAdapterCallback != null) {
				homeActivityRecyclerViewAdapterCallback.refreshOldNews();
			}
		}

		/*
		* 测试用
		* */
		//		RecyclerViewContentViewHolder recyclerViewContentViewHolder =
		//				(RecyclerViewContentViewHolder) holder;
		//		HomeActivityRecyclerViewItemInfo homeActivityRecyclerViewItemInfo = zhiHuNewsItemInfoList.get
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
		ZhiHuNewsItemInfo zhiHuNewsItemInfo = zhiHuNewsItemInfoList.get(position);
		int item_type = zhiHuNewsItemInfo.item_layout;
		if (item_type == 0) {
			//			Log.i("ZRH", "adapter中getItemViewType: " + ITEM_TYPE.ITEM_DATE.ordinal());
			return_type = 0;
		}
		if (item_type == 1) {
			//			Log.i("ZRH", "item_type: " + item_type);
			//			Log.i("ZRH", "adapter中getItemViewType: " + ITEM_TYPE.ITEM_CONTENT.ordinal());
			return_type = 1;
		}
		//		Log.i("ZRH", "item_type: " + item_type);
		//		Log.i("ZRH", "return_type: " + return_type);
		return return_type;
		//		return super.getItemViewType(position);
	}

	@Override
	public int getItemCount() {
		//		Log.i("ZRH",zhiHuNewsItemInfoList.size()+"");
		//		return 0;
		//		int count = 0;
		//		for (int i = 0; i < zhiHuNewsItemInfoList.size(); i++) {
		//			List<ZhiHuNewsItemInfo> list = zhiHuNewsItemInfoList.get(i).stories;
		//			Log.i("ZRH","zhiHuNewsItemInfoList.size(): "+ list.size());
		//			count = count + list.size();
		//		}
		//		Log.i("ZRH", "adapter中getItemCount, zhiHuNewsItemInfoList.size(): " +
		//				zhiHuNewsItemInfoList.size());
		return zhiHuNewsItemInfoList.size();
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
