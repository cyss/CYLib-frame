package com.cyss.android.lib;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cyss.android.lib.annotation.BindView;
import com.cyss.android.lib.impl.CYAutoData;
import com.cyss.android.lib.impl.CYViewParent;
import com.cyss.android.lib.utils.ActivityUtils;
import com.cyss.android.lib.utils.CYLog;
import com.cyss.android.lib.utils.ViewsManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by cyjss on 2015/8/6.
 */
public abstract class CYActivity extends Activity implements View.OnClickListener, View.OnLongClickListener, CYAutoData {

    private Map<Integer, View> needViews = new HashMap<Integer, View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}
