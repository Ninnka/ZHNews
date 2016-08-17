package com.rainnka.ZHNews.ViewLayer.Activity.Base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by rainnka on 2016/8/11 13:06
 * Project name is ZHKUNews
 */
public class SwipeBackAty extends BaseAty implements SwipeBackActivityBase {

	private SwipeBackActivityHelper swipeBackActivityHelper;

	public void setupWindowAnimations() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			//			Fade fade = new Fade();
			//			fade.setDuration(500);
			//			Explode explode = new Explode();
			//			explode.setDuration(300);
			Slide slide = new Slide();
			slide.setSlideEdge(Gravity.RIGHT);
			slide.setDuration(150);
			getWindow().setEnterTransition(slide);
			//			getWindow().setReturnTransition(slide);
		}
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		swipeBackActivityHelper = new SwipeBackActivityHelper(this);
		swipeBackActivityHelper.onActivityCreate();
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_RIGHT | SwipeBackLayout
				.EDGE_LEFT);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		swipeBackActivityHelper.onPostCreate();
	}

	@Override
	public View findViewById(@IdRes int id) {
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return swipeBackActivityHelper.findViewById(id);
	}

	@Override
	public SwipeBackLayout getSwipeBackLayout() {
		return swipeBackActivityHelper.getSwipeBackLayout();
	}

	@Override
	public void setSwipeBackEnable(boolean enable) {
		getSwipeBackLayout().setEnabled(enable);
		getSwipeBackLayout().setEnableGesture(enable);
	}

	@Override
	public void scrollToFinishActivity() {
		Utils.convertActivityToTranslucent(this);
		getSwipeBackLayout().scrollToFinishActivity();
	}

	@Override
	public void onBackPressed() {
		getSwipeBackLayout().scrollToFinishActivity();
	}
}
