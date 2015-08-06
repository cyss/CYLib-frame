package com.cyss.android.lib.utils;

import android.util.Log;

/**
 * Created by cyjss on 2015/8/6.
 */
public class CYLog {

    public static void d(Class clazz, String msg) {
        Log.d(clazz.getSimpleName(), msg);
    }

    public static void d(Object obj, String msg) {
        Log.d(obj.getClass().getSimpleName(), msg);
    }

    public static void d(Object obj, String msg, Throwable err) {
        Log.d(obj.getClass().getSimpleName(), msg, err);
    }
}
