package com.rainnka.ZHNews.Activity;

import android.app.ProgressDialog;
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
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rainnka.ZHNews.Activity.Base.SwipeBackAty;
import com.rainnka.ZHNews.Adapter.Star_History_PraiseActivityRecyclerViewAdapter;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemInfo;
import com.rainnka.ZHNews.Callback_Listener.SimpleItemTouchHelperCallback;
import com.rainnka.ZHNews.Callback_Listener.onSHPActRecyclerItemClickListener;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.ConstantUtility;
import com.rainnka.ZHNews.Utility.LengthConverterUtility;
import com.rainnka.ZHNews.Utility.SQLiteCreateTableHelper;
import com.rainnka.ZHNews.Utility.SnackbarUtility;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by rainnka on 2016/5/21 14:10
 * Project name is ZHKUNews
 */
public class Star_History_PraiseAty extends SwipeBackAty {

	public CoordinatorLayout coordinatorLayout;
	protected AppBarLayout appBarLayout;
	protected Toolbar toolbar;
	protected RecyclerView recyclerView;
	protected SwipeRefreshLayout swipeRefreshLayout;
	protected ImageView imageView_MultChoice;
	protected ImageView imageView_Search;
	protected TextView textView_Clear;
	protected SearchView searchView;

	public ProgressDialog progressDialog;

	public ActionMode actionMode;

	public ActionMode searchActionMode = null;

	public LinearLayoutManager linearLayoutManager;

	public Star_History_PraiseActivityRecyclerViewAdapter
			star_history_praiseActivityRecyclerViewAdapter;

	public String title = "";

	public List<ZhiHuNewsItemInfo> zhiHuNewsItemInfoList = new ArrayList<>();
	public List<ZhiHuNewsItemInfo> zhiHuNewsItemInfoList_backup = new ArrayList<>();

	public Gson gson;

	public OkHttpClient okHttpClient;

	public loadZhiHuNewsItemHandler loadZhiHuNewsItemHandler;

	public SQLiteDatabase sqLiteDatabase;

	public boolean hasPaused = false;

	public int checkId;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//			Window window = getWindow();
//			// Translucent status bar
//			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
//					.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			//			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager
//			//					.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}

		setContentView(R.layout.star_history_praise_aty);
		setupWindowAnimations();

		/*
		* 另statusbar悬浮于activity上面
		* */
		setFullScreenLayout();

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
		initToolbarSetting();

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
		* 搜索按钮点击事件
		* */
		creayeSearchImageViewActionMode();

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
		if (ConstantUtility.userIsLogin) {
			if (hasPaused) {
				switch (title) {
					case ConstantUtility.STAR_KEY:
						if (sqLiteDatabase == null) {
							sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase
									(BaseApplication.getDATABASE_PATH() + "/myInfo" +
											".db3", null);
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
								updateStarItemInfoFromResumeOrCreate();

							}
						}).start();
						break;
					case ConstantUtility.PRAISE_KEY:
						try {
							if (sqLiteDatabase == null) {
								sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase
										(BaseApplication.getDATABASE_PATH() + "/myInfo" +
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
					case ConstantUtility.HISTORY_KEY:
						try {
							if (sqLiteDatabase == null) {
								sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase
										(BaseApplication.getDATABASE_PATH() + "/myInfo" +
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
		super.onDestroy();
		if (sqLiteDatabase.isOpen()) {
			sqLiteDatabase.close();
		}
		loadZhiHuNewsItemHandler.removeCallbacksAndMessages(null);
		loadZhiHuNewsItemHandler = null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
		}
		return true;
	}

	/*
	* 初试化页面
	* 从数据库获取信息
	* */
	private void initMyItemInfoFromDatabase() {
		if (ConstantUtility.userIsLogin) {
			if (title.equals(ConstantUtility.STAR_KEY)) {
				if (sqLiteDatabase == null) {
					sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(BaseApplication
							.getDATABASE_PATH() + "/myInfo.db3", null);
					new Thread(new Runnable() {
						@Override
						public void run() {
							updateStarItemInfoFromResumeOrCreate();
						}
					}).start();
				}
			}
			if (title.equals(ConstantUtility.PRAISE_KEY)) {
				if (sqLiteDatabase == null) {
					sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(BaseApplication.getDATABASE_PATH() +
							"/myInfo.db3", null);
					new Thread(new Runnable() {
						@Override
						public void run() {
							updatePraiseItemInfoFromResumeOrCreate();
						}
					}).start();
				}
			}
			if (title.equals(ConstantUtility.HISTORY_KEY)) {
				if (sqLiteDatabase == null) {
					sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(BaseApplication.getDATABASE_PATH() +
							"/myInfo.db3", null);
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						updateHistoryItemInfoFromResumeOrCreate();
					}
				}).start();
			}
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
		imageView_Search = (ImageView) findViewById(R.id
				.star_history_praiseActivity_Content_main_Search_ImageView);
		textView_Clear = (TextView) findViewById(R.id
				.star_history_praiseActivity_Content_main_clear_TextView);
		//		searchView = (SearchView) findViewById(R.id
		//				.star_history_praiseActivity_Content_main_SearchView);
	}

	private void initToolbarSetting() {
		switch (title) {
			case ConstantUtility.STAR_KEY:
				toolbar.setTitle("收藏");
				break;
			case ConstantUtility.HISTORY_KEY:
				toolbar.setTitle("浏览历史");
				break;
			case ConstantUtility.PRAISE_KEY:
				toolbar.setTitle("点赞");
				break;
		}
		toolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
		layoutParams.setMargins(0, LengthConverterUtility.dip2px(BaseApplication
				.getBaseApplicationContext(), 24), 0, 0);
		toolbar.setLayoutParams(layoutParams);
	}

	private void initMultChoiceClickListener() {
		if (ConstantUtility.userIsLogin) {
			if (title.equals(ConstantUtility.HISTORY_KEY)) {
				imageView_MultChoice.setVisibility(View.GONE);
				textView_Clear.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
								"确认删除", Snackbar.LENGTH_LONG);
						snackbar.setAction("删除", new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										deleteAllItem();
									}
								}).start();
							}
						});
						snackbar.show();

						//						final PopupWindow popupWindow = new PopupWindow(getLayoutInflater().inflate(R
						//								.layout.popup_delete_confirm,null), LengthTransitionUtility
						//								.dip2px(BaseApplication.getBaseApplicationContext(),120),
						//								LengthTransitionUtility
						//								.dip2px(BaseApplication.getBaseApplicationContext(),40));
						//						TextView textView = (TextView) popupWindow.getContentView().findViewById(R.id
						//								.confirm_delete);
						//						popupWindow.showAsDropDown(v);
						//						textView.setOnClickListener(new View.OnClickListener() {
						//							@Override
						//							public void onClick(View view) {
						//								new Thread(new Runnable() {
						//									@Override
						//									public void run() {
						//										deleteAllItem();
						//									}
						//								}).start();
						//								popupWindow.dismiss();
						//							}
						//						});
					}
				});
			} else {
				textView_Clear.setVisibility(View.GONE);
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
	}


	private void initRecyclerViewAdapter() {
		star_history_praiseActivityRecyclerViewAdapter = new
				Star_History_PraiseActivityRecyclerViewAdapter(Star_History_PraiseAty.this);
	}

	private void intiLinearLayoutManager() {
		linearLayoutManager = new LinearLayoutManager(Star_History_PraiseAty.this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
	}

	public String getInentInfo() {
		return getIntent().getStringExtra(ConstantUtility.INTENT_STRING_DATA_KEY);
	}

	private void initHandler() {
		loadZhiHuNewsItemHandler = new loadZhiHuNewsItemHandler(Star_History_PraiseAty.this);
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
		* 设置viewholder的缓存
		* */
		recyclerView.setItemViewCacheSize(0);
		//		recyclerView.setRecycledViewPool();

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
					sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(BaseApplication.getDATABASE_PATH() +
									"/myInfo.db3",
							null);
				}
				sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_HISTORY_TABLE);
				sqLiteDatabase.delete("my_history", "ItemId like ?", new
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
						star_history_praiseActivityRecyclerViewAdapter.removeSelectedItem
								(viewHolder.getAdapterPosition());
						star_history_praiseActivityRecyclerViewAdapter.setSignVisibility
								(viewHolder, View.GONE);
					} else {
						star_history_praiseActivityRecyclerViewAdapter.setItemChecked(viewHolder
								.getAdapterPosition(), true);
						star_history_praiseActivityRecyclerViewAdapter.setSignVisibility
								(viewHolder, View.VISIBLE);
					}
					setActionModeTitle();

				} else {
					Intent intent = new Intent();
					intent.setAction(ConstantUtility.INTENT_TO_NEWS_KEY);
					Bundle bundle = new Bundle();
					bundle.putSerializable(ConstantUtility.SER_KEY,
							star_history_praiseActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.get
									(viewHolder.getAdapterPosition()));
					intent.putExtras(bundle);
					startActivity(intent);
				}

			}

			@Override
			public void onItemLongClick(RecyclerView.ViewHolder viewHolder) {
				if (!title.equals(ConstantUtility.HISTORY_KEY)) {
					if (!star_history_praiseActivityRecyclerViewAdapter.mIsSelected &&
							searchActionMode == null) {
						try {
							itemTouchHelper.startSwipe(viewHolder);
						} catch (Exception e) {
							Log.i("ZRH", e.toString());
						}
					} else if (star_history_praiseActivityRecyclerViewAdapter.mIsSelected) {
						if (!star_history_praiseActivityRecyclerViewAdapter.getItemChecked(viewHolder
								.getAdapterPosition())) {
							if (star_history_praiseActivityRecyclerViewAdapter.mSelectedPositions
									.size() != 0) {
								LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
								int lastPositionBeChecked =
										star_history_praiseActivityRecyclerViewAdapter
												.mSelectedPositions.keyAt
												(star_history_praiseActivityRecyclerViewAdapter
														.mSelectedPositions.size() - 1);
								int position = viewHolder.getAdapterPosition();
								if (position > lastPositionBeChecked) {
									for (int i = lastPositionBeChecked + 1; i <= position; i++) {
										if (manager.findViewByPosition(i) != null) {
											View view = manager.findViewByPosition(i);
											if (recyclerView.getChildViewHolder(view) != null) {
												Star_History_PraiseActivityRecyclerViewAdapter.RecyclerViewContentViewHolder
														recyclerViewContentViewHolder =
														(Star_History_PraiseActivityRecyclerViewAdapter
																.RecyclerViewContentViewHolder) recyclerView.getChildViewHolder(view);
												star_history_praiseActivityRecyclerViewAdapter
														.setSignVisibility
																(recyclerViewContentViewHolder, View.VISIBLE);

											}
										}
									}
								} else {
									for (int i = position; i < lastPositionBeChecked; i++) {
										if (manager.findViewByPosition(i) != null) {
											View view = manager.findViewByPosition(i);
											if (recyclerView.getChildViewHolder(view) != null) {
												Star_History_PraiseActivityRecyclerViewAdapter.RecyclerViewContentViewHolder
														recyclerViewContentViewHolder =
														(Star_History_PraiseActivityRecyclerViewAdapter
																.RecyclerViewContentViewHolder) recyclerView.getChildViewHolder(view);
												star_history_praiseActivityRecyclerViewAdapter
														.setSignVisibility
																(recyclerViewContentViewHolder, View.VISIBLE);

											}
										}
									}
								}
							} else {
								star_history_praiseActivityRecyclerViewAdapter.setItemChecked
										(viewHolder.getAdapterPosition(), true);
								star_history_praiseActivityRecyclerViewAdapter.setSignVisibility
										(viewHolder, View.VISIBLE);
							}
							star_history_praiseActivityRecyclerViewAdapter.setMultiSelectable
									(viewHolder);
							setActionModeTitle();
						}
					}
				}
			}
		});
	}

	private void creayeSearchImageViewActionMode() {
		imageView_Search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				zhiHuNewsItemInfoList_backup.clear();
				zhiHuNewsItemInfoList_backup.addAll(zhiHuNewsItemInfoList);
				zhiHuNewsItemInfoList.clear();
				//				star_history_praiseActivityRecyclerViewAdapter.notifyItemRangeRemoved(0,
				//						zhiHuNewsItemInfoList_backup.size() - 1);
				star_history_praiseActivityRecyclerViewAdapter.notifyDataSetChanged();
				searchActionMode = startSupportActionMode(new ActionMode.Callback() {
					@Override
					public boolean onCreateActionMode(ActionMode mode, Menu menu) {
						if (searchActionMode == null) {
							searchActionMode = mode;
							//							MenuItem search = menu.add(0, 0x257, Menu.NONE, "");
							//							search.setActionView()
							//							search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
							getMenuInflater().inflate(R.menu.shp_searchactionmode_menu, menu);
							searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem
									(R.id.search));
							searchView.setIconifiedByDefault(false);
							searchView.setSubmitButtonEnabled(true);
							searchView.onActionViewExpanded();
							searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
								@Override
								public boolean onQueryTextSubmit(final String query) {
									new Thread(new Runnable() {
										@Override
										public void run() {
											searchItemByQuery(query);
										}
									}).start();
									showProgressDialog();
									return true;
								}

								@Override
								public boolean onQueryTextChange(String newText) {
									new Thread(new Runnable() {
										@Override
										public void run() {
											if (zhiHuNewsItemInfoList.size() > 0) {
												zhiHuNewsItemInfoList.clear();
												loadZhiHuNewsItemHandler.sendEmptyMessage
														(ConstantUtility.SEARCHITEM_FINISHED);
											}
										}
									}).start();
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
						if (searchActionMode != null) {
							searchActionMode.finish();
							searchActionMode = null;
						}
						zhiHuNewsItemInfoList.clear();
						zhiHuNewsItemInfoList.addAll(zhiHuNewsItemInfoList_backup);
						star_history_praiseActivityRecyclerViewAdapter.notifyDataSetChanged();
						//						star_history_praiseActivityRecyclerViewAdapter.notifyItemRangeInserted(0,
						//								zhiHuNewsItemInfoList.size() - 1);
						zhiHuNewsItemInfoList_backup.clear();
					}
				});
			}
		});
	}

	public void SHPAdapterDataChanged() {
		star_history_praiseActivityRecyclerViewAdapter.notifyDataSetChanged();
		dismissProgress();
	}

	protected void searchItemByQuery(String query) {
		for (int i = 0; i < zhiHuNewsItemInfoList_backup.size(); i++) {
			if (zhiHuNewsItemInfoList_backup.get(i).title.contains(query)) {
				zhiHuNewsItemInfoList.add(zhiHuNewsItemInfoList_backup.get(i));
			}
		}
		loadZhiHuNewsItemHandler.sendEmptyMessage(ConstantUtility.SEARCHITEM_FINISHED);
	}

	protected void showProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("waiting");
		progressDialog.show();
	}

	protected void dismissProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
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
					menuItem_delete.setIcon(R.mipmap.delete_85px_white);
					menuItem_delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
					menuItem_delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
									"确认删除", Snackbar.LENGTH_LONG);
							snackbar.setAction("删除", new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									if (star_history_praiseActivityRecyclerViewAdapter.getItemCount() != 0
											&& star_history_praiseActivityRecyclerViewAdapter
											.mSelectedPositions.size() != 0) {
										if (sqLiteDatabase == null) {
											sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase
													(BaseApplication.getDATABASE_PATH() + "/myInfo.db3", null);
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
								}
							});
							snackbar.show();
							return true;
						}
					});

					MenuItem menuItem_selectAll = menu.add(0, 0x427, Menu.NONE, "");
					menuItem_selectAll.setIcon(R.mipmap.select_all_72px_white);
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
		setActionModeTitle();
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
				if (star_history_praiseActivityRecyclerViewAdapter.getItemChecked(i)) {
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
		setActionModeTitle();

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
		LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
		for (int i = 0; i < star_history_praiseActivityRecyclerViewAdapter.getItemCount(); i++) {
			try {
				View view = linearLayoutManager.findViewByPosition(i);
				if (view != null) {
					if (recyclerView.getChildViewHolder(view) != null) {
						Star_History_PraiseActivityRecyclerViewAdapter.RecyclerViewContentViewHolder
								recyclerViewContentViewHolder = (Star_History_PraiseActivityRecyclerViewAdapter
								.RecyclerViewContentViewHolder) recyclerView.getChildViewHolder(view);
						recyclerViewContentViewHolder.check_tv.setVisibility(View.GONE);
					}
				}
			} catch (Exception e) {
				Log.i("ZRH", e.toString());
				Log.i("ZRH", e.getMessage());
				Log.i("ZRH", e.getLocalizedMessage());
			}
		}
		star_history_praiseActivityRecyclerViewAdapter.clearCheckedItem();
		setActionModeTitle();
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

		if (loadZhiHuNewsItemHandler != null) {
			loadZhiHuNewsItemHandler.sendEmptyMessage(0x643);
		}
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

		if (loadZhiHuNewsItemHandler != null) {
			loadZhiHuNewsItemHandler.sendEmptyMessage(0x671);
		}

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


		if (loadZhiHuNewsItemHandler != null) {
			loadZhiHuNewsItemHandler.sendEmptyMessage(0x981);
		}

	}

	protected void deleteChoiceItem() {
		SparseBooleanArray sparseBooleanArray = star_history_praiseActivityRecyclerViewAdapter
				.mSelectedPositions;
		for (int i = 0; i < sparseBooleanArray.size(); i++) {
			int id = star_history_praiseActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.get
					(sparseBooleanArray.keyAt(i)).id;
			if (title.equals(ConstantUtility.STAR_KEY)) {
				sqLiteDatabase.delete("my_star", "ItemId like ?", new String[]{String.valueOf(id)});
			} else {
				sqLiteDatabase.delete("my_praise", "ItemId like ?", new String[]{String.valueOf(id)});
			}
		}
		if (star_history_praiseActivityRecyclerViewAdapter.mSelectedPositions.size() > 0) {
			List<Integer> list = new ArrayList<>();
			for (int i = 0; i < star_history_praiseActivityRecyclerViewAdapter.mSelectedPositions.size(); i++) {
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
				if (loadZhiHuNewsItemHandler != null) {
					loadZhiHuNewsItemHandler.sendMessage(msg);
				}
			}
		}

	}

	protected void deleteAllItem() {
		if (title.equals(ConstantUtility.STAR_KEY)) {
			sqLiteDatabase.delete("my_star", null, null);
		} else if (title.equals(ConstantUtility.PRAISE_KEY)) {
			sqLiteDatabase.delete("my_praise", null, null);
		} else {
			sqLiteDatabase.delete("my_history", null, null);
		}
		int deleteCount = star_history_praiseActivityRecyclerViewAdapter.getItemCount();
		star_history_praiseActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.clear();
		star_history_praiseActivityRecyclerViewAdapter.clearCheckedItem();
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("deleteCount", deleteCount);
		msg.setData(bundle);
		if (!title.equals(ConstantUtility.HISTORY_KEY)) {
			msg.what = 0x999;
		} else {
			msg.what = 0x777;
		}
		if (loadZhiHuNewsItemHandler != null) {
			loadZhiHuNewsItemHandler.sendMessage(msg);
		}
	}

	protected void setActionModeTitle() {
		actionMode.setTitle("已选: " + star_history_praiseActivityRecyclerViewAdapter
				.mSelectedPositions.size() + "/" + star_history_praiseActivityRecyclerViewAdapter
				.getItemCount());
	}

	/*
	* 静态内部类-获取资源后的处理
	* */
	static class loadZhiHuNewsItemHandler extends Handler {

		public WeakReference<Star_History_PraiseAty> star_history_praiseActivityWeakReference;
		public Star_History_PraiseAty star_history_praiseAct;

		public loadZhiHuNewsItemHandler(Star_History_PraiseAty star_history_praiseAct) {
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
					star_history_praiseAct.setActionModeTitle();
					break;
				case 0x999:
					star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
							.notifyItemRangeRemoved(0, msg.getData().getInt("deleteCount"));
					star_history_praiseAct.setActionModeTitle();
					break;
				case 0x777:
					star_history_praiseAct.star_history_praiseActivityRecyclerViewAdapter
							.notifyItemRangeRemoved(0, msg.getData().getInt("deleteCount"));
					break;
				case ConstantUtility.SEARCHITEM_FINISHED:
					star_history_praiseAct.SHPAdapterDataChanged();
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
