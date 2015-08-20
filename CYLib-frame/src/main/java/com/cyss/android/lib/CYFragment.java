package com.cyss.android.lib;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by cyjss on 2015/8/11.
 */
public class CYFragment extends Fragment {
    public static enum StoreState {
        REPLACE,
        HideOrShow
    }

    private StoreState state = StoreState.HideOrShow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
    }

    protected void viewOnResume() { }

    protected void viewOnPause() { }

    public StoreState getState() {
        return this.state;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            viewOnResume();
        } else {
            viewOnPause();
        }
    }
}
