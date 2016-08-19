package com.rainnka.ZHNews.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rainnka.ZHNews.Utility.ConstantUtility;

/**
 * Created by rainnka on 2016/8/14 16:26
 * Project name is ZHKUNews
 */
public class RecvDividerItemDecoration extends RecyclerView.ItemDecoration {

	private Drawable divider;

	private int orientation;

	public RecvDividerItemDecoration(Context context, int orientation) {
		final TypedArray typedArray = context.obtainStyledAttributes(ConstantUtility.ATTRS);
		divider = typedArray.getDrawable(0);
		typedArray.recycle();
		this.orientation = orientation;
	}

	public void setOrientation(int orientation) {
		if (orientation != ConstantUtility.HORIZONTAL_LIST && orientation != ConstantUtility
				.VERTICAL_LIST) {
			throw new IllegalArgumentException("invalid orientation");
		}
		this.orientation = orientation;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		if (orientation == ConstantUtility.VERTICAL_LIST) {
			outRect.set(0, 0, 0, divider.getIntrinsicHeight());
		} else {
			outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
		}
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		if (orientation == ConstantUtility.VERTICAL_LIST) {
			drawVertical(c, parent);
		} else {
			drawHorizontal(c, parent);
		}
	}

	private void drawHorizontal(Canvas c, RecyclerView parent) {
		final int top = parent.getPaddingTop();
		final int bottom = parent.getHeight() - parent.getPaddingBottom();

		final int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
					.getLayoutParams();
			final int left = child.getRight() + params.rightMargin;
			final int right = left + divider.getIntrinsicHeight();
			divider.setBounds(left, top, right, bottom);
			divider.draw(c);
		}
	}

	private void drawVertical(Canvas c, RecyclerView parent) {
		final int left = parent.getPaddingLeft();
		final int right = parent.getWidth() - parent.getPaddingRight();

		final int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			RecyclerView v = new RecyclerView(parent.getContext());
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
					.getLayoutParams();
			final int top = child.getBottom() + params.bottomMargin;
			final int bottom = top + divider.getIntrinsicHeight();
			divider.setBounds(left, top, right, bottom);
			divider.draw(c);
		}
	}
}
