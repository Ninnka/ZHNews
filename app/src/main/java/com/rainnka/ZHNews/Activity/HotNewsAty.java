package com.rainnka.ZHNews.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.rainnka.ZHNews.Activity.Base.SwipeBackAty;
import com.rainnka.ZHNews.Adapter.HotNewsAtyRecvAdp;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.Bean.HotNews;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemHot;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.LengthConverterUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by rainnka on 2016/8/16 15:02
 * Project name is ZHKUNews
 */
public class HotNewsAty extends SwipeBackAty {

	/********
	 * main self members
	 *********/
	protected Toolbar toolbar;
	protected RecyclerView recyclerView;

	public HotNewsAtyRecvAdp hotNewsAtyRecvAdp;

	public List<ZhiHuNewsItemHot> zhiHuNewsItemHotList;

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

		initComponent();
		initToolbarSetting();

		initRecyclerViewSetting();

		retrofit = new Retrofit.Builder()
				.baseUrl("http://news-at.zhihu.com")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		HotNewsService hotNewsService = retrofit.create(HotNewsService.class);
		Call<HotNews> hotNews = hotNewsService.hot("3");
		Log.i("ZRH", "builder call");

		hotNews.enqueue(new Callback<HotNews>() {
			@Override
			public void onResponse(Call<HotNews> call, Response<HotNews> response) {
				if (response.code() == 200) {
					Log.i("ZRH", "success");
				}
			}

			@Override
			public void onFailure(Call<HotNews> call, Throwable t) {

			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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

	public void initRecyclerViewSetting() {
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
		gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(gridLayoutManager);
		zhiHuNewsItemHotList = new ArrayList<>();
		hotNewsAtyRecvAdp = new HotNewsAtyRecvAdp(this);
		hotNewsAtyRecvAdp.setZhiHuNewsItemHotList(zhiHuNewsItemHotList);
		recyclerView.setAdapter(hotNewsAtyRecvAdp);
	}

	public void initZhihuHotNewsContent() {

	}

	/****************************
	 * main inner class
	 *****************************/

	public interface HotNewsService {
		@GET("/api/{v}/news/hot")
		Call<HotNews> hot(@Path("v") String v);
	}

}
