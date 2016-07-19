package com.rainnka.zhkunews.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.rainnka.zhkunews.Adapter.HomeActivityRecyclerViewAdapter;
import com.rainnka.zhkunews.Adapter.HomeActivityViewPagerAdapter;
import com.rainnka.zhkunews.Animation_Transformer.DepthPageTransformer;
import com.rainnka.zhkunews.Bean.ZhiHuNewsItemInfo;
import com.rainnka.zhkunews.Bean.ZhiHuNewsLatestItemInfo;
import com.rainnka.zhkunews.Callback_Listener.onHActRecyclerItemClickListener;
import com.rainnka.zhkunews.CustomView.HomeActivityViewPagerIndicator;
import com.rainnka.zhkunews.R;
import com.rainnka.zhkunews.Utility.LengthTransitionUtility;
import com.rainnka.zhkunews.Utility.SQLiteCreateTableHelper;
import com.rainnka.zhkunews.Utility.SnackbarUtility;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by rainnka on 2016/5/12 20:45
 * Project name is ZHKUNews
 */
public class HomeAct extends AppCompatActivity implements ViewPager.OnPageChangeListener,
		HomeActivityRecyclerViewAdapter.HomeActivityRecyclerViewAdapterCallback, SwipeRefreshLayout
				.OnRefreshListener, AppBarLayout.OnOffsetChangedListener, NavigationView
				.OnNavigationItemSelectedListener {

	protected SharedPreferences sharedPreferences;

	protected Toolbar toolbar;
	protected DrawerLayout drawerLayout;
	protected NavigationView navigationView;
	protected CoordinatorLayout coordinatorLayout;
	protected AppBarLayout appBarLayout;
	protected CollapsingToolbarLayout collapsingToolbarLayout;
	protected ViewPager viewPager;
	protected RecyclerView recyclerView;
	protected SwipeRefreshLayout swipeRefreshLayout;
	protected HomeActivityViewPagerIndicator homeActivityViewPagerIndicator;
	protected FloatingActionButton floatingActionButton;
	protected FrameLayout frameLayout;
	protected com.getbase.floatingactionbutton.FloatingActionButton floatingActionButton_quickUp;
	protected com.getbase.floatingactionbutton.FloatingActionButton
			floatingActionButton_quickDown;
	protected FloatingActionsMenu floatingActionsMenu;

	protected ImageView profile_iv;
	protected TextView profile_tv;

	public HomeActivityViewPagerAdapter homeActivityViewPagerAdapter;
	public HomeActivityRecyclerViewAdapter homeActivityRecyclerViewAdapter;

	public Handler mhandler;
	public BannerHandler bannerHandler;
	public RecyclerRefreshHandler recyclerRefreshHandler;

	public int viewPagerStatusPosition = 1;
	public final static int BANNER_SCROLL_INTERVAL = 4000;
	public final static int BANNER_SCROLL_KEY = 0x123;

	public final static int RECYCLER_REFRESH_NEW = 0x111111;
	public final static int RECYCLER_REFRESH_NEW_FAILURE = 0x111222;
	public final static int RECYCLER_REFRESH_OLD = 0x222222;
	public final static int RECYCLER_REFRESH_OLD_FAILURE = 0x222333;
	public final static int RECYCLER_REFRESH_LATEST = 0x333333;

	public boolean BACKPRESS_STATUS = false;

	public Runnable mRunnable;

	public Date date;
	public String current_simple_date_format;
	public SimpleDateFormat simpleDateFormat;

	public String current_date_from_zhihu;
	public String current_date_year;
	public String current_date_month;
	public String current_date_day;
	public String refressh_old_date;

	public LinearLayoutManager linearLayoutManager;

	public Gson gson;

	String username = "";
	String password = "";
	String nickname = "";

	public final static String INTENT_TO_NEWS_KEY = "android.intent.action.NewsActivity";
	public final static String INTENT_TO_STAR_HISTORY_PRAISE_KEY = "android.intent.action" +
			".Star_History_Praise";
	public final static String INTENT_TO_LOGIN_KEY = "android.intent.action.Login";
	public final static int ITENT_TO_LOGIN_REQUESTCODE = 0x1234;

	public final static String INTENT_TO_PROFILE_KEY = "android.intent.action.ProfilePage";
	public final static int ITENT_TO_PROFILE_REQUESTCODE = 0x7654;

	public final static String INTENT_STRING_DATA_KEY = "STRING_DATA_KEY";
	public final static String STAR_KEY = "star";
	public final static String HISTORY_KEY = "history";
	public final static String PRAISE_KEY = "praise";

	public static String SER_KEY = "SER";

	public static String ZHIHUAPI_LATEST = "http://news-at.zhihu.com/api/4/news/latest";
	public static String ZHIHUAPI_BEFORE = "http://news.at.zhihu.com/api/4/news/before/";

	private Boolean isLoadBanner = false;

	protected OkHttpClient okHttpClient;

	public ZhiHuNewsLatestItemInfo zhiHuNewsLatestItemInfo;
	public ZhiHuNewsLatestItemInfo zhiHuNewsLatestItemInfo_old;
	public ZhiHuNewsLatestItemInfo zhiHuNewsLatestItemInfo_new;

	public List<ZhiHuNewsItemInfo> zhiHuNewsTopItemInfoList;

	private ConnectivityManager connectivityManager;

	public SQLiteDatabase sqLiteDatabase;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home_act);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			Fade slideTransition = new Fade();
			slideTransition.setDuration(400);
			getWindow().setReenterTransition(slideTransition);
			getWindow().setExitTransition(slideTransition);
		}

		//		ViewStubCompat viewStubCompat = (ViewStubCompat) findViewById(R.id.home_activity_content_viewstub);
		//		viewStubCompat.setVisibility(View.VISIBLE);

		/*
		* 初始化网络连接管理器
		* */
		initConnectivityManager();

		/*
		* 初始化网络连接客户端
		* */
		initOkhttpClient();

		/*
		* 初始化组件
		* */
		initComponent();

		/*
		* 获取已登录的用户
		* */
		initUser();

		/*
		* 用toolBar代替actionBar
		* */
		initToolbar();

		/*
		* 初始化 Handler
		* */
		initHandler();

		/*
		* 初始化 线程
		* */
		initThreadORRunnable();

		/*
		* 初始化Gson
		* */
		initGson();

		/*
		* 初始化日期
		* */
		initDate();

		/*
		* 设置NavigationView Toggle
		* */
		initActionBarDrawerToggle();

		/*
		* 根据SDK版本修改UI
		* */
		changeUIBySDK_VER();

		/*
		* 添加viewPager的切换效果
		* */
		//		addViewPagerTransformer();

		/*
		* 为viewpager 添加 OnPageChangeListener
		* */
		addViewPagerOnPageChangeListener();

		/*
		* viewPager添加触控事件
		* */
		addViewPagerOnTouchListener();

		//		item_layout = getApplicationContext().getResources().getIntArray(R.array
		//				.recyclerview_iteminfo_type);

		/*
		* 准备LinearLayoutManager
		* */
		initLinearLayoutManager();

		/*
		* 实例化homeActivityRecyclerViewAdapter
		* 设置homeActivityRecyclerViewAdapter 相关参数
		* */
		initRecyclerViewAdapter();

		/*
		* 添加recyclerview的clicklistener
		* */
		addRecyclerViewOnItemClickListener();


		/*
		* 首次启动时加载最新的内容
		* */
		initRecyclerViewFirstZhiHuContent();


		/*
		* 设置 swipeRefreshLayout 监听事件
		* */
		initSettingSwipeRefreshLayout();


		/*
		* 设置appbar下的fab按钮的点击事件
		* */
		addFABAnchorInAppBarOnClickListener();

		/*
		* 设置floatingactionbutton的快速向下和快速向上功能
		* */
		addFABInMenuOnClickListener();

		/*
		* appbar监听事件
		* */
		addAppBarOffsetChangedListener();

		/*
		* 添加navigationView menu item的点击事件
		* */
		addNavigationViewItemSelectedListener();

		/*
		* 侧栏头像点击事件
		* */
		initDrawerNavigationProfileOnClickListener();

		/*
		* 设置单个item可见
		* */
		//		navigationView.getMenu().findItem(R.id.drawer_star).setVisible(true);
		/*
		* 设置单个group可见
		* */
		//		navigationView.getMenu().setGroupVisible(R.id.group2, true);


	}


	@Override
	protected void onResume() {
		super.onResume();
		if (isLoadBanner) {
			bannerStartAutoScroll();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		bannerStopAutoScroll();
		if (floatingActionsMenu.isExpanded()) {
			floatingActionsMenu.collapse();
		}
	}

	@Override
	protected void onDestroy() {
		if (sqLiteDatabase != null) {
			sqLiteDatabase.close();
		}
		mhandler.removeCallbacks(mRunnable);
		bannerHandler.removeMessages(BANNER_SCROLL_KEY);
		recyclerRefreshHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//		menu.add(0, 0, Menu.NONE, "").setIcon(R.mipmap.notification_light).setShowAsAction
		//				(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//		menu.add(0, 1, Menu.NONE, "设置");
		//		menu.add(0, 1, Menu.NONE, "关于");
		getMenuInflater().inflate(R.menu.home_act_menu, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ITENT_TO_LOGIN_REQUESTCODE && resultCode == LoginAct.RESULTCODE) {
			profile_tv.setText(sharedPreferences.getString("nickname", ""));
			navigationView.getMenu().setGroupVisible(R.id.group2, true);
			Snackbar.make(coordinatorLayout, "你已经成功登录", Snackbar.LENGTH_SHORT).show();
		}
		if (requestCode == ITENT_TO_PROFILE_REQUESTCODE && resultCode == ProfilePageAct.RESULTCODE_NORMALBACK) {
			String nn = sharedPreferences.getString("nickname", "");
			if (!profile_tv.getText().equals(nn)) {
				profile_tv.setText(nn);
				Snackbar.make(coordinatorLayout, "修改昵称成功", Snackbar.LENGTH_SHORT).show();
			}
		}
		if (requestCode == ITENT_TO_PROFILE_REQUESTCODE && resultCode == ProfilePageAct.RESULTCODE) {
			profile_tv.setText("点击头像登录");
			navigationView.getMenu().setGroupVisible(R.id.group2, false);
			Snackbar.make(coordinatorLayout, "你已经成功退出登录", Snackbar.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}


	private void addRecyclerViewOnItemClickListener() {
		recyclerView.addOnItemTouchListener(new onHActRecyclerItemClickListener(recyclerView) {
			@Override
			public void onItemClickListener(RecyclerView.ViewHolder viewHolder) {
				if (homeActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.get(viewHolder
						.getAdapterPosition()).item_layout == 1) {
					final ZhiHuNewsItemInfo temp_zhiHuNewsItemInfo = homeActivityRecyclerViewAdapter
							.zhiHuNewsItemInfoList.get(viewHolder.getAdapterPosition());
					new Thread(new Runnable() {
						@Override
						public void run() {
							if (sqLiteDatabase == null) {
								sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(HomeAct.this
										.getFilesDir().toString() + "/myInfo.db3", null);
							}
							sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_HISTORY_TABLE);
							sqLiteDatabase.beginTransaction();
							try{
								sqLiteDatabase.beginTransaction();
								sqLiteDatabase.delete("my_history", "ItemId like ?", new
										String[]{String.valueOf(temp_zhiHuNewsItemInfo.id)});
								sqLiteDatabase.setTransactionSuccessful();
							}catch (Exception e){
								Log.i("ZRH", e.getStackTrace().toString());
								Log.i("ZRH", e.getMessage());
								Log.i("ZRH", e.toString());
							}finally {
								sqLiteDatabase.endTransaction();
							}


							ContentValues contentValues = new ContentValues();
							contentValues.put("ItemId", temp_zhiHuNewsItemInfo.id);
							try {
								contentValues.put("ItemImage", temp_zhiHuNewsItemInfo
										.images.get(0));
							} catch (Exception e) {
								contentValues.put("ItemImage", temp_zhiHuNewsItemInfo.image);
							}
							contentValues.put("ItemTitle", temp_zhiHuNewsItemInfo.title);
							try{
								sqLiteDatabase.beginTransaction();
								sqLiteDatabase.insert("my_history", null, contentValues);
								sqLiteDatabase.setTransactionSuccessful();
							}catch (Exception e){
								Log.i("ZRH", e.getStackTrace().toString());
								Log.i("ZRH", e.getMessage());
								Log.i("ZRH", e.toString());
							}finally {
								sqLiteDatabase.endTransaction();
							}
						}
					}).start();

					Intent intent = new Intent();
					intent.setAction(INTENT_TO_NEWS_KEY);
					Bundle bundle = new Bundle();
					bundle.putSerializable(SER_KEY, homeActivityRecyclerViewAdapter
							.zhiHuNewsItemInfoList.get(viewHolder.getAdapterPosition()));
					intent.putExtras(bundle);

					startActivity(intent);

					//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					//					Pair<View, String> pair = new Pair<View, String>
					//							(floatingActionsMenu,
					//									"transition");
					//					ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
					//							.makeSceneTransitionAnimation(HomeActivity.this, pair);
					//					Log.i("ZRH", "startActivity(intent, activityOptionsCompat.toBundle())");
					//					startActivity(intent, activityOptionsCompat.toBundle());
					//				} else {
					//					Log.i("ZRH", "startActivity(intent)");
					//					startActivity(intent);
					//				}
				}
			}

			@Override
			public void onItemLongClickListener(RecyclerView.ViewHolder viewHolder) {

			}
		});
	}

	/*
	* 获取已登录的用户
	* */
	private void initUser() {
		sharedPreferences = getSharedPreferences("up", MODE_PRIVATE);
		if (sharedPreferences.getString("isLogin", " ").equals("Y")) {
			username = sharedPreferences.getString("username", " ");
			password = sharedPreferences.getString("password", " ");
			if (username.equals("admin") && password.equals("root")) {
				nickname = sharedPreferences.getString("nickname", "");
				profile_tv.setText(nickname);
				navigationView.getMenu().setGroupVisible(R.id.group2, true);
				username = "";
				password = "";
				nickname = "";
			}
		}
	}

	/*
	* 添加FAB menu中两个fab的点击事件
	* */
	private void addFABInMenuOnClickListener() {
		floatingActionButton_quickUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (homeActivityRecyclerViewAdapter.getItemCount() != 0) {
					recyclerView.scrollToPosition(0);
					appBarLayout.setExpanded(true);
				}
			}
		});

		floatingActionButton_quickDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (homeActivityRecyclerViewAdapter.getItemCount() != 0) {
					recyclerView.scrollToPosition(homeActivityRecyclerViewAdapter.getItemCount()
							- 1);
					appBarLayout.setExpanded(false, true);
				}
			}
		});
	}

	/*
	* 设置appbar下的fab按钮的点击事件
	* */
	private void addFABAnchorInAppBarOnClickListener() {
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/* nothing */
			}
		});
	}

	/*
	* 添加navigationView menu item的点击事件
	* */
	private void addNavigationViewItemSelectedListener() {
		navigationView.setNavigationItemSelectedListener(this);
	}

	/*
	* appbar监听事件
	* */
	private void addAppBarOffsetChangedListener() {
		appBarLayout.addOnOffsetChangedListener(this);
	}

	/*
	* 实例化homeActivityRecyclerViewAdapter
	* 设置homeActivityRecyclerViewAdapter 相关参数
	* */
	private void initRecyclerViewAdapter() {
		homeActivityRecyclerViewAdapter = new HomeActivityRecyclerViewAdapter(this);
		homeActivityRecyclerViewAdapter.setHomeActivityRecyclerViewAdapterCallback(this);
	}

	/*
	* 准备LinearLayoutManager
	* */
	private void initLinearLayoutManager() {
		linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
	}


	/*
	* 侧栏头像点击事件
	* */
	private void initDrawerNavigationProfileOnClickListener() {
		profile_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sharedPreferences.getString("isLogin", "N").equals("N")) {
					//					Log.i("ZRH","isLogin"+sharedPreferences.getString("isLogin",""));
					drawerLayout.closeDrawer(navigationView);
					Intent intent_to_login = new Intent();
					intent_to_login.setAction(INTENT_TO_LOGIN_KEY);
					startActivityForResult(intent_to_login, ITENT_TO_LOGIN_REQUESTCODE);
				} else {
					drawerLayout.closeDrawer(navigationView);
					Intent intent_to_profile = new Intent();
					intent_to_profile.setAction(INTENT_TO_PROFILE_KEY);
					startActivityForResult(intent_to_profile, ITENT_TO_PROFILE_REQUESTCODE);
				}
			}
		});
	}

	/*
	* 设置 swipeRefreshLayout 监听事件
	* */
	private void initSettingSwipeRefreshLayout() {
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setDistanceToTriggerSync(800);
		swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
	}

	/*
	* 添加viewPager的切换动画
	* */
	private void addViewPagerTransformer() {
		viewPager.setPageTransformer(false, new DepthPageTransformer());
		//		viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
	}

	/*
	* 为viewpager 添加 OnPageChangeListener
	* */
	private void addViewPagerOnPageChangeListener() {
		viewPager.addOnPageChangeListener(this);
	}

	/*
	* viewPager添加触控事件
	* */
	private void addViewPagerOnTouchListener() {
		viewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						bannerHandler.removeMessages(BANNER_SCROLL_KEY);
						break;
					case MotionEvent.ACTION_UP:
						bannerStartAutoScroll();
						break;
				}
				return false;
			}
		});
	}

	/*
	* 初始化网络连接客户端
	* */
	private void initOkhttpClient() {
		okHttpClient = new OkHttpClient();
		okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
	}

	/*
	* 获取网络连接管理器
	* */
	private void initConnectivityManager() {
		connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService
				(CONNECTIVITY_SERVICE);
	}

	/*
	* 获取网络连接相关状况
	* */
	private boolean getConnectivityCondition() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Network[] networks = connectivityManager.getAllNetworks();
			if (networks != null && networks.length > 0) {
				for (int i = 0; i < networks.length; i++) {
					//					Log.i("ZRH", "network status: " + connectivityManager.getNetworkInfo
					//							(networks[i]).getState());
					//					Log.i("ZRH", "network tyoe: " + connectivityManager.getNetworkInfo(networks[i]
					//					).getType());
					if (connectivityManager.getNetworkInfo(networks[i]).getState() == NetworkInfo
							.State.CONNECTED) {
						return true;
					}
				}
			} else {
				Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
						"咦，没有可用的网络吔", 3000);
				snackbar.show();
				//				Log.i("ZRH", "无可用网络");
				return false;
			}
		} else {
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			if (networkInfos != null && networkInfos.length > 0) {
				for (int i = 0; i < networkInfos.length; i++) {
					//					Log.i("ZRH", "network status: " + networkInfos[i].getState());
					//					Log.i("ZRH", "network type: " + networkInfos[i].getType());
					if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			} else {
				Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
						"咦，没有可用的网络吔", 3000);
				snackbar.show();
				return false;
			}
		}
		return false;
	}

	/*
	* 首次启动时加载最新的内容
	* */
	private void initRecyclerViewFirstZhiHuContent() {
		if (getConnectivityCondition()) {
			Request request = new Request.Builder()
					.url(ZHIHUAPI_LATEST)
					.build();

			Call call = okHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
							"咦，网络不太顺畅吔", 3000);
					snackbar.show();
				}

				@Override
				public void onResponse(Response response) throws IOException {
					if (response.code() == 200) {
						//						Log.i("ZRH", "success in access url: " + response.request().urlString());
						zhiHuNewsLatestItemInfo = gson.fromJson(response.body().string(),
								ZhiHuNewsLatestItemInfo.class);
						zhiHuNewsTopItemInfoList = zhiHuNewsLatestItemInfo
								.top_stories;
						current_date_from_zhihu = zhiHuNewsLatestItemInfo.date + "";
						current_date_year = current_date_from_zhihu.substring(0, 4);
						current_date_month = current_date_from_zhihu.substring(4, 6);
						current_date_day = current_date_from_zhihu.substring(6, 8);
						Message message = new Message();
						message.what = RECYCLER_REFRESH_LATEST;
						recyclerRefreshHandler.sendMessage(message);
					}
				}
			});
		}
	}

	/*
	* 根据SDK版本修改UI
	* */
	private void changeUIBySDK_VER() {
		if (Build.VERSION.SDK_INT == 19) {
			viewPager.setFitsSystemWindows(false);
			frameLayout.setFitsSystemWindows(false);
			appBarLayout.setFitsSystemWindows(false);
			drawerLayout.setFitsSystemWindows(false);
			coordinatorLayout.setFitsSystemWindows(false);
			navigationView.setFitsSystemWindows(false);

			CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
			layoutParams.setMargins(0, LengthTransitionUtility.getStatusBarHeight(this), 0, 0);
			toolbar.setLayoutParams(layoutParams);
		}
	}

	private void initDate() {
		simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		date = new Date();
		current_simple_date_format = simpleDateFormat.format(date);
	}

	private void initGson() {
		gson = new Gson();
	}

	/*
		* 初始化 线程
		* */
	private void initThreadORRunnable() {
		mRunnable = new Runnable() {
			@Override
			public void run() {
				BACKPRESS_STATUS = false;
			}
		};
	}

	/*
	* 初始化组件
	* */
	private void initComponent() {
		toolbar = (Toolbar) findViewById(R.id.homeActivity_Content_main_ToolBar);
		drawerLayout = (DrawerLayout) findViewById(R.id.homeActivity_DrawerLayout);
		navigationView = (NavigationView) findViewById(R.id.homeActivity_NavigationView);
		coordinatorLayout = (CoordinatorLayout) findViewById(R.id
				.homeActivity_Content_root_CoordinatorLayout);
		appBarLayout = (AppBarLayout) findViewById(R.id.homeActivity_Content_main_AppBarLayout);
		collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id
				.homeActivity_Content_main_CollapsingToolbarLayout);
		viewPager = (ViewPager) findViewById(R.id.homeActivity_Content_main_ViewPager);
		recyclerView = (RecyclerView) findViewById(R.id.homeActivity_Content_main_RecyclerView);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id
				.homeActivity_Content_main_SwipeRefreshLayout);
		homeActivityViewPagerIndicator = (HomeActivityViewPagerIndicator) findViewById(R.id
				.homeActivity_Content_main_HomeActivityRecyclerViewIndicator);
		floatingActionButton = (FloatingActionButton) findViewById(R.id
				.homeActivity_Content_main_FAB_anchorInAppBar);
		frameLayout = (FrameLayout) findViewById(R.id.homeActivity_Content_main_FrameLayout);
		floatingActionButton_quickUp = (com.getbase.floatingactionbutton.FloatingActionButton)
				findViewById(R.id.homeActivity_Content_main_FABMenu_item_FAB_quickUp);
		floatingActionButton_quickDown = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id
				.homeActivity_Content_main_FABMenu_item_FAB_quickDown);
		floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id
				.homeActivity_Content_main_FABMenu);
		floatingActionButton.setVisibility(View.GONE);
		profile_iv = (ImageView) navigationView.getHeaderView(0).findViewById(R.id
				.home_activity_drawer_header_login_info_profile_iv);
		profile_tv = (TextView) navigationView.getHeaderView(0).findViewById(R.id
				.home_activity_drawer_header_login_info_profile_tv);
	}

	/*
	* 初始化Handler
	* */
	private void initHandler() {
		mhandler = new Handler();
		bannerHandler = new BannerHandler(this);
		recyclerRefreshHandler = new RecyclerRefreshHandler(this);
	}

	/*
	* 初始化toolbar
	* */
	private void initToolbar() {
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
	}

	private void initActionBarDrawerToggle() {
		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
				drawerLayout, toolbar, R.string.open_drawer, R.string
				.close_drawer);
		actionBarDrawerToggle.syncState();

		drawerLayout.addDrawerListener(actionBarDrawerToggle);
	}

	/*
	* 获取前一天的时间
	* */
	public String getPrecedingDate(String currentDate) {
		Calendar calendar = Calendar.getInstance();

		Date date_current = null;
		try {
			date_current = simpleDateFormat.parse(currentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(date_current);
		int day = calendar.get(Calendar.DATE);
		calendar.set(Calendar.DATE, day - 1);
		return simpleDateFormat.format(calendar.getTime());
	}


	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(navigationView)) {
			drawerLayout.closeDrawer(navigationView);
		} else {
			if (BACKPRESS_STATUS) {
				super.onBackPressed();
			}
			if (!BACKPRESS_STATUS) {
				BACKPRESS_STATUS = true;
				Snackbar snackbar = SnackbarUtility.getSnackbarLight(coordinatorLayout, "再次点击退出键退出",
						Snackbar.LENGTH_SHORT);
				snackbar.show();
			}

			mhandler.postDelayed(mRunnable, 2000);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		if (position == 0) {
			collapsingToolbarLayout.setTitle(zhiHuNewsTopItemInfoList
					.get(zhiHuNewsTopItemInfoList.size() - 1).title);
		} else if (position == zhiHuNewsTopItemInfoList.size() + 1) {
			collapsingToolbarLayout.setTitle(zhiHuNewsTopItemInfoList.get(0).title);
		} else {
			collapsingToolbarLayout.setTitle(zhiHuNewsTopItemInfoList
					.get(position - 1).title);
		}
		viewPagerStatusPosition = position;
		homeActivityViewPagerIndicator.changeColorForStatus(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (viewPagerStatusPosition == 0) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				viewPager.setCurrentItem(zhiHuNewsTopItemInfoList.size(), false);
			}
		} else if (viewPagerStatusPosition == zhiHuNewsTopItemInfoList.size() + 1) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				viewPager.setCurrentItem(1, false);
			}
		}
	}

	@Override
	public void onRefresh() {
		if (getConnectivityCondition()) {
			Request request = new Request.Builder()
					.url(ZHIHUAPI_LATEST)
					.build();

			Call call = okHttpClient.newCall(request);

			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
							"咦，网络不太顺畅吔", 3000);
					snackbar.show();
					recyclerRefreshHandler.sendEmptyMessage(RECYCLER_REFRESH_NEW_FAILURE);
				}

				@Override
				public void onResponse(Response response) throws IOException {
					if (response.code() == 200) {
						zhiHuNewsLatestItemInfo_new = gson.fromJson(response.body().string(),
								ZhiHuNewsLatestItemInfo.class);
						Message message = new Message();
						message.what = RECYCLER_REFRESH_NEW;
						recyclerRefreshHandler.sendMessage(message);
					}
				}
			});
		}
	}

	@Override
	public void refreshOldNews() {
		swipeRefreshLayout.setRefreshing(true);

		if (getConnectivityCondition()) {
			refressh_old_date = current_simple_date_format;
			String oldUrl = ZHIHUAPI_BEFORE + refressh_old_date;
			Request request = new Request.Builder()
					.url(oldUrl)
					.build();
			Call call = okHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					//				Log.i("ZRH", "failure in oldurl");
					Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
							"咦，网络不太顺畅吔", 3000);
					snackbar.show();
					recyclerRefreshHandler.sendEmptyMessage(RECYCLER_REFRESH_OLD_FAILURE);
				}

				@Override
				public void onResponse(Response response) throws IOException {
					if (response.code() == 200) {
						//					Log.i("ZRH", "success in access url: " + response.request().urlString());
						zhiHuNewsLatestItemInfo_old = gson.fromJson(response.body
								().string(), ZhiHuNewsLatestItemInfo.class);
						Message message = new Message();
						message.what = RECYCLER_REFRESH_OLD;
						recyclerRefreshHandler.sendMessage(message);
					}
				}
			});
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		Intent intent;
		//		drawerLayout.closeDrawers();
		switch (item.getItemId()) {

			case R.id.drawer_home:
				onRefresh();

				break;

			case R.id.drawer_star:
				intent = new Intent();
				intent.setAction(INTENT_TO_STAR_HISTORY_PRAISE_KEY);
				intent.putExtra(INTENT_STRING_DATA_KEY, STAR_KEY);
				startActivity(intent);

				break;

			case R.id.drawer_good:
				intent = new Intent();
				intent.setAction(INTENT_TO_STAR_HISTORY_PRAISE_KEY);
				intent.putExtra(INTENT_STRING_DATA_KEY, PRAISE_KEY);
				startActivity(intent);

				break;

			case R.id.drawer_history:
				intent = new Intent();
				intent.setAction(INTENT_TO_STAR_HISTORY_PRAISE_KEY);
				intent.putExtra(INTENT_STRING_DATA_KEY, HISTORY_KEY);
				startActivity(intent);

				break;

			case R.id.drawer_notification:
				actionForNavigationItemSelected(item);

				break;
			case R.id.drawer_theme:
				actionForNavigationItemSelected(item);

				break;
			case R.id.drawer_setting:
				actionForNavigationItemSelected(item);

				break;
			case R.id.drawer_response:
				actionForNavigationItemSelected(item);

				break;
			case R.id.drawer_about:
				actionForNavigationItemSelected(item);

				break;
			case R.id.drawer_exit:
				finish();
				break;
		}
		return true;
	}

	private void actionForNavigationItemSelected(MenuItem item) {
		drawerLayout.closeDrawer(navigationView);
		Snackbar.make(coordinatorLayout, item.getTitle(), Snackbar.LENGTH_SHORT).show();
	}

	/*
	* 首页顶部banner开始自动轮播
	* */
	protected void bannerStartAutoScroll() {
		bannerHandler.sendEmptyMessageDelayed(BANNER_SCROLL_KEY, BANNER_SCROLL_INTERVAL);
	}

	/*
	* 发送自动滚动的消息
	* */
	protected void sendScrollMessage() {
		bannerHandler.removeMessages(BANNER_SCROLL_KEY);
		bannerHandler.sendEmptyMessageDelayed(BANNER_SCROLL_KEY,
				BANNER_SCROLL_INTERVAL);
	}

	/*
	* banner滚动到下一个页面
	* */
	protected void bannerScrollToNext() {
		viewPager.setCurrentItem(viewPagerStatusPosition + 1, true);
	}

	/*
	* 手动停止轮播图的自动滚动
	* */
	protected void bannerStopAutoScroll() {
		bannerHandler.removeMessages(BANNER_SCROLL_KEY);
	}

	@Override
	public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
		if (verticalOffset <= -315) {
			collapsingToolbarLayout.setTitle(getApplicationContext().getResources().getString(R
					.string.home_activity_content_collapsingtoolbar_title));
			bannerStopAutoScroll();
		} else {
			if (!bannerHandler.hasMessages(BANNER_SCROLL_KEY)) {
				bannerStartAutoScroll();
			}
			if (collapsingToolbarLayout.getTitle() == getApplicationContext().getResources()
					.getString(R.string.home_activity_content_collapsingtoolbar_title)) {
				int viewPgerCurrentPosition = viewPager.getCurrentItem();
				switch (viewPgerCurrentPosition) {
					case 0:
						if (zhiHuNewsTopItemInfoList.size() != 0) {
							collapsingToolbarLayout.setTitle(zhiHuNewsTopItemInfoList
									.get(zhiHuNewsTopItemInfoList.size() - 1).title);
						}
						break;
					case 4:
						if (zhiHuNewsTopItemInfoList.size() != 0) {
							collapsingToolbarLayout.setTitle(zhiHuNewsTopItemInfoList.get(0).title);
						}
						break;
					default:
						if (zhiHuNewsTopItemInfoList.size() != 0) {
							collapsingToolbarLayout.setTitle(zhiHuNewsTopItemInfoList.get
									(viewPgerCurrentPosition - 1).title);
						}
						break;
				}
			}
		}
		if (verticalOffset != 0) {

		}
	}

	/*
	* 静态内部类 BannerHandler
	* 处理轮播图
	* */
	static class BannerHandler extends Handler {

		WeakReference<HomeAct> homeActivityWeakReference;
		HomeAct homeAct;

		public BannerHandler(HomeAct homeAct) {
			this.homeActivityWeakReference = new WeakReference<>(homeAct);
			this.homeAct = this.homeActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case BANNER_SCROLL_KEY:
					homeAct.bannerScrollToNext();
					homeAct.sendScrollMessage();
			}
		}
	}

	/*
	* 静态内部类 RecyclerVRefreshHandler
	* 处理RecyclerView的刷新事件
	* */
	static class RecyclerRefreshHandler extends Handler {

		WeakReference<HomeAct> homeActivityWeakReference;
		HomeAct homeAct;

		public RecyclerRefreshHandler(HomeAct homeAct) {
			this.homeActivityWeakReference = new WeakReference<>(homeAct);
			this.homeAct = this.homeActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case RECYCLER_REFRESH_LATEST:

					/*
					* 加载顶部banner的内容
					* */

					//					Log.i("ZRH", "准备数据");
					List<View> viewList = new ArrayList<>();

					/*
					* 加载图片
					* */
					for (int i = 0; i < homeAct.zhiHuNewsTopItemInfoList.size() + 2; i++) {
						View convertView = LayoutInflater.from(homeAct).inflate(R.layout
								.home_act_content_viewpager_item, null);
						ImageView imageView = (ImageView) convertView.findViewById(R.id
								.homeActivity_Content_ViewPager_CustomItem_ImageView);
						if (i == 0) {
							Glide.with(homeAct)
									.load(homeAct.zhiHuNewsTopItemInfoList.get
											(homeAct.zhiHuNewsTopItemInfoList
													.size() - 1).image)
									.skipMemoryCache(true)
									.diskCacheStrategy(DiskCacheStrategy.RESULT)
									.into(imageView);
							//							Log.i("ZRH", "首次加载：" + homeActivity.zhiHuNewsTopItemInfoList.get(homeActivity.zhiHuNewsTopItemInfoList.size() - 1).image);
						} else if (i == homeAct.zhiHuNewsTopItemInfoList.size() + 1) {
							Glide.with(homeAct)
									.load(homeAct.zhiHuNewsTopItemInfoList.get(0).image)
									.skipMemoryCache(true)
									.diskCacheStrategy(DiskCacheStrategy.RESULT)
									.into(imageView);
							//							Log.i("ZRH", "首次加载: " + homeActivity.zhiHuNewsTopItemInfoList.get(0).image);
						} else {
							Glide.with(homeAct)
									.load(homeAct.zhiHuNewsTopItemInfoList.get(i - 1)
											.image)
									.skipMemoryCache(true)
									.diskCacheStrategy(DiskCacheStrategy.RESULT)
									.into(imageView);
							//							Log.i("ZRH", "首次加载: " + homeActivity.zhiHuNewsTopItemInfoList.get(i - 1).image);
						}
						viewList.add(convertView);
					}
					//					Log.i("ZRH", "准备数据完毕,数据长度：" + viewList.size());

					/*
					* 设置viewPager的indicator
					* */
					homeAct.homeActivityViewPagerIndicator.setRecyclerViewIndicatorAttribute
							(homeAct.zhiHuNewsTopItemInfoList.size(), homeAct);
					homeAct.homeActivityViewPagerIndicator.setColorForStart();
					//					Log.i("ZRH", "设置viewPager的indicator");

					/*
					* 实例化viewPagerAdapter
					* 初始化viewPager的参数
					* */
					homeAct.homeActivityViewPagerAdapter = new HomeActivityViewPagerAdapter
							(homeAct, viewList);
					homeAct.homeActivityViewPagerAdapter.setZhiHuNewsTopItemInfoList
							(homeAct.zhiHuNewsTopItemInfoList);
					//					Log.i("ZRH", "实例化viewPagerAdapter");

					/*
					* 设置viewPager的adapter
					* viewPager状态初始化
					* */
					//					homeAct.viewPager.setOffscreenPageLimit(0);
					homeAct.viewPager.setAdapter(homeAct.homeActivityViewPagerAdapter);
					//					Log.i("ZRH", "设置viewPager的adapter");
					homeAct.viewPager.setCurrentItem(1);
					//					Log.i("ZRH", "viewPager状态初始化");

					/*
					* 设置标题
					* */
					homeAct.collapsingToolbarLayout.setTitle
							(homeAct.zhiHuNewsTopItemInfoList.get(0).title);
					//					Log.i("ZRH", "设置标题");

					/*
					* 开始自动滚动
					* */
					homeAct.bannerStartAutoScroll();
					homeAct.isLoadBanner = true;
					//					Log.i("ZRH", "开始自动滚动");

					/*
					* 加载列表内容
					* */
					ZhiHuNewsItemInfo zhiHuNewsItemInfo = new ZhiHuNewsItemInfo();
					zhiHuNewsItemInfo.item_layout = 0;
					zhiHuNewsItemInfo.date_cus = homeAct.zhiHuNewsLatestItemInfo.date;
					homeAct.zhiHuNewsLatestItemInfo.stories.add(0, zhiHuNewsItemInfo);
					for (int i = 0; i < homeAct.zhiHuNewsLatestItemInfo.stories.size(); i++) {
						if (i != 0) {
							homeAct.zhiHuNewsLatestItemInfo.stories.get(i).item_layout = 1;
						}
					}
					homeAct.homeActivityRecyclerViewAdapter.setZhiHuNewsItemInfoList
							(homeAct.zhiHuNewsLatestItemInfo.stories);
					homeAct.recyclerView.setAdapter(homeAct.homeActivityRecyclerViewAdapter);
					homeAct.recyclerView.setLayoutManager(homeAct.linearLayoutManager);
					break;

				case RECYCLER_REFRESH_NEW:

					/*
					* 更新顶部banner的内容
					* */
					if (homeAct.zhiHuNewsTopItemInfoList.get(0).id != homeAct
							.zhiHuNewsLatestItemInfo_new.top_stories.get(0).id) {
						homeAct.zhiHuNewsTopItemInfoList = homeAct
								.zhiHuNewsLatestItemInfo_new.top_stories;
						homeAct.homeActivityViewPagerAdapter.setZhiHuNewsTopItemInfoList
								(homeAct.zhiHuNewsTopItemInfoList);
						//						Log.i("ZRH", "更新完banner数据");
						homeAct.homeActivityViewPagerAdapter.updateViewImage();
						//						Log.i("ZRH", "调用完homeActivity.homeActivityViewPagerAdapter.updateViewImage" +
						//								"()");
					}

					/*
					* 加载新的列表内容
					* */
					List<ZhiHuNewsItemInfo> tempList = new ArrayList<>();
					int id = homeAct.zhiHuNewsLatestItemInfo.stories.get(1).id;
					for (int i = 0; i < homeAct.zhiHuNewsLatestItemInfo_new.stories.size(); i++) {
						if (homeAct.zhiHuNewsLatestItemInfo_new.stories.get(i).id == id) {
							break;
						} else {
							homeAct.zhiHuNewsLatestItemInfo_new.stories.get(i).item_layout = 1;
							tempList.add(homeAct.zhiHuNewsLatestItemInfo_new.stories.get
									(i));
						}
					}
					if (tempList.size() != 0) {
						//						Log.i("ZRH", "tempList.size(): " + tempList.size());
						homeAct.homeActivityRecyclerViewAdapter.addItemIntoFirst(tempList);

						/*
						* 加载完后滚动到新的最高那一列
						* */
						homeAct.recyclerView.scrollToPosition(0);
						SnackbarUtility.getSnackbarDefault(homeAct.coordinatorLayout,
								"成功更新" + tempList.size() + "条日报", 2000).show();
					} else {
						SnackbarUtility.getSnackbarLight(homeAct.coordinatorLayout,
								"已经是最新日报", Snackbar.LENGTH_SHORT);
					}
					homeAct.swipeRefreshLayout.setRefreshing(false);

					break;

				case RECYCLER_REFRESH_NEW_FAILURE:
					homeAct.swipeRefreshLayout.setRefreshing(false);
					break;

				case RECYCLER_REFRESH_OLD:

					homeAct.current_simple_date_format = homeAct.getPrecedingDate
							(homeAct.current_simple_date_format);
					ZhiHuNewsItemInfo zhiHuNewsItemInfo_old = new ZhiHuNewsItemInfo();
					zhiHuNewsItemInfo_old.item_layout = 0;
					zhiHuNewsItemInfo_old.date_cus = homeAct.zhiHuNewsLatestItemInfo_old.date;
					homeAct.zhiHuNewsLatestItemInfo_old.stories.add(0, zhiHuNewsItemInfo_old);
					for (int i = 0; i < homeAct.zhiHuNewsLatestItemInfo_old.stories.size(); i++) {
						if (i != 0) {
							homeAct.zhiHuNewsLatestItemInfo_old.stories.get(i).item_layout = 1;
						}
					}

					homeAct.homeActivityRecyclerViewAdapter.addItemIntoLast(homeAct
							.zhiHuNewsLatestItemInfo_old.stories);

					homeAct.swipeRefreshLayout.setRefreshing(false);

					/*
					* 加载完后滚动到加载出的那一行
					* */
					homeAct.recyclerView.scrollToPosition(homeAct
							.homeActivityRecyclerViewAdapter.getItemCount() - homeAct
							.zhiHuNewsLatestItemInfo_old.stories.size() + 1);

					break;

				case RECYCLER_REFRESH_OLD_FAILURE:
					homeAct.swipeRefreshLayout.setRefreshing(false);
					break;
			}
		}
	}
}
