package com.rainnka.zhkunews.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainnka.zhkunews.R;
import com.rainnka.zhkunews.Utility.SnackbarUtility;

/**
 * Created by rainnka on 2016/7/10 15:26
 * Project name is ZHKUNews
 */
public class ProfilePageAct extends AppCompatActivity {

	LinearLayout linearLayout;
	Toolbar toolbar;
	ImageView imageView_pencil;
	EditText editText_username;
	TextView textViewLogout;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	InputMethodManager imeManager;

	public final static int RESULTCODE = 0x638912;
	public final static int RESULTCODE_NORMALBACK = 0x41985;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profilepage_act);

		//
		initComponent();

		//
		initSettingToolbar();

		//
		initUsernameEditText();

		//
		addImageViewPencilClickListener();

		//
		addTextViewLogoutClickListener();
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
				ProfilePageAct.this.setResult(RESULTCODE, intent);
				ProfilePageAct.this.finish();
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
					imeManager = (InputMethodManager) getSystemService
							(Context.INPUT_METHOD_SERVICE);
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
				Intent intent = getIntent();
				ProfilePageAct.this.setResult(RESULTCODE_NORMALBACK, intent);
				ProfilePageAct.this.finish();
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
	}

	private void initSettingToolbar() {
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.setTitle("个人主页");
		setSupportActionBar(toolbar);
		;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
