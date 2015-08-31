package com.cyss.android.lib.service;

import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cyjss on 2015/8/29.
 */
public class CYASyncResult {
    private int resultType;
    private Bundle mArgs = new Bundle();


    public static final int SUCCESS = 1;
    public static final int FAIL = 2;
    public static final int CANCEL = 3;
    public static final String CANCEL_REASON_KEY = "com.cyss.android.lib.service.CYSyncResult.CANCEL_REASON_KEY";
    public static final String CRASH_KEY = "com.cyss.android.lib.service.CYSyncResult.CRASH_KEY";

    public CYASyncResult(int type) {
        this.resultType = type;
    }

    public int getResultType() {
        return resultType;
    }

    public Bundle getArgs() {
        return mArgs;
    }

    public void setArgs(Bundle mArgs) {
        this.mArgs = mArgs;
    }

    public CYASyncResult addArg(String key, String val) {
        this.mArgs.putString(key, val);
        return this;
    }

    public CYASyncResult addArg(String key, Boolean val) {
        this.mArgs.putBoolean(key, val);
        return this;
    }

    public CYASyncResult addArg(String key, ArrayList<String> val) {
        this.mArgs.putStringArrayList(key, val);
        return this;
    }

    public CYASyncResult addArg(String key, String[] val) {
        this.mArgs.putStringArray(key, val);
        return this;
    }

    public CYASyncResult addArg(String key, Integer val) {
        this.mArgs.putInt(key, val);
        return this;
    }

    public CYASyncResult addArg(String key, Long val) {
        this.mArgs.putLong(key, val);
        return this;
    }

    public CYASyncResult addArg(String key, char val) {
        this.mArgs.putChar(key, val);
        return this;
    }

    public CYASyncResult addArg(String key, CharSequence val) {
        this.mArgs.putCharSequence(key, val);
        return this;
    }

    public CYASyncResult addArg(String key, Serializable val) {
        this.mArgs.putSerializable(key, val);
        return this;
    }

    public CYASyncResult addArg(String key, Bundle bundle) {
        this.mArgs.putBundle(key, bundle);
        return this;
    }

    public CYASyncResult addArg(Bundle bundle) {
        this.mArgs.putAll(bundle);
        return this;
    }
}
