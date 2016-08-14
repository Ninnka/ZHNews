package com.rainnka.ZHNews.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainnka.ZHNews.Activity.Base.SwipeBackAty;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.LengthConverterUtility;

/**
 * Created by rainnka on 2016/7/22 10:40
 * Project name is ZHKUNews
 */
public class NotificationAty extends SwipeBackAty {

	Toolbar toolbar;
	ViewStubCompat viewStubCompat;
	TextView textView;
	ImageView imageView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//			Window window = getWindow();
//			// Translucent status bar
//			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
//					.LayoutParams.FLAG_TRANSLUCENT_STATUS);
////			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager
////					.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}
		setContentView(R.layout.notificationpage_aty);
		setupWindowAnimations();

		/*
		* 另statusbar悬浮于activity上面
		* */
		setFullScreenLayout();

		initComponent();

		initToolbarSetting();

		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textView.setVisibility(View.GONE);
				try {
					viewStubCompat.inflate();
				}catch (Exception e){
					imageView.setVisibility(View.VISIBLE);
				}
				imageView = (ImageView) findViewById(R.id.notificationpage_act_maincontent_ImageView);
				imageView.setImageResource(R.drawable.bg2);
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						imageView.setVisibility(View.GONE);
						textView.setVisibility(View.VISIBLE);
					}
				});
			}
		});
	}

	public void initComponent() {
		toolbar = (Toolbar) findViewById(R.id.notificationpage_act_Toolbar);
		viewStubCompat = (ViewStubCompat) findViewById(R.id.notificationpage_act_ViewStub);
		textView = (TextView) findViewById(R.id.notificationpage_act_TextView);
	}

	public void initToolbarSetting() {
		toolbar.setTitle("通知");
		toolbar.setTitleTextColor(getApplicationContext().getResources().getColor(R.color
				.md_white_1000));
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
		layoutParams.setMargins(0, LengthConverterUtility.dip2px(BaseApplication
				.getBaseApplicationContext(), 24), 0, 0);
		toolbar.setLayoutParams(layoutParams);
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

}
