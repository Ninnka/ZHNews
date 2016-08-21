package com.rainnka.ZHNews.ViewLayer.Adapter;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemThemeStories;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemThemeStoriesHeader;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.ConstantUtility;
import com.rainnka.ZHNews.Utility.IntentActionUtility;
import com.rainnka.ZHNews.Utility.SQLiteCreateTableHelper;
import com.rainnka.ZHNews.Utility.TransitionHelper;
import com.rainnka.ZHNews.ViewLayer.Activity.NewsCategoryAty;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
	public List<ZhiHuNewsItemThemeStoriesHeader> zhiHuNewsItemThemeStoriesHeaderList = new ArrayList<>();

	//	public SQLiteDatabase sqLiteDatabase;

	public NewsCategoryRecvAdp(NewsCategoryAty newsCategoryAty) {
		this.newsCategoryAtyWeakReference = new WeakReference<>(newsCategoryAty);
		this.newsCategoryAty = this.newsCategoryAtyWeakReference.get();
		this.layoutInflater = LayoutInflater.from(this.newsCategoryAty);
		//		initSQLiteDatabase();
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
		if (viewType == ConstantUtility.NEWSCATEGORY_RECVITEM_CONTENT) {
			return new NewsCategoryViewHolder(layoutInflater
					.inflate(R.layout.news_category_frgm_recv_item, parent, false));
		} else {
			return new NewsCategoryRecvHeaderViewHolder(layoutInflater.inflate(R.layout
					.news_category_frgm_recv_item_header, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
		if (holder instanceof NewsCategoryViewHolder) {
			final ZhiHuNewsItemThemeStories zhiHuNewsItemThemeStories = zhiHuNewsItemThemeStoriesList
					.get(position);
			if (zhiHuNewsItemThemeStories.images.size() > 0) {
				Glide.with(BaseApplication.getBaseApplicationContext())
						.load(zhiHuNewsItemThemeStories.images.get(0))
						.placeholder(R.drawable.placeholder)
						.skipMemoryCache(true)
						.diskCacheStrategy(DiskCacheStrategy.RESULT)
						.crossFade(0)
						.into(((NewsCategoryViewHolder) holder).thumbnail);
			} else {
				((NewsCategoryViewHolder) holder).thumbnail.setImageResource(R.drawable.drawer_header_bg);
			}
			((NewsCategoryViewHolder) holder).title.setText(zhiHuNewsItemThemeStories.title);
			((NewsCategoryViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ConstantUtility.userIsLogin) {
//						ZhiHuNewsItemThemeStories zhiHuNewsItemThemeStories = zhiHuNewsItemThemeStoriesList.get
//								(position);
						newsCategoryAty.sqLiteDatabase.execSQL(SQLiteCreateTableHelper
								.CREATE_HISTORY_TABLE);
						try {
							newsCategoryAty.sqLiteDatabase.beginTransaction();
							newsCategoryAty.sqLiteDatabase.delete("my_history", "ItemId like ?", new
									String[]{String.valueOf(zhiHuNewsItemThemeStories.id)});
							newsCategoryAty.sqLiteDatabase.setTransactionSuccessful();
						} catch (Exception e) {
							Log.i("ZRH", e.getStackTrace().toString());
							Log.i("ZRH", e.getMessage());
							Log.i("ZRH", e.toString());
						} finally {
							newsCategoryAty.sqLiteDatabase.endTransaction();
						}

						ContentValues contentValues = new ContentValues();
						contentValues.put("ItemId", zhiHuNewsItemThemeStories.id);
						contentValues.put("ItemImage", zhiHuNewsItemThemeStories.images.get(0));
						contentValues.put("ItemTitle", zhiHuNewsItemThemeStories.title);
						contentValues.put("ItemSeriType", ConstantUtility.SER_KEY_THEME);
						try {
							newsCategoryAty.sqLiteDatabase.beginTransaction();
							newsCategoryAty.sqLiteDatabase.insert("my_history", null, contentValues);
							newsCategoryAty.sqLiteDatabase.setTransactionSuccessful();
						} catch (Exception e) {
							Log.i("ZRH", e.getStackTrace().toString());
							Log.i("ZRH", e.getMessage());
							Log.i("ZRH", e.toString());
						} finally {
							newsCategoryAty.sqLiteDatabase.endTransaction();
						}

						Intent intent = new Intent();
						intent.setAction(IntentActionUtility.INTENT_TO_NEWS_KEY);
						Bundle bundle = new Bundle();
						bundle.putSerializable(ConstantUtility.SER_KEY_THEME, zhiHuNewsItemThemeStoriesList
								.get(position));
						intent.putExtras(bundle);
						startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs())
								.toBundle(), true);
					}
				}
			});
		} else if (holder instanceof NewsCategoryRecvHeaderViewHolder) {
			ZhiHuNewsItemThemeStoriesHeader zhiHuNewsItemThemeStoriesHeader =
					zhiHuNewsItemThemeStoriesHeaderList.get(position);
			Glide.with(newsCategoryAty)
					.load(zhiHuNewsItemThemeStoriesHeader.image)
					.skipMemoryCache(true)
					.diskCacheStrategy(DiskCacheStrategy.RESULT)
					.crossFade(0)
					.into(((NewsCategoryRecvHeaderViewHolder) holder).image);
			((NewsCategoryRecvHeaderViewHolder) holder).description.setText
					(zhiHuNewsItemThemeStoriesHeader.description);
			((NewsCategoryRecvHeaderViewHolder) holder).name.setText
					(zhiHuNewsItemThemeStoriesHeader.name);
		}
	}

	@Override
	public int getItemCount() {
		return zhiHuNewsItemThemeStoriesList.size() + zhiHuNewsItemThemeStoriesHeaderList.size();
	}

	@Override
	public int getItemViewType(int position) {
		int type = -1;
		if (zhiHuNewsItemThemeStoriesHeaderList.size() > 0) {
			int headerListSize = zhiHuNewsItemThemeStoriesHeaderList.size();
			if (position >= headerListSize) {
				type = zhiHuNewsItemThemeStoriesList.get(position - headerListSize).item_layout;
			} else {
				type = zhiHuNewsItemThemeStoriesHeaderList.get(position).item_layout;
			}
		} else {
			type = zhiHuNewsItemThemeStoriesList.get(position).item_layout;
		}
		//		if ()
		if (type == -1) {
			return super.getItemViewType(position);
		} else {
			return type;
		}
	}

	public Pair<View, String>[] getTransitionPairs() {
		Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants
				(newsCategoryAty, false);
		return pairs;
	}

	public void addHeaderViewItem(ZhiHuNewsItemThemeStoriesHeader zhiHuNewsItemThemeStoriesHeader) {
		this.zhiHuNewsItemThemeStoriesHeaderList.add(zhiHuNewsItemThemeStoriesHeaderList.size(),
				zhiHuNewsItemThemeStoriesHeader);
	}

	public int getHeaderViewItemCount() {
		return zhiHuNewsItemThemeStoriesHeaderList.size();
	}

	public ActivityOptionsCompat getTranstitionOptions(Pair<View, String>[] pairs) {
		ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
				.makeSceneTransitionAnimation(newsCategoryAty, pairs);
		return activityOptionsCompat;
	}

	public void startActivityInTransition(Intent intent, Bundle bundle, boolean transitionFlag) {
		if (transitionFlag) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				newsCategoryAty.startActivity(intent, bundle);
			} else {
				newsCategoryAty.startActivity(intent);
			}
		} else {
			newsCategoryAty.startActivity(intent);
		}

	}

	public void startActivityInTransitionForResult(Intent intent, int code, Bundle bundle, boolean transitionFlag) {
		if (transitionFlag) {
			newsCategoryAty.startActivityForResult(intent, code, bundle);
		} else {
			newsCategoryAty.startActivityForResult(intent, code);
		}
	}

	//	public void initSQLiteDatabase() {
	//		try {
	//			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(BaseApplication.getDATABASE_PATH() +
	//					"/myInfo.db3", null);
	//		} catch (Exception e) {
	//			Log.i("ZRH", e.getMessage());
	//			Log.i("ZRH", e.toString());
	//		}
	//	}

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

	public static class NewsCategoryRecvHeaderViewHolder extends RecyclerView.ViewHolder {

		public ImageView image;
		public TextView name;
		public TextView description;

		public NewsCategoryRecvHeaderViewHolder(View itemView) {
			super(itemView);
			image = (ImageView) itemView.findViewById(R.id
					.news_category_frgm_recv_item_header_image_ImageView);
			name = (TextView) itemView.findViewById(R.id
					.news_category_frgm_recv_item_header_name_TextView);
			description = (TextView) itemView.findViewById(R.id
					.news_category_frgm_recv_item_header_description_TextView);
		}
	}
}
