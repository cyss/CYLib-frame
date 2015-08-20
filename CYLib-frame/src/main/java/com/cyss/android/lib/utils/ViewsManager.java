package com.cyss.android.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyss.android.lib.CYActivity;
import com.cyss.android.lib.CYFragmentActivity;
import com.cyss.android.lib.annotation.BindView;
import com.cyss.android.lib.impl.CYViewParent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Map<Integer, String> layoutsReverse = new HashMap<Integer, String>();
    private Map<Integer, String> idsReverse = new HashMap<Integer, String>();

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
        Boolean layoutFlag = this.injectFields(layouts, layoutsReverse, context.getPackageName() + ".R$" + LAYOUT_NAME);
        Boolean idsFlag = this.injectFields(ids, idsReverse, context.getPackageName() + ".R$" + ID_NAME);
    }

    private Boolean injectFields(Map<String, Integer> map, Map<Integer, String> mapReverse, String clazzName) {
        Boolean flag = true;
        try {
            Class clazz = Class.forName(clazzName);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String name = field.getName();
                Integer id = field.getInt(null);
                map.put(name, id);
                mapReverse.put(id, name);
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

    public String findNameById(Integer id) {
        return idsReverse.get(id);
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
            if (v instanceof CYViewParent) {
                ((CYViewParent) v).setCustomData(obj);
            } else if (v instanceof TextView) {
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
            }
        }
    }

    public static Object getViewData(View v) {
        Object obj = null;
        if (v != null) {
            if (v instanceof TextView) {
                obj = ((TextView) v).getText();
            } else if (v instanceof Button) {
                obj = ((Button) v).getTag();
            } else if (v instanceof EditText) {
                obj = ((EditText) v).getText();
            } else if (v instanceof CYViewParent) {
                obj = ((CYViewParent) v).getCustomData();
            }
        }
        return obj;
    }

    public static void invokeSetMethod(Object obj, Method method, Object val) throws InvocationTargetException, IllegalAccessException, NumberFormatException {
        Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 1) {
            Class c = parameterTypes[0];
            if (c.equals(Integer.class)) {
                method.invoke(obj, Integer.parseInt(val.toString()));
            } else if (c.equals(String.class)) {
                method.invoke(obj, val.toString());
            } else if (c.equals(Boolean.class)) {
                method.invoke(obj, Boolean.parseBoolean(val.toString()));
            } else if (c.equals(Long.class)) {
                method.invoke(obj, Long.parseLong(val.toString()));
            } else if (c.equals(Float.class)) {
                method.invoke(obj, Float.parseFloat(val.toString()));
            } else if (c.equals(Double.class)) {
                method.invoke(obj, Double.parseDouble(val.toString()));
            } else {
                method.invoke(obj, val);
            }
        }
    }

    /**
     * 获取所有子View
     *
     * @param view
     * @return
     */
    public static List<View> getAllHasIdViews(View view) {
        List<View> allChildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewChild = vp.getChildAt(i);
                if (viewChild.getId() != -1) {
                    allChildren.add(viewChild);
                }
                allChildren.addAll(getAllHasIdViews(viewChild));
            }
        }
        return allChildren;
    }

    public static View findViewById(Object obj, int id) {
        if (obj instanceof CYActivity) {
            return ((CYActivity) obj).findViewById(id);
        } else if (obj instanceof CYFragmentActivity) {
            return ((CYFragmentActivity) obj).findViewById(id);
        } else if (obj instanceof View) {
            return ((View) obj).findViewById(id);
        } else if (obj instanceof Activity) {
            return ((Activity) obj).findViewById(id);
        }
        return null;
    }
}
