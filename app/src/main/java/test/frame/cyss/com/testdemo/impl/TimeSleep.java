package test.frame.cyss.com.testdemo.impl;

import com.cyss.android.lib.service.CYASyncBehaviour;
import com.cyss.android.lib.service.CYASyncResult;

/**
 * Created by cyjss on 2015/8/29.
 */
public class TimeSleep extends CYASyncBehaviour {

    @Override
    public CYASyncResult run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int num = getArgs().getInt("send_num", 0);
        return success().addArg("test", "TimeSleep").addArg("number", num);
    }

}
