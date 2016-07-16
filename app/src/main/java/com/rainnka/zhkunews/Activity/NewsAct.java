package com.rainnka.zhkunews.Activity;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.rainnka.zhkunews.Bean.ZhiHuNewsItemInfo;
import com.rainnka.zhkunews.R;
import com.rainnka.zhkunews.Utility.LengthTransitionUtility;
import com.rainnka.zhkunews.Utility.SQLiteCreateTableHelper;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by rainnka on 2016/5/15 16:19
 * Project name is ZHKUNews
 */
public class NewsAct extends AppCompatActivity {

	protected CoordinatorLayout coordinatorLayout;
	protected AppBarLayout appBarLayout;
	protected CollapsingToolbarLayout collapsingToolbarLayout;
	protected Toolbar toolbar;
	protected ImageView imageView;
	protected WebView webView;
	protected ImageView imageView_praise;
	protected ImageView imageView_star;

	public OkHttpClient okHttpClient;
	public Request request;
	public Call call;

	public Gson gson;

	public ZhiHuNewsItemInfo zhiHuNewsItemInfo;

	public ZhiHuNewsItemInfo zhiHuNewsItemInfoFromHome;

	public WebViewHandler webViewHandler;

	public String header = "<html><head><link href=\"%s\" type=\"text/css\" " +
			"rel=\"stylesheet\"/></head><body>";
	public String footer = "</body></html>";

	protected SQLiteDatabase sqLiteDatabase;

	//	public Boolean isGood = false;
	//	public Boolean isStar = false;

	public static final String getInfoByAPI = "http://news-at.zhihu.com/api/4/news/";


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.news_act);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Fade enterTransition = new Fade();
			enterTransition.setDuration(600);
			//			enterTransition.excludeTarget(R.id.newsActivity_WebView, true);

			Fade returnTransition = new Fade();
			returnTransition.setDuration(500);

			getWindow().setEnterTransition(enterTransition);
			getWindow().setReturnTransition(returnTransition);
		}

		/*
		* 获取intent中的数据
		* */
		getInfomationFromIntent();

		/*
		* 初始化组件
		* */
		initComponent();

		/*
		* 沉浸式效果
		* 适应 （API19） android4.4的版本
		* */
		if (Build.VERSION.SDK_INT == 19) {
			appBarLayout.setFitsSystemWindows(false);

			CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
			layoutParams.setMargins(0, LengthTransitionUtility.getStatusBarHeight(this), 0, 0);
			toolbar.setLayoutParams(layoutParams);
		}

		/*
		* 设置toolbar
		* */
		initToolbar();

		/*
		* 初始化webview
		* */
		initWebView();

		/*
		* 初始化handler
		* */
		initHandler();

		/*
		* 添加点赞点击事件
		* */
		addPraiseImageViewOnClickListener();

		/*
		* 添加收藏点击事件
		* */
		addStarImageViewOnClickListener();

		/*
		* 初始化gson
		* */
		gson = new Gson();

		/*
		* 初始化okhttpclient相关
		* */
		okHttpClient = new OkHttpClient();
		String getInfoUrl = getInfoByAPI + zhiHuNewsItemInfoFromHome.id;
		//		Log.i("ZRH","getInfoUrl: "+getInfoUrl);
		request = new Request.Builder()
				.url(getInfoUrl)
				.build();
		call = okHttpClient.newCall(request);

		/*
		* 根据获取的信息类加载头布局实际信息
		* */
		//		loadCollapsingToolbarContent();

		/*
		* 格局获得信息类加载webview中的主要内容
		* */
		loadWebViewContent();


		/*
		* 判断是否收藏
		* */
		judgeStarState();

		/*
		* 判断是否点赞
		* */
		judgePraiseState();

	}

	private void addStarImageViewOnClickListener() {
		imageView_star.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sqLiteDatabase == null) {
					try {
						sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(NewsAct.this
								.getFilesDir().toString() + "/myInfo.db3", null);
					} catch (Exception e) {
						Log.i("ZRH", e.toString());
					}
				}

				if (isStar()) {
					sqLiteDatabase.delete("my_star", "ItemId like ?", new
							String[]{String.valueOf(zhiHuNewsItemInfoFromHome.id)});
					imageView_star.setImageResource(R.drawable.unstared);
				} else {
					sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_STAR_TABLE);
					ContentValues contentValues = new ContentValues();
					contentValues.put("ItemId", zhiHuNewsItemInfoFromHome.id);
					try{
						contentValues.put("ItemImage", zhiHuNewsItemInfoFromHome.images.get(0));
					}catch (Exception e){
						contentValues.put("ItemImage", zhiHuNewsItemInfoFromHome.image);
					}
					contentValues.put("ItemTitle", zhiHuNewsItemInfoFromHome.title);
					sqLiteDatabase.insert("my_star", null, contentValues);
					imageView_star.setImageResource(R.drawable.stared);
				}

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
			}
		});
	}

	private void addPraiseImageViewOnClickListener() {
		imageView_praise.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sqLiteDatabase == null) {
					try {
						sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(NewsAct.this
								.getFilesDir().toString() + "/myInfo.db3", null);
					} catch (Exception e) {
						Log.i("ZRH", e.toString());
					}
				}

				if (isPraise()) {
					sqLiteDatabase.delete("my_praise", "ItemId like ?", new
							String[]{String.valueOf(zhiHuNewsItemInfoFromHome.id)});
					imageView_praise.setImageResource(R.drawable.ungood);
				} else {
					sqLiteDatabase.execSQL(SQLiteCreateTableHelper.CREATE_PRAISE_TABLE);
					ContentValues contentValues = new ContentValues();
					contentValues.put("ItemId", zhiHuNewsItemInfoFromHome.id);
					try{
						contentValues.put("ItemImage", zhiHuNewsItemInfoFromHome.images.get(0));
					}catch (Exception e){
						contentValues.put("ItemImage", zhiHuNewsItemInfoFromHome.image);
					}
					contentValues.put("ItemTitle", zhiHuNewsItemInfoFromHome.title);
					sqLiteDatabase.insert("my_praise", null, contentValues);
					imageView_praise.setImageResource(R.drawable.good);
				}
			}
		});
	}

	private void initToolbar() {
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
	}

	private void initWebView() {
		webView.getSettings().setJavaScriptEnabled(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setDisplayZoomControls(false);
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
	}

	private void initHandler() {
		webViewHandler = new WebViewHandler(NewsAct.this);
	}

	private void getInfomationFromIntent() {
		Intent intent = getIntent();
		//		homeActivityRecyclerViewItemInfo = (HomeActivityRecyclerViewItemInfo) intent.getSerializableExtra(HomeActivity.SER_KEY);
		zhiHuNewsItemInfoFromHome = (ZhiHuNewsItemInfo) intent.getSerializableExtra(HomeAct.SER_KEY);
	}

	/*
	* 根据获取的信息类加载头布局实际信息
	* */
	private void loadCollapsingToolbarContent() {
		//		imageView.setImageResource();
		Glide.with(NewsAct.this)
				.load(zhiHuNewsItemInfo.image)
				.into(imageView);
		collapsingToolbarLayout.setTitle(zhiHuNewsItemInfo.title);
		//		textView.setText(homeActivityRecyclerViewItemInfo.title);
	}

	/*
	* 格局获得信息类加载webview中的主要内容
	* */
	private void loadWebViewContent() {
		//		webView.loadUrl("http://news-at.zhihu.com/api/4/news/8288126");
		//		webView.loadDataWithBaseURL();
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Log.i("ZRH", "failure");
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.code() == 200) {
					//					Log.i("ZRH", "code = " + response.code() + "\n" + response.body().string());
					try {
						zhiHuNewsItemInfo = gson.fromJson(response.body().string(),
								ZhiHuNewsItemInfo.class);
						webViewHandler.sendEmptyMessage(0x123);
					} catch (Exception e) {
						Log.i("ZRH", e.getMessage());
					}
				}
			}
		});
	}

	/*
	* 判断是否收藏
	* */
	public boolean isStar() {
		boolean star = false;
		String queryID = "SELECT * FROM my_star where ItemId like ?";
		if (sqLiteDatabase == null) {
			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(NewsAct.this
					.getFilesDir().toString() + "/myInfo.db3", null);
		}
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
		return star;
	}

	/*
	* 判断是否点赞
	* */
	public boolean isPraise() {
		boolean praise = false;
		String queryID = "SELECT * FROM my_praise where ItemId like ?";
		if (sqLiteDatabase == null) {
			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(NewsAct.this
					.getFilesDir().toString() + "/myInfo.db3", null);
		}
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
		return praise;
	}

	private void judgeStarState() {
		if (isStar()) {
			imageView_star.setImageResource(R.drawable.stared);
		}
	}

	private void judgePraiseState() {
		if (isPraise()) {
			imageView_praise.setImageResource(R.drawable.good);
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
		if (webView != null) {
			webView.removeAllViews();
			webView.onPause();
			webView.destroy();
		}
		super.onDestroy();
		webViewHandler.removeMessages(0x123);
		if (sqLiteDatabase != null) {
			sqLiteDatabase.close();
		}
		sqLiteDatabase = null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();

				//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				//					Log.i("ZRH", "finishAfterTransition");
				//					finishAfterTransition();
				//				} else {
				//					Log.i("ZRH", "finish");
				//					finish();
				//				}

				break;
			//			case R.id.news_activity_menu_good:
			//				if (!isGood) {
			//					item.setIcon(R.drawable.good);
			//					isGood = true;
			//				} else {
			//					item.setIcon(R.drawable.ungood);
			//					isGood = false;
			//				}
			//				break;
			//			case R.id.news_activity_menu_star:
			//				if (!isStar) {
			//					item.setIcon(R.drawable.stared);
			//					isStar = true;
			//				} else {
			//					item.setIcon(R.drawable.unstared);
			//					isStar = false;
			//				}
			//				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();

		//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		//			Log.i("ZRH", "finishAfterTransition");
		//			finishAfterTransition();
		//		} else {
		//			Log.i("ZRH", "finish");
		//			finish();
		//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.news_act_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	static class WebViewHandler extends Handler {

		WeakReference<NewsAct> newsActivityWeakReference;
		NewsAct newsAct;

		public WebViewHandler(NewsAct newsAct) {
			this.newsActivityWeakReference = new WeakReference<>(newsAct);
			this.newsAct = this.newsActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			newsAct.loadCollapsingToolbarContent();
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
