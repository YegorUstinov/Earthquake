package com.ustin.earthquake;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

public class TabListener<T extends Fragment> implements ActionBar.TabListener {

    public Fragment fragment;
    private Activity activity;
    private Class<T> fragmentClass;
    private int fragmentContainer;
    private static final String TAG = "<== TAB_LISTENER ==>";

    public TabListener(Activity activity, int fragmentContainer, Class<T> fragmentClass) {
        this.activity = activity;
        this.fragmentContainer = fragmentContainer;
        this.fragmentClass = fragmentClass;
        Log.w(TAG, "TabListener");
    }

    // вызывается при выборе новой вкладки
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment == null) {
            String fragmentName = fragmentClass.getName();
            fragment = Fragment.instantiate(activity, fragmentName);
            ft.add(fragmentContainer, fragment, fragmentName);
            Log.w(TAG, "onTabSelected if");
        } else {
            ft.attach(fragment);
            Log.w(TAG, "onTabSelected else");
        }
    }

    // вызывается для текущей вкладки, если была выбрана другая
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment != null)
            ft.detach(fragment);
        Log.w(TAG, "onTabUnselected");
    }

    // вызывается при повторном выборе вкладки
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment != null)
            ft.attach(fragment);
        Log.w(TAG, "onTabReselected");
    }
}
