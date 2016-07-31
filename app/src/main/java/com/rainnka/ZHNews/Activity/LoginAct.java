package com.rainnka.ZHNews.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.SnackbarUtility;

/**
 * Created by rainnka on 2016/6/26 21:52
 * Project name is ZHKUNews
 */
public class LoginAct extends BaseAct {

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	CoordinatorLayout coordinatorLayout;
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

		//设置editText的监听事件
		setEditTextChangedListener();
	}

	private void setEditTextChangedListener() {
		usernameTextInputEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				notifyEditTextChanged();

				//				if(!s.equals("")){
				//					String tempUser = passwordTextInputEditText.getText().toString();
				//					if(!tempUser.equals("")){
				//						signInTextView.setBackgroundDrawable(getApplicationContext()
				//								.getResources().getDrawable(R.drawable.login_act_valid_button));
				//					}else {
				//						signInTextView.setBackgroundDrawable(getApplicationContext()
				//								.getResources().getDrawable(R.drawable.login_act_invalid_button));
				//					}
				//				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		passwordTextInputEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				notifyEditTextChanged();

				//				if(!s.equals("")){
				//					String tempUser = usernameTextInputEditText.getText().toString();
				//					if(!tempUser.equals("")){
				//						signInTextView.setBackgroundDrawable(getApplicationContext()
				//								.getResources().getDrawable(R.drawable.login_act_valid_button));
				//					}else {
				//						signInTextView.setBackgroundDrawable(getApplicationContext()
				//								.getResources().getDrawable(R.drawable.login_act_invalid_button));
				//					}
				//				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void setSignInClickListener() {
		signInTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (usernameTextInputEditText.getText().toString().equals("admin") &&
						passwordTextInputEditText.getText().toString().equals("root")) {
					VALIDCODE = true;
					editor = sharedPreferences.edit();
					editor.putString("isLogin", "Y");
					editor.putString("username", usernameTextInputEditText.getText().toString());
					editor.putString("password", passwordTextInputEditText.getText().toString());
					editor.putString("nickname", "nickname");
					editor.apply();
					Intent intent = getIntent();
					intent.putExtra(DATAKEY, VALIDCODE);
					LoginAct.this.setResult(RESULTCODE, intent);
					LoginAct.this.finish();
				} else {
					SnackbarUtility.getSnackbarDefault(coordinatorLayout, "username or password " +
							"error", Snackbar.LENGTH_SHORT).show();
				}
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
		sharedPreferences = getSharedPreferences("up", MODE_PRIVATE);

		coordinatorLayout = (CoordinatorLayout) findViewById(R.id.login_activity_CoordinatorLayout);
		toolbar = (Toolbar) findViewById(R.id.login_activity_Toolbar);
		usernameTextInputEditText = (TextInputEditText) findViewById(R.id
				.login_activity_UserName_TextInputEditText);
		passwordTextInputEditText = (TextInputEditText) findViewById(R.id
				.login_activity_UserPassword_TextInputEditText);
		signInTextView = (TextView) findViewById(R.id.login_activity_signInButton);
	}

	protected void notifyEditTextChanged() {
		String tempUser = usernameTextInputEditText.getText().toString();
		String tempPW = passwordTextInputEditText.getText().toString();
		if (!tempUser.equals("") && !tempPW.equals("")) {
			modifySignInTextViewButton(true);
		} else {
			modifySignInTextViewButton(false);
		}
	}

	protected void modifySignInTextViewButton(boolean flag) {
		signInTextView.setClickable(flag);
		if (flag) {
			signInTextView.setBackgroundDrawable(getApplicationContext()
					.getResources().getDrawable(R.drawable.login_act_valid_button));
		} else {
			signInTextView.setBackgroundDrawable(getApplicationContext()
					.getResources().getDrawable(R.drawable.login_act_invalid_button));
		}

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
