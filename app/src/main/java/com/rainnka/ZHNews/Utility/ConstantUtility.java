package com.rainnka.ZHNews.Utility;

import android.animation.AnimatorSet;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by rainnka on 2016/8/4 15:48
 * Project name is ZHKUNews
 */
public class ConstantUtility {

	public static boolean userIsLogin = false;

	public final static int BANNER_SCROLL_INTERVAL = 4000;
	public final static int BANNER_SCROLL_KEY = 0x123;

	public final static int RECYCLER_REFRESH_NEW = 0x111111;
	public final static int RECYCLER_REFRESH_NEW_FAILURE = 0x111222;
	public final static int RECYCLER_REFRESH_OLD = 0x222222;
	public final static int RECYCLER_REFRESH_OLD_FAILURE = 0x222333;
	public final static int RECYCLER_REFRESH_LATEST = 0x333333;

	public final static int COMMENTSLIST_CHANGED = 0x1293;

	public final static int PENDINGINTENT_NEWS_REQUESTCODE = 0x87;

	public final static String STAR_KEY = "star";
	public final static String HISTORY_KEY = "history";
	public final static String PRAISE_KEY = "praise";

	public final static String SER_KEY = "SER";
	public final static String SER_KEY_HOTNEWS = "SER_HOTNEWS";

	public final static int NEWS_TO_UNSTARED = 0x4693;
	public final static int NEWS_TO_STARED = 0x4692;
	public final static int JUDGE_STAR_STATUS = 0x5555;

	public final static int NEWS_TO_PRAISE = 0x9418;
	public final static int NEWS_TO_UNPRAISE = 0x9419;
	public final static int JUDGE_PRAISE_STATUS = 0x5556;

	public final static int SEARCHITEM_FINISHED = 0x91734;

	public final static String DATAKEY_LOGIN_ATY = "VALIDCODE";
	public final static int RESULTCODE_LOGIN_ATY = 0x4567;

	public final static int RESULTCODE_PROFILE_ATY = 0x638912;
	public final static int RESULTCODE_NORMALBACK_PROFILE_ATY = 0x41985;

	public final static int[] ATTRS = new int[]{
			android.R.attr.listDivider
	};
	public final static int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
	public final static int VERTICAL_LIST = LinearLayoutManager.VERTICAL;


	public static AnimatorSet getAnimatorDelegate(){
		return new AnimatorSet();
	}

}
