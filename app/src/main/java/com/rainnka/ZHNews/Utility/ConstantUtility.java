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

	public final static String INTENT_TO_NEWS_KEY = "android.intent.action.NewsActivity";
	public final static String INTENT_TO_STAR_HISTORY_PRAISE_KEY = "android.intent.action" +
			".Star_History_Praise";
	public final static String INTENT_TO_LOGIN_KEY = "android.intent.action.Login";
	public final static int ITENT_TO_LOGIN_REQUESTCODE = 0x1234;

	public final static String INTENT_TO_PROFILE_KEY = "android.intent.action.ProfilePage";
	public final static int ITENT_TO_PROFILE_REQUESTCODE = 0x7654;

	public final static String INTENT_TO_NOTIFICATION_KEY = "android.intent.action" +
			".NotificationPage";

	public final static String INTENT_TO_FEEDBACK_KEY = "android.intent.action.FeedbackPage";
	public final static String INTENT_TO_SETTINGDETAIL_KEY = "android.intent.action.Setting_Detail";

	public final static String INTENT_TO_ABOUT_KEY = "android.intent.action.AboutPage";

	public final static String INTENT_STRING_DATA_KEY = "STRING_DATA_KEY";
	public final static String STAR_KEY = "star";
	public final static String HISTORY_KEY = "history";
	public final static String PRAISE_KEY = "praise";

	public final static String SER_KEY = "SER";

	public final static String INTENT_TO_COMMENTS_KET = "android.intent.action.Comments";

	public final static int ADD_COMMENTSLIST = 0x1293;
	public final static int NO_COMMENTSLIST = 0x346754;

	public final static String ZHIHUAPI_LATEST = "http://news-at.zhihu.com/api/4/news/latest";
	public final static String ZHIHUAPI_BEFORE = "http://news.at.zhihu.com/api/4/news/before/";

	public final static String ZHIHUAPI_LONG_COMMENTS = "http://news-at.zhihu" +
			".com/api/4/story/%s/long-comments";
	public final static String ZHIHUAPI_SHORT_COMMENTS = "http://news-at.zhihu" +
			".com/api/4/story/%s/short-comments";

	public final static String ZHIHUAPI_THEMES_LIST = "http://news-at.zhihu.com/api/4/themes";
	public final static String ZHIHUAPI_THEMES_CONTENT = "http://news-at.zhihu.com/api/4/theme/%s";

	public final static String ZHIHUAPI_NEWS_HOT = "http://news-at.zhihu.com/api/3/news/hot";

	public final static String ZHIHUAPI_SECTIONS_LIST = "http://news-at.zhihu.com/api/3/sections";
	public final static String ZHIHUAPI_SECTIONS_CONTENT = "http://news-at.zhihu" +
			".com/api/3/section/%s";
	public final static String ZHIHUAPI_SECTIONS_CONTENT_BEFORE = "http://news-at.zhihu" +
			".com/api/4/section/%s/before/%s";

	public final static String ZHIHUAPI_NEWS_RECOMMENDERS = "http://news-at.zhihu" +
			".com/api/4/story/%s/recommenders";
	public final static String ZHIHUAPI_NEWS_EDITOR = "http://news-at.zhihu" +
			".com/api/4/editor/%s/profile-page/android";

	public final static String ZHIHUAPI_WELCOME_IMAGE = "http://news-at.zhihu" +
			".com/api/4/start-image/1080*1776";

	public final static int PENDINGINTENT_NEWS_REQUESTCODE = 0x87;

	public final static String getInfoByAPI = "http://news-at.zhihu.com/api/4/news/";

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
