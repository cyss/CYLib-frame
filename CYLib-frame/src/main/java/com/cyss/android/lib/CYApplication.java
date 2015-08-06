package com.cyss.android.lib;

import android.app.Application;
import android.util.Log;
import android.widget.LinearLayout;

import com.cyss.android.lib.utils.ViewsManager;

/**
 * Created by cyjss on 2015/8/6.
 */
public class CYApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ViewsManager.getInstance(this);
    }
}
