package com.cyss.android.lib.utils;

import android.content.Context;
import android.view.View;

import com.cyss.android.lib.CYActivity;
import com.cyss.android.lib.CYFragment;
import com.cyss.android.lib.CYFragmentActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cyjss on 2015/8/20.
 */
public class ActivityUtils {

    public static void fillMapData(Map<String, Object> params, Object context) {
        for (String key : params.keySet()) {
            Integer id = ViewsManager.getInstance((Context) context).findIdByName(key.toString());
            if (id != null) {
                View v = ViewsManager.findViewById(context, id);
                ViewsManager.fillViewData(v, params.get(key));
            }
        }
    }

    public static void fillBeanData(Object obj, Object context) {
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
                Integer id = ViewsManager.getInstance((Context) context).findIdByName(fieldName);
                if (id != null) {
                    View v = ViewsManager.findViewById(context, id);
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

    public static Map<String, Object> getMapData(View parentView, Context context) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<View> list = ViewsManager.getAllHasIdViews(parentView);
        for (View v : list) {
            Object val = ViewsManager.getInstance(context).getViewData(v);
            String name = ViewsManager.getInstance(context).findNameById(v.getId());
            if (name != null) {
                map.put(name, val);
            }
        }
        return map;
    }

    public static Map<String, Object> getMapFieldData(Map<Integer, View> needViews, Context context) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Integer key : needViews.keySet()) {
            View v = needViews.get(key);
            Object val = ViewsManager.getInstance(context).getViewData(v);
            String name = ViewsManager.getInstance(context).findNameById(key);
            if (name != null) {
                map.put(name, val);
            }
        }
        return map;
    }

    public static Object getBeanData(Class clazz, CYActivity activity) {
        return getCommonBeanData(clazz, activity);
    }

    public static Object getBeanData(Class clazz, CYFragmentActivity activity) {
        return getCommonBeanData(clazz, activity);
    }

    public static Object getBeanData(Class clazz, CYFragment fragment) {
        return getCommonBeanData(clazz, fragment.getActivity());
    }

    private static Object getCommonBeanData(Class clazz, Object context) {
        Field[] fields = clazz.getDeclaredFields();
        Object obj = null;
        try {
            obj = clazz.newInstance();
            for (Field field : fields) {
                String fieldName = field.getName();
                String methodName = "";
                if (!Character.isUpperCase(fieldName.charAt(0))) {
                    methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                }
                methodName = "set" + methodName;
                Integer id = ViewsManager.getInstance((Context) context).findIdByName(fieldName);
                if (id != null) {
                    View v = ViewsManager.findViewById(context, id);
                    Object val = ViewsManager.getInstance((Context) context).getViewData(v);
                    if (val != null) {
                        Method method = null;
                        try {
                            method = clazz.getMethod(methodName, field.getType());
                            ViewsManager.invokeSetMethod(obj, method, val);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
