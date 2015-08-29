package com.cyss.android.lib.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cyjss on 2015/8/24.
 */
public class CYSyncTask implements Serializable {
    private Long taskId = System.nanoTime();
    private long groupId = 0;
    private int type = 0;       //0:立即执行 1:进入队列
    private Bundle mArgs = new Bundle();
    private Context context;
    private Class<? extends CYSyncBehaviour> behaviour;
    private CYSyncCallBack callBack;
    public static final String TASK_ARG_KEY = "com.cyss.android.lib.service.CYSyncTask.TASK_ARG_KEY";
    public static final String TASK_KEY = "com.cyss.android.lib.service.CYSyncTask.TASK_KEY";
    public static final String BEHAVIOUR_KEY = "com.cyss.android.lib.service.CYSyncTask.BEHAVIOUR_KEY";
    public static final String GROUP_KEY = "com.cyss.android.lib.service.CYSyncTask.GROUP_KEY";
    public static final String RECEIVER_KEY = "com.cyss.android.lib.service.CYSyncTask.RECEIVER_KEY";
    public static final String EXECUTED = "com.cyss.android.lib.service.CYSyncTask.EXECUTED";
    public static final String IN_QUEUE = "com.cyss.android.lib.service.CYSyncTask.IN_QUEUE";

    private CYSyncTask() {
    }

    public static CYSyncTask create(Context context) {
        CYSyncTask task = new CYSyncTask();
        task.context = context;
        return task;
    }

    public CYSyncTask setBehaviour(Class<? extends CYSyncBehaviour> behaviour) {
        this.behaviour = behaviour;
        return this;
    }

    public Class<? extends CYSyncBehaviour> getBehaviour() {
        return behaviour;
    }

    public CYSyncTask addQueue() {
        intentToService(true);
        return this;
    }

    public CYSyncTask execute() {
        intentToService(false);
        return this;
    }

    public CYSyncTask setCallBack(CYSyncCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    public CYSyncCallBack getCallBack() {
        return this.callBack;
    }

    public void cancel() {

    }

    private void intentToService(Boolean syncFlag) {
        Intent intent = new Intent(context, CYSyncService.class);
        intent.setAction(syncFlag ? IN_QUEUE : EXECUTED);
        intent.putExtra(TASK_ARG_KEY, mArgs);
        if (behaviour != null) {
            intent.putExtra(BEHAVIOUR_KEY, behaviour);
        }
        intent.putExtra(GROUP_KEY, groupId);
        intent.putExtra(TASK_KEY, taskId);
        intent.putExtra(RECEIVER_KEY, new CYSyncReceiver(this));
        context.startService(intent);
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public CYSyncTask addArg(String key, String val) {
        this.mArgs.putString(key, val);
        return this;
    }

    public CYSyncTask addArg(String key, Boolean val) {
        this.mArgs.putBoolean(key, val);
        return this;
    }

    public CYSyncTask addArg(String key, ArrayList<String> val) {
        this.mArgs.putStringArrayList(key, val);
        return this;
    }

    public CYSyncTask addArg(String key, String[] val) {
        this.mArgs.putStringArray(key, val);
        return this;
    }

    public CYSyncTask addArg(String key, Integer val) {
        this.mArgs.putInt(key, val);
        return this;
    }

    public CYSyncTask addArg(String key, Long val) {
        this.mArgs.putLong(key, val);
        return this;
    }

    public CYSyncTask addArg(String key, char val) {
        this.mArgs.putChar(key, val);
        return this;
    }

    public CYSyncTask addArg(String key, CharSequence val) {
        this.mArgs.putCharSequence(key, val);
        return this;
    }

    public CYSyncTask addArg(String key, Serializable val) {
        this.mArgs.putSerializable(key, val);
        return this;
    }

    public CYSyncTask addArg(String key, Bundle bundle) {
        this.mArgs.putBundle(key, bundle);
        return this;
    }

    public CYSyncTask addArg(Bundle bundle) {
        this.mArgs.putAll(bundle);
        return this;
    }

    private class CYSyncTaskHandler implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
