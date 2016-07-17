package com.rainnka.zhkunews.Adapter;

import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rainnka.zhkunews.Activity.Star_History_PraiseAct;
import com.rainnka.zhkunews.Bean.ZhiHuNewsItemInfo;
import com.rainnka.zhkunews.R;
import com.rainnka.zhkunews.Utility.SnackbarUtility;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by rainnka on 2016/5/21 14:54
 * Project name is ZHKUNews
 */
public class Star_History_PraiseActivityRecyclerViewAdapter extends RecyclerView
		.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

	public WeakReference<AppCompatActivity> appCompatActivityWeakReference;
	public AppCompatActivity appCompatActivity;
	public LayoutInflater layoutInflater;
	public List<ZhiHuNewsItemInfo> zhiHuNewsItemInfoList;

	public SQLiteDatabase sqLiteDatabase;

	public Star_History_PraiseActivityRecyclerViewAdapter(AppCompatActivity appCompatActivity) {
		this.appCompatActivityWeakReference = new WeakReference<>
				(appCompatActivity);
		this.appCompatActivity = this.appCompatActivityWeakReference.get();
		layoutInflater = LayoutInflater.from(this.appCompatActivity);
	}

	public void setZhiHuNewsItemInfoList(List<ZhiHuNewsItemInfo> zhiHuNewsItemInfoList) {
		this.zhiHuNewsItemInfoList = zhiHuNewsItemInfoList;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new RecyclerViewContentViewHolder(layoutInflater.inflate(R.layout
				.star_history_praise_act_content_recyclerview_item, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof RecyclerViewContentViewHolder) {
			ZhiHuNewsItemInfo zhiHuNewsItemInfo = zhiHuNewsItemInfoList.get(position);
			RecyclerViewContentViewHolder recyclerViewContentViewHolder =
					(RecyclerViewContentViewHolder) holder;
			recyclerViewContentViewHolder.title_tv.setText(zhiHuNewsItemInfo.title);
			Glide.with(appCompatActivity)
					.load(zhiHuNewsItemInfo.images.get(0))
					.crossFade(500)
					.skipMemoryCache(true)
					.into(recyclerViewContentViewHolder.image_iv);
		}
	}



	@Override
	public int getItemCount() {
		return zhiHuNewsItemInfoList.size();
	}

	@Override
	public void onItemDismiss(int targetPosition) {
		try {
			int itemId = zhiHuNewsItemInfoList.get(targetPosition).id;
			if (sqLiteDatabase == null) {
				sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(this.appCompatActivity
						.getFilesDir().toString() + "/myInfo.db3", null);
			}
			sqLiteDatabase.delete("my_star", "ItemId like ?", new String[]{String.valueOf
					(itemId)});
			SnackbarUtility.getSnackbarDefault(((Star_History_PraiseAct) appCompatActivity)
					.coordinatorLayout, "删除成功", Snackbar.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.i("ZRH", "exception in onItemDismiss" + e.toString());
			Log.i("ZRH", "exception in onItemDismiss" + e.getMessage());
		}
		zhiHuNewsItemInfoList.remove(targetPosition);
		notifyItemRemoved(targetPosition);
	}

	public static class RecyclerViewContentViewHolder extends RecyclerView.ViewHolder {

		public ImageView image_iv;
		public TextView title_tv;

		public RecyclerViewContentViewHolder(View itemView) {
			super(itemView);

			image_iv = (ImageView) itemView.findViewById(R.id
					.star_history_praise_activity_content_recyclerview_item_picture_iv);
			title_tv = (TextView) itemView.findViewById(R.id
					.star_history_praise_activity_content_recyclerview_item_content_tv);
		}

	}
}
