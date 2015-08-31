package com.cyss.android.lib.service;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * Created by cyjss on 2015/8/29.
 */
public abstract class CYASyncBehaviour {

    private Context context;
    private Bundle mArgs;
    private long groupId;
    private int startId;
    private boolean cancelFlag = false;
    private ResultReceiver mReceiver;
    private int cancelReason = 0;
    public static final String CANCEL_REASON_KEY = "com.cyss.android.lib.service.CYSyncBehaviour.CANCEL_REASON_KEY";

    public void markCancel(int reason) {
        this.cancelFlag = true;
        this.cancelReason = reason;
    }

    public int getCancelReason() {
        return cancelReason;
    }

    public Boolean isCancel() {
        return this.cancelFlag;
    }

    public ResultReceiver getReceiver() {
        return mReceiver;
    }

    public void setReceiver(ResultReceiver mReceiver) {
        this.mReceiver = mReceiver;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public void setArgs(Bundle mArgs) {
        this.mArgs = mArgs;
    }

    public Bundle getArgs() {
        return mArgs;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public abstract CYASyncResult run();

    public static CYASyncBehaviour create(Class<? extends CYASyncBehaviour> behaviourClazz, Context context) {
        CYASyncBehaviour behaviour = null;
        try {
            behaviour = behaviourClazz.newInstance();
            behaviour.setContext(context);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return behaviour;
    }

    public static CYASyncResult success() {
        CYASyncResult res = new CYASyncResult(CYASyncResult.SUCCESS);
        return res;
    }

    public static CYASyncResult fail() {
        CYASyncResult res = new CYASyncResult(CYASyncResult.FAIL);
        return res;
    }

    public static CYASyncResult cancel() {
        CYASyncResult res = new CYASyncResult(CYASyncResult.CANCEL);
        return res;
    }
}
