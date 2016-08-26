package com.rainnka.ZHNews.ViewLayer.Activity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.Bean.LocalThemesList;
import com.rainnka.ZHNews.Bean.NewsCategoryBottomSheetItem;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.ConstantUtility;
import com.rainnka.ZHNews.ViewLayer.Activity.Base.BaseAty;
import com.rainnka.ZHNews.ViewLayer.Adapter.NewsCategoryBottomSheetAdp;
import com.rainnka.ZHNews.ViewLayer.Adapter.NewsCategoryFrgmAdapter;
import com.rainnka.ZHNews.ViewLayer.Fragment.NewsCategoryFrgm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainnka on 2016/8/18 15:03
 * Project name is ZHKUNews
 */
public class NewsCategoryAty extends BaseAty implements NewsCategoryBottomSheetAdp.ItemOnClickListener {

	/**
	 * main self members
	 */
	protected CoordinatorLayout coordinatorLayout;
	protected Toolbar toolbar;
	protected TabLayout tabLayout;
	protected ViewPager viewPager;
	protected ImageView imageView_sortcategorytheme;

	protected RecyclerView recyclerView_bottomsheet;
	protected NewsCategoryBottomSheetAdp newsCategoryBottomSheetAdp;
	protected LinearLayoutManager linearLayoutManager;
	protected BottomSheetDialog bottomSheetDialog;
	protected BottomSheetBehavior bottomSheetBehavior;

	public List<Fragment> fragmentList;

	public FragmentManager fragmentManager;

	public NewsCategoryFrgmAdapter newsCategoryFrgmAdapter;

	public SharedPreferences sharedPreferences_themes;

	public SQLiteDatabase sqLiteDatabase;

	public Gson gson;

	public static List<String> TabNames;
	public static List<Integer> FrgmCategoryId;
	public final static String[] STRINGS = new String[]{"开始游戏", "电影日报", "设计日报", "大公司日报", "财经日报"};

	public LocalThemesList localThemesList;
	public List<NewsCategoryBottomSheetItem> bottomSheetItems;

	public String themes_list;

	/**
	 * main inherited methods
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newscategory_aty);
		setupWindowAnimations();
		setFullScreen();

		initGson();

		initSharedPreferencesAndThemesList();
		initTabNamesAndFrgmCategoryId();

		initSQLiteDatabase();
		initComponent();
		initToolSetting();

		initFragmentManager();

		initTabLayoutSetting();
		initViewPagerSetting();

		bindViewPagerTabLayout();

		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				setFullScreen();
				//				Log.i("ZRH", "visibility: " + visibility);
			}

		});

		initBottomSheet();
		addBottomSheetToggleViewOnClickListener();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sqLiteDatabase.isOpen()) {
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

	@Override
	public void onItemClick(NewsCategoryBottomSheetAdp.NewsCategoryBottomSheetViewHolder holder,
							int position) {
		if (!holder.select.isChecked() && newsCategoryBottomSheetAdp.selectedCount < 5) {
			holder.select.setChecked(true);
			newsCategoryBottomSheetAdp.getBottomSheetItem(position).select = 1;
			if (newsCategoryBottomSheetAdp.selectedCount < 5) {
				newsCategoryBottomSheetAdp.selectedCount++;
			}
		} else if (holder.select.isChecked()) {
			holder.select.setChecked(false);
			newsCategoryBottomSheetAdp.getBottomSheetItem(position).select = 0;
			if (newsCategoryBottomSheetAdp.selectedCount > 0) {
				newsCategoryBottomSheetAdp.selectedCount--;
			}
		} else {
			Toast.makeText(BaseApplication.getBaseApplicationContext(), "只能选取5个日报主题",
					Toast.LENGTH_SHORT).show();
		}
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
		recyclerView_bottomsheet = (RecyclerView) findViewById(R.id
				.newscategory_aty_bottomsheet_RecyclerView);
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
			tabLayout.getTabAt(i).setText(TabNames.get(i));
		}
	}

	private void initGson() {
		gson = new Gson();
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

	private void initSharedPreferencesAndThemesList() {
		sharedPreferences_themes = getSharedPreferences("custom_themes_list", MODE_PRIVATE);
		themes_list = sharedPreferences_themes.getString("themes_list", "");
		if (!themes_list.equals("")) {
			localThemesList = gson.fromJson(themes_list, LocalThemesList.class);
			bottomSheetItems = localThemesList.list;
		}
	}

	private void initTabNamesAndFrgmCategoryId() {
		TabNames = new ArrayList<>();
		FrgmCategoryId = new ArrayList<>();
		if (bottomSheetItems != null && bottomSheetItems.size() > 0) {
			try {
				for (int i = 0; i < bottomSheetItems.size(); i++) {
					if (bottomSheetItems.get(i).select == 1) {
						TabNames.add(bottomSheetItems.get(i).name);
						FrgmCategoryId.add(bottomSheetItems.get(i).id);
					}
				}
			} catch (Exception e) {
				Log.i("ZRH", "initTabNamesAndFrgmCategoryId Exception: " + e.toString());
			}

		} else {
			for (int i = 0; i < STRINGS.length; i++) {
				TabNames.add(STRINGS[i]);
				FrgmCategoryId.add(i + 2);
			}
		}
	}

	private void initViewPagerSetting() {
		newsCategoryFrgmAdapter = new NewsCategoryFrgmAdapter(getSupportFragmentManager());
		fragmentList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			NewsCategoryFrgm newsCategoryFrgm = NewsCategoryFrgm.newInstance(true, this);
			newsCategoryFrgm.setCategoryId(FrgmCategoryId.get(i));
			fragmentList.add(newsCategoryFrgm);
		}
		newsCategoryFrgmAdapter.setFragmentList(fragmentList);
		viewPager.setAdapter(newsCategoryFrgmAdapter);
		viewPager.setOffscreenPageLimit(4);
		viewPager.setCurrentItem(0, false);
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

	private void initBottomSheet() {
		linearLayoutManager = new LinearLayoutManager(this);
		newsCategoryBottomSheetAdp = new NewsCategoryBottomSheetAdp(this);
		newsCategoryBottomSheetAdp.setItemOnClickListener(this);

		if (bottomSheetItems != null && bottomSheetItems.size() > 0) {
			newsCategoryBottomSheetAdp.setBottomSheetItems(bottomSheetItems);
		} else {
			List<NewsCategoryBottomSheetItem> list = new ArrayList<>();
			for (int i = 0; i < ConstantUtility.NEWS_THEME_NAMES.length; i++) {
				NewsCategoryBottomSheetItem newsCategoryBottomSheetItem = new
						NewsCategoryBottomSheetItem();
				newsCategoryBottomSheetItem.id = i + 2;
				newsCategoryBottomSheetItem.name = ConstantUtility.NEWS_THEME_NAMES[i];
				if (i < 5) {
					newsCategoryBottomSheetItem.select = 1;
				} else {
					newsCategoryBottomSheetItem.select = 0;
				}
				list.add(newsCategoryBottomSheetItem);
			}
			newsCategoryBottomSheetAdp.setBottomSheetItems(list);
			LocalThemesList localThemesList = new LocalThemesList();
			localThemesList.list = list;
			String localThemesStr = gson.toJson(localThemesList, LocalThemesList.class);
			SharedPreferences.Editor editor = sharedPreferences_themes.edit();
			editor.putString("themes_list", localThemesStr);
			editor.apply();
		}


		recyclerView_bottomsheet.setAdapter(newsCategoryBottomSheetAdp);
		recyclerView_bottomsheet.setLayoutManager(linearLayoutManager);

		bottomSheetDialog = new BottomSheetDialog(this);

		bottomSheetBehavior = BottomSheetBehavior.from(recyclerView_bottomsheet);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				if (newState == BottomSheetBehavior.STATE_HIDDEN) {
					if(newsCategoryBottomSheetAdp.getSelectedCount() == 5){
						try {
							SharedPreferences.Editor editor = sharedPreferences_themes.edit();
							LocalThemesList localThemesList = new LocalThemesList();
							localThemesList.list = new ArrayList<>();
							localThemesList.list.addAll(newsCategoryBottomSheetAdp.getBottomSheetItems());
							String localThemesStr = gson.toJson(localThemesList, LocalThemesList
									.class);
							editor.putString("themes_list", localThemesStr);
							editor.apply();
							checkTabAndReplaceFragment();
							//						viewPager.setAdapter(newsCategoryFrgmAdapter);
						} catch (Exception e) {
							Log.i("ZRH", e.toString());
						}
					}else {
						Toast.makeText(BaseApplication.getBaseApplicationContext(),"请选择5个日报主题",
								Toast.LENGTH_SHORT).show();
						bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
					}
				}
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {

			}
		});
	}

	private void addBottomSheetToggleViewOnClickListener() {
		imageView_sortcategorytheme.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(BaseApplication.getBaseApplicationContext(), "可选择5个日报主题常驻于导航栏",
						Toast.LENGTH_SHORT).show();
				bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
		});
	}

	private void checkTabAndReplaceFragment() {
		boolean isSomethingChanged = false;
		List<String> list = newsCategoryBottomSheetAdp.getSelectedItemList();
		for (int i = 0; i < list.size(); i++) {
			if (!tabLayout.getTabAt(i).getText().equals(list.get(i))) {
				isSomethingChanged = true;
				tabLayout.getTabAt(i).setText(list.get(i));
				NewsCategoryFrgm newsCategoryFrgm = NewsCategoryFrgm.newInstance(true, this);
				newsCategoryFrgm.setCategoryId(newsCategoryBottomSheetAdp.getSelectedId(list.get(i)));
				newsCategoryFrgmAdapter.replace(newsCategoryFrgm, i);
			}
		}
		if(isSomethingChanged){
			int currentpage = viewPager.getCurrentItem();
			viewPager.setAdapter(newsCategoryFrgmAdapter);
			viewPager.setCurrentItem(currentpage);
		}
	}

	/**
	 * main inner class & interface
	 * */
}
