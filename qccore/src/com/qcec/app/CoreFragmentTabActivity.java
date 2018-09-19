/*
 * Copyright (C) 2011 The Android Open Source Project
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
package com.qcec.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import com.qcec.core.R;

import java.util.HashMap;

/**
 * This demonstrates how you can implement switching between the tabs of a
 * TabHost through fragments. It uses a trick (see the code below) to allow the
 * tabs to switch between fragments instead of simple views.
 */
public class CoreFragmentTabActivity extends CoreActivity {

	private static final String LOG_TAG = CoreFragmentTabActivity.class
			.getSimpleName();

	protected TabHost mTabHost;
	protected TabManager mTabManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setOnContentView();
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

		setTabWidgetBackground(0);
	}

	protected void setOnContentView() {
		setContentView(R.layout.fragment_tabs);
	}

	public void addTab(String title, Class<?> clss, Bundle args) {
		addTab(title, 0, clss, args);
	}

	public void addTab(String title, int indicatorView, Class<?> clss,
			Bundle args) {
		if (title == null) {
			throw new IllegalArgumentException("title cann't be null!");
		}

		mTabManager.addTab(
				mTabHost.newTabSpec(title).setIndicator(
						new LabelIndicatorStrategy(this, title, indicatorView)
								.createIndicatorView(mTabHost)), clss, args);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	/**
	 * This is a helper class that implements a generic mechanism for
	 * associating fragments with the tabs in a tab host. It relies on a trick.
	 * Normally a tab host has a simple API for supplying a View or Intent that
	 * each tab will show. This is not sufficient for switching between
	 * fragments. So instead we make the content part of the tab host 0dp high
	 * (it is not shown) and the TabManager supplies its own dummy view to show
	 * as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct fragment shown in a separate content area whenever
	 * the selected tab changes.
	 */
	public static class TabManager implements TabHost.OnTabChangeListener {
		private final CoreFragmentTabActivity mActivity;
		private final TabHost mTabHost;
		private final int mContainerId;
		private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
		TabInfo mLastTab;

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;
			private Fragment fragment;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabManager(CoreFragmentTabActivity activity, TabHost tabHost,
                          int containerId) {
			mActivity = activity;
			mTabHost = tabHost;
			mContainerId = containerId;
			mTabHost.setOnTabChangedListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			info.fragment = mActivity.getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (info.fragment != null && !info.fragment.isHidden()) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager()
						.beginTransaction();
				ft.hide(info.fragment);
				ft.commitAllowingStateLoss();
			}

			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);
		}

		@Override
		public void onTabChanged(String tabId) {
			TabInfo newTab = mTabs.get(tabId);
			if (mLastTab != newTab) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager()
						.beginTransaction();
				if (mLastTab != null) {
					if (mLastTab.fragment != null) {
						ft.hide(mLastTab.fragment);
					}
				}
				if (newTab != null) {
					if (newTab.fragment == null) {
						newTab.fragment = Fragment.instantiate(mActivity,
								newTab.clss.getName(), newTab.args);
						ft.add(mContainerId, newTab.fragment, newTab.tag);
						Log.i(LOG_TAG, "onTabChanged with tabId:" + tabId
								+ ", newTab.fragment is null, newTab.tag is "
								+ newTab.tag);
					} else {
						ft.show(newTab.fragment);
						Log.i(LOG_TAG, "onTabChanged with tabId:" + tabId
								+ ", show fragment success");
					}
				} else {
					Log.i(LOG_TAG, "onTabChanged with tabId:" + tabId
							+ ", newTab is null");
				}

				mLastTab = newTab;
				ft.commitAllowingStateLoss();
				mActivity.getSupportFragmentManager()
						.executePendingTransactions();
			}
			mActivity.onTabChanged(tabId);
		}
	}

	public void onTabChanged(String tabId) {

	}

	protected void setTabWidgetBackground(int drawableId) {
		if (drawableId > 0) {
			mTabHost.getTabWidget().setBackgroundResource(drawableId);
		}
	}

}
