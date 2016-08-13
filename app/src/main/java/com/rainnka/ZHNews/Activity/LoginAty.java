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
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rainnka.ZHNews.Activity.Base.SwipeBackAty;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.ConstantUtility;
import com.rainnka.ZHNews.Utility.SnackbarUtility;

/**
 * Created by rainnka on 2016/6/26 21:52
 * Project name is ZHKUNews
 */
public class LoginAty extends SwipeBackAty {

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	CoordinatorLayout coordinatorLayout;
	Toolbar toolbar;
	TextInputEditText usernameTextInputEditText;
	TextInputEditText passwordTextInputEditText;
	TextView signInTextView;

	private boolean VALIDCODE = false;


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
		setContentView(R.layout.login_act);
		setupWindowAnimations();

		/*
		* 另statusbar悬浮于activity上面
		* */
		getWindow().getDecorView().setSystemUiVisibility(View
				.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

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
					intent.putExtra(ConstantUtility.DATAKEY_LOGIN_ATY, VALIDCODE);
					LoginAty.this.setResult(ConstantUtility.RESULTCODE_LOGIN_ATY, intent);
					getSwipeBackLayout().scrollToFinishActivity();
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
				onBackPressed();
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		getSwipeBackLayout().scrollToFinishActivity();
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
