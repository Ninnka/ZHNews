package com.rainnka.ZHNews.ViewLayer.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.rainnka.ZHNews.Utility.IntentActionUtility;
import com.rainnka.ZHNews.Utility.SQLiteCreateTableHelper;
import com.rainnka.ZHNews.ViewLayer.Adapter.HotNewsAtyRecvAdp;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.Bean.HotNews;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemHot;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.ConstantUtility;
import com.rainnka.ZHNews.Utility.LengthConverterUtility;
import com.rainnka.ZHNews.Utility.TransitionHelper;
import com.rainnka.ZHNews.ViewLayer.Activity.Base.SwipeBackAty;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by rainnka on 2016/8/16 15:02
 * Project name is ZHKUNews
 */
public class HotNewsAty extends SwipeBackAty implements HotNewsAtyRecvAdp.HotNewsRecvItemOnClickListener {

	/********
	 * main self members
	 *********/
	protected Toolbar toolbar;
	protected RecyclerView recyclerView;

	protected ProgressDialog progressDialog;

	public HotNewsAtyRecvAdp hotNewsAtyRecvAdp;

	public List<ZhiHuNewsItemHot> zhiHuNewsItemHotList;

	public HotNewsHandler hotNewsHandler;

	public SQLiteDatabase sqLiteDatabase;

	public Retrofit retrofit;

	/**************************
	 * main inherited methods
	 **************************/

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotnews_aty);
		setupWindowAnimations();
		setFullScreenLayout();
		initProgressDialog();

		iniHandler();
		initSQLiteDatabase();

		initComponent();
		initToolbarSetting();

		initRecyclerViewSetting();

		initZhihuHotNewsContent();

		addHotNewsItemOnClickListener();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sqLiteDatabase.isOpen()) {
			sqLiteDatabase.close();
		}
		hotNewsHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return true;
	}

	@Override
	public void onItemClick(int position) {
		if (ConstantUtility.userIsLogin) {
			ZhiHuNewsItemHot zhiHuNewsItemHot = hotNewsAtyRecvAdp.getZhiHuNewsItemHot(position);
			sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_HISTORY_TABLE);
			try {
				sqLiteDatabase.beginTransaction();
				sqLiteDatabase.delete("my_history", "ItemId like ?", new
						String[]{String.valueOf(zhiHuNewsItemHot.news_id)});
				sqLiteDatabase.setTransactionSuccessful();
			} catch (Exception e) {
				Log.i("ZRH", e.getStackTrace().toString());
				Log.i("ZRH", e.getMessage());
				Log.i("ZRH", e.toString());
			} finally {
				sqLiteDatabase.endTransaction();
			}

			ContentValues contentValues = new ContentValues();
			contentValues.put("ItemId", zhiHuNewsItemHot.news_id);
			contentValues.put("ItemImage", zhiHuNewsItemHot.thumbnail);
			contentValues.put("ItemTitle", zhiHuNewsItemHot.title);
			contentValues.put("ItemSeriType", ConstantUtility.SER_KEY_HOTNEWS);
			try {
				sqLiteDatabase.beginTransaction();
				sqLiteDatabase.insert("my_history", null, contentValues);
				sqLiteDatabase.setTransactionSuccessful();
			} catch (Exception e) {
				Log.i("ZRH", e.getStackTrace().toString());
				Log.i("ZRH", e.getMessage());
				Log.i("ZRH", e.toString());
			} finally {
				sqLiteDatabase.endTransaction();
			}
			Intent intent = new Intent();
			intent.setAction(IntentActionUtility.INTENT_TO_NEWS_KEY);
			Bundle bundle = new Bundle();
			bundle.putSerializable(ConstantUtility.SER_KEY_HOTNEWS, zhiHuNewsItemHot);
			intent.putExtras(bundle);
			startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs()).toBundle(),
					true);
		}

	}

	@Override
	public void onItemLongClick() {

	}

	/**************************
	 * main self methods
	 **************************/

	public void initComponent() {
		toolbar = (Toolbar) findViewById(R.id.hotnews_aty_Toolbar);
		recyclerView = (RecyclerView) findViewById(R.id.hotnews_aty_RecyclerView);
	}

	public void initToolbarSetting() {
		toolbar.setTitle("热门精选");
		toolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
		layoutParams.setMargins(0, LengthConverterUtility.dip2px(BaseApplication
				.getBaseApplicationContext(), 24), 0, 0);
		toolbar.setLayoutParams(layoutParams);
	}

	private void initSQLiteDatabase() {
		try {
			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(BaseApplication.getDATABASE_PATH() +
					"/myInfo.db3", null);
		} catch (Exception e) {
			Log.i("ZRH", e.getMessage());
			Log.i("ZRH", e.toString());
		}
	}

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("loading");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	public void initRecyclerViewSetting() {
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
		//		gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(gridLayoutManager);
		zhiHuNewsItemHotList = new ArrayList<>();
		hotNewsAtyRecvAdp = new HotNewsAtyRecvAdp(this);
		hotNewsAtyRecvAdp.setZhiHuNewsItemHotList(zhiHuNewsItemHotList);
		recyclerView.setAdapter(hotNewsAtyRecvAdp);
	}

	private void iniHandler() {
		hotNewsHandler = new HotNewsHandler(this);
	}

	private void addHotNewsItemOnClickListener() {
		hotNewsAtyRecvAdp.setHotNewsRecvItemOnClickListener(this);
	}

	public void initZhihuHotNewsContent() {
		retrofit = new Retrofit.Builder()
				.baseUrl("http://news-at.zhihu.com")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		HotNewsService hotNewsService = retrofit.create(HotNewsService.class);
		Call<HotNews> hotNewsCall = hotNewsService.hot();

		hotNewsCall.enqueue(new Callback<HotNews>() {
			@Override
			public void onResponse(Call<HotNews> call, Response<HotNews> response) {
				if (response.code() == 200) {
					try {
						HotNews hotNews = response.body();
						//						Log.i("ZRH", "hotNews.recent.size: " + hotNews.recent.size());
						zhiHuNewsItemHotList = hotNews.recent;
						hotNewsHandler.sendEmptyMessage(0x12398664);
					} catch (Exception e) {
						Log.i("ZRH", e.toString());
					}
				}
			}

			@Override
			public void onFailure(Call<HotNews> call, Throwable t) {

			}
		});
	}

	protected void hotNewsDataHasChanged() {
		hotNewsAtyRecvAdp.addZhiHuNewsItemHotList(zhiHuNewsItemHotList);
		hotNewsAtyRecvAdp.notifyItemRangeInserted(0, hotNewsAtyRecvAdp.getItemCount() - 1);
		progressDialog.dismiss();
	}

	public Pair<View, String>[] getTransitionPairs() {
		Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants
				(this, false);
		return pairs;
	}

	public ActivityOptionsCompat getTranstitionOptions(Pair<View, String>[] pairs) {
		ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
				.makeSceneTransitionAnimation(this, pairs);
		return activityOptionsCompat;
	}

	public void startActivityInTransition(Intent intent, Bundle bundle, boolean transitionFlag) {
		if (transitionFlag) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				startActivity(intent, bundle);
			} else {
				startActivity(intent);
			}
		} else {
			startActivity(intent);
		}

	}

	public void startActivityInTransitionForResult(Intent intent, int code, Bundle bundle, boolean transitionFlag) {
		if (transitionFlag) {
			startActivityForResult(intent, code, bundle);
		} else {
			startActivityForResult(intent, code);
		}
	}


	/****************************
	 * main inner class
	 *****************************/

	public interface HotNewsService {
		@GET("/api/3/news/hot")
		Call<HotNews> hot();
	}

	public static class HotNewsHandler extends Handler {

		protected HotNewsAty hotNewsAty;
		protected WeakReference<HotNewsAty> hotNewsAtyWeakReference;

		public HotNewsHandler(HotNewsAty hotNewsAty) {
			this.hotNewsAtyWeakReference = new WeakReference<>(hotNewsAty);
			this.hotNewsAty = this.hotNewsAtyWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				default:
					hotNewsAty.hotNewsDataHasChanged();
			}
		}
	}

}
