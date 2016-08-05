package com.rainnka.ZHNews.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.gson.Gson;
import com.rainnka.ZHNews.Bean.ZhiHuNewsLatestItemInfo;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.ConstantUtility;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rainnka on 2016/8/4 15:42
 * Project name is ZHKUNews
 */
public class InitNotiRecService extends Service {

	OkHttpClient okHttpClient;
	Request request;
	Call call;
	NotificationHandler notificationHandler;
	NotificationManager notificationManager;
	ZhiHuNewsLatestItemInfo zhiHuNewsLatestItemInfo;

	Gson gson;
	Timer timer;
	TimerTask task;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationHandler = new NotificationHandler(this);
		okHttpClient = new OkHttpClient();
		request = new Request.Builder()
				.url(ConstantUtility.ZHIHUAPI_LATEST)
				.build();
		call = okHttpClient.newCall(request);
		gson = new Gson();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Log.i("ZRH", "onFailure");
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.code() == 200) {
					//						Log.i("ZRH", "success in access url: " + response.request().urlString());
					zhiHuNewsLatestItemInfo = gson.fromJson(response.body().string(),
							ZhiHuNewsLatestItemInfo.class);
					try {
						notificationHandler.sendEmptyMessage(0x74292);
						//						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
						//								(getApplicationContext());
						//						boolean lr = preferences.getBoolean("loadRecommendation", true);
						//						if (lr) {
						//						}
					} catch (Exception e) {
						Log.i("ZRH", e.getMessage());
						Log.i("ZRH", e.toString());
					}

				}
			}
		});
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		notificationHandler.removeCallbacksAndMessages(null);
		task.cancel();
		timer.cancel();
		notificationManager.cancelAll();
		stopSelf();
		Log.i("ZRH","stopSelf");
	}

	/*
	* 初始化notification
	* */
	public void initNotification(int targetNum) {
		RemoteViews remoteViews_notification = new RemoteViews(getPackageName(), R.layout
				.home_notification_content_remoteview);
		remoteViews_notification.setTextViewText(R.id.home_notification_content_text,
				zhiHuNewsLatestItemInfo.stories.get(targetNum).title);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(R.mipmap.app_icon)
				.setContent(remoteViews_notification)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setContentIntent(getPendingIntent_News(targetNum))
				//				.setWhen(System.currentTimeMillis())
				.setDefaults(Notification.DEFAULT_LIGHTS);
		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(1, notification);

		NotificationTarget notificationTarget;
		notificationTarget = new NotificationTarget(this, remoteViews_notification, R.id
				.home_notification_content_image, notification, 1);
		Glide.with(this.getApplicationContext())
				.load(zhiHuNewsLatestItemInfo.stories.get(targetNum).images.get(0))
				.asBitmap()
				.into(notificationTarget);
	}

	private PendingIntent getPendingIntent_News(int targetNum) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ConstantUtility.SER_KEY, zhiHuNewsLatestItemInfo.stories.get
				(targetNum));
		Intent intent = new Intent();
		intent.setAction(ConstantUtility.INTENT_TO_NEWS_KEY);
		intent.putExtras(bundle);
		PendingIntent pendingIntent_News = PendingIntent.getActivity(this,
				ConstantUtility.PENDINGINTENT_NEWS_REQUESTCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent_News;
	}

	private void createNotificationThread() {
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				int maxLength = zhiHuNewsLatestItemInfo.stories.size();
				Random random = new Random();
				int targetNum = random.nextInt(maxLength - 1) + 1;
				notificationHandler.sendEmptyMessage(targetNum);
			}
		};
		try {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
					(getApplicationContext());
			if (preferences.getBoolean("loadRecommendation", false)) {
				String intervalRec_string = preferences.getString("recommandInterval", "30");
				int intervalRec = Integer.valueOf(intervalRec_string);
				timer.schedule(task, 0, intervalRec * 60000);
			} else {
				timer.schedule(task, 0);
			}
		} catch (Exception e) {
			Log.i("ZRH", e.getMessage());
			Log.i("ZRH", e.toString());
		}
	}

	/*
	* 静态内部类
	* 处理notification
	* */
	public static class NotificationHandler extends Handler {
		WeakReference<InitNotiRecService> notificationServiceWeakReference;
		InitNotiRecService initNotiRecService;

		public NotificationHandler(InitNotiRecService service) {
			this.notificationServiceWeakReference = new WeakReference<>(service);
			this.initNotiRecService = this.notificationServiceWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0x74292:
					initNotiRecService.createNotificationThread();
					break;
				default:
					initNotiRecService.initNotification(msg.what);
			}
		}
	}

}
