package test.frame.cyss.com.testdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cyss.android.lib.CYFragment;
import com.cyss.android.lib.annotation.BindView;

import test.frame.cyss.com.testdemo.R;
import test.frame.cyss.com.testdemo.SlidingMenuActivity;

/**
 * Created by cyjss on 2015/8/21.
 */
public class Tab3Fragment extends CYFragment {

    @BindView(id = R.id.toSlideMenu, click = true)
    private Button toSlideMenu;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab3, null);
    }

    @Override
    public void viewClick(View v) {
        if (v.getId() == R.id.toSlideMenu) {
            Intent intent = new Intent(getActivity(), SlidingMenuActivity.class);
            startActivity(intent);
        }
    }
}
