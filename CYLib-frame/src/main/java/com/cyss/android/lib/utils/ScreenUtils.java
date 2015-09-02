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
}
