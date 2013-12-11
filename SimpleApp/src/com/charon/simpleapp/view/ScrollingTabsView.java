/*
 * Copyright (C) 2013 Charon Chui <charon.chui@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.charon.simpleapp.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.charon.simpleapp.inter.TabAdapter;

public class ScrollingTabsView extends HorizontalScrollView implements
		OnPageChangeListener {

	private LinearLayout mContainer;

	private ViewPager mViewPager;

	private TabAdapter mTabAdapter;

	private int mWindowWidth;
	/**
	 * True if have segmentation view between two tab view.
	 */
	private boolean isUseSeperator;

	private TabClickListener mTabClickListener;

	private PageSelectedListener mPageSelectedListener;

	public ScrollingTabsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ScrollingTabsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ScrollingTabsView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		this.setHorizontalScrollBarEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);

		mContainer = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		mContainer.setLayoutParams(params);
		mContainer.setOrientation(LinearLayout.HORIZONTAL);

		addView(mContainer);

		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(outMetrics);
		mWindowWidth = outMetrics.widthPixels;
	}

	public void setTabAdapter(TabAdapter adapter) {
		this.mTabAdapter = adapter;
		initTabView();
	}

	public void setViewPager(ViewPager pager) {
		this.mViewPager = pager;
		mViewPager.setOnPageChangeListener(this);
		initTabView();
	}

	/**
	 * Add tab view.
	 */
	private void initTabView() {
		if (mViewPager != null && mTabAdapter != null) {
			mContainer.removeAllViews();

			for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
				final View tab = mTabAdapter.getView(i);
				tab.setTag(i);

				mContainer.addView(tab);

				// Segmentation view
				if (mTabAdapter.getSeparator() != null
						&& i != mViewPager.getAdapter().getCount() - 1) {
					isUseSeperator = true;
					mContainer.addView(mTabAdapter.getSeparator());
				}

				// Set click listener on tab.
				tab.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int index = (Integer) tab.getTag();
						if (mTabClickListener != null) {
							mTabClickListener.onClick(index);
						} else {
							if (mViewPager.getCurrentItem() == index) {
								selectTab(index);
							} else {
								// If ViewPager change the page, the listener
								// will call selectTab method
								mViewPager.setCurrentItem(index, true);
							}
						}
					}
				});

			}

			// Set the current selected tab when first coming.
			selectTab(mViewPager.getCurrentItem());
		}
	}

	/**
	 * Select the tab, and move the tab to the middle position
	 * 
	 * @param position
	 *            Position of the tab.
	 */
	private void selectTab(int position) {
		if (!isUseSeperator) {
			for (int i = 0; i < mContainer.getChildCount(); i++) {
				View tab = mContainer.getChildAt(i);
				// Make the current tab selected, others unselected.
				tab.setSelected(i == position);
			}
		} else {
			// pos is the position of the tab.
			for (int i = 0, pos = 0; i < mContainer.getChildCount(); i += 2, pos++) {
				View tab = mContainer.getChildAt(i);
				tab.setSelected(pos == position);
			}
		}
		View selectedView = null;
		if (!isUseSeperator) {
			selectedView = mContainer.getChildAt(position);
		} else {
			selectedView = mContainer.getChildAt(position * 2);
		}

		int tabWidth = selectedView.getMeasuredWidth();
		int tabLeft = selectedView.getLeft();

		// (tabLeft + tabWidth / 2) is the distance from current tab's middle to
		// the left of the screen
		int distance = (tabLeft + tabWidth / 2) - mWindowWidth / 2;

		smoothScrollTo(distance, this.getScrollY());
	}

	public void setTabClickListener(TabClickListener listener) {
		this.mTabClickListener = listener;
	}

	public void setPageSelectedListener(
			PageSelectedListener pageSelectedListener) {
		this.mPageSelectedListener = pageSelectedListener;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (changed) {
			selectTab(mViewPager.getCurrentItem());
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		selectTab(position);
		if (mPageSelectedListener != null) {
			mPageSelectedListener.onPageSelected(position);
		}
	}

	/**
	 * On click Listener of the tab.
	 */
	public interface TabClickListener {
		public void onClick(int position);
	}

	/**
	 * Callback when ViewPager change Pager.To avoid use setOnPageChangeListener
	 * more times will override.
	 */
	public interface PageSelectedListener {
		public void onPageSelected(int position);
	}
}
