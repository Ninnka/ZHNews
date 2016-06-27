package com.rainnka.zhkunews.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.rainnka.zhkunews.R;

/**
 * Created by rainnka on 2016/6/26 21:52
 * Project name is ZHKUNews
 */
public class LoginAct extends AppCompatActivity {

	Toolbar toolbar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_act);

		//初始化组件
		initComponent();

		//设置toolbar
		settingToolbar();
	}

	private void settingToolbar() {
		toolbar.setTitle("Login");
		setSupportActionBar(toolbar);
	}

	private void initComponent(){
		toolbar = (Toolbar) findViewById(R.id.login_activity_Toolbar);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
