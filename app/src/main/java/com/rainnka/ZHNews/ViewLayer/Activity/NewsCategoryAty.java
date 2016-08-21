package com.rainnka.ZHNews.ViewLayer.Activity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.ViewLayer.Activity.Base.BaseAty;
import com.rainnka.ZHNews.ViewLayer.Adapter.NewsCategoryFrgmAdapter;
import com.rainnka.ZHNews.ViewLayer.Fragment.NewsCategoryFrgm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainnka on 2016/8/18 15:03
 * Project name is ZHKUNews
 */
public class NewsCategoryAty extends BaseAty {

	/**
	 * main self members
	 */
	protected CoordinatorLayout coordinatorLayout;
	protected Toolbar toolbar;
	protected TabLayout tabLayout;
	protected ViewPager viewPager;
	ImageView imageView_sortcategorytheme;

	public List<Fragment> fragmentList;

	public FragmentManager fragmentManager;

	public NewsCategoryFrgmAdapter newsCategoryFrgmAdapter;

	public SQLiteDatabase sqLiteDatabase;

	public final static String[] STRINGS = new String[]{"开始游戏", "电影日报", "设计日报", "大公司日报", "财经日报"};

	/**
	 * main inherited methods
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newscategory_aty);
		setFullScreen();
		initSQLiteDatabase();
		initComponent();
		initToolSetting();

		initFragmentManager();

		initTabLayoutSetting();
		initViewPagerSetting();

		bindViewPagerTabLayout();

		addSortCategoryThemeOnClickListener();
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				setFullScreen();
//				Log.i("ZRH", "visibility: " + visibility);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(sqLiteDatabase.isOpen()){
			sqLiteDatabase.close();
		}
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

	/**
	 * main self methods
	 */
	private void initComponent() {
		coordinatorLayout = (CoordinatorLayout) findViewById(R.id
				.newscategory_aty_CoordinatorLayout);
		toolbar = (Toolbar) findViewById(R.id.newscategory_aty_Toolbar);
		tabLayout = (TabLayout) findViewById(R.id.newscategory_aty_TabLayout);
		viewPager = (ViewPager) findViewById(R.id.newscategory_aty_ViewPager);
		imageView_sortcategorytheme = (ImageView) findViewById(R.id
				.newscategory_aty_sortcategorytheme_ImageView);
	}

	private void initToolSetting() {
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.setTitle("日报分类");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
		//		layoutParams.setMargins(0, LengthConverterUtility.dip2px(BaseApplication
		//				.getBaseApplicationContext(), 24), 0, 0);
		//		toolbar.setLayoutParams(layoutParams);
	}

	private void initTabLayoutSetting() {
		for (int i = 0; i < 5; i++) {
			tabLayout.addTab(tabLayout.newTab());
			tabLayout.getTabAt(i).setText(STRINGS[i]);
		}
	}

	public void initSQLiteDatabase() {
		try {
			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(BaseApplication.getDATABASE_PATH() +
					"/myInfo.db3", null);
		} catch (Exception e) {
			Log.i("ZRH", e.getMessage());
			Log.i("ZRH", e.toString());
		}
	}

	private void initViewPagerSetting() {
		newsCategoryFrgmAdapter = new NewsCategoryFrgmAdapter(getSupportFragmentManager());
		fragmentList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			NewsCategoryFrgm newsCategoryFrgm = NewsCategoryFrgm.newInstance(true, this);
			newsCategoryFrgm.setCategoryId(i + 2);
			fragmentList.add(newsCategoryFrgm);
		}
		newsCategoryFrgmAdapter.setFragmentList(fragmentList);
		viewPager.setAdapter(newsCategoryFrgmAdapter);
		viewPager.setOffscreenPageLimit(4);
		viewPager.setCurrentItem(3, false);
	}

	private void initFragmentManager() {
		fragmentManager = getSupportFragmentManager();
	}

	private void bindViewPagerTabLayout() {
		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				viewPager.setCurrentItem(tab.getPosition(), false);
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

	private void addSortCategoryThemeOnClickListener() {
		imageView_sortcategorytheme.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	/**
	 * main inner class & interface
	 * */
}
