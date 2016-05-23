package com.rainnka.zhkunews.Behavior;

import android.content.Context;
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
 * Created by rainnka on 2016/5/20 16:27
 * Project name is ZHKUNews
 */
public class FABScrollToHaWBehavior extends CoordinatorLayout.Behavior<FloatingActionsMenu> {

	private static final Interpolator INTERPOLATOR_FOSI = new FastOutSlowInInterpolator();
	private boolean mIsAnimation = false;

	public FABScrollToHaWBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionsMenu
			child, View directTargetChild, View target, int nestedScrollAxes) {
		return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll
				(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
	}

	@Override
	public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionsMenu child,
							   View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
				dxUnconsumed, dyUnconsumed);

		if (child.getVisibility() == View.VISIBLE && !this.mIsAnimation && dyConsumed > 0) {
			if (child.isExpanded()) {
				child.collapse();
			}
			animateOut(child);
		}

		if (child.getVisibility() != View.VISIBLE && dyConsumed < 0) {
			if (child.isExpanded()) {
				child.collapse();
			}
			animateIn(child);
		}
	}

	private void animateOut(final FloatingActionsMenu floatingActionsMenu) {
		ViewCompat
				.animate(floatingActionsMenu)
				.translationY(floatingActionsMenu.getHeight() + getMarginBottom(floatingActionsMenu))
				.setInterpolator(INTERPOLATOR_FOSI)
				.withLayer()
				.setListener(new ViewPropertyAnimatorListener() {
					@Override
					public void onAnimationStart(View view) {
						FABScrollToHaWBehavior.this.mIsAnimation = true;
					}

					@Override
					public void onAnimationEnd(View view) {
						FABScrollToHaWBehavior.this.mIsAnimation = false;
						view.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationCancel(View view) {
						FABScrollToHaWBehavior.this.mIsAnimation = false;
					}
				})
				.start();
	}

	private void animateIn(final FloatingActionsMenu floatingActionsMenu) {
		floatingActionsMenu.setVisibility(View.VISIBLE);
		ViewCompat
				.animate(floatingActionsMenu)
				.translationY(0)
				.setInterpolator(INTERPOLATOR_FOSI)
				.withLayer()
				.setListener(null)
				.start();
	}

	private int getMarginBottom(View view) {
		int marginBottom = 0;
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
			marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
		}
		return marginBottom;
	}
}
