package com.rainnka.zhkunews.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rainnka.zhkunews.R;

/**
 * Created by rainnka on 2016/6/26 21:52
 * Project name is ZHKUNews
 */
public class LoginAct extends AppCompatActivity {

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	Toolbar toolbar;
	TextInputEditText usernameTextInputEditText;
	TextInputEditText passwordTextInputEditText;
	TextView signInTextView;

	public final static String DATAKEY = "VALIDCODE";
	private boolean VALIDCODE = false;
	public final static int RESULTCODE = 0x4567;

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

		//设置登录点击事件
		setSignInClickListener();
	}

	private void setSignInClickListener() {
		signInTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (usernameTextInputEditText.getText().toString().equals("admin") &&
						passwordTextInputEditText.getText().toString().equals("root")) {
					VALIDCODE = true;
					editor = sharedPreferences.edit();
					editor.putString("isLogin","Y");
					editor.putString("username",usernameTextInputEditText.getText().toString());
					editor.putString("password",passwordTextInputEditText.getText().toString());
					editor.apply();
				}
				Intent intent = getIntent();
				intent.putExtra(DATAKEY, VALIDCODE);
				LoginAct.this.setResult(RESULTCODE, intent);
				LoginAct.this.finish();
			}
		});
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
		sharedPreferences = getSharedPreferences("up",MODE_PRIVATE);

		toolbar = (Toolbar) findViewById(R.id.login_activity_Toolbar);
		usernameTextInputEditText = (TextInputEditText) findViewById(R.id
				.login_activity_UserName_TextInputEditText);
		passwordTextInputEditText = (TextInputEditText) findViewById(R.id
				.login_activity_UserPassword_TextInputEditText);
		signInTextView = (TextView) findViewById(R.id.login_activity_signInButton);
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
