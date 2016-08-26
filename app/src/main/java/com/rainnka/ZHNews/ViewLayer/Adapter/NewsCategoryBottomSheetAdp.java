package com.rainnka.ZHNews.ViewLayer.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.rainnka.ZHNews.Bean.NewsCategoryBottomSheetItem;
import com.rainnka.ZHNews.R;
import com.rainnka.ZHNews.ViewLayer.Activity.NewsCategoryAty;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainnka on 2016/8/26 13:08
 * Project name is ZHKUNews
 */
public class NewsCategoryBottomSheetAdp extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	public NewsCategoryAty newsCategoryAty;
	public WeakReference<NewsCategoryAty> newsCategoryAtyWeakReference;
	public LayoutInflater layoutInflater;
	public List<NewsCategoryBottomSheetItem> bottomSheetItems;

	public ItemOnClickListener itemOnClickListener;

	public int selectedCount = 5;

	public NewsCategoryBottomSheetAdp(NewsCategoryAty newsCategoryAty) {
		this.newsCategoryAtyWeakReference = new WeakReference<>(newsCategoryAty);
		this.newsCategoryAty = this.newsCategoryAtyWeakReference.get();
		layoutInflater = LayoutInflater.from(this.newsCategoryAty);
	}

	public void setBottomSheetItems(List<NewsCategoryBottomSheetItem> list) {
		bottomSheetItems = list;
	}

	public List<NewsCategoryBottomSheetItem> getBottomSheetItems() {
		return bottomSheetItems;
	}

	public NewsCategoryBottomSheetItem getBottomSheetItem(int position) {
		return bottomSheetItems.get(position);
	}

	public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
		this.itemOnClickListener = itemOnClickListener;
	}

	public List<String> getSelectedItemList() {
		List<String> selectList = new ArrayList<>();
		for (int i = 0; i < bottomSheetItems.size(); i++) {
			if (bottomSheetItems.get(i).select == 1) {
				selectList.add(bottomSheetItems.get(i).name);
			}
		}
		return selectList;
	}

	public int getSelectedId(String name) {
		for (int i = 0; i < bottomSheetItems.size(); i++) {
			if (name.equals(bottomSheetItems.get(i).name)) {
				return bottomSheetItems.get(i).id;
			}
		}
		return 0;
	}

	public int getSelectedCount(){
		return selectedCount;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NewsCategoryBottomSheetViewHolder(layoutInflater.inflate(R.layout
				.news_category_aty_bottomsheet_item, parent, false));
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (holder instanceof NewsCategoryBottomSheetViewHolder) {
			if (bottomSheetItems.get(position).select == 1) {
				((NewsCategoryBottomSheetViewHolder) holder).select.setChecked(true);
			} else {
				((NewsCategoryBottomSheetViewHolder) holder).select.setChecked(false);
			}
			((NewsCategoryBottomSheetViewHolder) holder).name.setText(bottomSheetItems.get
					(position).name);
			((NewsCategoryBottomSheetViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					if (itemOnClickListener != null) {
						itemOnClickListener.onItemClick(((NewsCategoryBottomSheetViewHolder)
								holder), position);
					}
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return bottomSheetItems.size();
	}

	public interface ItemOnClickListener {
		void onItemClick(NewsCategoryBottomSheetViewHolder holder, int position);
	}

	public static class NewsCategoryBottomSheetViewHolder extends RecyclerView.ViewHolder {

		public TextView name;
		public CheckBox select;

		public NewsCategoryBottomSheetViewHolder(View itemView) {
			super(itemView);
			name = (TextView) itemView.findViewById(R.id
					.news_category_aty_bottomsheet_item_name_TextView);
			select = (CheckBox) itemView.findViewById(R.id
					.news_category_aty_bottomsheet_item_select_checkbox);
		}
	}
}
