package test.frame.cyss.com.testdemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyss.android.lib.CYFragment;
import com.cyss.android.lib.CYFragmentActivity;
import com.cyss.android.lib.annotation.BindView;
import com.cyss.android.lib.utils.CYLog;

import test.frame.cyss.com.testdemo.fragment.*;

/**
 * Created by cyjss on 2015/8/21.
 */
public class FragmentActivity extends CYFragmentActivity {

    private final int containerId = R.id.contentContainer;
    private final String tab1Tag = "tab1";
    private final String tab2Tag = "tab2";
    private final String tab3Tag = "tab3";
    private final String tab4Tag = "tab4";

    @BindView(id = containerId)
    private LinearLayout contentContainer;

    @BindView(id = R.id.tab1, click = true)
    private TextView tab1Item;
    @BindView(id = R.id.tab2, click = true)
    private RelativeLayout tab2Item;
    @BindView(id = R.id.tab3, click = true)
    private RelativeLayout tab3Item;
    @BindView(id = R.id.tab4, click = true)
    private RelativeLayout tab4Item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        try {
            createFragmentToContainer(containerId, tab1Tag, Tab1Fragment.class, "Test constructor args for string", 1010);
            createFragmentToContainer(containerId, tab2Tag, Tab2Fragment.class);
            createFragmentToContainer(containerId, tab3Tag, Tab3Fragment.class);
            createFragmentToContainer(containerId, tab4Tag, Tab4Fragment.class);
        } catch (Exception e) {
            //handle error
            CYLog.d(this, "===>Create fragment error", e);
        }
        showFragment(containerId, tab1Tag);
    }

    @Override
    public void viewClick(View v) {
        switch (v.getId()) {
            case R.id.tab1:
                showFragment(containerId, tab1Tag);
                break;
            case R.id.tab2:
                showFragment(containerId, tab2Tag);
                break;
            case R.id.tab3:
                showFragment(containerId, tab3Tag);
                break;
            case R.id.tab4:
                showFragment(containerId, tab4Tag);
                break;
        }
    }
}
