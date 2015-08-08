package com.cyss.android.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.cyss.android.lib.impl.CYViewParent;

/**
 * Created by cyjss on 2015/8/8.
 */
public class CYView extends View implements CYViewParent {

    public CYView(Context context) {
        super(context);
    }

    public CYView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CYView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CYView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setCustomData(Object obj) {

    }

    @Override
    public Object getCustomData() {
        return null;
    }
}
