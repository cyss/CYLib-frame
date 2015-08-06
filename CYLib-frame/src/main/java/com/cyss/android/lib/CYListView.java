package com.cyss.android.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by cyjss on 2015/8/6.
 */
public class CYListView extends ListView {
    public CYListView(Context context) {
        super(context);
    }

    public CYListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CYListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CYListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}
