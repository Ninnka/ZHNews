package com.rainnka.zhkunews;

import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rainnka on 2016/5/12 20:45
 * Project name is ZHKUNews
 */
public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
		HomeActivityRecyclerViewAdapter.HomeActivityRecyclerViewAdapterCallback,
		HomeActivityRecyclerViewAdapter.onRecyclerViewItemClickListener, SwipeRefreshLayout
				.OnRefreshListener, AppBarLayout.OnOffsetChangedListener, NavigationView.OnNavigationItemSelectedListener {

	protected Toolbar toolbar;
	protected DrawerLayout drawerLayout;
	protected NavigationView navigationView;
	protected CoordinatorLayout coordinatorLayout;
	protected AppBarLayout appBarLayout;
	protected CollapsingToolbarLayout collapsingToolbarLayout;
	protected ViewPager viewPager;
	protected RecyclerView recyclerView;
	protected SwipeRefreshLayout swipeRefreshLayout;
	protected HomeActivityRecyclerViewIndicator homeActivityRecyclerViewIndicator;
	protected FloatingActionButton floatingActionButton;
	protected FrameLayout frameLayout;

	protected ImageView profile_iv;
	protected TextView profile_tv;

	public HomeActivityViewPagerAdapter homeActivityViewPagerAdapter;
	public HomeActivityRecyclerViewAdapter homeActivityRecyclerViewAdapter;

	public Handler mhandler;
	public BannerHandler bannerHandler;
	public RecyclerRefreshHandler recyclerRefreshHandler;

	public int[] mRecyclerViewItemPicture;
	public String[] mRecyclerViewItemContent;

	public int viewPagerStatusPosition = 1;
	public final static int BANNER_SCROLL_INTERVAL = 4000;
	public final static int BANNER_SCROLL_KEY = 0x123;

	public final static int RECYCLER_REFRESH_NEW = 0x111111;
	public final static int RECYCLER_REFRESH_OLD = 0x222222;
	public final static int RECYCLER_REFRESH_LATEST = 0x333333;

	public boolean BACKPRESS_STATUS = false;

	public Runnable mRunnable;

	public Date date;
	public int date_year;
	public int date_month;
	public int date_day;
	public String date_current_string;

	public String current_date;
	public String refressh_old_date;
	public String current_date_year;
	public String current_date_month;
	public String current_date_day;

	public LinearLayoutManager linearLayoutManager;

	public Gson gson;

	public HomeActivityViewPagerBannerData homeActivityViewPagerBannerData;
	public final HomeActivityViewPagerBannerInfo[] homeActivityViewPagerBannerInfo = new
			HomeActivityViewPagerBannerInfo[3];

	public int[] item_layout;

	public final static String INTENT_TO_NEWS_KEY = "android.intent.NewsActivity";

	public static String TOKEN_KEY = "TOKEN";
	public static String SER_KEY = "SER";

	public static String ZHIHUAPI_LATEST = "http://news-at.zhihu.com/api/4/news/latest";
	public static String ZHIHUAPI_BEFORE = "http://news.at.zhihu.com/api/4/news/before/";

	protected OkHttpClient okHttpClient;
	//	protected Request request;

	public ZhiHuNewsLatestItemInfo zhiHuNewsLatestItemInfo;
	//	public List<ZhiHuNewsItemInfo> zhiHuNewsItemInfoList;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);

		/*
		* 初始化组件
		* */
		initComponent();

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
		* 初始化适配器
		* 准备viewPager中的数据
		* HomeActivityViewPagerAdapter
		* */
		homeActivityViewPagerBannerData = new HomeActivityViewPagerBannerData(this);
		for (int i = 0; i < homeActivityViewPagerBannerInfo.length; i++) {
			homeActivityViewPagerBannerInfo[i] = new HomeActivityViewPagerBannerInfo();
			homeActivityViewPagerBannerInfo[i].pictureID = homeActivityViewPagerBannerData
					.pictureID[i];
			homeActivityViewPagerBannerInfo[i].pictureTitle = homeActivityViewPagerBannerData
					.pictureTitle[i];
		}
		List<View> viewList = new ArrayList<>();
		for (int i = 0; i < homeActivityViewPagerBannerInfo.length + 2; i++) {
			View convertView = LayoutInflater.from(this).inflate(R.layout
					.home_activity_content_viewpager_item, null);
			ImageView imageView = (ImageView) convertView.findViewById(R.id
					.homeActivity_Content_ViewPager_CustomItem_ImageView);
			if (i == 0) {
				imageView.setImageResource(homeActivityViewPagerBannerInfo[homeActivityViewPagerBannerInfo.length -
						1].pictureID);
			} else if (i == homeActivityViewPagerBannerInfo.length + 1) {
				imageView.setImageResource(homeActivityViewPagerBannerInfo[0].pictureID);
			} else {
				imageView.setImageResource(homeActivityViewPagerBannerInfo[i - 1].pictureID);
			}
			viewList.add(convertView);
		}
		homeActivityViewPagerAdapter = new HomeActivityViewPagerAdapter(viewList);

		/*
		* 为 viewPager 装载 ViewPagerAdapter
		* 添加 OnPageChangeListener
		* */
		viewPager.setAdapter(homeActivityViewPagerAdapter);
		viewPager.setCurrentItem(1);
		//设置对应的标题
		collapsingToolbarLayout.setTitle(homeActivityViewPagerBannerInfo[0].pictureTitle);
		//添加页面改变事件
		viewPager.addOnPageChangeListener(this);
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
		//设置 homeActivityRecyclerViewIndicator
		homeActivityRecyclerViewIndicator.setRecyclerViewIndicatorAttribute(homeActivityViewPagerBannerInfo.length,
				HomeActivity.this);
		homeActivityRecyclerViewIndicator.setColorForStart();

		/*
		* viewPager添加触控事件
		* */
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

		/*
		* 设置NavigationView Toggle
		* */
		initActionBarDrawerToggle();






		/*
		* 准备RecyclerViewAdapter
		* 准备数据
		* 准备LinearLayoutManager
		* */
		item_layout = getApplicationContext().getResources().getIntArray(R.array
				.recyclerview_iteminfo_type);
		linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		homeActivityRecyclerViewAdapter = new HomeActivityRecyclerViewAdapter(this);
		homeActivityRecyclerViewAdapter.setHomeActivityRecyclerViewAdapterCallback(this);
		homeActivityRecyclerViewAdapter.setOnRecyclerViewItemClickListener(this);
		//		recyclerView.setLayoutManager(linearLayoutManager);

		//		mRecyclerViewItemPicture = new int[]{R.drawable.bg1};
		//		mRecyclerViewItemContent = new String[]{getResources().getString(R.string.testText)};


		okHttpClient = new OkHttpClient();
		Request request = new Request.Builder()
				.url(ZHIHUAPI_LATEST)
				.build();

		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {

			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.code() == 200) {
					Log.i("ZRH", "success");
					zhiHuNewsLatestItemInfo = gson.fromJson(response.body
							().string(), ZhiHuNewsLatestItemInfo.class);
					//					Log.i("ZRH", zhiHuNewsLatestItemInfo.date + "");
					current_date = zhiHuNewsLatestItemInfo.date + "";
					current_date_year = current_date.substring(0, 4);
					current_date_month = current_date.substring(4, 6);
					current_date_day = current_date.substring(6, 8);
					//					Log.i("ZRH", "current_date: " + current_date);
					//					Log.i("ZRH", "current_date: " + current_date_year);
					//					Log.i("ZRH", "current_date: " + current_date_month);
					//					Log.i("ZRH", "current_date: " + current_date_day);
					Message message = new Message();
					message.what = RECYCLER_REFRESH_LATEST;
					recyclerRefreshHandler.sendMessage(message);
				}
			}
		});


		//		List<HomeActivityRecyclerViewItemInfo> zhiHuNewsItemInfoList = new ArrayList<>();
		//		for (int i = 0; i < 10; i++) {
		//			HomeActivityRecyclerViewItemInfo tempItemInfo = new HomeActivityRecyclerViewItemInfo();
		//			if (i == 0) {
		//				tempItemInfo.type = item_layout[0];
		//			} else {
		//				tempItemInfo.type = item_layout[1];
		//				tempItemInfo.title = mRecyclerViewItemContent[0];
		//				tempItemInfo.pictureID = mRecyclerViewItemPicture[0];
		//			}
		//
		//			zhiHuNewsItemInfoList.add(tempItemInfo);
		//		}
		//		homeActivityRecyclerViewAdapter.setZhiHuNewsItemInfoList(zhiHuNewsItemInfoList);



		/*
		* 为 RecyclerView 装载Adapter
		* 为 RecyclerView 装载LinearLayoutManager
		* */
		//		recyclerView.setAdapter(homeActivityRecyclerViewAdapter);









		/*
		* 设置 swipeRefreshLayout 监听事件
		* */
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setDistanceToTriggerSync(800);
		swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);

		/*
		* 设置fab按钮的点击事件
		* */
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
						/*nothing to do*/
			}
		});

		/*
		* appbar监听事件
		* */
		appBarLayout.addOnOffsetChangedListener(this);

		/*
		* 添加navigationView menu item的点击事件
		* */
		navigationView.setNavigationItemSelectedListener(this);
		/*
		* 设置单个item可见
		* */
		//		navigationView.getMenu().findItem(R.id.drawer_star).setVisible(true);
		/*
		* 设置单个group可见
		* */
		//		navigationView.getMenu().setGroupVisible(R.id.group2, true);

		/*
		* 头像点击事件
		* */
		//		View HeaderView = navigationView.inflateHeaderView(R.layout.home_activity_drawer_header);
		//		profile_iv = (ImageView) HeaderView.findViewById(R.id
		//				.home_activity_drawer_header_login_info_profile_iv);
		profile_iv = (ImageView) navigationView.getHeaderView(0).findViewById(R.id
				.home_activity_drawer_header_login_info_profile_iv);
		profile_tv = (TextView) navigationView.getHeaderView(0).findViewById(R.id
				.home_activity_drawer_header_login_info_profile_tv);
		profile_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(navigationView);
				Snackbar.make(coordinatorLayout, "你已经成功登录", Snackbar.LENGTH_SHORT).show();
				navigationView.getMenu().setGroupVisible(R.id.group2, true);
				profile_tv.setText("admin(假定账号)");
			}
		});
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
		mhandler.removeCallbacks(mRunnable);
		bannerHandler.removeMessages(BANNER_SCROLL_KEY);
		recyclerRefreshHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		bannerStartAutoScroll();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//		menu.add(0, 0, Menu.NONE, "").setIcon(R.mipmap.notification_light).setShowAsAction
		//				(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//		menu.add(0, 1, Menu.NONE, "设置");
		//		menu.add(0, 1, Menu.NONE, "关于");
		getMenuInflater().inflate(R.menu.home_activity_menu, menu);
		return true;
	}

	private void initDate() {
		date = new Date();
		date_year = date.getYear();
		Log.i("ZRH", "date_year" + date_year);
		date_month = date.getMonth() + 1;
		Log.i("ZRH", "date_year" + date_month);

		date_day = date.getDay();
		Log.i("ZRH", "date_year" + date_day);

		//		date_current_string = String.valueOf(date_year) + String.valueOf(date_month) + String.valueOf(date_day);
		//		Log.i("ZRH", "date_current_string: " + date_current_string);
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
		homeActivityRecyclerViewIndicator = (HomeActivityRecyclerViewIndicator) findViewById(R.id
				.homeActivity_Content_main_HomeActivityRecyclerViewIndicator);
		floatingActionButton = (FloatingActionButton) findViewById(R.id
				.homeActivity_Content_main_FAB_anchorInAppBar);
		frameLayout = (FrameLayout) findViewById(R.id.homeActivity_Content_main_FrameLayout);


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


	@Override
	public void onBackPressed() {
		if (BACKPRESS_STATUS) {
			super.onBackPressed();
		}
		if (!BACKPRESS_STATUS) {
			BACKPRESS_STATUS = true;
			Snackbar.make(coordinatorLayout, "再次点击退出键退出", Snackbar.LENGTH_SHORT).show();
		}

		mhandler.postDelayed(mRunnable, 2000);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
			case 0:
				collapsingToolbarLayout.setTitle(homeActivityViewPagerBannerInfo[homeActivityViewPagerBannerInfo
						.length - 1].pictureTitle);
				break;
			case 4:
				collapsingToolbarLayout.setTitle(homeActivityViewPagerBannerInfo[0].pictureTitle);
				break;
			default:
				collapsingToolbarLayout.setTitle(homeActivityViewPagerBannerInfo[position - 1].pictureTitle);
				break;
		}
		viewPagerStatusPosition = position;
		homeActivityRecyclerViewIndicator.changeColorForStatus(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (viewPagerStatusPosition == 0) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				viewPager.setCurrentItem(3, false);
			}
		} else if (viewPagerStatusPosition == 4) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				viewPager.setCurrentItem(1, false);
			}
		}
	}

	@Override
	public void onRefresh() {
		Message message = new Message();
		message.what = RECYCLER_REFRESH_NEW;
		recyclerRefreshHandler.sendMessageDelayed(message, 2000);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.drawer_home:
				actionForNavigationItemSelected(item);
				break;
			case R.id.drawer_star:
				actionForNavigationItemSelected(item);

				break;
			case R.id.drawer_good:
				actionForNavigationItemSelected(item);

				break;
			case R.id.drawer_history:
				actionForNavigationItemSelected(item);

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

	protected void bannerStartAutoScroll() {
		bannerHandler.sendEmptyMessageDelayed(BANNER_SCROLL_KEY, BANNER_SCROLL_INTERVAL);
	}

	protected void sendScrollMessage() {
		bannerHandler.removeMessages(BANNER_SCROLL_KEY);
		bannerHandler.sendEmptyMessageDelayed(BANNER_SCROLL_KEY,
				BANNER_SCROLL_INTERVAL);
	}

	protected void bannerScrollToNext() {
		viewPager.setCurrentItem(viewPagerStatusPosition + 1, true);
	}

	protected void bannerStopAutoScroll() {
		bannerHandler.removeMessages(BANNER_SCROLL_KEY);
	}

	@Override
	public void refreshOldNews() {
		swipeRefreshLayout.setRefreshing(true);
		if (date_day != 0) {
			date_day = date_day - 1;
		}
		//		date_current_string = String.valueOf(date_year) + String.valueOf(date_month) + String
		//				.valueOf(date_day);
		//		Log.i("ZRH", "date_current_String: " + date_current_string);
		if (Integer.valueOf(current_date_day) < 10) {
			date_day = Integer.valueOf(current_date_day);
			if (date_day > 0) {
				refressh_old_date = current_date_year + current_date_month + "0" + (date_day - 1);
				Log.i("ZRH", "refressh_old_date: " + refressh_old_date);
			}
		} else {
			date_day = Integer.valueOf(current_date_day);
			if (date_day > 0) {
				refressh_old_date = current_date_year + current_date_month + (date_day - 1);
				Log.i("ZRH", "refressh_old_date: " + refressh_old_date);
			}
		}

		String oldUrl = ZHIHUAPI_BEFORE + refressh_old_date;
		Log.i("ZRH", "refreshOldDate: " + refressh_old_date);
		Request request = new Request.Builder()
				.url(oldUrl)
				.build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Log.i("ZRH", "failure in oldurl");
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.code() == 200) {
					Log.i("ZRH", "success");
					zhiHuNewsLatestItemInfo = gson.fromJson(response.body
							().string(), ZhiHuNewsLatestItemInfo.class);
					//					Log.i("ZRH", zhiHuNewsLatestItemInfo.date + "");
					Message message = new Message();
					message.what = RECYCLER_REFRESH_OLD;
					recyclerRefreshHandler.sendMessage(message);
				}
			}
		});

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
						collapsingToolbarLayout.setTitle(homeActivityViewPagerBannerInfo[homeActivityViewPagerBannerInfo
								.length - 1].pictureTitle);
						break;
					case 4:
						collapsingToolbarLayout.setTitle(homeActivityViewPagerBannerInfo[0].pictureTitle);
						break;
					default:
						collapsingToolbarLayout.setTitle(homeActivityViewPagerBannerInfo[viewPgerCurrentPosition - 1]
								.pictureTitle);
						break;
				}
			}
		}
		if (verticalOffset != 0) {

		}
	}

	@Override
	public void onItemClick(ZhiHuNewsItemInfo zhiHuNewsItemInfo) {
		Intent intent = new Intent();
		intent.setAction(INTENT_TO_NEWS_KEY);
		Bundle bundle = new Bundle();
		bundle.putSerializable(SER_KEY, zhiHuNewsItemInfo);
		intent.putExtras(bundle);
		startActivity(intent);
	}


	/*
	* 静态内部类 BannerHandler
	* 处理轮播图
	* */
	static class BannerHandler extends Handler {

		WeakReference<HomeActivity> homeActivityWeakReference;
		HomeActivity homeActivity;

		public BannerHandler(HomeActivity homeActivity) {
			this.homeActivityWeakReference = new WeakReference<>(homeActivity);
			this.homeActivity = this.homeActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case BANNER_SCROLL_KEY:
					homeActivity.bannerScrollToNext();
					homeActivity.sendScrollMessage();
			}
		}
	}

	/*
	* 静态内部类 RecyclerVRefreshHandler
	* 处理RecyclerView的刷新事件
	* */
	static class RecyclerRefreshHandler extends Handler {

		WeakReference<HomeActivity> homeActivityWeakReference;
		HomeActivity homeActivity;

		public RecyclerRefreshHandler(HomeActivity homeActivity) {
			this.homeActivityWeakReference = new WeakReference<>(homeActivity);
			this.homeActivity = this.homeActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			List<HomeActivityRecyclerViewItemInfo> itemInfoList;
			//			homeActivity.swipeRefreshLayout.setRefreshing(false);
			switch (msg.what) {
				case RECYCLER_REFRESH_LATEST:
					//					List<ZhiHuNewsLatestItemInfo> zhiHuNewsLatestItemInfoList = new ArrayList<>();
					//					homeActivity.zhiHuNewsItemInfoList = new ArrayList<>();
					//					Log.i("ZRH", "in handler");
					ZhiHuNewsItemInfo zhiHuNewsItemInfo = new ZhiHuNewsItemInfo();
					zhiHuNewsItemInfo.item_layout = 0;
					zhiHuNewsItemInfo.date_cus = homeActivity.zhiHuNewsLatestItemInfo.date;
					//					Log.i("ZRH", "date: " + homeActivity.zhiHuNewsLatestItemInfo.date);
					homeActivity.zhiHuNewsLatestItemInfo.stories.add(0, zhiHuNewsItemInfo);
					//					Log.i("ZRH", "homeActivity.zhiHuNewsLatestItemInfo.stories.size()" +
					//							": " + homeActivity.zhiHuNewsLatestItemInfo.stories.size());
					for (int i = 0; i < homeActivity.zhiHuNewsLatestItemInfo.stories.size(); i++) {
						if (i != 0) {
							homeActivity.zhiHuNewsLatestItemInfo.stories.get(i).item_layout = 1;
						}
					}
					//					zhiHuNewsLatestItemInfoList.add(homeActivity.zhiHuNewsLatestItemInfo);
					homeActivity.homeActivityRecyclerViewAdapter.setZhiHuNewsItemInfoList
							(homeActivity.zhiHuNewsLatestItemInfo.stories);
					//					Log.i("ZRH", "执行完setZhiHuNewsItemInfoList");
					homeActivity.recyclerView.setAdapter(homeActivity.homeActivityRecyclerViewAdapter);
					//					Log.i("ZRH", "执行完setAdapter");
					homeActivity.recyclerView.setLayoutManager(homeActivity.linearLayoutManager);
					//					Log.i("ZRH", "执行完setLayoutManager");
					break;
				case RECYCLER_REFRESH_NEW:
					//					itemInfoList = new ArrayList<>();
					//					for (int i = 0; i < 5; i++) {
					//						HomeActivityRecyclerViewItemInfo tempItemInfo = new
					//								HomeActivityRecyclerViewItemInfo();
					//						if (i == 0) {
					//							tempItemInfo.type = homeActivity.item_layout[0];
					//						} else {
					//							tempItemInfo.type = homeActivity.item_layout[1];
					//							tempItemInfo.title = homeActivity.mRecyclerViewItemContent[0];
					//							tempItemInfo.pictureID = R.drawable.bg2;
					//						}
					//						itemInfoList.add(tempItemInfo);
					//					}
					//					homeActivity.homeActivityRecyclerViewAdapter.addItemIntoFirst(itemInfoList);
					//					/*
					//					* 加载完后滚动到新的最高那一列
					//					* */
					//					homeActivity.recyclerView.scrollToPosition(0);
					homeActivity.swipeRefreshLayout.setRefreshing(false);
					break;
				case RECYCLER_REFRESH_OLD:
					//					itemInfoList = new ArrayList<>();
					//					for (int i = 0; i < 5; i++) {
					//						HomeActivityRecyclerViewItemInfo tempItemInfo = new HomeActivityRecyclerViewItemInfo();
					//						if (i == 0) {
					//							tempItemInfo.type = homeActivity.item_layout[0];
					//						} else {
					//							tempItemInfo.type = homeActivity.item_layout[1];
					//							tempItemInfo.title = homeActivity.mRecyclerViewItemContent[0];
					//							tempItemInfo.pictureID = R.drawable.bg3;
					//						}
					//						itemInfoList.add(tempItemInfo);
					//					}

					ZhiHuNewsItemInfo zhiHuNewsItemInfo_old = new ZhiHuNewsItemInfo();
					zhiHuNewsItemInfo_old.item_layout = 0;
					zhiHuNewsItemInfo_old.date_cus = homeActivity.zhiHuNewsLatestItemInfo.date;
					homeActivity.zhiHuNewsLatestItemInfo.stories.add(0, zhiHuNewsItemInfo_old);
					for (int i = 0; i < homeActivity.zhiHuNewsLatestItemInfo.stories.size(); i++) {
						if (i != 0) {
							homeActivity.zhiHuNewsLatestItemInfo.stories.get(i).item_layout = 1;
						}
					}


					homeActivity.homeActivityRecyclerViewAdapter.addItemIntoLast(homeActivity
							.zhiHuNewsLatestItemInfo.stories);


					homeActivity.swipeRefreshLayout.setRefreshing(false);
					/*
					* 加载完后滚动到加载出的那一行
					* */
					//					homeActivity.recyclerView.scrollToPosition(homeActivity
					//							.homeActivityRecyclerViewAdapter.getItemCount() - zhiHuNewsItemInfoList.size());
					break;
			}
		}
	}
}
