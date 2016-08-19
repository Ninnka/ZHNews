package com.rainnka.ZHNews.ViewLayer.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rainnka.ZHNews.ViewLayer.Activity.CommentsAty;
import com.rainnka.ZHNews.Bean.ZhihuNewsItemComments;
import com.rainnka.ZHNews.R;

import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainnka on 2016/8/14 15:21
 * Project name is ZHKUNews
 */
public class CommentsAtyRecvAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	public CommentsAty commentsAty;
	public WeakReference<CommentsAty> commentsAtyWeakReference;
	public LayoutInflater layoutInflater;

	public FastDateFormat format = FastDateFormat.getInstance( "MM-dd HH:mm:ss" );

	public List<ZhihuNewsItemComments> zhihuNewsItemCommentsList;

	public CommentsAtyRecvAdp(CommentsAty commentsAty) {
		this.commentsAtyWeakReference = new WeakReference<CommentsAty>(commentsAty);
		this.commentsAty = this.commentsAtyWeakReference.get();
		layoutInflater = LayoutInflater.from(this.commentsAty);
		zhihuNewsItemCommentsList = new ArrayList<>();
	}

	public void setZhihuNewsItemCommentsList(List<ZhihuNewsItemComments> zhihuNewsItemCommentsList) {
		this.zhihuNewsItemCommentsList = zhihuNewsItemCommentsList;
	}

	public void addZhihuNewsItemCommentsList(List<ZhihuNewsItemComments>
													 zhihuNewsItemCommentsList) {
		this.zhihuNewsItemCommentsList.addAll(zhihuNewsItemCommentsList);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new commentsViewHolder(layoutInflater.inflate(R.layout.comments_aty_recv_item,
				parent, false));
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof commentsViewHolder) {
			ZhihuNewsItemComments zhihuNewsItemComments = zhihuNewsItemCommentsList.get(position);
			Glide.with(commentsAty)
					.load(zhihuNewsItemComments.avatar)
					.skipMemoryCache(true)
					.diskCacheStrategy(DiskCacheStrategy.RESULT)
					.crossFade(0)
					.into(new SimpleTarget<GlideDrawable>() {
						@Override
						public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
							((commentsViewHolder) holder).avatar.setImageDrawable(resource);
						}
					});
			((commentsViewHolder) holder).author.setText(zhihuNewsItemComments.author);
			((commentsViewHolder) holder).content.setText(zhihuNewsItemComments.content);
			String timeStr = format.format(zhihuNewsItemComments.time);
			((commentsViewHolder) holder).time.setText(timeStr);
			((commentsViewHolder) holder).like.setText(String.valueOf(zhihuNewsItemComments.likes));
		}
	}

	@Override
	public int getItemCount() {
		return zhihuNewsItemCommentsList.size();
	}

	public static class commentsViewHolder extends RecyclerView.ViewHolder {

		public RoundedImageView avatar;
		public TextView author;
		public TextView content;
		public TextView time;
		public TextView like;

		public commentsViewHolder(View itemView) {
			super(itemView);
			avatar = (RoundedImageView) itemView.findViewById(R.id.comments_aty_recv_item_avatar);
			author = (TextView) itemView.findViewById(R.id.comments_aty_recv_item_author);
			content = (TextView) itemView.findViewById(R.id.comments_aty_recv_item_content);
			time = (TextView) itemView.findViewById(R.id.comments_aty_recv_item_time);
			like = (TextView) itemView.findViewById(R.id.comments_aty_recv_item_like);
		}
	}

}
