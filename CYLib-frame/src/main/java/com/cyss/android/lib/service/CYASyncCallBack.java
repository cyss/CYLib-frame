package com.cyss.android.lib.service;

import android.os.Bundle;

/**
 * Created by cyjss on 2015/8/29.
 */
public interface CYASyncCallBack {
    public void success(Bundle bundle);
    public void fail(Bundle bundle, Exception ex);
    public void cancel(int reason);
}
