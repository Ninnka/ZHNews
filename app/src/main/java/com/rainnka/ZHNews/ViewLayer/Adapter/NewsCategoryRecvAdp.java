package com.rainnka.ZHNews.ViewLayer.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemThemeStories;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.ViewLayer.Activity.NewsCategoryAty;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by rainnka on 2016/8/19 11:05
 * Project name is ZHKUNews
 */
public class NewsCategoryRecvAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	protected WeakReference<NewsCategoryAty> newsCategoryAtyWeakReference;
	protected NewsCategoryAty newsCategoryAty;
	public LayoutInflater layoutInflater;
	public List<ZhiHuNewsItemThemeStories> zhiHuNewsItemThemeStoriesList;


	public NewsCategoryRecvAdp(NewsCategoryAty newsCategoryAty) {
		this.newsCategoryAtyWeakReference = new WeakReference<>(newsCategoryAty);
		this.newsCategoryAty = this.newsCategoryAtyWeakReference.get();
		this.layoutInflater = LayoutInflater.from(this.newsCategoryAty);
	}

	public void setZhiHuNewsItemInfoList(List<ZhiHuNewsItemThemeStories> zhiHuNewsItemThemeStoriesList) {
		this.zhiHuNewsItemThemeStoriesList = zhiHuNewsItemThemeStoriesList;
//		for (int i = 0; i < this.zhiHuNewsItemThemeStoriesList.size(); i++) {
//			Log.i("ZRH", "type: " + this.zhiHuNewsItemThemeStoriesList.get(i).type);
//			Log.i("ZRH", "title: " + this.zhiHuNewsItemThemeStoriesList.get(i).title);
//			if (this.zhiHuNewsItemThemeStoriesList.get(i).images.size() > 0) {
//				Log.i("ZRH", "images: " + this.zhiHuNewsItemThemeStoriesList.get(i).images.get(0));
//			}
//			Log.i("ZRH", "id: " + this.zhiHuNewsItemThemeStoriesList.get(i).id);
//		}
	}

	public void addZhiHuNewsItemInfoList(List<ZhiHuNewsItemThemeStories> zhiHuNewsItemThemeStoriesList) {
		this.zhiHuNewsItemThemeStoriesList.addAll(zhiHuNewsItemThemeStoriesList);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		NewsCategoryViewHolder newsCategoryViewHolder = null;
		try {
			newsCategoryViewHolder = new NewsCategoryViewHolder(layoutInflater.inflate(R.layout
					.news_category_frgm_recv_item, parent, false));
		} catch (Exception e) {
			Log.i("ZRH", e.toString());
		}
		return newsCategoryViewHolder;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof NewsCategoryViewHolder) {
			if(zhiHuNewsItemThemeStoriesList.get(position).images.size() > 0){
				Glide.with(BaseApplication.getBaseApplicationContext())
						.load(zhiHuNewsItemThemeStoriesList.get(position).images.get(0))
						.placeholder(R.drawable.placeholder)
						.skipMemoryCache(true)
						.diskCacheStrategy(DiskCacheStrategy.RESULT)
						.crossFade(0)
						.into(((NewsCategoryViewHolder) holder).thumbnail);
			}else {
				((NewsCategoryViewHolder) holder).thumbnail.setImageResource(R.drawable.drawer_header_bg);
			}
			((NewsCategoryViewHolder) holder).title.setText(zhiHuNewsItemThemeStoriesList.get
					(position).title);
			((NewsCategoryViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(BaseApplication.getBaseApplicationContext(), "click", Toast
							.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return zhiHuNewsItemThemeStoriesList.size();
	}

	public static class NewsCategoryViewHolder extends RecyclerView.ViewHolder {

		public ImageView thumbnail;
		public TextView title;

		public NewsCategoryViewHolder(View itemView) {
			super(itemView);
			thumbnail = (ImageView) itemView.findViewById(R.id
					.news_category_frgm_recv_item_thumbnail_ImageView);
			title = (TextView) itemView.findViewById(R.id
					.news_category_frgm_recv_item_title_TextView);
		}
	}
}
