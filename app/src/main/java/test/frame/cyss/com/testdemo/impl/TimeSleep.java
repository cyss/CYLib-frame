package test.frame.cyss.com.testdemo.impl;

import com.cyss.android.lib.service.CYSyncBehaviour;
import com.cyss.android.lib.service.CYSyncResult;
import com.cyss.android.lib.service.CYSyncTask;

/**
 * Created by cyjss on 2015/8/29.
 */
public class TimeSleep extends CYSyncBehaviour {

    @Override
    public CYSyncResult run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int num = getArgs().getInt("send_num", 0);
        return success().addArg("test", "TimeSleep").addArg("number", num);
    }

}