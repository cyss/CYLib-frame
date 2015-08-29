package test.frame.cyss.com.testdemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyss.android.lib.CYFragment;
import com.cyss.android.lib.annotation.BindView;
import com.cyss.android.lib.utils.CYLog;

import test.frame.cyss.com.testdemo.R;

/**
 * Created by cyjss on 2015/8/21.
 */
public class Tab1Fragment extends CYFragment {

    @BindView(id = R.id.tempTv)
    private TextView tempTv;
    @BindView(id = R.id.tempBtn, click = true)
    private TextView tempBtn;

    private String changeStr;

    public Tab1Fragment(String changeStr, Integer appendInt) {
        CYLog.d(this, "===>" + changeStr);
        this.changeStr = changeStr + appendInt;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab1, null);
    }

    @Override
    public void viewClick(View v) {
        tempTv.setText(this.changeStr);
    }
}
