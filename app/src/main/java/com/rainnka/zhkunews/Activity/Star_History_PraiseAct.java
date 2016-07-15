package com.rainnka.zhkunews.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.rainnka.zhkunews.Adapter.Star_History_PraiseActivityRecyclerViewAdapter;
import com.rainnka.zhkunews.Bean.ZhiHuNewsItemInfo;
import com.rainnka.zhkunews.Callback_Listener.SimpleItemTouchHelperCallback;
import com.rainnka.zhkunews.Callback_Listener.onSHPActRecyclerItemClickListener;
import com.rainnka.zhkunews.R;
import com.rainnka.zhkunews.Utility.SQLiteCreateTableHelper;
import com.squareup.okhttp.OkHttpClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainnka on 2016/5/21 14:10
 * Project name is ZHKUNews
 */
public class Star_History_PraiseAct extends AppCompatActivity {

	public CoordinatorLayout coordinatorLayout;
	protected AppBarLayout appBarLayout;
	protected Toolbar toolbar;
	protected RecyclerView recyclerView;
	protected SwipeRefreshLayout swipeRefreshLayout;

	public LinearLayoutManager linearLayoutManager;

	public Star_History_PraiseActivityRecyclerViewAdapter
			star_history_praiseActivityRecyclerViewAdapter;

	String title = "";

	public List<ZhiHuNewsItemInfo> zhiHuNewsItemInfoList = new ArrayList<>();

	public Gson gson;

	public OkHttpClient okHttpClient;

	public StarZhiHuNewsItemHandler starZhiHuNewsItemHandler;

	public GetStarItemInfoHandler getStarItemInfoHandler;

	public SQLiteDatabase sqLiteDatabase;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.star_history_praise_act);

		/*
		* 获取Intent中的信息
		* */
		title = getInentInfo();

		/*
		* 初始化组件
		* */
		initComponent();

		/*
		* 初始化gson对象
		* */
		//		initGson();
		
		/*
		* 初始化handler
		* */
		initHandler();

		/*
		* 初始化toolbar
		* */
		initToolbar();

		/*
		* 准备recyclerview用的linearLayoutManager
		* */
		intiLinearLayoutManager();

		/*
		* 初始化RecyclerViewAdapter
		* */
		initRecyclerViewAdapter();

		/*
		* 设置recyclerview的相关参数
		* */
		initRecyclerViewSetting();

		/*
		* 获取本地数据库中存储的信息
		* */
		initMyStarItemInfoFromDatabase();


		/*
		* 网络访问资源
		* */
		//		swipeRefreshLayout.setRefreshing(true);
		//		swipeRefreshLayout.setDistanceToTriggerSync(800);
		//		okHttpClient = new OkHttpClient();
		//		Request request = new Request.Builder()
		//				.url("http://news-at.zhihu.com/api/4/news/latest")
		//				.build();
		//		Call call = okHttpClient.newCall(request);
		//		call.enqueue(new Callback() {
		//			@Override
		//			public void onFailure(Request request, IOException e) {
		//				Log.i("ZRH", "onFailure to access url: " + request.urlString());
		//			}
		//
		//			@Override
		//			public void onResponse(Response response) throws IOException {
		//				if (response.code() == 200) {
		//					Log.i("ZRH", "success access url: " + response.request().urlString());
		//					zhiHuNewsLatestItemInfo = gson.fromJson(response.body().string(),
		//							ZhiHuNewsLatestItemInfo.class);
		//					starZhiHuNewsItemHandler.sendEmptyMessage(0x123);
		//				}
		//			}
		//		});
	}

	private void initMyStarItemInfoFromDatabase() {
		if (title.equals(HomeAct.STAR_KEY)) {
			if (sqLiteDatabase == null) {
				//				String queryStar = "SELECT * FROM my_star";
				sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(Star_History_PraiseAct.this
						.getFilesDir().toString() + "/myInfo.db3", null);
				new Thread(new Runnable() {
					@Override
					public void run() {
						sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_STAR_TABLE);
						getStarItemInfoHandler.sendEmptyMessage(0x643);
					}
				}).start();

				//				Cursor cursor = sqLiteDatabase.rawQuery(queryStar, null);
				//				if (cursor != null) {
				//					if (cursor.moveToFirst()) {
				//						do {
				//							ZhiHuNewsItemInfo zhiHuNewsItemInfo = new ZhiHuNewsItemInfo();
				//							zhiHuNewsItemInfo.id = Integer.parseInt(cursor.getString(1));
				//							zhiHuNewsItemInfo.images.add(0, cursor.getString(2));
				//							zhiHuNewsItemInfo.title = cursor.getString(3);
				//							Log.i("ZRH","zhiHuNewsItemInfo.id: "+zhiHuNewsItemInfo.id);
				//							Log.i("ZRH","zhiHuNewsItemInfo.images.get(0): "+zhiHuNewsItemInfo
				//									.images.get(0));
				//							Log.i("ZRH","zhiHuNewsItemInfo.title: "+zhiHuNewsItemInfo.title);
				//
				//							zhiHuNewsItemInfoList.add(zhiHuNewsItemInfo);
				//						} while (cursor.moveToNext());
				//					}
				//				}
				//				cursor.close();

			}
			//			starZhiHuNewsItemHandler.sendEmptyMessage(0x123);
		}
		if (title.equals(HomeAct.PRAISE_KEY)) {
			if (sqLiteDatabase == null) {
				String queryStar = "SELECT * FROM my_praise";
				sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(Star_History_PraiseAct.this
						.getFilesDir().toString() + "/myInfo.db3", null);
			}
			starZhiHuNewsItemHandler.sendEmptyMessage(0x123);
		}
		if (title.equals(HomeAct.HISTORY_KEY)) {
			if (sqLiteDatabase == null) {
				String queryStar = "";
				sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(Star_History_PraiseAct.this
						.getFilesDir().toString() + "/myInfo.db3", null);
			}
			starZhiHuNewsItemHandler.sendEmptyMessage(0x123);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sqLiteDatabase != null) {
			sqLiteDatabase.close();
		}
		sqLiteDatabase = null;
		starZhiHuNewsItemHandler.removeCallbacksAndMessages(null);
		getStarItemInfoHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
		}
		return true;
	}

	private void initComponent() {
		coordinatorLayout = (CoordinatorLayout) findViewById(R.id
				.star_history_praiseActivity_Content_root_CoordinatorLayout);
		appBarLayout = (AppBarLayout) findViewById(R.id
				.star_history_praiseActivity_Content_main_AppBarLayout);
		toolbar = (Toolbar) findViewById(R.id.star_history_praiseActivity_Content_main_ToolBar);
		recyclerView = (RecyclerView) findViewById(R.id
				.star_history_praiseActivity_Content_main_RecyclerView);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id
				.star_history_praiseActivity_Content_main_SwipeRefreshLayout);
		swipeRefreshLayout.setEnabled(false);
	}

	private void initToolbar() {
		switch (title) {
			case HomeAct.STAR_KEY:
				toolbar.setTitle("收藏");
				break;
			case HomeAct.HISTORY_KEY:
				toolbar.setTitle("浏览历史");
				break;
			case HomeAct.PRAISE_KEY:
				toolbar.setTitle("点赞");
				break;
		}
		toolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void initRecyclerViewAdapter() {
		star_history_praiseActivityRecyclerViewAdapter = new
				Star_History_PraiseActivityRecyclerViewAdapter(Star_History_PraiseAct.this);
	}

	private void intiLinearLayoutManager() {
		linearLayoutManager = new LinearLayoutManager(Star_History_PraiseAct.this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
	}

	/*
	* 设置recyclerview的相关参数
	* */
	private void initRecyclerViewSetting() {
		/*
		* 添加linearLayoutmanager
		* */
		recyclerView.setLayoutManager(linearLayoutManager);

		/*
		* 添加itemTouchHelper
		* */
		ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback
				(star_history_praiseActivityRecyclerViewAdapter);
		final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
		itemTouchHelper.attachToRecyclerView(recyclerView);

		/*
		* 添加点击和长控事件
		* */
		recyclerView.addOnItemTouchListener(new onSHPActRecyclerItemClickListener(recyclerView) {
			@Override
			public void onItemClick(RecyclerView.ViewHolder viewHolder) {
				Intent intent = new Intent();
				intent.setAction(HomeAct.INTENT_TO_NEWS_KEY);
				Bundle bundle = new Bundle();
				bundle.putSerializable(HomeAct.SER_KEY,
						star_history_praiseActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.get
								(viewHolder.getAdapterPosition()));
				intent.putExtras(bundle);
				startActivity(intent);
			}

			@Override
			public void onItemLongClick(RecyclerView.ViewHolder viewHolder) {
				if (!title.equals(HomeAct.HISTORY_KEY)) {
					itemTouchHelper.startSwipe(viewHolder);

//					int itemId = star_history_praiseActivityRecyclerViewAdapter
//							.zhiHuNewsItemInfoList.get(viewHolder.getAdapterPosition()).id;
//					if (sqLiteDatabase == null) {
//						sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(Star_History_PraiseAct.this
//								.getFilesDir().toString() + "/myInfo.db3", null);
//					}
//					sqLiteDatabase.delete("my_star", "ItemId like ?", new String[]{String.valueOf
//							(itemId)});
//					SnackbarUtility.getSnackbarDefault(coordinatorLayout, "删除成功", Snackbar
//							.LENGTH_SHORT).show();

				}
			}
		});
	}

	public String getInentInfo() {
		return getIntent().getStringExtra(HomeAct.INTENT_STRING_DATA_KEY);
	}

	private void initHandler() {
		starZhiHuNewsItemHandler = new StarZhiHuNewsItemHandler(Star_History_PraiseAct.this);
		getStarItemInfoHandler = new GetStarItemInfoHandler(Star_History_PraiseAct.this);
	}

	private void initGson() {
		gson = new Gson();
	}

	//	@Override
	//	public void onRefresh() {
	//
	//	}

	/*
	* 静态内部类-处理访问的网络资源
	* */
	static class StarZhiHuNewsItemHandler extends Handler {

		public WeakReference<Star_History_PraiseAct> star_history_praiseActivityWeakReference;
		public Star_History_PraiseAct star_history_praiseAct;

		public StarZhiHuNewsItemHandler(Star_History_PraiseAct star_history_praiseAct) {
			this.star_history_praiseActivityWeakReference = new WeakReference<>
					(star_history_praiseAct);
			this.star_history_praiseAct = this.star_history_praiseActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//			if (star_history_praiseAct.zhiHuNewsLatestItemInfo.stories.size() >= 6) {
			//				for (int i = 0; i < 10; i++) {
			//					star_history_praiseAct.zhiHuNewsItemInfoList.add
			//							(star_history_praiseAct.zhiHuNewsLatestItemInfo.stories.get(i));
			//					//					Log.i("ZRH", star_history_praiseActivity.zhiHuNewsLatestItemInfo.stories.get
			//					//							(i).title);
			//				}
			//			} else {
			//				for (int i = 0; i < star_history_praiseAct.zhiHuNewsLatestItemInfo.stories
			//						.size(); i++) {
			//					star_history_praiseAct.zhiHuNewsItemInfoList.add
			//							(star_history_praiseAct.zhiHuNewsLatestItemInfo.stories.get(i));
			//				}
			//			}
			star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
					.setZhiHuNewsItemInfoList(star_history_praiseAct.zhiHuNewsItemInfoList);
			star_history_praiseAct.recyclerView.setAdapter(star_history_praiseAct
					.star_history_praiseActivityRecyclerViewAdapter);
			//			star_history_praiseAct.swipeRefreshLayout.setRefreshing(false);
		}
	}

	/*
	* 静态内部类-建表
	* */
	static class GetStarItemInfoHandler extends Handler {
		public WeakReference<Star_History_PraiseAct> star_history_praiseActivityWeakReference;
		public Star_History_PraiseAct star_history_praiseAct;

		public GetStarItemInfoHandler(Star_History_PraiseAct star_history_praiseAct) {
			this.star_history_praiseActivityWeakReference = new WeakReference<>
					(star_history_praiseAct);
			this.star_history_praiseAct = this.star_history_praiseActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0x643:
					String queryStar = "SELECT * FROM my_star";
					Cursor cursor = star_history_praiseAct.sqLiteDatabase.rawQuery(queryStar, null);
					if (cursor != null) {
						if (cursor.moveToFirst()) {
							do {
								ZhiHuNewsItemInfo zhiHuNewsItemInfo = new ZhiHuNewsItemInfo();
								zhiHuNewsItemInfo.id = Integer.parseInt(cursor.getString(1));
								zhiHuNewsItemInfo.images.add(0, cursor.getString(2));
								zhiHuNewsItemInfo.title = cursor.getString(3);

//								Log.i("ZRH", "zhiHuNewsItemInfo.id: " + zhiHuNewsItemInfo.id);
//								Log.i("ZRH", "zhiHuNewsItemInfo.images.get(0): " + zhiHuNewsItemInfo
//										.images.get(0));
//								Log.i("ZRH", "zhiHuNewsItemInfo.title: " + zhiHuNewsItemInfo.title);

								star_history_praiseAct.zhiHuNewsItemInfoList.add(zhiHuNewsItemInfo);
							} while (cursor.moveToNext());
						}
					}
					cursor.close();
					star_history_praiseAct.starZhiHuNewsItemHandler.sendEmptyMessage(0x123);

					break;
				case 0x671:
					break;
				case 0x981:
					break;
			}
		}
	}
}
