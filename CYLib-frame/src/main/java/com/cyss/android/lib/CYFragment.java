package com.cyss.android.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.cyss.android.lib.impl.CYAutoData;
import com.cyss.android.lib.utils.ActivityUtils;
import com.cyss.android.lib.utils.CYLog;
import com.cyss.android.lib.utils.ViewsManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cyjss on 2015/8/11.
 */
public abstract class CYFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, CYAutoData {

    public static enum StoreState {
        REPLACE,
        HideOrShow
    }

    private StoreState state = StoreState.HideOrShow;
    private Map<Integer, View> needViews = new HashMap<Integer, View>();
    private View mainView;
    private int parentContainerId = -1;
    public static final String RESULT_INTENT_TAG_KEY = "_fragment_tag_";
    public static final String RESULT_INTENT_CONTAINER_KEY = "_fragment_container_";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = this.inflaterView(inflater, container, savedInstanceState);
        if (v != null) {
            this.mainView = v;
//            v.setOnTouchListener(this);
            ViewsManager.injectVariable(this, needViews);
        }
        viewLoaded(v);
        return v;
    }

    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected void viewLoaded(View v) {
    }

    public CYFragmentActivity getCYFragmentActivity() {
        return (CYFragmentActivity) super.getActivity();
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        CYLog.d(this, "===>onInflate");
    }

    public void viewClick(View v) {
    }

    public void viewLongClick(View v) {
    }

    public void fillMapData(Map<String, Object> params) {
        ActivityUtils.fillMapData(params, this);
    }

    public void fillBeanData(Object obj) {
        ActivityUtils.fillBeanData(obj, this);
    }

    /**
     * 复杂布局效率不高，获取全部信息
     *
     * @return
     */
    public Map<String, Object> getMapData() {
        return getMapData(getActivity().getWindow().getDecorView());
    }

    public Map<String, Object> getMapData(View parentView) {
        return ActivityUtils.getMapData(parentView, getActivity());
    }

    public Map<String, Object> getMapFieldData() {
        return ActivityUtils.getMapFieldData(needViews, getActivity());
    }

    public Object getBeanData(Class clazz) {
        return ActivityUtils.getBeanData(clazz, this);
    }


    protected void viewOnResume() {
    }

    protected void viewOnPause() {
    }

    public StoreState getState() {
        return this.state;
    }

    public void setState(StoreState state) {
        this.state = state;
    }

    public int getParentContainerId() {
        return parentContainerId;
    }

    public void setParentContainerId(int parentContainerId) {
        this.parentContainerId = parentContainerId;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        String tag = getTag();
        if (tag != null) {
            intent.putExtra(RESULT_INTENT_TAG_KEY, tag);
            intent.putExtra(RESULT_INTENT_CONTAINER_KEY, getParentContainerId());
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            CYLog.d(this, getClass().getName() + "===>viewOnResume");
            viewOnResume();
        } else {
            CYLog.d(this, getClass().getName() + "===>viewOnPause");
            viewOnPause();
        }
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        return true;
//    }

    @Override
    public void onClick(View v) {
        this.viewClick(v);
    }

    @Override
    public boolean onLongClick(View v) {
        this.viewLongClick(v);
        return true;
    }

    public View getMainView() {
        return mainView;
    }

}
