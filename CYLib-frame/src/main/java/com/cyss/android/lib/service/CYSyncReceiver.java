package com.cyss.android.lib.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by cyjss on 2015/8/29.
 */
public class CYSyncReceiver extends ResultReceiver {

    private CYSyncTask task;

    public CYSyncReceiver(CYSyncTask task) {
        super(new Handler());
        this.task = task;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        CYSyncCallBack callBack = this.task.getCallBack();
        if (callBack != null) {
            if (resultCode == CYSyncResult.SUCCESS) {
                callBack.success(resultData);
            } else if (resultCode == CYSyncResult.FAIL) {
                Object obj = resultData.getSerializable(CYSyncResult.CRASH_KEY);
                Exception e = null;
                if (obj != null) {
                    try {
                        e = (Exception) resultData.getSerializable(CYSyncResult.CRASH_KEY);
                    } catch (Exception ex) {
                        e = null;
                    }
                }
                callBack.fail(resultData, e);
            } else if (resultCode == CYSyncResult.CANCEL) {
                callBack.cancel(resultData.getInt(CYSyncResult.CANCEL_REASON_KEY, -1));
            }
        }
    }
}
