package com.cyss.android.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by cyjss on 2015/8/24.
 */
public class CYSyncService extends Service {

    private SyncHandler mDefaultHandler;
    private Looper mDefaultLooper;
    private static final String mDefaultHandlerThreadName = "_dcyThread";
    private SortedMap<Long, CYSyncBehaviour> mTasks;
    private int mStartBehavior = START_NOT_STICKY;

    public CYSyncService() {
        mTasks = Collections.synchronizedSortedMap(new TreeMap<Long, CYSyncBehaviour>());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread(mDefaultHandlerThreadName);
        thread.start();
        this.mDefaultLooper = thread.getLooper();
        this.mDefaultHandler = new SyncHandler(this.mDefaultLooper);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf(startId);
            return mStartBehavior;
        }
        String action = intent.getAction();
        if (CYSyncTask.IN_QUEUE.equals(action)) {
            Message msg = this.mDefaultHandler.obtainMessage();
            msg.obj = getSyncBehaviour(intent, startId);
            this.mDefaultHandler.sendMessage(msg);
        } else if (CYSyncTask.EXECUTED.equals(action)) {
            HandlerThread thread = new HandlerThread("CYAsyncThread");
            thread.start();
            SyncHandler queueHandler = new SyncHandler(thread.getLooper());
            Message msg = queueHandler.obtainMessage();
            msg.obj = getSyncBehaviour(intent, startId);
            queueHandler.sendMessage(msg);
        }
        return mStartBehavior;
    }

    public void cancelTaskById(long taskId) {
        mTasks.get(taskId).markCancel();
    }

    private CYSyncBehaviour getSyncBehaviour(Intent intent, int startId) {
        Class<? extends CYSyncBehaviour> behaviourClazz = (Class<? extends CYSyncBehaviour>) intent.getSerializableExtra(CYSyncTask.BEHAVIOUR_KEY);
        CYSyncBehaviour behaviour = CYSyncBehaviour.create(behaviourClazz, this);
        behaviour.setArgs(intent.getBundleExtra(CYSyncTask.TASK_ARG_KEY));
        behaviour.setGroupId(intent.getIntExtra(CYSyncTask.GROUP_KEY, 0));
        behaviour.setReceiver((ResultReceiver) intent.getParcelableExtra(CYSyncTask.RECEIVER_KEY));
        behaviour.setStartId(startId);
        mTasks.put(intent.getLongExtra(CYSyncTask.TASK_KEY, 0), behaviour);
        return behaviour;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDefaultLooper.quit();
    }

    private class SyncHandler extends Handler {

        public SyncHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            CYSyncBehaviour behaviour = (CYSyncBehaviour) msg.obj;
            CYSyncResult res = null;
            try {
                res = behaviour.run();
            } catch (Exception e) {
                res = CYSyncBehaviour.fail();
                res.getArgs().putSerializable(CYSyncResult.CRASH_KEY, e);
            }
            behaviour.getReceiver().send(res.getResultType(), res.getArgs());
            if (this != mDefaultHandler) {
                getLooper().quit();
            }
        }
    }
}
