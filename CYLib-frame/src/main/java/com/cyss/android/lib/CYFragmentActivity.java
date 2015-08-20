package com.cyss.android.lib;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.cyss.android.lib.impl.CYAutoData;
import com.cyss.android.lib.utils.ActivityUtils;
import com.cyss.android.lib.utils.CYLog;
import com.cyss.android.lib.utils.ViewsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cyjss on 2015/8/11.
 */
public abstract class CYFragmentActivity extends FragmentActivity implements View.OnClickListener, View.OnLongClickListener, CYAutoData {

    private Map<Integer, View> needViews = new HashMap<Integer, View>();
    private Map<Integer, Map<String, CYFragment>> fragments = new HashMap<Integer, Map<String, CYFragment>>();
    private Map<Integer, ArrayList<String>> tags = new HashMap<Integer, ArrayList<String>>();
    private final String TAG_NAME = "tags";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            FragmentManager manager = getSupportFragmentManager();
            Bundle saveTags = savedInstanceState.getBundle(TAG_NAME);
            for (String key : saveTags.keySet()) {
                Map<String, CYFragment> map = new HashMap<String, CYFragment>();
                fragments.put(Integer.parseInt(key), map);
                ArrayList<String> tagList = saveTags.getStringArrayList(key);
                for (String tag : tagList) {
                    map.put(tag, (CYFragment) manager.findFragmentByTag(tag));
                }
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ViewsManager.injectVariable(this, needViews);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ViewsManager.injectVariable(this, needViews);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ViewsManager.injectVariable(this, needViews);
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
        return getMapData(getWindow().getDecorView());
    }

    public Map<String, Object> getMapData(View parentView) {
        return ActivityUtils.getMapData(parentView, this);
    }

    public Map<String, Object> getMapFieldData() {
        return ActivityUtils.getMapFieldData(needViews, this);
    }

    public Object getBeanData(Class clazz) {
        return ActivityUtils.getBeanData(clazz, this);
    }

    public void addFragmentToContainer(CYFragment fragment, Integer containerId, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragments.get(containerId).put(tag, fragment);
        this.addTags(containerId, tag);
        showFragment(fragment, containerId, tag);
    }

    public Boolean showFragment(Integer containerId, String tag) {
        Boolean flag = true;
        CYFragment fragment = null;
        if ((fragment = getFragment(containerId, tag)) != null) {
            this.showFragment(fragment, containerId, tag);
        } else {
            CYLog.d(this, "no found tag named:" + tag);
            flag = false;
        }
        return flag;
    }

    public CYFragment getFragment(Integer containerId, String tag) {
        Map<String, CYFragment> map = fragments.get(containerId);
        if (map != null) {
            CYFragment fragment = map.get(tag);
            return fragment;
        } else {
            CYLog.d(this, "no found containerId named:" + containerId);
        }
        return null;
    }

    private void showFragment(CYFragment fragment, Integer containerId, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideAllFragment(transaction, containerId);
        if (fragment.getState() == CYFragment.StoreState.HideOrShow) {
            if (fragment.isAdded()) {
                transaction.show(fragment);
            } else {
                transaction.add(containerId, fragment, tag);
            }
        } else if (fragment.getState() == CYFragment.StoreState.REPLACE) {
            transaction.replace(containerId, fragment, tag);
        }
        transaction.commit();
    }

    protected void hideAllFragment(FragmentTransaction transaction, Integer containerId) {
        Map<String, CYFragment> map = null;
        if (fragments.containsKey(containerId)) {
            map = fragments.get(containerId);
        } else {
            map = new HashMap<String, CYFragment>();
            fragments.put(containerId, map);
        }
        for (String tag : map.keySet()) {
            transaction.hide(map.get(tag));
        }
    }

    private void addTags(Integer containerId, String tag) {
        ArrayList<String> tagList = tags.get(containerId);
        if (tagList == null) {
            tagList = new ArrayList<String>();
            tags.put(containerId, tagList);
        }
        tagList.add(tag);
    }

    @Override
    public void onClick(View v) {
        this.viewClick(v);
    }

    @Override
    public boolean onLongClick(View v) {
        this.viewLongClick(v);
        return true;
    }

    @Override
    public View findViewById(int id) {
        if (this.needViews.containsKey(id)) {
            return needViews.get(id);
        }
        View v = super.findViewById(id);
        if (v != null) {
            needViews.put(id, v);
        }
        return v;
    }

    /**
     * 保存状态，防止fragment重叠
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle();
        for (Integer id : tags.keySet()) {
            bundle.putStringArrayList(id.toString(), tags.get(id));
        }
        outState.putBundle(TAG_NAME, bundle);
        super.onSaveInstanceState(outState);
    }
}
