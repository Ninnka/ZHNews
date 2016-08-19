package com.rainnka.ZHNews.ViewLayer.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rainnka.ZHNews.Bean.CategoryTheme;
import com.rainnka.ZHNews.Bean.ZhiHuNewsItemThemeStories;
import com.rainnka.ZHNews.CustomView.RecvDividerItemDecoration;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.Utility.ConstantUtility;
import com.rainnka.ZHNews.ViewLayer.Activity.NewsCategoryAty;
import com.rainnka.ZHNews.ViewLayer.Adapter.NewsCategoryRecvAdp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class NewsCategoryFrgm extends Fragment {

	/**
	 * main self members
	 */
	protected FrameLayout view_root;
	protected RecyclerView recyclerView;
	protected TextView textView_loading;

	protected LayoutInflater inflater;
	protected ViewGroup container;

	public NewsCategoryRecvAdp newsCategoryRecvAdp;

	public List<ZhiHuNewsItemThemeStories> stories;

	public boolean isLazyModel = true;
	public boolean isInit = false;

	public int categoryId;

	public NewsCategoryHandler newsCategoryHandler;

	public LayoutInflater layoutInflater;

	private OnFragmentInteractionListener mListener;

	protected WeakReference<NewsCategoryAty> newsCategoryAtyWeakReference;
	protected NewsCategoryAty newsCategoryAty;

	public Retrofit retrofit;

	/**
	 * main inherited methods
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		if (getArguments() != null) {
		//			isLazyModel = getArguments().getBoolean("lazyFlag");
		//			Log.i("ZRH", "isLazyModel: " + isLazyModel);
		//		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		this.inflater = inflater;
		this.container = container;
		initHandler();
		FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.newscategory_aty_frgm,
				container, false);
		setView_root(frameLayout);
		textView_loading = (TextView) frameLayout.findViewById(R.id
				.newscategory_aty_frgm_loading_TextView);
		recyclerView = (RecyclerView) view_root.findViewById(R.id
				.newscategory_aty_frgm_RecyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this.newsCategoryAty));
		recyclerView.addItemDecoration(new RecvDividerItemDecoration(this.newsCategoryAty,
				ConstantUtility.VERTICAL_LIST));
		newsCategoryRecvAdp = new NewsCategoryRecvAdp(newsCategoryAty);
		stories = new ArrayList<>();
		newsCategoryRecvAdp.setZhiHuNewsItemInfoList(stories);
		recyclerView.setAdapter(newsCategoryRecvAdp);
		if (isLazyModel) {
			if (getUserVisibleHint() && !isInit) {
				onCreateViewLazy();
				isInit = true;
			}
		} else {
			onCreateViewLazy();
		}
		return frameLayout;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && !isInit && getView_root() != null) {
			onCreateViewLazy();
			isInit = true;
		} else {

		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		//		if (context instanceof OnFragmentInteractionListener) {
		//			mListener = (OnFragmentInteractionListener) context;
		//		} else {
		//			throw new RuntimeException(context.toString()
		//					+ " must implement OnFragmentInteractionListener");
		//		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		newsCategoryHandler.removeCallbacksAndMessages(null);
	}

	/**
	 * main self methods
	 */
	public NewsCategoryFrgm() {
		// Required empty public constructor
	}

	public static NewsCategoryFrgm newInstance(boolean isLazyModel, NewsCategoryAty newsCategoryAty) {
		NewsCategoryFrgm fragment = new NewsCategoryFrgm();
		fragment.setIsLazyModel(isLazyModel);
		fragment.setBindActivity(newsCategoryAty);
		fragment.layoutInflater = fragment.getLayoutInflater();
		//		Bundle args = new Bundle();
		//		args.putBoolean("lazyFlag", lazyFlag);
		//		fragment.setArguments(args);
		return fragment;
	}

	public void setBindActivity(NewsCategoryAty newsCategoryAty) {
		this.newsCategoryAtyWeakReference = new WeakReference<>(newsCategoryAty);
		this.newsCategoryAty = this.newsCategoryAtyWeakReference.get();
	}

	public void initHandler() {
		newsCategoryHandler = new NewsCategoryHandler(this);
	}

	public void setIsLazyModel(boolean lazyModel) {
		this.isLazyModel = lazyModel;
	}

	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(this.newsCategoryAty);
	}

	public void onCreateViewLazy() {
		retrofit = new Retrofit.Builder()
				.baseUrl("http://news-at.zhihu.com")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		CategoryThemeService categoryThemeService = retrofit.create(CategoryThemeService.class);
		Call<CategoryTheme> categoryThemeCall = categoryThemeService.CATEGORY_THEME_CALL
				(String.valueOf(getCategoryId()));
		categoryThemeCall.enqueue(new Callback<CategoryTheme>() {
			@Override
			public void onResponse(Call<CategoryTheme> call, Response<CategoryTheme> response) {
				if (response.isSuccessful()) {
					newsCategoryRecvAdp.addZhiHuNewsItemInfoList(response.body().stories);
					newsCategoryHandler.sendEmptyMessage(0x2367865);
				}
			}

			@Override
			public void onFailure(Call<CategoryTheme> call, Throwable t) {

			}
		});
	}

	public void setView_root(FrameLayout view_root) {
		this.view_root = view_root;
	}

	public FrameLayout getView_root() {
		return view_root;
	}

	public void setCategoryId(int id) {
		categoryId = id;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void newCategoryDataHasLoaded() {
		newsCategoryRecvAdp.notifyDataSetChanged();
		textView_loading.setVisibility(View.GONE);
	}

	public void setmListener(OnFragmentInteractionListener mListener) {
		this.mListener = mListener;
	}

	/**
	 * main inner Class & interface
	 */

	public interface CategoryThemeService {
		@GET("/api/4/theme/{id}")
		Call<CategoryTheme> CATEGORY_THEME_CALL(@Path("id") String id);
	}

	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}

	public static class NewsCategoryHandler extends Handler {

		public NewsCategoryFrgm newsCategoryFrgm;
		public WeakReference<NewsCategoryFrgm> newsCategoryFrgmWeakReference;

		public NewsCategoryHandler(NewsCategoryFrgm newsCategoryFrgm) {
			this.newsCategoryFrgmWeakReference = new WeakReference<>(newsCategoryFrgm);
			this.newsCategoryFrgm = this.newsCategoryFrgmWeakReference.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			newsCategoryFrgm.newCategoryDataHasLoaded();
		}
	}
}
