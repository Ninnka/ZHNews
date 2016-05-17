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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by rainnka on 2016/5/15 16:19
 * Project name is ZHKUNews
 */
public class NewsActivity extends AppCompatActivity {

	protected CoordinatorLayout coordinatorLayout;
	protected AppBarLayout appBarLayout;
	protected CollapsingToolbarLayout collapsingToolbarLayout;
	protected Toolbar toolbar;
	protected ImageView imageView;
	//	protected TextView textView;
	protected WebView webView;

	public OkHttpClient okHttpClient;
	public Request request;
	public Call call;
	public FormEncodingBuilder builder = new FormEncodingBuilder();

	public Gson gson;

	public ZhiHuNewsItemInfo zhiHuNewsItemInfo;

	public WebViewHandler webViewHandler;

	public String header = "<html><head><link href=\"%s\" type=\"text/css\" " +
			"rel=\"stylesheet\"/></head><body>";
	public String footer = "</body></html>";

	public Boolean isGood = false;
	public Boolean isStar = false;

	/*
	* 用于接收token
	* */
	//	String token;

	/*
	* 用于接收一个信息类
	* */
	public HomeActivityRecyclerViewItemInfo homeActivityRecyclerViewItemInfo;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_activity);

		/*
		* 获取intent中的数据
		* */
		getInfomationFromIntent();

		/*
		* 初始化组件
		* */
		initComponent();

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
		* 初始化gson
		* */
		gson = new Gson();

		/*
		* 初始化okhttpclient相关
		* */
		okHttpClient = new OkHttpClient();
		request = new Request.Builder()
				.url("http://news-at.zhihu.com/api/4/news/8309359")
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
		//		textView = (TextView) findViewById(R.id.newsActivity_TextView_InContent);
		webView = (WebView) findViewById(R.id.newsActivity_WebView);
	}

	private void initWebView() {
		webView.getSettings().setJavaScriptEnabled(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setDisplayZoomControls(false);
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
	}

	private void initHandler() {
		webViewHandler = new WebViewHandler(NewsActivity.this);
	}

	private void getInfomationFromIntent() {
		Intent intent = getIntent();
		homeActivityRecyclerViewItemInfo = (HomeActivityRecyclerViewItemInfo) intent.getSerializableExtra(HomeActivity.SER_KEY);
	}

	/*
	* 根据获取的信息类加载头布局实际信息
	* */
	private void loadCollapsingToolbarContent() {
		//		imageView.setImageResource();
		Glide.with(NewsActivity.this)
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
						//						Log.i("ZRH", "" + zhiHuNewsItemInfo.body);
						webViewHandler.sendEmptyMessage(0x123);
					} catch (Exception e) {
						Log.i("ZRH", e.getMessage());
					}
				}
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
		webViewHandler.removeMessages(0x123);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.news_activity_menu_good:
				if (!isGood) {
					item.setIcon(R.drawable.good);
					isGood = true;
				} else {
					item.setIcon(R.drawable.ungood);
					isGood = false;
				}
				break;
			case R.id.news_activity_menu_star:
				if (!isStar) {
					item.setIcon(R.drawable.stared);
					isStar = true;
				} else {
					item.setIcon(R.drawable.unstared);
					isStar = false;
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.news_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	static class WebViewHandler extends Handler {

		WeakReference<NewsActivity> newsActivityWeakReference;
		NewsActivity newsActivity;

		public WebViewHandler(NewsActivity newsActivity) {
			this.newsActivityWeakReference = new WeakReference<>(newsActivity);
			this.newsActivity = this.newsActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			newsActivity.loadCollapsingToolbarContent();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(String.format(newsActivity.header, newsActivity
					.zhiHuNewsItemInfo.css.get(0)));
			stringBuilder.append(newsActivity.zhiHuNewsItemInfo.body);
			stringBuilder.append(newsActivity.footer);
			this.newsActivity.webView.loadData(stringBuilder.toString(), "text/html; " +
					"charset=UTF-8", null);
		}
	}
}
