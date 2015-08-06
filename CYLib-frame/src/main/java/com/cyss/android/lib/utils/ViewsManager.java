package com.cyss.android.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyss.android.lib.CYActivity;
import com.cyss.android.lib.annotation.BindView;
import com.cyss.android.lib.impl.CYViewParent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cyjss on 2015/8/6.
 */
public class ViewsManager {

    private static final String LOG_TAG = "ViewsManager";

    private static ViewsManager manager;

    private Context context;
    private Map<String, Integer> layouts = new HashMap<String, Integer>();
    private Map<String, Integer> ids = new HashMap<String, Integer>();

    private final String LAYOUT_NAME = "layout";
    private final String ID_NAME = "id";

    private ViewsManager() {
    }

    public static ViewsManager getInstance(Context context) {
        if (manager == null) {
            manager = new ViewsManager();
        }
        manager.context = context;
        if (manager.layouts.isEmpty() && manager.ids.isEmpty()) {
            manager.refreshViews();
        }
        return manager;
    }

    private void refreshViews() {
        Boolean layoutFlag = this.injectFields(layouts, context.getPackageName() + ".R$" + LAYOUT_NAME);
        Boolean idsFlag = this.injectFields(ids, context.getPackageName() + ".R$" + ID_NAME);
    }

    private Boolean injectFields(Map<String, Integer> map, String clazzName) {
        Boolean flag = true;
        try {
            Class clazz = Class.forName(clazzName);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                map.put(field.getName(), field.getInt(null));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            flag = false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public Integer findIdByName(String name) {
        return ids.get(name);
    }

    public Integer findLayoutByName(String name) {
        return layouts.get(name);
    }

    public void destroy() {
        this.layouts.clear();
        this.ids.clear();
    }

    public static void injectVariable(Object obj, Map<Integer, View> viewStorage) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            BindView bindView = null;
            try {
                bindView = field.getAnnotation(BindView.class);
                field.setAccessible(true);
                field.set(obj, findViewById(obj, bindView.id()));
                View v = (View) field.get(obj);
                if (bindView.click()) {
                    v.setOnClickListener((View.OnClickListener) obj);
                }
                if (bindView.longClick()) {
                    v.setOnLongClickListener((View.OnLongClickListener) obj);
                }
                if (viewStorage != null) {
                    viewStorage.put(bindView.id(), v);
                }
            } catch (NullPointerException e) {
                //can't find annotation
                Log.e(LOG_TAG, "", e);
            } catch (IllegalAccessException e) {
                Log.e(LOG_TAG, "", e);
            } catch (ClassCastException e) {
                Log.e(LOG_TAG, "", e);
            }
        }
    }

    public static void injectVariable(Object obj) {
        injectVariable(obj, null);
    }

    public static void fillViewData(View v, Object obj) {
        if (v != null) {
            if (v instanceof TextView) {
                ((TextView) v).setText(obj.toString());
            } else if (v instanceof Button) {
                ((Button) v).setText(obj.toString());
            } else if (v instanceof EditText) {
                ((EditText) v).setText(obj.toString());
            } else if (v instanceof ImageView) {
                if (obj instanceof Integer) {
                    ((ImageView) v).setImageResource((Integer) obj);
                } else {
                    try {
                        ((ImageView) v).setImageResource(Integer.parseInt(obj.toString()));
                    } catch (NumberFormatException e) {
                    }
                }
            } else if (v instanceof CYViewParent) {
                ((CYViewParent) v).setMyText(obj);
            }
        }
    }

    private static View findViewById(Object obj, int id) {
        if (obj instanceof CYActivity) {
            return ((CYActivity) obj).findViewById(id);
        }
        return null;
    }
}
