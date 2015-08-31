package com.cyss.android.lib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.lang.reflect.Constructor;
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
    private CYFragment shownFragment = null;

    private FragmentManager manager = getSupportFragmentManager();

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

    private CYFragment createFragment(Class clazz, Object... constructorArgs) throws Exception {
        CYFragment fragment = null;
        Class[] argsType = new Class[constructorArgs.length];
        int i = 0;
        for (Object obj : constructorArgs) {
            argsType[i++] = obj.getClass();
        }
        Constructor constructor = clazz.getConstructor(argsType);
        if (constructor != null) {
            fragment = (CYFragment) constructor.newInstance(constructorArgs);
            CYLog.d(this, "===>constructor is not null");
        } else {
            CYLog.d(this, "===>constructor is null");
        }
        return fragment;
    }

    public CYFragment createFragment(Integer containerId, String tag, Class clazz, Object... constructorArgs) throws Exception {
        CYFragment fragment = null;
        Map<String, CYFragment> map = fragments.get(containerId);
        if (map == null) {
            map = new HashMap<String, CYFragment>();
            fragments.put(containerId, map);
            fragment = createFragment(clazz, constructorArgs);
            map.put(tag, fragment);
            this.addTags(containerId, tag);
        } else {
            if (map.containsKey(tag)) {
                fragment = map.get(tag);
            } else {
                fragment = createFragment(clazz, constructorArgs);
                map.put(tag, fragment);
                this.addTags(containerId, tag);
            }
        }
        return fragment;
    }

    public CYFragment createFragment(Integer containerId, String tag, Class clazz) throws Exception {
        return createFragment(containerId, tag, clazz, new Object[0]);
    }

    public CYFragment createFragmentToContainer(Integer containerId, String tag, Class clazz) throws Exception {
        CYFragment fragment = createFragment(containerId, tag, clazz);
        showFragment(fragment, containerId, tag);
        return fragment;
    }

    public CYFragment createFragmentToContainer(Integer containerId, String tag, Class clazz, Object... constructorArgs) throws Exception {
        CYFragment fragment = createFragment(containerId, tag, clazz, constructorArgs);
        showFragment(fragment, containerId, tag);
        return fragment;
    }

    public Boolean showFragment(final Integer containerId, final String tag) {
        Boolean flag = true;
        final CYFragment fragment = getFragment(containerId, tag);
        if (fragment != null) {
            if (!fragment.isAdded()) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFragment(fragment, containerId, tag);
                    }
                }, 100);
            } else {
                this.showFragment(fragment, containerId, tag);
            }
        } else {
            CYLog.d(this, "no found tag named:" + tag);
            flag = false;
        }
        return flag;
    }

    public Boolean showFragment(CYFragment fragment, Integer containerId) {
        Boolean flag = false;
        FragmentTransaction transaction = manager.beginTransaction();
        hideAllFragment(transaction, containerId);
        if (fragment.isAdded()) {
            transaction.show(fragment);
            flag = true;
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

        FragmentTransaction transaction = manager.beginTransaction();
        if (!isShownFragment(fragment)) {
            hideAllFragment(transaction, containerId);
        }
        boolean resumeFlag = false;
        if (fragment.getState() == CYFragment.StoreState.HideOrShow) {
            if (fragment.isAdded()) {
                if (!isShownFragment(fragment)) {
                    transaction.show(fragment);
                    resumeFlag = true;

                }
            } else {
                fragment.setParentContainerId(containerId);
                transaction.add(containerId, fragment, tag);
                resumeFlag = true;
            }
        } else if (fragment.getState() == CYFragment.StoreState.REPLACE) {
            transaction.replace(containerId, fragment, tag);
            transaction.show(fragment);
            resumeFlag = true;
        }
        this.shownFragment = fragment;
        transaction.commit();

        fragment.setUserVisibleHint(true);
    }

    private boolean isShownFragment(CYFragment fragment) {
        return fragment == this.shownFragment;
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
            if (this.shownFragment != null && this.shownFragment == map.get(tag)) {
                this.shownFragment.setUserVisibleHint(false);
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String tag = data.getStringExtra(CYFragment.RESULT_INTENT_TAG_KEY);
        int containerId = data.getIntExtra(CYFragment.RESULT_INTENT_CONTAINER_KEY, -1);
        if (tag != null && containerId != -1) {
            data.removeExtra(CYFragment.RESULT_INTENT_TAG_KEY);
            data.removeExtra(CYFragment.RESULT_INTENT_CONTAINER_KEY);
            CYFragment fragment = getFragment(containerId, tag);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
