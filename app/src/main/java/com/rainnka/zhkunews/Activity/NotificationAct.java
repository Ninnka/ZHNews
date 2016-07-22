package com.rainnka.zhkunews.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainnka.zhkunews.R;

/**
 * Created by rainnka on 2016/7/22 10:40
 * Project name is ZHKUNews
 */
public class NotificationAct extends AppCompatActivity {

	Toolbar toolbar;
	ViewStubCompat viewStubCompat;
	TextView textView;
	ImageView imageView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notificationpage_act);

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
				finish();
				break;
		}
		return true;
	}
}
