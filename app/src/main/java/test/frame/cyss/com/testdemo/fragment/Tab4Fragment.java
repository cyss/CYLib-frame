package test.frame.cyss.com.testdemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyss.android.lib.CYFragment;

import test.frame.cyss.com.testdemo.R;

/**
 * Created by cyjss on 2015/8/21.
 */
public class Tab4Fragment extends CYFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tab4, null);
    }


}
