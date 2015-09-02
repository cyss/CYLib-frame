package test.frame.cyss.com.testdemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyss.android.lib.CYFragment;
import com.cyss.android.lib.CYSlidingMenu;

import test.frame.cyss.com.testdemo.R;

/**
 * Created by cyjss on 2015/8/21.
 */
public class Tab4Fragment extends CYFragment {

    private CYSlidingMenu slidingMenu;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        slidingMenu = new CYSlidingMenu(getActivity());
        slidingMenu.setContentView(R.layout.activity_listview);
        slidingMenu.setLeftMenuView(R.layout.listview_item);
        return slidingMenu;
    }

}
