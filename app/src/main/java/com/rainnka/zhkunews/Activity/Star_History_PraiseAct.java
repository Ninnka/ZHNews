package com.rainnka.zhkunews.Activity;

import android.content.ContentValues;
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
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

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
	protected ImageView imageView_MultChoice;

	public ActionMode actionMode;

	public LinearLayoutManager linearLayoutManager;

	public Star_History_PraiseActivityRecyclerViewAdapter
			star_history_praiseActivityRecyclerViewAdapter;

	public String title = "";

	public List<ZhiHuNewsItemInfo> zhiHuNewsItemInfoList = new ArrayList<>();

	public Gson gson;

	public OkHttpClient okHttpClient;

	public loadZhiHuNewsItemHandler loadZhiHuNewsItemHandler;

	//	public final static int getStarItemInfoHandler_KEY = 0x643;

	//	public GetStarItemInfoHandler getStarItemInfoHandler;

	public SQLiteDatabase sqLiteDatabase;

	public boolean hasPaused = false;

	public int checkId;


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
		initMyItemInfoFromDatabase();

		/*
		* 多选按钮的点击事件
		* */
		initMultChoiceClickListener();

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


	@Override
	protected void onResume() {
		if (hasPaused) {
			switch (title) {
				case HomeAct.STAR_KEY:
					if (sqLiteDatabase == null) {
						sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase
								(Star_History_PraiseAct.this.getFilesDir().toString() + "/myInfo" +
										".db3", null);
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							updateStarItemInfoFromResumeOrCreate();

						}
					}).start();
					break;
				case HomeAct.PRAISE_KEY:
					try {
						if (sqLiteDatabase == null) {
							sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase
									(Star_History_PraiseAct.this.getFilesDir().toString() + "/myInfo" +
											".db3", null);
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
								updatePraiseItemInfoFromResumeOrCreate();

							}
						}).start();
					} catch (Exception e) {
						Log.i("ZRH in resume", e.getStackTrace().toString());
						Log.i("ZRH in resume", e.getMessage());
						Log.i("ZRH in resume", e.toString());
					}

					break;
				case HomeAct.HISTORY_KEY:
					try {
						if (sqLiteDatabase == null) {
							sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase
									(Star_History_PraiseAct.this.getFilesDir().toString() + "/myInfo" +
											".db3", null);
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
								updateHistoryItemInfoFromResumeOrCreate();

							}
						}).start();
					} catch (Exception e) {
						Log.i("ZRH in resume", e.getStackTrace().toString());
						Log.i("ZRH in resume", e.getMessage());
						Log.i("ZRH in resume", e.toString());
					}
					break;
			}
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		hasPaused = true;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (sqLiteDatabase != null) {
			sqLiteDatabase.close();
		}
		loadZhiHuNewsItemHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
		//		getStarItemInfoHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public void onBackPressed() {
		//		if (actionMode != null) {
		//			recyclerViewDisableMultChoice();
		//			destoryActionModel();
		//			Log.i("ZRH","before recyclerViewWithdrawNormalItem()");
		//			recyclerViewWithdrawNormalItem();
		//		} else {
		//			Log.i("ZRH","actionMode is not null");
		super.onBackPressed();
		//		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				//				if (actionMode != null) {
				//					recyclerViewDisableMultChoice();
				//					destoryActionModel();
				//					Log.i("ZRH","before recyclerViewWithdrawNormalItem()");
				//					recyclerViewWithdrawNormalItem();
				//				} else {
				//					Log.i("ZRH","actionMode is not null");
				finish();
				//				}
		}
		return true;
	}

	/*
	* 初试化页面
	* 从数据库获取信息
	* */
	private void initMyItemInfoFromDatabase() {
		if (title.equals(HomeAct.STAR_KEY)) {
			if (sqLiteDatabase == null) {
				sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(Star_History_PraiseAct.this
						.getFilesDir().toString() + "/myInfo.db3", null);
				new Thread(new Runnable() {
					@Override
					public void run() {
						//						sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_STAR_TABLE);
						updateStarItemInfoFromResumeOrCreate();
						//						getStarItemInfoHandler.sendEmptyMessage(getStarItemInfoHandler_KEY);
					}
				}).start();
			}
		}
		if (title.equals(HomeAct.PRAISE_KEY)) {
			if (sqLiteDatabase == null) {
				sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(Star_History_PraiseAct.this
						.getFilesDir().toString() + "/myInfo.db3", null);
				new Thread(new Runnable() {
					@Override
					public void run() {
						//						sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_PRAISE_TABLE);
						updatePraiseItemInfoFromResumeOrCreate();

					}
				}).start();
			}
		}
		if (title.equals(HomeAct.HISTORY_KEY)) {
			if (sqLiteDatabase == null) {
				sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(Star_History_PraiseAct.this
						.getFilesDir().toString() + "/myInfo.db3", null);
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					//					sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_HISTORY_TABLE);
					updateHistoryItemInfoFromResumeOrCreate();

				}
			}).start();
		}
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
		imageView_MultChoice = (ImageView) findViewById(R.id
				.star_history_praiseActivity_Content_main_MultChoice_ImageView);
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

	private void initMultChoiceClickListener() {
		if (title.equals(HomeAct.HISTORY_KEY)) {
			imageView_MultChoice.setVisibility(View.GONE);
		} else {
			imageView_MultChoice.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (actionMode == null) {
						createActionModel();
						recyclerViewEnableMultChoice();
					}
				}
			});
		}
	}


	private void initRecyclerViewAdapter() {
		star_history_praiseActivityRecyclerViewAdapter = new
				Star_History_PraiseActivityRecyclerViewAdapter(Star_History_PraiseAct.this);
	}

	private void intiLinearLayoutManager() {
		linearLayoutManager = new LinearLayoutManager(Star_History_PraiseAct.this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
	}

	public String getInentInfo() {
		return getIntent().getStringExtra(HomeAct.INTENT_STRING_DATA_KEY);
	}

	private void initHandler() {
		loadZhiHuNewsItemHandler = new loadZhiHuNewsItemHandler(Star_History_PraiseAct.this);
		//		getStarItemInfoHandler = new GetStarItemInfoHandler(Star_History_PraiseAct.this);
	}

	private void initGson() {
		gson = new Gson();
	}

	//	@Override
	//	public void onRefresh() {
	//
	//	}

	/*
	* 设置recyclerview的相关参数
	* 包括item的点击事件
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
				if (sqLiteDatabase == null) {
					sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(Star_History_PraiseAct
							.this.getFilesDir().toString() + "/myInfo.db3", null);
				}
				sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_HISTORY_TABLE);
				int deleteCount = sqLiteDatabase.delete("my_history", "ItemId like ?", new
						String[]{String.valueOf(zhiHuNewsItemInfoList.get(viewHolder
						.getAdapterPosition()).id)});

				ContentValues contentValues = new ContentValues();
				contentValues.put("ItemId", zhiHuNewsItemInfoList.get(viewHolder
						.getAdapterPosition()).id);
				try {
					contentValues.put("ItemImage", zhiHuNewsItemInfoList.get(viewHolder
							.getAdapterPosition()).images.get(0));
				} catch (Exception e) {
					contentValues.put("ItemImage", zhiHuNewsItemInfoList.get(viewHolder
							.getAdapterPosition()).image);
				}
				contentValues.put("ItemTitle", zhiHuNewsItemInfoList.get(viewHolder
						.getAdapterPosition()).title);
				sqLiteDatabase.insert("my_history", null, contentValues);

				if (star_history_praiseActivityRecyclerViewAdapter.getSelectable()) {

					if (star_history_praiseActivityRecyclerViewAdapter.getItemChecked(viewHolder
							.getAdapterPosition())) {
						((Star_History_PraiseActivityRecyclerViewAdapter
								.RecyclerViewContentViewHolder) viewHolder).check_tv.setVisibility
								(View.GONE);
						star_history_praiseActivityRecyclerViewAdapter.removeSelectedItem
								(viewHolder.getAdapterPosition());
					} else {
						((Star_History_PraiseActivityRecyclerViewAdapter
								.RecyclerViewContentViewHolder) viewHolder).check_tv.setVisibility
								(View.VISIBLE);
						star_history_praiseActivityRecyclerViewAdapter.setItemChecked(viewHolder
								.getAdapterPosition(), true);
					}
					actionMode.setTitle("已选: " + star_history_praiseActivityRecyclerViewAdapter
							.mSelectedPositions.size() + "/" +
							star_history_praiseActivityRecyclerViewAdapter.getItemCount());

				} else {
					Intent intent = new Intent();
					intent.setAction(HomeAct.INTENT_TO_NEWS_KEY);
					Bundle bundle = new Bundle();
					bundle.putSerializable(HomeAct.SER_KEY,
							star_history_praiseActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.get
									(viewHolder.getAdapterPosition()));
					intent.putExtras(bundle);
					startActivity(intent);
				}

			}

			@Override
			public void onItemLongClick(RecyclerView.ViewHolder viewHolder) {
				if (!title.equals(HomeAct.HISTORY_KEY)) {
					itemTouchHelper.startSwipe(viewHolder);
				}
			}
		});
	}

	protected void recyclerViewEnableMultChoice() {
		star_history_praiseActivityRecyclerViewAdapter.setSelectable(true);
	}

	protected void recyclerViewDisableMultChoice() {
		star_history_praiseActivityRecyclerViewAdapter.setSelectable(false);
	}

	protected void createActionModel() {
		actionMode = startSupportActionMode(new ActionMode.Callback() {
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				if (actionMode == null) {
					actionMode = mode;

					MenuItem menuItem_delete = menu.add(0, 0x428, Menu.NONE, "");
					menuItem_delete.setIcon(R.mipmap.delete_84px);
					menuItem_delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
					menuItem_delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							if (star_history_praiseActivityRecyclerViewAdapter.getItemCount() != 0
									&& star_history_praiseActivityRecyclerViewAdapter
									.mSelectedPositions.size() != 0) {
								if (sqLiteDatabase == null) {
									sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(Star_History_PraiseAct.this
											.getFilesDir().toString() + "/myInfo.db3", null);
								}
								new Thread(new Runnable() {
									@Override
									public void run() {
										if (star_history_praiseActivityRecyclerViewAdapter
												.mSelectedPositions.size() !=
												star_history_praiseActivityRecyclerViewAdapter.getItemCount()) {
											deleteChoiceItem();
										} else {
											deleteAllItem();
										}
									}
								}).start();
							}
							return true;
						}
					});

					MenuItem menuItem_selectAll = menu.add(0, 0x427, Menu.NONE, "");
					menuItem_selectAll.setIcon(R.mipmap.select_all_filled_75px);
					menuItem_selectAll.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
					menuItem_selectAll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							if (star_history_praiseActivityRecyclerViewAdapter.getItemCount() ==
									star_history_praiseActivityRecyclerViewAdapter.mSelectedPositions.size()) {
								recyclerViewAllUnSelected();
							} else {
								recyclerViewAllSelected();
							}
							return true;
						}
					});
					return true;
				}
				return false;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				destoryActionModel();
				recyclerViewDisableMultChoice();
				recyclerViewWithdrawNormalItem();
			}
		});

		actionMode.setTitle("已选: 0/" + star_history_praiseActivityRecyclerViewAdapter
				.getItemCount());

	}

	protected void destoryActionModel() {
		if (actionMode != null) {
			actionMode.finish();
			actionMode = null;
		}
	}

	public void recyclerViewWithdrawNormalItem() {
		/*
		* 方法1
		* 推荐使用这种方法
		* 无刷新列表的情况下更新item的状态
		* */
		LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
		for (int i = 0; i < star_history_praiseActivityRecyclerViewAdapter.getItemCount(); i++) {
			try {
				if(star_history_praiseActivityRecyclerViewAdapter.getItemChecked(i)){
					View view = linearLayoutManager.findViewByPosition(i);
					if (view != null) {
						if (recyclerView.getChildViewHolder(view) != null) {
							Star_History_PraiseActivityRecyclerViewAdapter.RecyclerViewContentViewHolder
									recyclerViewContentViewHolder =
									(Star_History_PraiseActivityRecyclerViewAdapter
											.RecyclerViewContentViewHolder) recyclerView.getChildViewHolder(view);
							recyclerViewContentViewHolder.check_tv.setVisibility(View.GONE);
						}
					}
				}
			} catch (Exception e) {
				Log.i("ZRH", e.toString());
				Log.i("ZRH", e.getMessage());
				Log.i("ZRH", e.getLocalizedMessage());
			}
		}
		star_history_praiseActivityRecyclerViewAdapter.clearCheckedItem();

		/*
		* 方法2
		* 不推荐
		* 因为每次都会调用notifyDataSetChanged()
		* 列表会重新刷新，如果有网络请求，体验会更差
		* */
		//		star_history_praiseActivityRecyclerViewAdapter.notifyDataSetChanged();
		//		int firstVisiableItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
		//				.findFirstVisibleItemPosition();
		//		for (int i = 0; i < star_history_praiseActivityRecyclerViewAdapter.getItemCount(); i++) {
		//			if (i - firstVisiableItem >= 0 && i - firstVisiableItem <= 100) {
		//				try {
		//					if (star_history_praiseActivityRecyclerViewAdapter.getItemChecked
		//							(i - firstVisiableItem)) {
		//						View view = recyclerView.getChildAt(i - firstVisiableItem);
		//						if (view != null) {
		//							if (recyclerView.getChildViewHolder(view) != null) {
		//								Star_History_PraiseActivityRecyclerViewAdapter.RecyclerViewContentViewHolder
		//										recyclerViewContentViewHolder =
		//										(Star_History_PraiseActivityRecyclerViewAdapter
		//												.RecyclerViewContentViewHolder) recyclerView.getChildViewHolder(view);
		//								recyclerViewContentViewHolder.check_tv.setVisibility(View.GONE);
		//							}
		//						}
		//					}
		//				} catch (Exception e) {
		//					Log.i("ZRH", e.toString());
		//					Log.i("ZRH", e.getMessage());
		//					Log.i("ZRH", e.getLocalizedMessage());
		//				}
		//
		//			}
		//		}
	}

	public void recyclerViewAllSelected() {
		/*
		* 方法1
		* 推荐使用
		* 理由同上
		* */
		star_history_praiseActivityRecyclerViewAdapter.notifyDataSetChanged();
		star_history_praiseActivityRecyclerViewAdapter.setAllItemChecked();
		LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
		for (int i = 0; i < star_history_praiseActivityRecyclerViewAdapter.getItemCount(); i++) {
			try {
				View view = linearLayoutManager.findViewByPosition(i);
				if (view != null) {
					if (recyclerView.getChildViewHolder(view) != null) {
						Star_History_PraiseActivityRecyclerViewAdapter.RecyclerViewContentViewHolder
								recyclerViewContentViewHolder =
								(Star_History_PraiseActivityRecyclerViewAdapter
										.RecyclerViewContentViewHolder) recyclerView.getChildViewHolder(view);
						recyclerViewContentViewHolder.check_tv.setVisibility(View.VISIBLE);
					}
				}
			} catch (Exception e) {
				Log.i("ZRH", e.toString());
				Log.i("ZRH", e.getMessage());
				Log.i("ZRH", e.getLocalizedMessage());
			}
		}

		/*
		* 方法2
		* 不推荐
		* 理由同上
		* */
//		star_history_praiseActivityRecyclerViewAdapter.notifyDataSetChanged();
//		int firstVisiableItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
//				.findFirstVisibleItemPosition();
//		for (int i = 0; i < star_history_praiseActivityRecyclerViewAdapter.getItemCount(); i++) {
//			try {
//				if (i - firstVisiableItem >= 0) {
//					View view = recyclerView.getChildAt(i - firstVisiableItem);
//					if (view != null) {
//						if (recyclerView.getChildViewHolder(view) != null) {
//							Star_History_PraiseActivityRecyclerViewAdapter
//									.RecyclerViewContentViewHolder recyclerViewContentViewHolder =
//									(Star_History_PraiseActivityRecyclerViewAdapter
//											.RecyclerViewContentViewHolder) recyclerView.getChildViewHolder(view);
//							recyclerViewContentViewHolder.check_tv.setVisibility(View.VISIBLE);
//						}
//					}
//				}
//			} catch (Exception e) {
//				Log.i("ZRH", e.toString());
//				Log.i("ZRH", e.getMessage());
//				Log.i("ZRH", e.getLocalizedMessage());
//			}
//		}
	}

	public void recyclerViewAllUnSelected() {
		star_history_praiseActivityRecyclerViewAdapter.clearCheckedItem();

	}

	protected void updateStarItemInfoFromResumeOrCreate() {
		sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_STAR_TABLE);
		String queryStar = "SELECT * FROM my_star";
		Cursor cursor = sqLiteDatabase.rawQuery(queryStar, null);
		List<ZhiHuNewsItemInfo> tempList = new ArrayList<>();
		if (cursor != null) {
			if (cursor.moveToLast()) {
				do {
					ZhiHuNewsItemInfo zhiHuNewsItemInfo = new ZhiHuNewsItemInfo();
					zhiHuNewsItemInfo.id = Integer.parseInt(cursor.getString(1));
					zhiHuNewsItemInfo.images.add(0, cursor.getString(2));
					zhiHuNewsItemInfo.title = cursor.getString(3);

					tempList.add(zhiHuNewsItemInfo);
				} while (cursor.moveToPrevious());
			}
		}
		cursor.close();
		if (hasPaused) {
			checkId = -1;
			if ((tempList.size() == 0) && (zhiHuNewsItemInfoList.size() != 0)) {
				checkId = 0;
			} else {
				for (int i = 0; i < tempList.size(); i++) {
					if (zhiHuNewsItemInfoList.get(i).id != tempList.get(i).id) {
						checkId = i;
						break;
					}
				}
				if (checkId == -1 && (tempList.size() != zhiHuNewsItemInfoList.size())) {
					checkId = tempList.size();
				}
			}
		} else {
			zhiHuNewsItemInfoList.clear();
			zhiHuNewsItemInfoList.addAll(tempList);
		}

		loadZhiHuNewsItemHandler.sendEmptyMessage(0x643);
	}

	protected void updatePraiseItemInfoFromResumeOrCreate() {
		sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_PRAISE_TABLE);
		String queryPraise = "SELECT * FROM my_praise";
		Cursor cursor = sqLiteDatabase.rawQuery(queryPraise, null);
		List<ZhiHuNewsItemInfo> tempList = new ArrayList<>();
		if (cursor != null) {
			if (cursor.moveToLast()) {
				do {
					ZhiHuNewsItemInfo zhiHuNewsItemInfo = new ZhiHuNewsItemInfo();
					zhiHuNewsItemInfo.id = Integer.parseInt(cursor.getString(1));
					zhiHuNewsItemInfo.images.add(0, cursor.getString(2));
					zhiHuNewsItemInfo.title = cursor.getString(3);

					tempList.add(zhiHuNewsItemInfo);
				} while (cursor.moveToPrevious());
			}
		}
		cursor.close();
		if (hasPaused) {
			try {
				checkId = -1;
				if ((tempList.size() == 0) && (zhiHuNewsItemInfoList.size() != 0)) {
					checkId = 0;
				} else {
					for (int i = 0; i < tempList.size(); i++) {
						if (zhiHuNewsItemInfoList.get(i).id != tempList.get(i).id) {
							checkId = i;
							break;
						}
					}
					if (checkId == -1 && (tempList.size() != zhiHuNewsItemInfoList.size())) {
						checkId = tempList.size();
					}
				}
			} catch (Exception e) {
				Log.i("ZRH in update", e.getStackTrace().toString());
				Log.i("ZRH in update", e.getMessage());
				Log.i("ZRH in update", e.toString());
			}

		} else {
			zhiHuNewsItemInfoList.clear();
			zhiHuNewsItemInfoList.addAll(tempList);
		}

		loadZhiHuNewsItemHandler.sendEmptyMessage(0x671);

	}

	protected void updateHistoryItemInfoFromResumeOrCreate() {
		sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_HISTORY_TABLE);
		String queryHistory = "SELECT * FROM my_history";
		Cursor cursor = sqLiteDatabase.rawQuery(queryHistory, null);
		List<ZhiHuNewsItemInfo> tempList = new ArrayList<>();
		if (cursor != null) {
			if (cursor.moveToLast()) {
				do {
					ZhiHuNewsItemInfo zhiHuNewsItemInfo = new ZhiHuNewsItemInfo();
					zhiHuNewsItemInfo.id = Integer.parseInt(cursor.getString(1));
					zhiHuNewsItemInfo.images.add(0, cursor.getString(2));
					zhiHuNewsItemInfo.title = cursor.getString(3);

					tempList.add(zhiHuNewsItemInfo);
				} while (cursor.moveToPrevious());
			}
		}
		cursor.close();
		if (hasPaused) {
			checkId = -1;
			if (zhiHuNewsItemInfoList.get(0).id != tempList.get(0).id) {
				for (int i = 0; i < zhiHuNewsItemInfoList.size(); i++) {
					if (zhiHuNewsItemInfoList.get(i).id == tempList.get(0).id) {
						checkId = i;
						break;
					}
				}
			}
		} else {
			zhiHuNewsItemInfoList.clear();
			zhiHuNewsItemInfoList.addAll(tempList);
		}


		loadZhiHuNewsItemHandler.sendEmptyMessage(0x981);

	}

	protected void deleteChoiceItem() {
		//		sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_STAR_TABLE);
		//		sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_PRAISE_TABLE);
		SparseBooleanArray sparseBooleanArray = star_history_praiseActivityRecyclerViewAdapter
				.mSelectedPositions;
		for (int i = 0; i < sparseBooleanArray.size(); i++) {
			int id = star_history_praiseActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.get
					(sparseBooleanArray.keyAt(i)).id;
			if (title.equals(HomeAct.STAR_KEY)) {
				sqLiteDatabase.delete("my_star", "ItemId like ?", new String[]{String.valueOf(id)});
			} else {
				sqLiteDatabase.delete("my_praise", "ItemId like ?", new String[]{String.valueOf(id)});
			}
		}
		if (star_history_praiseActivityRecyclerViewAdapter.mSelectedPositions.size() > 0) {
			List<Integer> list = new ArrayList<>();
			for (int i = 0; i < star_history_praiseActivityRecyclerViewAdapter.mSelectedPositions.size(); i++) {
				//				star_history_praiseActivityRecyclerViewAdapter
				//						.removeSelectedItem
				//								(star_history_praiseActivityRecyclerViewAdapter.mSelectedPositions.keyAt(i));
				int tempPosition = star_history_praiseActivityRecyclerViewAdapter
						.mSelectedPositions.keyAt(i);
				if (i == 0) {
					list.add(tempPosition);
				} else {
					for (int j = 0; j < list.size(); j++) {
						if (tempPosition > list.get(j)) {
							list.add(j, tempPosition);
							break;
						}
						if (j == list.size()) {
							list.add(tempPosition);
						}
					}
				}
			}
			star_history_praiseActivityRecyclerViewAdapter.clearCheckedItem();
			for (int i = 0; i < list.size(); i++) {
				star_history_praiseActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.remove
						(list.get(i).intValue());
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("position", list.get(i));
				msg.setData(bundle);
				msg.what = 0x888;
				loadZhiHuNewsItemHandler.sendMessage(msg);
			}
		}

	}

	protected void deleteAllItem() {
		if (title.equals(HomeAct.STAR_KEY)) {
			sqLiteDatabase.delete("my_star", null, null);
		} else {
			sqLiteDatabase.delete("my_praise", null, null);
		}
		int deleteCount = star_history_praiseActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.size();
		//		Log.i("ZRH", "s: " + star_history_praiseActivityRecyclerViewAdapter.mSelectedPositions.size());
		//		Log.i("ZRH", "z: " + star_history_praiseActivityRecyclerViewAdapter.getItemCount());
		star_history_praiseActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.clear();
		star_history_praiseActivityRecyclerViewAdapter.clearCheckedItem();
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("deleteCount", deleteCount);
		msg.setData(bundle);
		msg.what = 0x999;
		loadZhiHuNewsItemHandler.sendMessage(msg);
	}

	/*
	* 静态内部类-获取资源后的处理
	* */
	static class loadZhiHuNewsItemHandler extends Handler {

		public WeakReference<Star_History_PraiseAct> star_history_praiseActivityWeakReference;
		public Star_History_PraiseAct star_history_praiseAct;

		public loadZhiHuNewsItemHandler(Star_History_PraiseAct star_history_praiseAct) {
			this.star_history_praiseActivityWeakReference = new WeakReference<>
					(star_history_praiseAct);
			this.star_history_praiseAct = this.star_history_praiseActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0x643:
				case 0x671:
					if (star_history_praiseAct.hasPaused) {
						/*
						* 恢复页面
						* */
						if (star_history_praiseAct.checkId != -1) {
							star_history_praiseAct.zhiHuNewsItemInfoList.remove(star_history_praiseAct.checkId);
							star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
									.notifyItemRemoved(star_history_praiseAct.checkId);
						}

						/*
						* 此方法与上两行相同
						* */
						//				star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
						//						.onItemDismiss(star_history_praiseAct.checkId);
					} else {
						/*
						* 初次打开页面
						* */
						star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
								.setZhiHuNewsItemInfoList(star_history_praiseAct.zhiHuNewsItemInfoList);
						star_history_praiseAct.recyclerView.setAdapter(star_history_praiseAct
								.star_history_praiseActivityRecyclerViewAdapter);
					}
					//			star_history_praiseAct.swipeRefreshLayout.setRefreshing(false);
					break;
				case 0x981:
					if (star_history_praiseAct.hasPaused) {
						if (star_history_praiseAct.checkId != -1) {
							star_history_praiseAct.zhiHuNewsItemInfoList.add(0, star_history_praiseAct
									.zhiHuNewsItemInfoList.get(star_history_praiseAct.checkId));
							star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
									.notifyItemInserted(0);
							star_history_praiseAct.zhiHuNewsItemInfoList.remove
									(star_history_praiseAct.checkId + 1);
							star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
									.notifyItemRemoved(star_history_praiseAct.checkId + 1);
						}
					} else {
						star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
								.setZhiHuNewsItemInfoList(star_history_praiseAct.zhiHuNewsItemInfoList);
						star_history_praiseAct.recyclerView.setAdapter(star_history_praiseAct
								.star_history_praiseActivityRecyclerViewAdapter);
					}

					break;
				case 0x888:
					star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
							.notifyItemRemoved(msg.getData().getInt("position"));
					star_history_praiseAct.actionMode.setTitle("已选: " + star_history_praiseAct
							.star_history_praiseActivityRecyclerViewAdapter.mSelectedPositions
							.size() + "/" + star_history_praiseAct
							.star_history_praiseActivityRecyclerViewAdapter.getItemCount());
					break;
				case 0x999:
					star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
							.notifyItemRangeRemoved(0, msg.getData().getInt("deleteCount"));
					star_history_praiseAct.actionMode.setTitle("已选: " + star_history_praiseAct
							.star_history_praiseActivityRecyclerViewAdapter.mSelectedPositions
							.size() + "/" + star_history_praiseAct
							.star_history_praiseActivityRecyclerViewAdapter.getItemCount());
					break;
				case 0x1986:

					break;
			}

		}
	}

	/*
	* 静态内部类-从新获取starItem信息
	* 重新加载列表
	* */
	//	static class GetStarItemInfoHandler extends Handler {
	//		public WeakReference<Star_History_PraiseAct> star_history_praiseActivityWeakReference;
	//		public Star_History_PraiseAct star_history_praiseAct;
	//
	//		public GetStarItemInfoHandler(Star_History_PraiseAct star_history_praiseAct) {
	//			this.star_history_praiseActivityWeakReference = new WeakReference<>
	//					(star_history_praiseAct);
	//			this.star_history_praiseAct = this.star_history_praiseActivityWeakReference.get();
	//		}
	//
	//		@Override
	//		public void handleMessage(Message msg) {
	//			switch (msg.what) {
	//				case 0x643:
	//					//					if(star_history_praiseAct.zhiHuNewsItemInfoList.size()>0){
	//					//						star_history_praiseAct.zhiHuNewsItemInfoList.clear();
	//					//					}
	//					//					String queryStar = "SELECT * FROM my_star";
	//					//					Cursor cursor = star_history_praiseAct.sqLiteDatabase.rawQuery(queryStar, null);
	//					//					if (cursor != null) {
	//					//						if (cursor.moveToFirst()) {
	//					//							do {
	//					//								ZhiHuNewsItemInfo zhiHuNewsItemInfo = new ZhiHuNewsItemInfo();
	//					//								zhiHuNewsItemInfo.id = Integer.parseInt(cursor.getString(1));
	//					//								zhiHuNewsItemInfo.images.add(0, cursor.getString(2));
	//					//								zhiHuNewsItemInfo.title = cursor.getString(3);
	//					//
	//					//								star_history_praiseAct.zhiHuNewsItemInfoList.add(zhiHuNewsItemInfo);
	//					//							} while (cursor.moveToNext());
	//					//						}
	//					//					}
	//					//					cursor.close();
	//					//					star_history_praiseAct.starZhiHuNewsItemHandler.sendEmptyMessage(0x123);
	//
	//					break;
	//				case 0x671:
	//					break;
	//				case 0x981:
	//					break;
	//			}
	//		}
	//	}
}
