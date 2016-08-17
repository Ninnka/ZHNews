package com.rainnka.ZHNews.ViewLayer.Activity;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.rainnka.ZHNews.ViewLayer.Activity.Base.SwipeBackAty;
import com.rainnka.ZHNews.Application.BaseApplication;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.LengthConverterUtility;

/**
 * Created by rainnka on 2016/8/2 15:32
 * Project name is ZHKUNews
 */
public class Setting_DetailAty extends SwipeBackAty {

	Toolbar toolbar;
	FrameLayout frameLayout;

	Setting_DetailFragment setting_detailFragment;

	FragmentManager fragmentManager;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_detail_aty);
		setupWindowAnimations();

		/*
		* 另statusbar悬浮于activity上面
		* */
		setFullScreenLayout();

		initComponent();
		initToolbarSetting();
		initFrameLayoutContent();

	}

	private void initFrameLayoutContent() {
		setting_detailFragment = new Setting_DetailFragment();
		fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.add(R.id.setting_detail_act_FrameLayout, setting_detailFragment)
				.commit();

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

	private void initToolbarSetting() {
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.setTitle("设置与帮助");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
		layoutParams.setMargins(0, LengthConverterUtility.dip2px(BaseApplication
				.getBaseApplicationContext(), 24), 0, 0);
		toolbar.setLayoutParams(layoutParams);
	}

	private void initComponent() {
		toolbar = (Toolbar) findViewById(R.id.setting_detail_act_toolbar);
		frameLayout = (FrameLayout) findViewById(R.id.setting_detail_act_FrameLayout);
	}

	public static class Setting_DetailFragment extends PreferenceFragment {

		CheckBoxPreference checkBoxPreference;
		ListPreference listPreference;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.setting_detail);
			checkBoxPreference = (CheckBoxPreference) findPreference("loadRecommendation");
			listPreference = (ListPreference) findPreference("recommandInterval");
			if (checkBoxPreference.isChecked()) {
				listPreference.setEnabled(true);
			} else {
				listPreference.setEnabled(false);
			}
		}

		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
			if (preference.getKey().equals("loadRecommendation")) {
				CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceManager().findPreference
						(preference.getKey());
				ListPreference listPreference = (ListPreference) getPreferenceManager().findPreference
						("recommandInterval");
				if (checkBoxPreference.isChecked()) {
					listPreference.setEnabled(true);
				} else {
					listPreference.setEnabled(false);
				}
			}
			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
	}

}
