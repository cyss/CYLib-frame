package com.cyss.android.lib.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * Created by cyjss on 2015/9/1.
 */
public class ScreenUtils {
    private static int[] screenSize;

    public static int[] getScreenSize(Context context) {
        if (screenSize == null) {
            screenSize = new int[2];
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point(0, 0);
            screenSize[0] = wm.getDefaultDisplay().getWidth();
            screenSize[1] = wm.getDefaultDisplay().getHeight();
        }
        return screenSize;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
