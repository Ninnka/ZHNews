package com.rainnka.zhkunews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by rainnka on 2016/5/16 20:57
 * Project name is ZHKUNews
 */
public class TestActivity extends AppCompatActivity {

	public HomeActivityRecyclerViewIndicator homeActivityRecyclerViewIndicator;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout);
		homeActivityRecyclerViewIndicator = (HomeActivityRecyclerViewIndicator) findViewById(R.id
				.hrvi);
		Log.i("ZRH","first");
		homeActivityRecyclerViewIndicator.setRecyclerViewIndicatorAttribute(3, this);
	}
}
