package com.cyss.android.lib.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by cyjss on 2015/8/29.
 */
public class CYASyncReceiver extends ResultReceiver {

    private CYASyncTask task;

    public CYASyncReceiver(CYASyncTask task) {
        super(new Handler());
        this.task = task;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        CYASyncCallBack callBack = this.task.getCallBack();
        if (callBack != null) {
            if (resultCode == CYASyncResult.SUCCESS) {
                callBack.success(resultData);
            } else if (resultCode == CYASyncResult.FAIL) {
                Object obj = resultData.getSerializable(CYASyncResult.CRASH_KEY);
                Exception e = null;
                if (obj != null) {
                    try {
                        e = (Exception) resultData.getSerializable(CYASyncResult.CRASH_KEY);
                    } catch (Exception ex) {
                        e = null;
                    }
                }
                callBack.fail(resultData, e);
            } else if (resultCode == CYASyncResult.CANCEL) {
                callBack.cancel(resultData.getInt(CYASyncResult.CANCEL_REASON_KEY, resultData.getInt(CYASyncBehaviour.CANCEL_REASON_KEY, -1)));
            }
        }
    }
}
