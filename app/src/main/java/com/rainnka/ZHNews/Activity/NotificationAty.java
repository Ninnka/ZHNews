package com.rainnka.ZHNews.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainnka.ZHNews.Activity.Base.SwipeBackAty;
import com.rainnka.ZHNews.R;

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
		setContentView(R.layout.notificationpage_act);
		setupWindowAnimations();
		/*
		* 另statusbar悬浮于activity上面
		* */
		getWindow().getDecorView().setSystemUiVisibility(View
				.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

		initComponent();

		initToolbar();

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

	private void setupWindowAnimations() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			//			Fade fade = new Fade();
			//			fade.setDuration(500);
			//			Explode explode = new Explode();
			//			explode.setDuration(300);
			Slide slide = new Slide();
			slide.setSlideEdge(Gravity.RIGHT);
			slide.setDuration(300);
			getWindow().setEnterTransition(slide);
			//			getWindow().setReturnTransition(slide);
		}
	}

	public void initComponent() {
		toolbar = (Toolbar) findViewById(R.id.notificationpage_act_Toolbar);
		viewStubCompat = (ViewStubCompat) findViewById(R.id.notificationpage_act_ViewStub);
		textView = (TextView) findViewById(R.id.notificationpage_act_TextView);
	}

	public void initToolbar() {
		toolbar.setTitle("通知");
		toolbar.setTitleTextColor(getApplicationContext().getResources().getColor(R.color
				.md_white_1000));
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

	@Override
	public void onBackPressed() {
		getSwipeBackLayout().scrollToFinishActivity();
	}
}
