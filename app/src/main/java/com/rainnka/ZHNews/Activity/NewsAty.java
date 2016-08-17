package com.rainnka.ZHNews.Activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.rainnka.ZHNews.Activity.Base.SwipeBackAty;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemHot;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemInfo;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.ConstantUtility;
import com.rainnka.ZHNews.Utility.SQLiteCreateTableHelper;
import com.rainnka.ZHNews.Utility.SnackbarUtility;
import com.rainnka.ZHNews.Utility.TransitionHelper;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rainnka on 2016/5/15 16:19
 * Project name is ZHKUNews
 */
public class NewsAty extends SwipeBackAty implements AppBarLayout.OnOffsetChangedListener {

	protected CoordinatorLayout coordinatorLayout;
	protected AppBarLayout appBarLayout;
	protected CollapsingToolbarLayout collapsingToolbarLayout;
	protected Toolbar toolbar;
	protected ImageView imageView;
	protected WebView webView;
	protected ImageView imageView_praise;
	protected ImageView imageView_star;
	protected ImageView imageView_comments;
	protected LinearLayout linearLayout_webViewContainer;

	public OkHttpClient okHttpClient;
	public Request request;
	public Call call;

	public Gson gson;

	public ZhiHuNewsItemInfo zhiHuNewsItemInfo;

	public ZhiHuNewsItemInfo zhiHuNewsItemInfoFromHome;

	public StarOrPraiseHandler starOrPraiseHandler;
	public WebViewHandler webViewHandler;

	public String header = "<html><head><link href=\"%s\" type=\"text/css\" " +
			"rel=\"stylesheet\"/></head><body>";
	public String footer = "</body></html>";

	protected SQLiteDatabase sqLiteDatabase;

	//	public Boolean isGood = false;
	//	public Boolean isStar = false;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		//			getWindow().getDecorView().setSystemUiVisibility(View
		//					.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		//		}
		setContentView(R.layout.news_act);
		setupWindowAnimations();

		/*
		* 获取intent中的数据
		* */
		bindIntentInfo();

		/*
		* 初始化组件
		* */
		initComponent();

		/*
		* 沉浸式效果
		* 适应 （API19） android4.4的版本
		* */
		if (Build.VERSION.SDK_INT == 19) {
			//			coordinatorLayout.setFitsSystemWindows(false);
			//			appBarLayout.setFitsSystemWindows(false);
			//			CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
			//			layoutParams.setMargins(0, LengthTransitionUtility.getStatusBarHeight(this), 0, 0);
			//			toolbar.setLayoutParams(layoutParams);
		}

		/*
		* 设置toolbar
		* */
		initToolbarSetting();

		/*
		* 初始化webview
		* */
		initWebView();

		/*
		* 初始化handler
		* */
		initHandler();

		initSQLiteDatabase();

		/*
		* 添加点赞点击事件
		* */
		addPraiseImageViewOnClickListener();

		/*
		* 添加收藏点击事件
		* */
		addStarImageViewOnClickListener();

		/*
		* 添加评论点击事件
		* */
		addCommentsImageViewOnClickListener();

		/*
		* 初始化gson
		* */
		gson = new Gson();

		/*
		* 判断是否收藏
		* */
		judgeStarState();

		/*
		* 判断是否点赞
		* */
		judgePraiseState();

		/*
		* 格局获得信息类加载webview中的主要内容
		* */
		loadWebViewContent();

		/*
		* appbar滚动监听
		* */
		addAppBarOffsetChangedListener();

		/*
		*
		* */
		//		setActivityOnTouchEvent();
	}

	private void addCommentsImageViewOnClickListener() {
		imageView_comments.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setAction(ConstantUtility.INTENT_TO_COMMENTS_KET);
				intent.putExtra("id", zhiHuNewsItemInfoFromHome.id);
				startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs()).toBundle(),
						true);
			}
		});
	}

	private void addStarImageViewOnClickListener() {
		imageView_star.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ConstantUtility.userIsLogin) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							while (true) {
								if (!sqLiteDatabase.inTransaction()) {
									if (isStar()) {
										sqLiteDatabase.beginTransaction();
										sqLiteDatabase.delete("my_star", "ItemId like ?", new
												String[]{String.valueOf(zhiHuNewsItemInfoFromHome.id)});
										sqLiteDatabase.setTransactionSuccessful();
										sqLiteDatabase.endTransaction();
										if (starOrPraiseHandler != null) {
											starOrPraiseHandler.sendEmptyMessage(ConstantUtility
													.NEWS_TO_UNSTARED);
										}
									} else {
										sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_STAR_TABLE);
										ContentValues contentValues = new ContentValues();
										contentValues.put("ItemId", zhiHuNewsItemInfoFromHome.id);
										try {
											contentValues.put("ItemImage", zhiHuNewsItemInfoFromHome.images.get(0));
										} catch (Exception e) {
											contentValues.put("ItemImage", zhiHuNewsItemInfoFromHome.image);
										}
										contentValues.put("ItemTitle", zhiHuNewsItemInfoFromHome.title);
										sqLiteDatabase.beginTransaction();
										sqLiteDatabase.insert("my_star", null, contentValues);
										sqLiteDatabase.setTransactionSuccessful();
										sqLiteDatabase.endTransaction();
										if (starOrPraiseHandler != null) {
											starOrPraiseHandler.sendEmptyMessage(ConstantUtility
													.NEWS_TO_STARED);
										}
									}
									break;
								}
							}
						}
					}).start();

					//测试用
					//				Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM my_star", null);
					//				if (cursor != null) {
					//					if (cursor.moveToFirst()) {
					//						Log.i("ZRH", "ItemId: " + cursor.getString(1));
					//						Log.i("ZRH", "ItemImage: " + cursor.getString(2));
					//						Log.i("ZRH", "ItemTitle: " + cursor.getString(3));
					//					}
					//				}
					//				cursor.close();
				} else {
					SnackbarUtility.getSnackbarDefault(coordinatorLayout, "登录后可使用收藏功能", Snackbar
							.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void addPraiseImageViewOnClickListener() {
		imageView_praise.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ConstantUtility.userIsLogin) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							while (true) {
								if (!sqLiteDatabase.inTransaction()) {
									if (isPraise()) {
										sqLiteDatabase.beginTransaction();
										sqLiteDatabase.delete("my_praise", "ItemId like ?", new
												String[]{String.valueOf(zhiHuNewsItemInfoFromHome.id)});
										sqLiteDatabase.setTransactionSuccessful();
										sqLiteDatabase.endTransaction();
										if (starOrPraiseHandler != null) {
											starOrPraiseHandler.sendEmptyMessage(ConstantUtility
													.NEWS_TO_UNPRAISE);
										}
									} else {
										sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_PRAISE_TABLE);
										ContentValues contentValues = new ContentValues();
										contentValues.put("ItemId", zhiHuNewsItemInfoFromHome.id);
										try {
											contentValues.put("ItemImage", zhiHuNewsItemInfoFromHome.images.get(0));
										} catch (Exception e) {
											contentValues.put("ItemImage", zhiHuNewsItemInfoFromHome.image);
										}
										contentValues.put("ItemTitle", zhiHuNewsItemInfoFromHome.title);
										sqLiteDatabase.beginTransaction();
										sqLiteDatabase.insert("my_praise", null, contentValues);
										sqLiteDatabase.setTransactionSuccessful();
										sqLiteDatabase.endTransaction();
										if (starOrPraiseHandler != null) {
											starOrPraiseHandler.sendEmptyMessage(ConstantUtility
													.NEWS_TO_PRAISE);
										}
									}
									break;
								}
							}
						}
					}).start();
				} else {
					SnackbarUtility.getSnackbarDefault(coordinatorLayout, "登录后可使用点赞功能", Snackbar
							.LENGTH_SHORT).show();
				}

			}
		});


	}

	private void initToolbarSetting() {
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		loadCollapsingToolbarContentTitle();
	}

	private void initComponent() {
		coordinatorLayout = (CoordinatorLayout) findViewById(R.id.newsActivity_CoordinatorLayout);
		appBarLayout = (AppBarLayout) findViewById(R.id.newsActivity_AppBarLayout);
		collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id
				.newsActivity_CollapsingToolbarLayout);
		toolbar = (Toolbar) findViewById(R.id.newsActivity_Toolbar);
		imageView = (ImageView) findViewById(R.id.newsActivity_ImageView_InCollapsingToolbarLayout);
		webView = (WebView) findViewById(R.id.newsActivity_WebView);
		imageView_praise = (ImageView) findViewById(R.id.newsActivity_praise_ImageView);
		imageView_star = (ImageView) findViewById(R.id.newsActivity_star_ImageView);
		imageView_comments = (ImageView) findViewById(R.id.newsActivity_comments_ImageView);
		linearLayout_webViewContainer = (LinearLayout) findViewById(R.id
				.newsActivity_WebView_container);
	}

	private void initWebView() {
		webView.getSettings().setJavaScriptEnabled(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setDisplayZoomControls(false);
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
	}

	private void initHandler() {
		starOrPraiseHandler = new StarOrPraiseHandler(NewsAty.this);
		webViewHandler = new WebViewHandler(NewsAty.this);
	}

	private void initSQLiteDatabase() {
		if (sqLiteDatabase == null) {
			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(BaseApplication.getDATABASE_PATH
					() + "/myInfo.db3", null);
		}
	}

	private void bindIntentInfo() {
		Intent intent = getIntent();
		zhiHuNewsItemInfoFromHome = (ZhiHuNewsItemInfo) intent.getSerializableExtra(ConstantUtility.SER_KEY);
		if(zhiHuNewsItemInfoFromHome == null){
			ZhiHuNewsItemHot zhiHuNewsItemHot = (ZhiHuNewsItemHot) intent.getSerializableExtra(ConstantUtility
					.SER_KEY_HOTNEWS);
			zhiHuNewsItemInfoFromHome = new ZhiHuNewsItemInfo();
			zhiHuNewsItemInfoFromHome.id = zhiHuNewsItemHot.news_id;
			zhiHuNewsItemInfoFromHome.title = zhiHuNewsItemHot.title;
			zhiHuNewsItemInfoFromHome.url_hot = zhiHuNewsItemHot.url;
		}
	}

	/*
	* 根据获取的信息类加载头布局标题
	* */
	private void loadCollapsingToolbarContentTitle() {
		collapsingToolbarLayout.setTitle(zhiHuNewsItemInfoFromHome.title);
	}

	/*
	*
	* */
	private void loadCollapsingToolbarContentPic() {
		Glide.with(NewsAty.this)
				.load(zhiHuNewsItemInfo.image)
				.into(imageView);
	}

	/*
	* 格局获得信息类加载webview中的主要内容
	* */
	private void loadWebViewContent() {
		okHttpClient = new OkHttpClient().newBuilder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.build();
		String getInfoUrl = "";
		if(zhiHuNewsItemInfoFromHome.url_hot == null){
			getInfoUrl = ConstantUtility.getInfoByAPI + zhiHuNewsItemInfoFromHome.id;
		}else {
			getInfoUrl = zhiHuNewsItemInfoFromHome.url_hot;
		}
		Log.i("ZRH","getInfoUrl: "+getInfoUrl);
		request = new Request.Builder()
				.url(getInfoUrl)
				.build();
		call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response.code() == 200) {
					//					Log.i("ZRH", "code = " + response.code() + "\n" + response.body().string());
					try {
						zhiHuNewsItemInfo = gson.fromJson(response.body().string(),
								ZhiHuNewsItemInfo.class);
						if (webViewHandler != null) {
							webViewHandler.sendEmptyMessage(0x123);
						}
					} catch (Exception e) {
						Log.i("ZRH", e.getMessage());
					}
				}
			}
		});
	}

	public void starAnima() {
		AnimatorSet animatorSet = ConstantUtility.getAnimatorDelegate();
		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imageView_star, "alpha", 0f, 1f);
		ObjectAnimator rotateOut = ObjectAnimator.ofFloat(imageView_star, "rotation", 0f, 360f);
		ObjectAnimator scaleXOut = ObjectAnimator.ofFloat(imageView_star, "scaleX", 0.8f, 1.2f, 1f);
		ObjectAnimator scaleYOut = ObjectAnimator.ofFloat(imageView_star, "scaleX", 0.8f, 1.2f, 1f);
		animatorSet.playTogether(fadeOut, rotateOut, scaleXOut, scaleYOut);
		animatorSet.setDuration(800);
		animatorSet.start();
	}

	public void unStarAnima() {
		AnimatorSet animatorSet = ConstantUtility.getAnimatorDelegate();
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView_star, "alpha", 0f, 1f);
		ObjectAnimator rotateIn = ObjectAnimator.ofFloat(imageView_star, "rotation", 0f, -360f);
		ObjectAnimator scaleXIn = ObjectAnimator.ofFloat(imageView_star, "scaleX", 1.2f, 0.8f, 1f);
		ObjectAnimator scaleYIn = ObjectAnimator.ofFloat(imageView_star, "scaleX", 1.2f, 0.8f, 1f);
		animatorSet.playTogether(fadeIn, rotateIn, scaleXIn, scaleYIn);
		animatorSet.setDuration(800);
		animatorSet.start();
	}

	public void praiseAnima() {
		AnimatorSet animatorSet = ConstantUtility.getAnimatorDelegate();
		ObjectAnimator scaleXOut = ObjectAnimator.ofFloat(imageView_praise, "scaleX", 0.8f, 1.3f,
				1f);
		ObjectAnimator scaleYOut = ObjectAnimator.ofFloat(imageView_praise, "scaleY", 0.8f, 1.3f,
				1f);
		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imageView_praise, "alpha", 0.5f, 1f);
		animatorSet.playTogether(scaleXOut, scaleYOut, fadeOut);
		animatorSet.setDuration(1000);
		animatorSet.start();
	}

	public void unPraiseAnima() {
		AnimatorSet animatorSet = ConstantUtility.getAnimatorDelegate();
		ObjectAnimator scaleXIn = ObjectAnimator.ofFloat(imageView_praise, "scaleX", 1.3f, 0.8f,
				1f);
		ObjectAnimator scaleYIn = ObjectAnimator.ofFloat(imageView_praise, "scaleY", 1.3f, 0.8f,
				1f);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView_praise, "alpha", 0.5f, 1f);
		animatorSet.playTogether(scaleXIn, scaleYIn, fadeIn);
		animatorSet.setDuration(1000);
		animatorSet.start();
	}

	/*
	* 判断是否收藏
	* */
	public boolean isStar() {
		boolean star = false;
		String queryID = "SELECT * FROM my_star where ItemId like ?";
		try {
			while (true) {
				if (!sqLiteDatabase.inTransaction()) {
					sqLiteDatabase.beginTransaction();
					sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_STAR_TABLE);
					try {
						Cursor cursor = sqLiteDatabase.rawQuery(queryID, new String[]{String.valueOf
								(zhiHuNewsItemInfoFromHome.id)});
						if (cursor.getCount() > 0) {
							star = true;
						}
						cursor.close();
					} catch (Exception e) {
						Log.i("ZRH", e.toString());
					}
					sqLiteDatabase.setTransactionSuccessful();
					break;
				}
			}
		} catch (Exception e) {
			Log.i("ZRH", e.toString());
			Log.i("ZRH", e.getMessage());
			Log.i("ZRH", e.getLocalizedMessage());
		} finally {
			sqLiteDatabase.endTransaction();
		}

		return star;
	}

	/*
	* 判断是否点赞
	* */
	public boolean isPraise() {
		boolean praise = false;
		String queryID = "SELECT * FROM my_praise where ItemId like ?";

		try {
			while (true) {
				if (!sqLiteDatabase.inTransaction()) {
					sqLiteDatabase.beginTransaction();
					sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_PRAISE_TABLE);
					try {
						Cursor cursor = sqLiteDatabase.rawQuery(queryID, new String[]{String.valueOf
								(zhiHuNewsItemInfoFromHome.id)});
						if (cursor.getCount() > 0) {
							praise = true;
						}
						cursor.close();
					} catch (Exception e) {
						Log.i("ZRH", e.toString());
					}
					sqLiteDatabase.setTransactionSuccessful();
					break;
				}
			}
		} catch (Exception e) {
			Log.i("ZRH", e.toString());
			Log.i("ZRH", e.getMessage());
			Log.i("ZRH", e.getLocalizedMessage());
		} finally {
			sqLiteDatabase.endTransaction();
		}

		return praise;
	}

	private void judgeStarState() {
		if (ConstantUtility.userIsLogin) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg = new Message();
					Bundle bundle = new Bundle();
					bundle.putBoolean("isStar", isStar());
					msg.setData(bundle);
					msg.what = ConstantUtility.JUDGE_STAR_STATUS;
					if (starOrPraiseHandler != null) {
						starOrPraiseHandler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	private void judgePraiseState() {
		if (ConstantUtility.userIsLogin) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg = new Message();
					Bundle bundle = new Bundle();
					bundle.putBoolean("isPraise", isPraise());
					msg.setData(bundle);
					msg.what = ConstantUtility.JUDGE_PRAISE_STATUS;
					if (starOrPraiseHandler != null) {
						starOrPraiseHandler.sendMessage(msg);
					}
				}
			}).start();
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
		starOrPraiseHandler.removeCallbacksAndMessages(null);
		starOrPraiseHandler = null;
		webViewHandler.removeCallbacksAndMessages(null);
		webViewHandler = null;
		if (sqLiteDatabase.isOpen()) {
			sqLiteDatabase.close();
			sqLiteDatabase = null;
		}
		linearLayout_webViewContainer.removeAllViews();
		if (webView != null) {
			webView.clearHistory();
			webView.clearCache(true);
			webView.loadUrl("about:blank");
			webView.pauseTimers();
			//			webView.destroy();
			webView = null;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();

				//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				//					Log.i("ZRH", "finishAfterTransition");
				//					finishAfterTransition();
				//				} else {
				//					Log.i("ZRH", "finish");
				//					finish();
				//				}

				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//		getMenuInflater().inflate(R.menu.news_act_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
		if (verticalOffset <= -315) {
			collapsingToolbarLayout.setTitle("");
		} else {
			collapsingToolbarLayout.setTitle(zhiHuNewsItemInfoFromHome.title);
		}
	}

	/*
	* appbar监听事件
	* */
	private void addAppBarOffsetChangedListener() {
		appBarLayout.addOnOffsetChangedListener(this);
	}


	/*
	* 静态内部类--处理数据库返回的信息
	* 判断是否收藏或点赞
	* */
	static class StarOrPraiseHandler extends Handler {
		WeakReference<NewsAty> newsActivityWeakReference;
		NewsAty newsAct;

		public StarOrPraiseHandler(NewsAty newsAct) {
			this.newsActivityWeakReference = new WeakReference<>(newsAct);
			this.newsAct = this.newsActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ConstantUtility.JUDGE_STAR_STATUS:
					if (msg.getData().getBoolean("isStar")) {
						newsAct.imageView_star.setImageResource(R.drawable.stared);
					} else {
						newsAct.imageView_star.setImageResource(R.drawable.unstared);
					}
					break;
				case ConstantUtility.JUDGE_PRAISE_STATUS:
					if (msg.getData().getBoolean("isPraise")) {
						newsAct.imageView_praise.setImageResource(R.drawable.good);
					} else {
						newsAct.imageView_praise.setImageResource(R.drawable.ungood);
					}
					break;
				case ConstantUtility.NEWS_TO_STARED:
					newsAct.imageView_star.setImageResource(R.drawable.stared);
					newsAct.starAnima();
					break;
				case ConstantUtility.NEWS_TO_UNSTARED:
					newsAct.imageView_star.setImageResource(R.drawable.unstared);
					newsAct.unStarAnima();
					break;
				case ConstantUtility.NEWS_TO_PRAISE:
					newsAct.imageView_praise.setImageResource(R.drawable.good);
					newsAct.praiseAnima();
					break;
				case ConstantUtility.NEWS_TO_UNPRAISE:
					newsAct.imageView_praise.setImageResource(R.drawable.ungood);
					newsAct.unPraiseAnima();
					break;
			}
		}
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

	/*
	* 静态内部类--处理返回的json信息
	* 加载webVie内容
	* */
	static class WebViewHandler extends Handler {

		WeakReference<NewsAty> newsActivityWeakReference;
		NewsAty newsAct;

		public WebViewHandler(NewsAty newsAct) {
			this.newsActivityWeakReference = new WeakReference<>(newsAct);
			this.newsAct = this.newsActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			newsAct.loadCollapsingToolbarContentPic();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(String.format(newsAct.header, newsAct
					.zhiHuNewsItemInfo.css.get(0)));
			stringBuilder.append(newsAct.zhiHuNewsItemInfo.body);
			stringBuilder.append(newsAct.footer);
			this.newsAct.webView.loadData(stringBuilder.toString(), "text/html; " +
					"charset=UTF-8", null);
		}
	}
}
