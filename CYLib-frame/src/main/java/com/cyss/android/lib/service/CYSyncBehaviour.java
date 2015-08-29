package com.cyss.android.lib.service;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.Serializable;

/**
 * Created by cyjss on 2015/8/29.
 */
public abstract class CYSyncBehaviour {

    private Context context;
    private Bundle mArgs;
    private long groupId;
    private int startId;
    private boolean cancelFlag = false;
    private ResultReceiver mReceiver;

    public void markCancel() {
        this.cancelFlag = true;
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

    public abstract CYSyncResult run();

    public static CYSyncBehaviour create(Class<? extends CYSyncBehaviour> behaviourClazz, Context context) {
        CYSyncBehaviour behaviour = null;
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

    public static CYSyncResult success() {
        CYSyncResult res = new CYSyncResult(CYSyncResult.SUCCESS);
        return res;
    }

    public static CYSyncResult fail() {
        CYSyncResult res = new CYSyncResult(CYSyncResult.FAIL);
        return res;
    }
}
