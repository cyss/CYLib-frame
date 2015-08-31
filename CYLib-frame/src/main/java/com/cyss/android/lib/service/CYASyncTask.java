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
public class CYASyncTask implements Serializable {
    private Long taskId = System.nanoTime();
    private long groupId = 0;
    private int type = 0;       //0:立即执行 1:进入队列
    private Bundle mArgs = new Bundle();
    private Context context;
    private Class<? extends CYASyncBehaviour> behaviour;
    private CYASyncCallBack callBack;
    public static final String TASK_ARG_KEY = "com.cyss.android.lib.service.CYSyncTask.TASK_ARG_KEY";
    public static final String TASK_KEY = "com.cyss.android.lib.service.CYSyncTask.TASK_KEY";
    public static final String BEHAVIOUR_KEY = "com.cyss.android.lib.service.CYSyncTask.BEHAVIOUR_KEY";
    public static final String GROUP_KEY = "com.cyss.android.lib.service.CYSyncTask.GROUP_KEY";
    public static final String RECEIVER_KEY = "com.cyss.android.lib.service.CYSyncTask.RECEIVER_KEY";
    public static final String EXECUTED = "com.cyss.android.lib.service.CYSyncTask.EXECUTED";
    public static final String IN_QUEUE = "com.cyss.android.lib.service.CYSyncTask.IN_QUEUE";

    private CYASyncTask() {
    }

    public static CYASyncTask create(Context context) {
        CYASyncTask task = new CYASyncTask();
        task.context = context;
        return task;
    }

    public CYASyncTask setBehaviour(Class<? extends CYASyncBehaviour> behaviour) {
        this.behaviour = behaviour;
        return this;
    }

    public Class<? extends CYASyncBehaviour> getBehaviour() {
        return behaviour;
    }

    public CYASyncTask addQueue() {
        intentToService(true);
        return this;
    }

    public CYASyncTask execute() {
        intentToService(false);
        return this;
    }

    public CYASyncTask setCallBack(CYASyncCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    public CYASyncCallBack getCallBack() {
        return this.callBack;
    }

    public void cancel(final int reason) {
        new CYASyncTaskHandler(this.context) {
            @Override
            public void handleService(Context mContext, CYASyncService.CYASyncBinder mBinder) {
                mBinder.cancelTaskById(taskId, reason);
            }
        }.start();
    }

    private void intentToService(Boolean syncFlag) {
        Intent intent = new Intent(context, CYASyncService.class);
        intent.setAction(syncFlag ? IN_QUEUE : EXECUTED);
        intent.putExtra(TASK_ARG_KEY, mArgs);
        if (behaviour != null) {
            intent.putExtra(BEHAVIOUR_KEY, behaviour);
        }
        intent.putExtra(GROUP_KEY, groupId);
        intent.putExtra(TASK_KEY, taskId);
        intent.putExtra(RECEIVER_KEY, new CYASyncReceiver(this));
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

    public CYASyncTask addArg(String key, String val) {
        this.mArgs.putString(key, val);
        return this;
    }

    public CYASyncTask addArg(String key, Boolean val) {
        this.mArgs.putBoolean(key, val);
        return this;
    }

    public CYASyncTask addArg(String key, ArrayList<String> val) {
        this.mArgs.putStringArrayList(key, val);
        return this;
    }

    public CYASyncTask addArg(String key, String[] val) {
        this.mArgs.putStringArray(key, val);
        return this;
    }

    public CYASyncTask addArg(String key, Integer val) {
        this.mArgs.putInt(key, val);
        return this;
    }

    public CYASyncTask addArg(String key, Long val) {
        this.mArgs.putLong(key, val);
        return this;
    }

    public CYASyncTask addArg(String key, char val) {
        this.mArgs.putChar(key, val);
        return this;
    }

    public CYASyncTask addArg(String key, CharSequence val) {
        this.mArgs.putCharSequence(key, val);
        return this;
    }

    public CYASyncTask addArg(String key, Serializable val) {
        this.mArgs.putSerializable(key, val);
        return this;
    }

    public CYASyncTask addArg(String key, Bundle bundle) {
        this.mArgs.putBundle(key, bundle);
        return this;
    }

    public CYASyncTask addArg(Bundle bundle) {
        this.mArgs.putAll(bundle);
        return this;
    }

    private abstract class CYASyncTaskHandler implements ServiceConnection {
        private CYASyncService.CYASyncBinder mBinder;
        private Context mContext;
        private boolean mAlreadyStarted = false;

        public CYASyncTaskHandler(Context context) {
            this.mContext = context.getApplicationContext();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof CYASyncService.CYASyncBinder) {
                mBinder = (CYASyncService.CYASyncBinder) service;
                handleService(mContext, mBinder);
            }
            mContext.unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        public abstract void handleService(Context mContext, CYASyncService.CYASyncBinder mBinder);

        public void start() {
            if (mAlreadyStarted) {
                return;
            }
            mAlreadyStarted = true;
            Intent intent = new Intent(mContext, CYASyncService.class);
            mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
        }
    }
}
