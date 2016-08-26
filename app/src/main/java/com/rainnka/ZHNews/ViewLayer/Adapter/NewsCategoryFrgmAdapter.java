package com.rainnka.ZHNews.ViewLayer.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.rainnka.ZHNews.ViewLayer.Fragment.NewsCategoryFrgm;

import java.util.List;

/**
 * Created by rainnka on 2016/8/18 16:41
 * Project name is ZHKUNews
 */
public class NewsCategoryFrgmAdapter extends FragmentStatePagerAdapter {

	public List<Fragment> fragmentList;

	public NewsCategoryFrgmAdapter(FragmentManager fm) {
		super(fm);
	}

	public void setFragmentList(List<Fragment> fragmentList) {
		this.fragmentList = fragmentList;
	}

	public List<Fragment> getFragmentList() {
		return fragmentList;
	}

	public void replace(NewsCategoryFrgm newsCategoryFrgm, int position) {
		fragmentList.remove(position);
		fragmentList.add(position, newsCategoryFrgm);
	}

	public void clear(){
		fragmentList.clear();
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}
}
