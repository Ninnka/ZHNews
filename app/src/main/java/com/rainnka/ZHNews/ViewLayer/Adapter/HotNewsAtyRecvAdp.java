package com.rainnka.ZHNews.ViewLayer.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rainnka.ZHNews.ViewLayer.Activity.HotNewsAty;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemHot;
import com.rainnka.ZHNews.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by rainnka on 2016/8/16 21:50
 * Project name is ZHKUNews
 */
public class HotNewsAtyRecvAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	protected WeakReference<HotNewsAty> hotNewsAtyWeakReference;
	protected HotNewsAty hotNewsAty;
	protected LayoutInflater layoutInflater;
	public List<ZhiHuNewsItemHot> zhiHuNewsItemHotList;

	public HotNewsRecvItemOnClickListener hotNewsRecvItemOnClickListener;

	public HotNewsAtyRecvAdp(HotNewsAty hotNewsAty) {
		this.hotNewsAtyWeakReference = new WeakReference<>(hotNewsAty);
		this.hotNewsAty = this.hotNewsAtyWeakReference.get();
		layoutInflater = LayoutInflater.from(this.hotNewsAty);
	}

	public void setZhiHuNewsItemHotList(List<ZhiHuNewsItemHot> zhiHuNewsItemHotList) {
		this.zhiHuNewsItemHotList = zhiHuNewsItemHotList;
	}

	public void addZhiHuNewsItemHotList(List<ZhiHuNewsItemHot> zhiHuNewsItemHotList) {
		this.zhiHuNewsItemHotList.addAll(getItemCount(), zhiHuNewsItemHotList);
	}

	public void setHotNewsRecvItemOnClickListener(HotNewsRecvItemOnClickListener hotNewsRecvItemOnClickListener) {
		this.hotNewsRecvItemOnClickListener = hotNewsRecvItemOnClickListener;
	}

	public ZhiHuNewsItemHot getZhiHuNewsItemHot(int position){
		return zhiHuNewsItemHotList.get(position);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new HotNewsViewHolder(layoutInflater.inflate(R.layout.hotnews_aty_recv_item,
				parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
		if (holder instanceof HotNewsViewHolder) {
			Glide.with(hotNewsAty)
					.load(zhiHuNewsItemHotList.get(position).thumbnail)
					.placeholder(R.drawable.placeholder)
					.skipMemoryCache(true)
					.diskCacheStrategy(DiskCacheStrategy.RESULT)
					.crossFade(0)
					.into(((HotNewsViewHolder) holder).imageView_pic);
			((HotNewsViewHolder) holder).textView_title.setText(zhiHuNewsItemHotList.get(position).title);
			((HotNewsViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (hotNewsRecvItemOnClickListener != null) {
						hotNewsRecvItemOnClickListener.onItemClick(position);
					}
				}
			});
			((HotNewsViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					if (hotNewsRecvItemOnClickListener != null) {
						hotNewsRecvItemOnClickListener.onItemLongClick();
						return true;
					}
					return false;
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return zhiHuNewsItemHotList.size();
	}

	public static class HotNewsViewHolder extends RecyclerView.ViewHolder {

		public ImageView imageView_pic;
		public TextView textView_title;

		public HotNewsViewHolder(View itemView) {
			super(itemView);
			imageView_pic = (ImageView) itemView.findViewById(R.id
					.hotnews_aty_recv_item_pic_ImageView);
			textView_title = (TextView) itemView.findViewById(R.id
					.hotnews_aty_recv_item_title_TextView);
		}
	}

	/*************
	 * main inner interface
	 **************/

	public interface HotNewsRecvItemOnClickListener {
		void onItemClick(int position);

		void onItemLongClick();
	}
}
