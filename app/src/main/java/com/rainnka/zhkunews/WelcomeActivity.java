package com.rainnka.zhkunews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * Created by rainnka on 2016/5/18 20:58
 * Project name is MaterialDesign
 */
public class WelcomeActivity extends AppCompatActivity {

	WelcomeHandler welcomeHandler;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		welcomeHandler = new WelcomeHandler(this);
		setContentView(R.layout.welcome_activity);
		welcomeHandler.sendEmptyMessageDelayed(0x123, 3000);
	}

	static class WelcomeHandler extends Handler {

		WeakReference<WelcomeActivity> welcomeActivityWeakReference;
		WelcomeActivity welcomeActivity;

		public WelcomeHandler(WelcomeActivity welcomeActivity) {
			this.welcomeActivityWeakReference = new WeakReference<>(welcomeActivity);
			this.welcomeActivity = this.welcomeActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent intent = new Intent();
			intent.setAction("android.intent.action.Home");
			welcomeActivity.startActivity(intent);
			welcomeActivity.finish();
		}
	}
}
