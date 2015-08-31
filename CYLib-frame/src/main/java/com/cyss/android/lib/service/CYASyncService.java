package com.cyss.android.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
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
public class CYASyncService extends Service {

    private ASyncHandler mDefaultHandler;
    private Looper mDefaultLooper;
    private static final String mDefaultHandlerThreadName = "_dcyThread";
    private SortedMap<Long, CYASyncBehaviour> mTasks;
    private int mStartBehavior = START_NOT_STICKY;

    public CYASyncService() {
        mTasks = Collections.synchronizedSortedMap(new TreeMap<Long, CYASyncBehaviour>());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread(mDefaultHandlerThreadName);
        thread.start();
        this.mDefaultLooper = thread.getLooper();
        this.mDefaultHandler = new ASyncHandler(this.mDefaultLooper);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new CYASyncBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf(startId);
            return mStartBehavior;
        }
        String action = intent.getAction();
        if (CYASyncTask.IN_QUEUE.equals(action)) {
            Message msg = this.mDefaultHandler.obtainMessage();
            msg.obj = getSyncBehaviour(intent, startId);
            this.mDefaultHandler.sendMessage(msg);
        } else if (CYASyncTask.EXECUTED.equals(action)) {
            HandlerThread thread = new HandlerThread("CYAsyncThread");
            thread.start();
            ASyncHandler queueHandler = new ASyncHandler(thread.getLooper());
            Message msg = queueHandler.obtainMessage();
            msg.obj = getSyncBehaviour(intent, startId);
            queueHandler.sendMessage(msg);
        }
        return mStartBehavior;
    }

    public void cancelTaskById(long taskId, int reason) {
        mTasks.get(taskId).markCancel(reason);
    }

    private CYASyncBehaviour getSyncBehaviour(Intent intent, int startId) {
        Class<? extends CYASyncBehaviour> behaviourClazz = (Class<? extends CYASyncBehaviour>) intent.getSerializableExtra(CYASyncTask.BEHAVIOUR_KEY);
        CYASyncBehaviour behaviour = CYASyncBehaviour.create(behaviourClazz, this);
        behaviour.setArgs(intent.getBundleExtra(CYASyncTask.TASK_ARG_KEY));
        behaviour.setGroupId(intent.getIntExtra(CYASyncTask.GROUP_KEY, 0));
        behaviour.setReceiver((ResultReceiver) intent.getParcelableExtra(CYASyncTask.RECEIVER_KEY));
        behaviour.setStartId(startId);
        mTasks.put(intent.getLongExtra(CYASyncTask.TASK_KEY, 0), behaviour);
        return behaviour;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDefaultLooper.quit();
    }

    public class CYASyncBinder extends Binder {
        public void cancelTaskById(long id, int reason) {
            CYASyncService.this.cancelTaskById(id, reason);
        }
    }

    private class ASyncHandler extends Handler {

        public ASyncHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            CYASyncBehaviour behaviour = (CYASyncBehaviour) msg.obj;
            CYASyncResult res = null;
            try {
                res = behaviour.run();
            } catch (Exception e) {
                res = CYASyncBehaviour.fail();
                res.getArgs().putSerializable(CYASyncResult.CRASH_KEY, e);
            }
            if (behaviour.isCancel()) {
                res = CYASyncBehaviour.cancel().addArg(CYASyncBehaviour.CANCEL_REASON_KEY, behaviour.getCancelReason());
            }
            behaviour.getReceiver().send(res.getResultType(), res.getArgs());
            if (this != mDefaultHandler) {
                getLooper().quit();
            }
        }
    }
}
