package com.rainnka.ZHNews.Application;

import android.app.Application;
import android.content.Context;

/**
 * Created by rainnka on 2016/8/5 12:21
 * Project name is ZHKUNews
 */
public class BaseApplication extends Application {
	public static BaseApplication baseApplication;

	public static String getDATABASE_PATH() {
		String ITEMINFO_DATABASE_PATH;
		ITEMINFO_DATABASE_PATH = baseApplication.getExternalFilesDir(null).toString();
		return ITEMINFO_DATABASE_PATH;
	}

	public static BaseApplication getBaseApplication(){
		return baseApplication;
	}

	public static Context getBaseApplicationContext(){
		return baseApplication.getApplicationContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		baseApplication = this;
	}
	
}
