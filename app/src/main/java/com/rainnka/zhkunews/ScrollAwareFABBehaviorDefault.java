package com.rainnka.zhkunews;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Created by rainnka on 2016/5/7 0:28
 * Project name is ZHKUNews
 */
public class ScrollAwareFABBehaviorDefault extends CoordinatorLayout.Behavior<FloatingActionsMenu> {

	private static final Interpolator INTERPOLATOR_FOSI = new FastOutSlowInInterpolator();
	private boolean mIsAnimatingOut = false;

	public ScrollAwareFABBehaviorDefault(Context context, AttributeSet attrs) {
		super();
	}

	@Override
	public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionsMenu
			child, View directTargetChild, View target, int nestedScrollAxes) {
		return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout,
				child, directTargetChild, target, nestedScrollAxes);
	}

	@Override
	public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionsMenu child,
							   View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
				dxUnconsumed, dyUnconsumed);
		if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
			//			child.hide();
			if(child.isExpanded()){
				child.collapse();
			}
			animateOut(child);
		}
		if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
			//			child.show();
			if(child.isExpanded()){
				child.collapseImmediately();
			}
			animateIn(child);
		}
	}

	public void animateOut(final FloatingActionsMenu floatingActionButton) {
		if (Build.VERSION.SDK_INT >= 14) {
			ViewCompat
					.animate(floatingActionButton)
					.translationY(floatingActionButton.getHeight() + getMarginBottom(floatingActionButton))
					//					.setDuration(1000)
					.setInterpolator(INTERPOLATOR_FOSI)
					.withLayer()
					.setListener(new ViewPropertyAnimatorListener() {
						@Override
						public void onAnimationStart(View view) {
							ScrollAwareFABBehaviorDefault.this.mIsAnimatingOut = true;
						}

						@Override
						public void onAnimationEnd(View view) {
							ScrollAwareFABBehaviorDefault.this.mIsAnimatingOut = false;
							view.setVisibility(View.GONE);
						}

						@Override
						public void onAnimationCancel(View view) {
							ScrollAwareFABBehaviorDefault.this.mIsAnimatingOut = false;
						}
					})
					.start();
		} else {

		}
	}

	public void animateIn(final FloatingActionsMenu floatingActionButton) {
		floatingActionButton.setVisibility(View.VISIBLE);
		if (Build.VERSION.SDK_INT >= 14) {
			ViewCompat
					.animate(floatingActionButton)
					.translationY(0)
					//					.setDuration(1000)
					.setInterpolator(INTERPOLATOR_FOSI)
					.withLayer()
					.setListener(null)
					.start();
		} else {

		}
	}

	public int getMarginBottom(View view) {
		int marginBottom = 0;
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
			marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
		}
		return marginBottom;
	}

}
