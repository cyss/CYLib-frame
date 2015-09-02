package test.frame.cyss.com.testdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cyss.android.lib.CYActivity;
import com.cyss.android.lib.CYFragment;
import com.cyss.android.lib.CYFragmentActivity;
import com.cyss.android.lib.CYSlidingMenu;
import com.cyss.android.lib.annotation.BindView;
import com.cyss.android.lib.service.CYASyncCallBack;
import com.cyss.android.lib.service.CYASyncTask;
import com.cyss.android.lib.utils.CYLog;

import java.util.HashMap;
import java.util.Map;

import test.frame.cyss.com.testdemo.fragment.Tab1Fragment;
import test.frame.cyss.com.testdemo.fragment.Tab2Fragment;
import test.frame.cyss.com.testdemo.fragment.Tab3Fragment;
import test.frame.cyss.com.testdemo.fragment.Tab4Fragment;
import test.frame.cyss.com.testdemo.impl.RequestHttp;
import test.frame.cyss.com.testdemo.impl.TimeSleep;
import test.frame.cyss.com.testdemo.pojo.Person;

public class MainActivity extends CYFragmentActivity implements RadioGroup.OnCheckedChangeListener {

    private final int contentContainer = R.id.contentContainer;
    @BindView(id = R.id.tabsContainer)
    private RadioGroup tabsContainer;
    private CYSlidingMenu slidingMenu;


    private static final String TAB1_TAG = "tab1";
    private static final String TAB2_TAG = "tab2";
    private static final String TAB3_TAG = "tab3";
    private static final String TAB4_TAG = "tab4";

    private static final Map<String, Class<? extends CYFragment>> fragments = new HashMap<String, Class<? extends CYFragment>>();

    static {
        fragments.put(TAB1_TAG, Tab1Fragment.class);
        fragments.put(TAB2_TAG, Tab2Fragment.class);
        fragments.put(TAB3_TAG, Tab3Fragment.class);
        fragments.put(TAB4_TAG, Tab4Fragment.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slidingMenu = new CYSlidingMenu(this);
        slidingMenu.setContentView(R.layout.activity_main);
        slidingMenu.setLeftMenuView(R.layout.layout_sliding_menu);
        setContentView(slidingMenu);
//        setContentView(R.layout.activity_main);
        tabsContainer.setOnCheckedChangeListener(this);
        for (String key : fragments.keySet()) {
            try {
                createFragmentToContainer(contentContainer, key, fragments.get(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showFragment(contentContainer, TAB1_TAG);
    }

    public void showLeftMenu() {
        slidingMenu.openLeftMenu();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton btn = (RadioButton) findViewById(group.getCheckedRadioButtonId());
        showFragment(contentContainer, btn.getTag().toString());
    }
}
