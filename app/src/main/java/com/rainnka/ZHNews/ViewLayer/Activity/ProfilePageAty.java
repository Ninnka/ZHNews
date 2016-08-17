package com.rainnka.ZHNews.ViewLayer.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainnka.ZHNews.ViewLayer.Activity.Base.SwipeBackAty;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.ConstantUtility;
import com.rainnka.ZHNews.Utility.LengthConverterUtility;
import com.rainnka.ZHNews.Utility.SnackbarUtility;
import com.rainnka.ZHNews.Utility.TransitionHelper;

/**
 * Created by rainnka on 2016/7/10 15:26
 * Project name is ZHKUNews
 */
public class ProfilePageAty extends SwipeBackAty {

	LinearLayout linearLayout;
	Toolbar toolbar;
	ImageView imageView_pencil;
	EditText editText_username;
	TextView textViewLogout;

	LinearLayout linearLayout_starCollection;
	LinearLayout linearLayout_praiseCollection;
	LinearLayout linearLayout_historyCollection;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	InputMethodManager imeManager;

	Intent intent;

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
		setContentView(R.layout.profilepage_aty);
		setupWindowAnimations();

		/*
		* 另statusbar悬浮于activity上面
		* */
		setFullScreenLayout();

		//
		initComponent();

		//
		initToolbarSetting();

		//
		initUsernameEditText();

		//
		addImageViewPencilClickListener();

		//
		addTextViewLogoutClickListener();

		//
		addCollectionClickListener();
	}

	private void addCollectionClickListener() {
		linearLayout_starCollection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				intent = new Intent();
				intent.setAction(ConstantUtility.INTENT_TO_STAR_HISTORY_PRAISE_KEY);
				intent.putExtra(ConstantUtility.INTENT_STRING_DATA_KEY, ConstantUtility.STAR_KEY);
				startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs())
						.toBundle(), true);
			}
		});
		linearLayout_praiseCollection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				intent = new Intent();
				intent.setAction(ConstantUtility.INTENT_TO_STAR_HISTORY_PRAISE_KEY);
				intent.putExtra(ConstantUtility.INTENT_STRING_DATA_KEY, ConstantUtility.PRAISE_KEY);
				startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs())
						.toBundle(), true);
			}
		});
		linearLayout_historyCollection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				intent = new Intent();
				intent.setAction(ConstantUtility.INTENT_TO_STAR_HISTORY_PRAISE_KEY);
				intent.putExtra(ConstantUtility.INTENT_STRING_DATA_KEY, ConstantUtility.HISTORY_KEY);
				startActivityInTransition(intent, getTranstitionOptions(getTransitionPairs())
						.toBundle(), true);
			}
		});
	}

	public void startActivityInTransition(Intent intent, Bundle bundle, boolean transitionFlag) {
		if (transitionFlag) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				startActivity(intent, bundle);
			} else {
				startActivity(intent);
			}
		} else {
			startActivity(intent);
		}

	}

	public void startActivityInTransitionForResult(Intent intent, int code, Bundle bundle, boolean transitionFlag) {
		if (transitionFlag) {
			startActivityForResult(intent, code, bundle);
		}else {
			startActivityForResult(intent, code);
		}
	}

	public Pair<View, String>[] getTransitionPairs() {
		Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants
				(this, false);
		return pairs;
	}

	public ActivityOptionsCompat getTranstitionOptions(Pair<View, String>[] pairs) {
		ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
				.makeSceneTransitionAnimation(this, pairs);
		return activityOptionsCompat;
	}

	private void addTextViewLogoutClickListener() {
		textViewLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor = sharedPreferences.edit();
				editor.putString("isLogin", "N");
				editor.remove("username");
				editor.remove("password");
				editor.remove("nickname");
				editor.apply();
				Intent intent = getIntent();
				intent.putExtra("VALIDCODE", false);
				ProfilePageAty.this.setResult(ConstantUtility.RESULTCODE_PROFILE_ATY, intent);
				getSwipeBackLayout().scrollToFinishActivity();
			}
		});
	}

	private void initUsernameEditText() {
		String userName = sharedPreferences.getString("nickname", "");
		editText_username.setText(userName);
		editText_username.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (editText_username.hasFocus()) {
					imeManager.showSoftInput(editText_username, InputMethodManager.RESULT_SHOWN);
					return false;
				}
				return true;
			}
		});
	}

	private void addImageViewPencilClickListener() {
		imageView_pencil.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!editText_username.hasFocus()) {
					editText_username.requestFocus();
					imageView_pencil.setImageResource(R.mipmap.check_ok);
				} else {
					//
					if (imeManager.isActive(editText_username)) {
						imeManager.hideSoftInputFromWindow(editText_username.getWindowToken(), 0);
					}

					editText_username.clearFocus();
					imageView_pencil.setImageResource(R.mipmap.pencil_edit);
					String tempNickname = editText_username.getText().toString();
					editor = sharedPreferences.edit();
					editor.putString("nickname", tempNickname);
					editor.apply();
					SnackbarUtility.getSnackbarDefault(linearLayout, "Modify nickname " +
							"successfully", Snackbar.LENGTH_SHORT).show();

				}
			}
		});
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

	private void initComponent() {
		sharedPreferences = getSharedPreferences("up", MODE_PRIVATE);

		linearLayout = (LinearLayout) findViewById(R.id.profilepage_act_LinearLayout_root);
		toolbar = (Toolbar) findViewById(R.id.profilepage_act_Toolbar);
		imageView_pencil = (ImageView) findViewById(R.id.profilepage_act_pencil_ImageView);
		editText_username = (EditText) findViewById(R.id.profilepage_act_username_EditText);
		textViewLogout = (TextView) findViewById(R.id.profilepage_act_logout_textView);

		linearLayout_starCollection = (LinearLayout) findViewById(R.id
				.profilepage_act_starcollection);
		linearLayout_praiseCollection = (LinearLayout) findViewById(R.id
				.profilepage_act_praisecollection);
		linearLayout_historyCollection = (LinearLayout) findViewById(R.id
				.profilepage_act_historycollection);

		imeManager = (InputMethodManager) getSystemService
				(Context.INPUT_METHOD_SERVICE);
	}

	private void initToolbarSetting() {
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.setTitle("个人主页");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
		layoutParams.setMargins(0, LengthConverterUtility.dip2px(BaseApplication
				.getBaseApplicationContext(), 24), 0, 0);
		toolbar.setLayoutParams(layoutParams);
	}
}
