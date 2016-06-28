package com.rainnka.zhkunews.Activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.rainnka.zhkunews.R;

/**
 * Created by rainnka on 2016/6/26 21:52
 * Project name is ZHKUNews
 */
public class LoginAct extends AppCompatActivity {

	Toolbar toolbar;
	TextInputEditText usernameTextInputEditText;
	TextInputEditText passwordTextInputEditText;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_act);

		//初始化组件
		initComponent();

		//设置toolbar
		initSettingToolbar();

		//设置输入框中的左图像
		initDrawableTextInputEditText();
	}

	private void initDrawableTextInputEditText() {
		Drawable[] drawableUsernames = usernameTextInputEditText.getCompoundDrawables();
		drawableUsernames[0].setBounds(0, 0, 70, 70);
		usernameTextInputEditText.setCompoundDrawables(drawableUsernames[0], null, null, null);
		//		drawableUsername.setBounds(LengthTransitionUtility.dip2px(getApplicationContext(), 5),
		//				LengthTransitionUtility.dip2px(getApplicationContext(), 5), LengthTransitionUtility
		//						.dip2px(getApplicationContext(), 5), LengthTransitionUtility.dip2px
		//						(getApplicationContext(), 5));
		//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
		//			passwordTextInputEditText.setCompoundDrawablesRelativeWithIntrinsicBounds
		//					(drawableUsername, null, null, null);
		//		}
		Drawable[] drawablePasswords = passwordTextInputEditText.getCompoundDrawables();
		drawablePasswords[0].setBounds(0, 0, 60, 60);
		passwordTextInputEditText.setCompoundDrawables(drawablePasswords[0], null, null, null);
	}

	private void initSettingToolbar() {
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.setTitle("Login");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void initComponent() {
		toolbar = (Toolbar) findViewById(R.id.login_activity_Toolbar);
		usernameTextInputEditText = (TextInputEditText) findViewById(R.id
				.login_activity_UserName_TextInputEditText);
		passwordTextInputEditText = (TextInputEditText) findViewById(R.id
				.login_activity_UserPassword_TextInputEditText);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
		}
		return true;
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
