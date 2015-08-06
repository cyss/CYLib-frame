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
import com.cyss.android.lib.impl.CYViewParent;
import com.cyss.android.lib.utils.CYLog;
import com.cyss.android.lib.utils.ViewsManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by cyjss on 2015/8/6.
 */
public abstract class CYActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

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
        for (String key : params.keySet()) {
            Integer id = ViewsManager.getInstance(this).findIdByName(key.toString());
            if (id != null) {
                View v = findViewById(id);
                ViewsManager.fillViewData(v, params.get(key));
            }
        }
    }

    public void fillBeanData(Object obj) {
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String methodName = "";
            if (!Character.isUpperCase(fieldName.charAt(0))) {
                methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            }
            methodName = "get" + methodName;
            try {
                Method method = clazz.getMethod(methodName);
                Integer id = ViewsManager.getInstance(this).findIdByName(fieldName);
                if (id != null) {
                    View v = findViewById(id);
                    ViewsManager.fillViewData(v, method.invoke(obj));
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
