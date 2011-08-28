/*
 * Copyright (C) 2011 Jake Wharton <jakewharton@gmail.com>
 * Copyright (C) 2010 Johan Nilsson <http://markupartist.com>
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

package com.actionbarsherlock.internal.app;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActionBar;
import android.support.v4.app.SupportActivity;
import android.support.v4.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SpinnerAdapter;
import com.actionbarsherlock.R;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.widget.ActionBarContainer;
import com.actionbarsherlock.internal.widget.ActionBarView;

public final class ActionBarImpl extends ActionBar {
    /** Action bar container. */
    private ActionBarContainer mContainerView;

    /** Action bar view. */
    private ActionBarView mActionView;

    /** List of listeners to the menu visibility. */
    private final List<OnMenuVisibilityListener> mMenuListeners = new ArrayList<OnMenuVisibilityListener>();



    public <T extends Activity & SupportActivity> ActionBarImpl(T activity) {
        super(activity);
    }


    // ------------------------------------------------------------------------
    // ACTION BAR SHERLOCK SUPPORT
    // ------------------------------------------------------------------------

    @Override
    protected ActionBar getPublicInstance() {
        return (mActionView != null) ? this : null;
    }

    public void init() {
        mActionView = (ActionBarView)mActivity.findViewById(R.id.abs__action_bar);
        mContainerView = (ActionBarContainer)mActivity.findViewById(R.id.abs__action_bar_container);

        if (mActionView == null) {
            throw new IllegalStateException(getClass().getSimpleName() + " can only be used with a screen_*.xml layout");
        }

        final PackageManager pm = mActivity.getPackageManager();
        ActivityInfo actInfo = null;
        try {
            actInfo = pm.getActivityInfo(mActivity.getComponentName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {}

        if (mActionView.getTitle() == null) {
            if ((actInfo != null) && (actInfo.labelRes != 0)) {
                //Load label string resource from the activity entry
                mActionView.setTitle(actInfo.labelRes);
            } else {
                //No activity label string resource and none in theme
                mActionView.setTitle(actInfo.loadLabel(pm));
            }
        }
    }

    public void onMenuInflated(MenuBuilder menu) {
        if (mActionView != null) {
            mActionView.setMenu(menu);
        }
    }

    public void dispatchVisibilityChanged(boolean isVisible) {
        //Marshal to all listeners
        for (OnMenuVisibilityListener listener : mMenuListeners) {
            listener.onMenuVisibilityChanged(isVisible);
        }
    }

    public void setProgressBarIndeterminateVisibility(boolean visible) {
        if (mActionView != null) {
            mActionView.setProgressBarIndeterminateVisibility(visible);
        }
    }

    // ------------------------------------------------------------------------
    // ACTION MODE METHODS
    // ------------------------------------------------------------------------

    @Override
    protected ActionMode startActionMode(ActionMode.Callback callback) {
        throw new RuntimeException("Not implemented.");
    }

    // ------------------------------------------------------------------------
    // ACTION BAR METHODS
    // ------------------------------------------------------------------------

    @Override
    public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
        if (!mMenuListeners.contains(listener)) {
            mMenuListeners.add(listener);
        }
    }

    @Override
    public void addTab(Tab tab) {
        mActionView.addTab(tab);
    }

    @Override
    public void addTab(Tab tab, boolean setSelected) {
        mActionView.addTab(tab, setSelected);
    }

    @Override
    public void addTab(Tab tab, int position) {
        mActionView.addTab(tab, position);
    }

    @Override
    public void addTab(ActionBar.Tab tab, int position, boolean setSelected) {
        mActionView.addTab(tab, position, setSelected);
    }

    @Override
    public View getCustomView() {
        return mActionView.getCustomView();
    }

    @Override
    public int getDisplayOptions() {
        return mActionView.getDisplayOptions();
    }

    @Override
    public int getHeight() {
        return mActionView.getHeight();
    }

    @Override
    public int getNavigationItemCount() {
        switch (mActionView.getNavigationMode()) {
            default:
            case ActionBar.NAVIGATION_MODE_STANDARD:
                return 0;

            case ActionBar.NAVIGATION_MODE_LIST:
                SpinnerAdapter dropdownAdapter = mActionView.getDropdownAdapter();
                return (dropdownAdapter != null) ? dropdownAdapter.getCount() : 0;

            case ActionBar.NAVIGATION_MODE_TABS:
                return mActionView.getTabCount();
        }
    }

    @Override
    public int getNavigationMode() {
        return mActionView.getNavigationMode();
    }

    @Override
    public int getSelectedNavigationIndex() {
        switch (mActionView.getNavigationMode()) {
            default:
            case ActionBar.NAVIGATION_MODE_STANDARD:
                return -1;

            case ActionBar.NAVIGATION_MODE_LIST:
                return mActionView.getDropdownSelectedPosition();

            case ActionBar.NAVIGATION_MODE_TABS:
                return mActionView.getSelectedTab().getPosition();
        }
    }

    @Override
    public ActionBar.Tab getSelectedTab() {
        return mActionView.getSelectedTab();
    }

    @Override
    public CharSequence getSubtitle() {
        return mActionView.getSubtitle();
    }

    @Override
    public ActionBar.Tab getTabAt(int index) {
        return mActionView.getTabAt(index);
    }

    @Override
    public int getTabCount() {
        return mActionView.getTabCount();
    }

    @Override
    public CharSequence getTitle() {
        return mActionView.getTitle();
    }

    @Override
    public void hide() {
        //TODO: animate
        mContainerView.setVisibility(View.GONE);
    }

    @Override
    public boolean isShowing() {
        return mContainerView.getVisibility() == View.VISIBLE;
    }

    @Override
    public ActionBar.Tab newTab() {
        return mActionView.newTab();
    }

    @Override
    public void removeAllTabs() {
        mActionView.removeAllTabs();
    }

    @Override
    public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
        mMenuListeners.remove(listener);
    }

    @Override
    public void removeTab(ActionBar.Tab tab) {
        removeTabAt(tab.getPosition());
    }

    @Override
    public void removeTabAt(int position) {
        mActionView.removeTabAt(position);
    }

    @Override
    public void setBackgroundDrawable(Drawable d) {
        mContainerView.setBackgroundDrawable(d);
    }

    @Override
    public void setCustomView(int resId) {
        View view = LayoutInflater.from(mContext).inflate(resId, mActionView, false);
        setCustomView(view);
    }

    @Override
    public void setCustomView(View view) {
        mActionView.setCustomNavigationView(view);
    }

    @Override
    public void setCustomView(View view, ActionBar.LayoutParams layoutParams) {
        view.setLayoutParams(layoutParams);
        mActionView.setCustomNavigationView(view);
    }

    @Override
    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
        setDisplayOptions(showHomeAsUp ? ActionBar.DISPLAY_HOME_AS_UP : 0, ActionBar.DISPLAY_HOME_AS_UP);
    }

    @Override
    public void setDisplayOptions(int options) {
        mActionView.setDisplayOptions(options);
    }

    @Override
    public void setDisplayOptions(int newOptions, int mask) {
        mActionView.setDisplayOptions((mActionView.getDisplayOptions() & ~mask) | newOptions);
    }

    @Override
    public void setDisplayShowCustomEnabled(boolean showCustom) {
        setDisplayOptions(showCustom ? ActionBar.DISPLAY_SHOW_CUSTOM : 0, ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    @Override
    public void setDisplayShowHomeEnabled(boolean showHome) {
        setDisplayOptions(showHome ? ActionBar.DISPLAY_SHOW_HOME : 0, ActionBar.DISPLAY_SHOW_HOME);
    }

    @Override
    public void setDisplayShowTitleEnabled(boolean showTitle) {
        setDisplayOptions(showTitle ? ActionBar.DISPLAY_SHOW_TITLE : 0, ActionBar.DISPLAY_SHOW_TITLE);
    }

    @Override
    public void setDisplayUseLogoEnabled(boolean useLogo) {
        setDisplayOptions(useLogo ? ActionBar.DISPLAY_USE_LOGO : 0, ActionBar.DISPLAY_USE_LOGO);
    }

    @Override
    public void setListNavigationCallbacks(SpinnerAdapter adapter, ActionBar.OnNavigationListener callback) {
        mActionView.setDropdownAdapter(adapter);
        mActionView.setCallback(callback);
    }

    @Override
    public void setNavigationMode(int mode) {
        mActionView.setNavigationMode(mode);
    }

    @Override
    public void setSelectedNavigationItem(int position) {
        switch (mActionView.getNavigationMode()) {
            default:
            case ActionBar.NAVIGATION_MODE_STANDARD:
                throw new IllegalStateException();

            case ActionBar.NAVIGATION_MODE_TABS:
                mActionView.getTabAt(position).select();
                break;

            case ActionBar.NAVIGATION_MODE_LIST:
                mActionView.setDropdownSelectedPosition(position);
                break;
        }
    }

    @Override
    public void selectTab(ActionBar.Tab tab) {
        mActionView.selectTab(tab);
    }

    @Override
    public void setSubtitle(CharSequence subtitle) {
        mActionView.setSubtitle(subtitle);
    }

    @Override
    public void setSubtitle(int resId) {
        mActionView.setSubtitle(resId);
    }

    @Override
    public void setTitle(CharSequence title) {
        mActionView.setTitle(title);
    }
    @Override
    public void setTitle(int resId) {
        mActionView.setTitle(resId);
    }

    @Override
    public void show() {
        //TODO: animate
        mContainerView.setVisibility(View.VISIBLE);
    }
}
