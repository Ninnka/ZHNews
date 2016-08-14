package com.rainnka.ZHNews.Utility;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.rainnka.ZHNews.Application.BaseApplication;

/**
 * Created by rainnka on 2016/8/14 23:08
 * Project name is ZHKUNews
 */
public class NetworkConnectivityUtility {

	public static ConnectivityManager getConnectivityManager(){
		String CONNECTIVITY_SERVICE = BaseApplication.CONNECTIVITY_SERVICE;
		return (ConnectivityManager) BaseApplication.getBaseApplicationContext().getSystemService
				(CONNECTIVITY_SERVICE);
	}

	public static boolean getConnectivityStatus(ConnectivityManager connectivityManager) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Network[] networks = connectivityManager.getAllNetworks();
			if (networks != null && networks.length > 0) {
				for (int i = 0; i < networks.length; i++) {
					if (connectivityManager.getNetworkInfo(networks[i]).getState() == NetworkInfo
							.State.CONNECTED) {
						return true;
					}
				}
			} else {
				return false;
			}
		} else {
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			if (networkInfos != null && networkInfos.length > 0) {
				for (int i = 0; i < networkInfos.length; i++) {
					if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			} else {
				return false;
			}
		}
		return false;
	}

}
