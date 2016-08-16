package com.rainnka.ZHNews.Activity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentValues;
import android.content.Context;
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
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.rainnka.ZHNews.Activity.Base.BaseAty;
import com.rainnka.ZHNews.Adapter.HomeActivityRecyclerViewAdapter;
import com.rainnka.ZHNews.Adapter.HomeActivityViewPagerAdapter;
import com.rainnka.ZHNews.Animation_Transformer.DepthPageTransformer;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemInfo;
import com.rainnka.ZHNews.Bean.ZhiHuNewsLatestItemInfo;
import com.rainnka.ZHNews.Callback_Listener.onHActRecyclerItemClickListener;
import com.rainnka.ZHNews.CustomView.HomeActivityViewPagerIndicator;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Service.InitNotiRecService;
import com.rainnka.ZHNews.Utility.ConstantUtility;
import com.rainnka.ZHNews.Utility.NetworkConnectivityUtility;
import com.rainnka.ZHNews.Utility.SQLiteCreateTableHelper;
import com.rainnka.ZHNews.Utility.SnackbarUtility;
import com.rainnka.ZHNews.Utility.TransitionHelper;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rainnka on 2016/5/12 20:45
 * Project name is ZHKUNews
 */
public class HomeAty extends BaseAty implements ViewPager.OnPageChangeListener,
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
	public RecyclerView recyclerView;
	protected SwipeRefreshLayout swipeRefreshLayout;
	protected HomeActivityViewPagerIndicator homeActivityViewPagerIndicator;
	protected FloatingActionButton floatingActionButton;
	protected com.getbase.floatingactionbutton.FloatingActionButton floatingActionButton_quickUp;
	protected com.getbase.floatingactionbutton.FloatingActionButton
			floatingActionButton_quickDown;
	protected FloatingActionsMenu floatingActionsMenu;

	public TextView textView_netStatus;

	//	public RemoteViews remoteViews_notification;

	protected ImageView profile_iv;
	protected TextView profile_tv;

	public HomeActivityViewPagerAdapter homeActivityViewPagerAdapter;
	public HomeActivityRecyclerViewAdapter homeActivityRecyclerViewAdapter;

	public Handler mhandler;
	public BannerHandler bannerHandler;
	public RecyclerRefreshHandler recyclerRefreshHandler;
	//	public NotificationHandler notificationHandler;

	public int viewPagerStatusPosition = 1;

	public boolean BACKPRESS_STATUS = false;

	public Runnable mRunnableBackPressStatus;

	public Date date;
	public String current_simple_date_format;
	public SimpleDateFormat simpleDateFormat;

	public String current_date_from_zhihu;
	public String current_date_year;
	public String current_date_month;
	public String current_date_day;
	public String refressh_old_date;

	public LinearLayoutManager linearLayoutManager;

	public Intent intent_notirec;

	public Gson gson;

	String username = "";
	String password = "";
	String nickname = "";

	private Boolean isFirstLoadingContent = true;

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

		//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		//			getWindow().getDecorView().setSystemUiVisibility(View
		//					.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		//		}

		setContentView(R.layout.home_aty);
		setupWindowAnimations();

		/*
		* 初始化网络连接管理器
		* */
		initConnectivityManager();

		/*
		* 初始化Gson
		* */
		initGson();

		/*
		* 初始化日期
		* */
		initDate();

		/*
		* 初始化 Handler
		* */
		initHandler();

		/*
		* 初始化网络连接客户端
		* */
		initOkhttpClient();

		/*
		* 初始化组件
		* */
		initComponent();

		/*
		* 初始化数据库
		* */
		initSQLiteDatabase();

		/*
		* 获取已登录的用户
		* */
		initUser();

		/*
		* 用toolBar代替actionBar
		* */
		initToolbar();

		/*
		* 初始化后退键状态监控线程
		* */
		initThreadORRunnable();

		/*
		* 设置 NavigationView Toggle
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
		initZhiHuContent();

		/*
		* 初始化推荐服务
		* */
		initNotificationService();

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
		addNavigationProfileImageOnClickListener();

		/*
		* 重新刷新按钮点击事件
		* */
		addNetStatusTextClickListener();

		/*
		* 隐藏navigationView的scrollbar
		* */
		hideNavigationViewScrollBar();



		/*
		* 设置单个item可见
		* */
		//		navigationView.getMenu().findItem(R.id.drawer_star).setVisible(true);
		/*
		* 设置单个group可见
		* */
		//		navigationView.getMenu().setGroupVisible(R.id.group2, true);

	}

	private void setupWindowAnimations() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			Fade fade = new Fade();
			fade.setDuration(100);
			getWindow().setEnterTransition(fade);
			//			getWindow().setExitTransition(null);
			//			Fade fade1 = new Fade();
			//			fade1.setDuration(0);
			//			getWindow().setReenterTransition(null);
		}
	}

	@Override
	protected void onResume() {
		if (!sqLiteDatabase.isOpen()) {
			initSQLiteDatabase();
		}
		super.onResume();
		if (isLoadBanner) {
			bannerStartAutoScroll();
		}
	}

	@Override
	protected void onPause() {
		if (sqLiteDatabase.isOpen()) {
			closeSQLiteDatabase();
		}
		bannerStopAutoScroll();
		if (floatingActionsMenu.isExpanded()) {
			floatingActionsMenu.collapse();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sqLiteDatabase != null) {
			sqLiteDatabase.close();
		}
		mhandler.removeCallbacks(mRunnableBackPressStatus);
		bannerHandler.removeMessages(ConstantUtility.BANNER_SCROLL_KEY);
		recyclerRefreshHandler.removeCallbacksAndMessages(null);
		if (getServiceStatus("com.rainnka.ZHNews.Service.InitNotiRecService")) {
			stopService(intent_notirec);
		}
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
		if (requestCode == ConstantUtility.ITENT_TO_LOGIN_REQUESTCODE && resultCode == ConstantUtility
				.RESULTCODE_LOGIN_ATY) {
			ConstantUtility.userIsLogin = true;
			profile_tv.setText(sharedPreferences.getString("nickname", ""));
			profile_iv.setImageResource(R.mipmap.profile_login);
			//			navigationView.getMenu().setGroupVisible(R.id.group2, true);
			Snackbar.make(coordinatorLayout, "你已经成功登录", Snackbar.LENGTH_SHORT).show();
		}
		if (requestCode == ConstantUtility.ITENT_TO_PROFILE_REQUESTCODE && resultCode == ConstantUtility
				.RESULTCODE_NORMALBACK_PROFILE_ATY) {
			String nn = sharedPreferences.getString("nickname", "");
			if (!profile_tv.getText().equals(nn)) {
				profile_tv.setText(nn);
				profile_iv.setImageResource(R.mipmap.profile_login);
				Snackbar.make(coordinatorLayout, "修改昵称成功", Snackbar.LENGTH_SHORT).show();
			}
		}
		if (requestCode == ConstantUtility.ITENT_TO_PROFILE_REQUESTCODE && resultCode == ConstantUtility
				.RESULTCODE_PROFILE_ATY) {
			ConstantUtility.userIsLogin = false;
			profile_tv.setText("点击头像登录");
			//			navigationView.getMenu().setGroupVisible(R.id.group2, false);
			profile_iv.setImageResource(R.mipmap.profile_light);
			Snackbar.make(coordinatorLayout, "你已经成功退出登录", Snackbar.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case R.id.home_activity_menu_setting:
				intent = new Intent();
				intent.setAction(ConstantUtility.INTENT_TO_SETTINGDETAIL_KEY);
				startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs())
						.toBundle(), true);
				break;
			case R.id.home_activity_menu_about:
				intent = new Intent();
				intent.setAction(ConstantUtility.INTENT_TO_ABOUT_KEY);
				startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs())
						.toBundle(), true);
				break;
			case R.id.home_activity_menu_notification:
				intent = new Intent();
				intent.setAction(ConstantUtility.INTENT_TO_NOTIFICATION_KEY);
				startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs())
						.toBundle(), true);
				break;
			//			case R.id.home_activity_menu_theme:
			//				if(getServiceStatus("com.rainnka.ZHNews.Service.InitNotiRecService")){
			//					stopService(intent_notirec);
			//				}
			//				SharedPreferences preferences = getSharedPreferences("NightMode", MODE_PRIVATE);
			//				SharedPreferences.Editor editor = preferences.edit();
			//				if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
			//					editor.putInt("nightmode", AppCompatDelegate.MODE_NIGHT_YES);
			//					editor.apply();
			//					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			//				} else {
			//					editor.putInt("nightmode", AppCompatDelegate.MODE_NIGHT_NO);
			//					editor.apply();
			//					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			//				}
			//				HomeAty.this.recreate();
			//				break;
			//			case R.id.home_activity_menu_response:
			//				intent = new Intent();
			//				intent.setAction(ConstantUtility.INTENT_TO_FEEDBACK_KEY);
			//				startActivity(intent);
			//				break;
			//			case R.id.home_activity_menu_exit:
			//				finish();
			//				break;
		}
		return true;
	}

	private void addRecyclerViewOnItemClickListener() {
		recyclerView.addOnItemTouchListener(new onHActRecyclerItemClickListener(recyclerView) {
			@Override
			public void onItemClickListener(RecyclerView.ViewHolder viewHolder) {
				if (homeActivityRecyclerViewAdapter.zhiHuNewsItemInfoList.get(viewHolder
						.getAdapterPosition()).item_layout == 1) {
					final ZhiHuNewsItemInfo temp_zhiHuNewsItemInfo = homeActivityRecyclerViewAdapter
							.zhiHuNewsItemInfoList.get(viewHolder.getAdapterPosition());

					if (ConstantUtility.userIsLogin) {
						sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_HISTORY_TABLE);
						try {
							sqLiteDatabase.beginTransaction();
							sqLiteDatabase.delete("my_history", "ItemId like ?", new
									String[]{String.valueOf(temp_zhiHuNewsItemInfo.id)});
							sqLiteDatabase.setTransactionSuccessful();
						} catch (Exception e) {
							Log.i("ZRH", e.getStackTrace().toString());
							Log.i("ZRH", e.getMessage());
							Log.i("ZRH", e.toString());
						} finally {
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
					}

					Intent intent = new Intent();
					intent.setAction(ConstantUtility.INTENT_TO_NEWS_KEY);
					Bundle bundle = new Bundle();
					bundle.putSerializable(ConstantUtility.SER_KEY, temp_zhiHuNewsItemInfo);
					intent.putExtras(bundle);
					startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs())
							.toBundle(), true);

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
		if (getConnectivityStatus()) {
			sharedPreferences = getSharedPreferences("up", MODE_PRIVATE);
			if (sharedPreferences.getString("isLogin", " ").equals("Y")) {
				ConstantUtility.userIsLogin = true;
				username = sharedPreferences.getString("username", " ");
				password = sharedPreferences.getString("password", " ");
				if (username.equals("admin") && password.equals("root")) {
					nickname = sharedPreferences.getString("nickname", "");
					profile_tv.setText(nickname);
					profile_iv.setImageResource(R.mipmap.profile_login);
					//					navigationView.getMenu().setGroupVisible(R.id.group2, true);
					username = "";
					password = "";
					nickname = "";
				}
			}
		}
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

	public void closeSQLiteDatabase() {
		sqLiteDatabase.close();
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
	private void addNavigationProfileImageOnClickListener() {
		profile_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sharedPreferences.getString("isLogin", "N").equals("N")) {
					//					Log.i("ZRH","isLogin"+sharedPreferences.getString("isLogin",""));
					drawerLayout.closeDrawer(navigationView);
					Intent intent_to_login = new Intent();
					intent_to_login.setAction(ConstantUtility.INTENT_TO_LOGIN_KEY);
					startActivityInTransitionForResult(intent_to_login, ConstantUtility
							.ITENT_TO_LOGIN_REQUESTCODE, getTranstitionOptions(getTransitionPairs
							()).toBundle(), true);
				} else {
					Intent intent_to_profile = new Intent();
					intent_to_profile.setAction(ConstantUtility.INTENT_TO_PROFILE_KEY);
					startActivityInTransitionForResult(intent_to_profile, ConstantUtility
							.ITENT_TO_PROFILE_REQUESTCODE, getTranstitionOptions
							(getTransitionPairs()).toBundle(), true);
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
	* 隐藏navigationView的scrollBar
	* */
	private void hideNavigationViewScrollBar() {
		NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
		navigationMenuView.setVerticalScrollBarEnabled(false);
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
						bannerHandler.removeMessages(ConstantUtility.BANNER_SCROLL_KEY);
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
	*
	* */
	private void addNetStatusTextClickListener() {
		textView_netStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initZhiHuContent();
			}
		});
	}

	/*
	* 初始化网络连接客户端
	* */
	private void initOkhttpClient() {
		okHttpClient = new OkHttpClient().newBuilder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.build();
	}

	/*
	* 获取网络连接管理器
	* */
	private void initConnectivityManager() {
		connectivityManager = NetworkConnectivityUtility.getConnectivityManager();
	}

	/*
	* 获取网络连接相关状况
	* */
	private boolean getConnectivityStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Network[] networks = connectivityManager.getAllNetworks();
			if (networks != null && networks.length > 0) {
				for (int i = 0; i < networks.length; i++) {
					if (connectivityManager.getNetworkInfo(networks[i]).getState() == NetworkInfo
							.State.CONNECTED) {
						return true;
					}
				}
			} else {
				Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
						"咦，没有可用的网络吔", 3000);
				snackbar.show();
				if (textView_netStatus.getVisibility() == View.GONE) {
					textView_netStatus.setVisibility(View.VISIBLE);
				}
				textView_netStatus.setText("无可用网络\n\n点击刷新咯");
				if (floatingActionsMenu.getVisibility() == View.VISIBLE) {
					floatingActionsMenu.setVisibility(View.GONE);
				}
				//				Log.i("ZRH", "无可用网络");
				return false;
			}
		} else {
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			if (networkInfos != null && networkInfos.length > 0) {
				for (int i = 0; i < networkInfos.length; i++) {
					if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			} else {
				Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
						"咦，没有可用的网络吔", 3000);
				snackbar.show();
				if (textView_netStatus.getVisibility() == View.GONE) {
					textView_netStatus.setVisibility(View.VISIBLE);
				}
				textView_netStatus.setText("无可用网络\n\n点击刷新咯");
				if (floatingActionsMenu.getVisibility() == View.VISIBLE) {
					floatingActionsMenu.setVisibility(View.GONE);
				}
				return false;
			}
		}
		return false;
	}

	/*
	* 首次启动时加载最新的内容
	* */
	private void initZhiHuContent() {
		if (getConnectivityStatus()) {
			if (isFirstLoadingContent) {
				swipeRefreshLayout.setEnabled(false);
				Request request = new Request.Builder()
						.url(ConstantUtility.ZHIHUAPI_LATEST)
						.build();
				Call call = okHttpClient.newCall(request);
				call.enqueue(new Callback() {


					@Override
					public void onFailure(Call call, IOException e) {
						Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
								"咦，网络不太顺畅吔", 3000);
						snackbar.show();
						recyclerRefreshHandler.sendEmptyMessage(0x7629);
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
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
							message.what = ConstantUtility.RECYCLER_REFRESH_LATEST;
							recyclerRefreshHandler.sendMessage(message);
							isFirstLoadingContent = false;
						}
					}

				});
			}
		}
	}

	/*
	* 根据SDK版本修改UI
	* */
	private void changeUIBySDK_VER() {
		if (Build.VERSION.SDK_INT == 19) {
			//			viewPager.setFitsSystemWindows(false);
			//			appBarLayout.setFitsSystemWindows(false);
			//			coordinatorLayout.setFitsSystemWindows(false);
			//			collapsingToolbarLayout.setFitsSystemWindows(false);
			//			drawerLayout.setFitsSystemWindows(true);
			//			navigationView.setFitsSystemWindows(false);

			//			CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
			//			layoutParams.setMargins(0, LengthTransitionUtility.getStatusBarHeight(this), 0, 0);
			//			toolbar.setLayoutParams(layoutParams);
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
		mRunnableBackPressStatus = new Runnable() {
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
		//		frameLayout = (FrameLayout) findViewById(R.id.homeActivity_Content_main_FrameLayout);
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

		textView_netStatus = (TextView) findViewById(R.id.home_act_netstatus_TextView);
	}

	/*
	* 初始化Handler
	* */
	private void initHandler() {
		mhandler = new Handler();
		bannerHandler = new BannerHandler(this);
		recyclerRefreshHandler = new RecyclerRefreshHandler(this);
		//		notificationHandler = new NotificationHandler(this);
	}

	public void initNotificationService() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
				(getApplicationContext());
		boolean lr = preferences.getBoolean("loadRecommendation", true);
		if (lr) {
			intent_notirec = new Intent(this, InitNotiRecService.class);
			startService(intent_notirec);
		}
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

		//		drawerLayout.addDrawerListener(actionBarDrawerToggle);
		drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!ConstantUtility.userIsLogin) {
					if (getConnectivityStatus()) {
						initUser();
					}
				}
			}
		});
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

	public boolean getServiceStatus(String serviceName) {
		if (!TextUtils.isEmpty(serviceName)) {
			ActivityManager activityManager
					= (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
			ArrayList<RunningServiceInfo> runningServiceInfoList
					= (ArrayList<ActivityManager.RunningServiceInfo>) activityManager.getRunningServices(100);
			for (Iterator<RunningServiceInfo> iterator = runningServiceInfoList.iterator(); iterator.hasNext(); ) {
				RunningServiceInfo runningServiceInfo = iterator.next();
				if (serviceName.equals(runningServiceInfo.service.getClassName())) {
					return true;
				}
			}
		} else {
			return false;
		}
		return false;
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
				Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout, "再次点击退出键退出",
						Snackbar.LENGTH_SHORT);
				snackbar.show();
			}

			mhandler.postDelayed(mRunnableBackPressStatus, 2000);
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
		if (!swipeRefreshLayout.isRefreshing()) {
			swipeRefreshLayout.setRefreshing(true);
		}
		if (getConnectivityStatus()) {
			Request request = new Request.Builder()
					.url(ConstantUtility.ZHIHUAPI_LATEST)
					.build();

			Call call = okHttpClient.newCall(request);

			call.enqueue(new Callback() {


				@Override
				public void onFailure(Call call, IOException e) {
					Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
							"咦，网络不太顺畅吔", 3000);
					snackbar.show();
					recyclerRefreshHandler.sendEmptyMessage(ConstantUtility.RECYCLER_REFRESH_NEW_FAILURE);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (response.code() == 200) {
						zhiHuNewsLatestItemInfo_new = gson.fromJson(response.body().string(),
								ZhiHuNewsLatestItemInfo.class);
						Message message = new Message();
						message.what = ConstantUtility.RECYCLER_REFRESH_NEW;
						recyclerRefreshHandler.sendMessage(message);

					}
				}

			});
		}
	}

	@Override
	public void refreshOldNews() {
		swipeRefreshLayout.setRefreshing(true);

		if (getConnectivityStatus()) {
			refressh_old_date = current_simple_date_format;
			String oldUrl = ConstantUtility.ZHIHUAPI_BEFORE + refressh_old_date;
			Request request = new Request.Builder()
					.url(oldUrl)
					.build();
			Call call = okHttpClient.newCall(request);
			call.enqueue(new Callback() {


				@Override
				public void onFailure(Call call, IOException e) {
					Snackbar snackbar = SnackbarUtility.getSnackbarDefault(coordinatorLayout,
							"咦，网络不太顺畅吔", 3000);
					snackbar.show();
					recyclerRefreshHandler.sendEmptyMessage(ConstantUtility.RECYCLER_REFRESH_OLD_FAILURE);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (response.code() == 200) {
						//					Log.i("ZRH", "success in access url: " + response.request().urlString());
						zhiHuNewsLatestItemInfo_old = gson.fromJson(response.body
								().string(), ZhiHuNewsLatestItemInfo.class);
						Message message = new Message();
						message.what = ConstantUtility.RECYCLER_REFRESH_OLD;
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
				drawerLayout.closeDrawers();
				if (isFirstLoadingContent) {
					initZhiHuContent();
				} else {
					onRefresh();
				}
				break;
			case R.id.drawer_hot:
				intent = new Intent();
				intent.setAction(ConstantUtility.INTENT_TO_HOTNEWS_KEY);
				startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs())
						.toBundle(), true);
				break;
			case R.id.drawer_programa:
				break;
			case R.id.drawer_classify:
				break;

			//			case R.id.drawer_star:
			//				intent = new Intent();
			//				intent.setAction(ConstantUtility.INTENT_TO_STAR_HISTORY_PRAISE_KEY);
			//				intent.putExtra(ConstantUtility.INTENT_STRING_DATA_KEY, ConstantUtility.STAR_KEY);
			//				startActivity(intent);
			//
			//				break;
			//
			//			case R.id.drawer_good:
			//				intent = new Intent();
			//				intent.setAction(ConstantUtility.INTENT_TO_STAR_HISTORY_PRAISE_KEY);
			//				intent.putExtra(ConstantUtility.INTENT_STRING_DATA_KEY, ConstantUtility.PRAISE_KEY);
			//				startActivity(intent);
			//
			//				break;
			//
			//			case R.id.drawer_history:
			//				intent = new Intent();
			//				intent.setAction(ConstantUtility.INTENT_TO_STAR_HISTORY_PRAISE_KEY);
			//				intent.putExtra(ConstantUtility.INTENT_STRING_DATA_KEY, ConstantUtility.HISTORY_KEY);
			//				startActivity(intent);
			//
			//				break;
			//
			//			case R.id.drawer_notification:
			//				intent = new Intent();
			//				intent.setAction(ConstantUtility.INTENT_TO_NOTIFICATION_KEY);
			//				startActivity(intent);
			//
			//				break;
			//			case R.id.drawer_setting:
			//				intent = new Intent();
			//				intent.setAction(ConstantUtility.INTENT_TO_SETTINGDETAIL_KEY);
			//				startActivity(intent);
			//				break;
			//			case R.id.drawer_about:
			//				intent = new Intent();
			//				intent.setAction(ConstantUtility.INTENT_TO_ABOUT_KEY);
			//				startActivity(intent);
			//				break;
			case R.id.drawer_theme:
				if (getServiceStatus("com.rainnka.ZHNews.Service.InitNotiRecService")) {
					stopService(intent_notirec);
				}
				SharedPreferences preferences = getSharedPreferences("NightMode", MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
					editor.putInt("nightmode", AppCompatDelegate.MODE_NIGHT_YES);
					editor.apply();
					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
				} else {
					editor.putInt("nightmode", AppCompatDelegate.MODE_NIGHT_NO);
					editor.apply();
					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
				}
				HomeAty.this.recreate();
				break;

			case R.id.drawer_response:
				intent = new Intent();
				intent.setAction(ConstantUtility.INTENT_TO_FEEDBACK_KEY);
				startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs())
						.toBundle(), true);
				break;

			case R.id.drawer_exit:
				finish();
				break;
		}
		return true;
	}

	/*
	* 首页顶部banner开始自动轮播
	* */
	protected void bannerStartAutoScroll() {
		bannerHandler.sendEmptyMessageDelayed(ConstantUtility.BANNER_SCROLL_KEY, ConstantUtility.BANNER_SCROLL_INTERVAL);
	}

	/*
	* 发送自动滚动的消息
	* */
	protected void sendScrollMessage() {
		bannerHandler.removeMessages(ConstantUtility.BANNER_SCROLL_KEY);
		bannerHandler.sendEmptyMessageDelayed(ConstantUtility.BANNER_SCROLL_KEY,
				ConstantUtility.BANNER_SCROLL_INTERVAL);
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
		bannerHandler.removeMessages(ConstantUtility.BANNER_SCROLL_KEY);
	}

	@Override
	public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
		if (verticalOffset <= -315) {
			collapsingToolbarLayout.setTitle(getApplicationContext().getResources().getString(R
					.string.home_activity_content_collapsingtoolbar_title));
			bannerStopAutoScroll();
		} else {
			if (!bannerHandler.hasMessages(ConstantUtility.BANNER_SCROLL_KEY)) {
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

		WeakReference<HomeAty> homeActivityWeakReference;
		HomeAty homeAct;

		public BannerHandler(HomeAty homeAct) {
			this.homeActivityWeakReference = new WeakReference<>(homeAct);
			this.homeAct = this.homeActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case ConstantUtility.BANNER_SCROLL_KEY:
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

		WeakReference<HomeAty> homeActivityWeakReference;
		HomeAty homeAct;

		public RecyclerRefreshHandler(HomeAty homeAct) {
			this.homeActivityWeakReference = new WeakReference<>(homeAct);
			this.homeAct = this.homeActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case ConstantUtility.RECYCLER_REFRESH_LATEST:
					if (homeAct.textView_netStatus.getVisibility() == View.VISIBLE) {
						homeAct.textView_netStatus.setVisibility(View.GONE);
					}
					if (homeAct.floatingActionsMenu.getVisibility() == View.GONE) {
						homeAct.floatingActionsMenu.setVisibility(View.VISIBLE);
					}

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
								.home_aty_content_viewpager_item, null);
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

					homeAct.swipeRefreshLayout.setEnabled(true);

					//					homeAct.notificationHandler.sendEmptyMessage(0x4826);

					break;

				case ConstantUtility.RECYCLER_REFRESH_NEW:
					if (homeAct.textView_netStatus.getVisibility() == View.VISIBLE) {
						homeAct.textView_netStatus.setVisibility(View.GONE);
					}
					if (homeAct.floatingActionsMenu.getVisibility() == View.GONE) {
						homeAct.floatingActionsMenu.setVisibility(View.VISIBLE);
					}

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
						//						homeAct.recyclerView.scrollToPosition(0);
						SnackbarUtility.getSnackbarDefault(homeAct.coordinatorLayout,
								"成功更新" + tempList.size() + "条日报", 2000).show();
					} else {
						SnackbarUtility.getSnackbarLight(homeAct.coordinatorLayout,
								"已经是最新日报", Snackbar.LENGTH_SHORT);
					}
					if (homeAct.swipeRefreshLayout.isRefreshing()) {
						homeAct.swipeRefreshLayout.setRefreshing(false);
					}

					break;

				case ConstantUtility.RECYCLER_REFRESH_NEW_FAILURE:
					homeAct.swipeRefreshLayout.setRefreshing(false);
					break;

				case ConstantUtility.RECYCLER_REFRESH_OLD:

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
					//					homeAct.recyclerView.scrollToPosition(homeAct
					//							.homeActivityRecyclerViewAdapter.getItemCount() - homeAct
					//							.zhiHuNewsLatestItemInfo_old.stories.size() + 1);

					break;

				case ConstantUtility.RECYCLER_REFRESH_OLD_FAILURE:
					homeAct.swipeRefreshLayout.setRefreshing(false);
					break;
				case 0x7629:
					if (homeAct.textView_netStatus.getVisibility() == View.GONE) {
						homeAct.textView_netStatus.setVisibility(View.VISIBLE);
					}
					homeAct.textView_netStatus.setText("咦，网络不太顺畅吔\n\n点击刷新咯");
					if (homeAct.floatingActionsMenu.getVisibility() == View.VISIBLE) {
						homeAct.floatingActionsMenu.setVisibility(View.GONE);
					}
					break;
			}
		}
	}
}
