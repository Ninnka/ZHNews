package com.rainnka.ZHNews.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.rainnka.ZHNews.Activity.Base.BaseAty;
import com.rainnka.ZHNews.R;
import com.squareup.okhttp.OkHttpClient;

import java.lang.ref.WeakReference;

/**
 * Created by rainnka on 2016/5/18 20:58
 * Project name is MaterialDesign
 */
public class WelcomeAty extends BaseAty {

	WelcomeHandler welcomeHandler;
	CallResponseHandler callResponseHandler;
	LinearLayout linearLayout;
	OkHttpClient okHttpClient;

	ZhiHuStartItemInfo zhiHuStartItemInfo;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sharedPreferences = getSharedPreferences("NightMode", MODE_PRIVATE);
		int mode = sharedPreferences.getInt("nightmode", AppCompatDelegate.MODE_NIGHT_NO);
		switch (mode) {
			case AppCompatDelegate.MODE_NIGHT_NO:
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
				break;
			case AppCompatDelegate.MODE_NIGHT_YES:
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
				break;
			case AppCompatDelegate.MODE_NIGHT_AUTO:
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
				break;
			case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
				break;
		}

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.contains("loadStartAnimation")) {
			boolean lsa = preferences.getBoolean("loadStartAnimation", true);
			if (!lsa) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.Home");
				startActivity(intent);
				finish();
			}
		}


		welcomeHandler = new WelcomeHandler(this);
		callResponseHandler = new CallResponseHandler(this);
		//		okHttpClient = new OkHttpClient();
		//		final Request request = new Request.Builder()
		//				.url(ConstantUtility.ZHIHUAPI_WELCOME_IMAGE)
		//				.build();
		//		Call call = okHttpClient.newCall(request);
		//		call.enqueue(new Callback() {
		//			@Override
		//			public void onFailure(Request request, IOException e) {
		//				Log.i("ZRH", "onFailure url: " + request.urlString());
		//			}
		//
		//			@Override
		//			public void onResponse(Response response) throws IOException {
		//				Log.i("ZRH", "success in access url: " + response.request().urlString());
		//				Gson gson = new Gson();
		//				zhiHuStartItemInfo = gson.fromJson(response.body().string(),
		//						ZhiHuStartItemInfo.class);
		//				callResponseHandler.sendEmptyMessage(0x234);
		//			}
		//		});
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			// Translucent status bar
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
					.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager
					.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		setContentView(R.layout.welcome_aty);
		linearLayout = (LinearLayout) findViewById(R.id.welCome_Activity_welcomeBackgroud);
		welcomeHandler.sendEmptyMessageDelayed(0x123, 3000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		welcomeHandler.removeCallbacksAndMessages(null);
		callResponseHandler.removeCallbacksAndMessages(null);
	}

	static class WelcomeHandler extends Handler {

		WeakReference<WelcomeAty> welcomeActivityWeakReference;
		WelcomeAty welcomeAct;

		public WelcomeHandler(WelcomeAty welcomeAct) {
			this.welcomeActivityWeakReference = new WeakReference<>(welcomeAct);
			this.welcomeAct = this.welcomeActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent intent = new Intent();
			intent.setAction("android.intent.action.Home");
			welcomeAct.startActivity(intent);
			welcomeAct.finish();
		}
	}

	static class CallResponseHandler extends Handler {

		WeakReference<WelcomeAty> welcomeActivityWeakReference;
		WelcomeAty welcomeAct;

		public CallResponseHandler(WelcomeAty welcomeAct) {
			this.welcomeActivityWeakReference = new WeakReference<>(welcomeAct);
			this.welcomeAct = this.welcomeActivityWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ViewTarget<LinearLayout, GlideDrawable> viewTarget = new ViewTarget<LinearLayout,
					GlideDrawable>(welcomeAct.linearLayout) {
				@Override
				public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						this.view.setBackground(resource);
						welcomeAct.welcomeHandler.sendEmptyMessageDelayed(0x123, 3500);
					} else {
						this.view.setBackgroundDrawable(resource);
						welcomeAct.welcomeHandler.sendEmptyMessageDelayed(0x123, 3500);
					}
				}
			};
			Glide.with(welcomeAct)
					.load(welcomeAct.zhiHuStartItemInfo.img)
					.skipMemoryCache(true)
					.into(viewTarget);
			//			Log.i("ZRH","loading viewTarget");
			//			Log.i("ZRH","welcomeHandler.sendEmptyMessageDelayed");
		}
	}

	static class ZhiHuStartItemInfo {

		public String text;

		public String img;

	}
}
