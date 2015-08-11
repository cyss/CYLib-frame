package com.cyss.android.lib.impl;

import android.view.ViewParent;

/**
 * Created by cyjss on 2015/8/6.
 */
public interface CYViewParent<T> {
    public void setCustomData(T obj);
    public T getCustomData();
}
