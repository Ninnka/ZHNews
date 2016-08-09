package com.rainnka.ZHNews.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.rainnka.ZHNews.R;

/**
 * Created by rainnka on 2016/7/23 13:46
 * Project name is ZHKUNews
 */
public class AboutAty extends BaseAty {

	Toolbar toolbar;
	CardView cardView_sources;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			// Translucent status bar
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
					.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager
//					.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		setContentView(R.layout.about_act);

		initComponent();

		initToolbar();

		addAuthorCardViewOnClickListener();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				finish();
				break;
		}
		return true;
	}

	private void initComponent() {
		toolbar = (Toolbar) findViewById(R.id.about_act_Toolbar);

		cardView_sources = (CardView) findViewById(R.id.about_act_CardView_sources);
	}

	private void initToolbar() {
		toolbar.setTitle("关于");
		toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void addAuthorCardViewOnClickListener() {
		cardView_sources.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent= new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(getString(R.string.about_act_sources));
				intent.setData(content_url);
				startActivity(intent);
			}
		});
	}
}
