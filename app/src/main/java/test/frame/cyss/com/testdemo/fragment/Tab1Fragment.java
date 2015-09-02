package test.frame.cyss.com.testdemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cyss.android.lib.CYFragment;
import com.cyss.android.lib.annotation.BindView;
import com.cyss.android.lib.utils.CYLog;

import test.frame.cyss.com.testdemo.MainActivity;
import test.frame.cyss.com.testdemo.R;
import test.frame.cyss.com.testdemo.pojo.Person;

/**
 * Created by cyjss on 2015/8/21.
 */
public class Tab1Fragment extends CYFragment {

    @BindView(id = R.id.tempTv)
    private TextView tempTv;
    @BindView(id = R.id.saveBtn, click = true)
    private Button saveBtn;
    @BindView(id = R.id.titleBar)
    private TextView titleBar;
    @BindView(id = R.id.leftBtn, click = true)
    private Button leftBtn;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab1, null);
    }

    @Override
    protected void viewLoaded(View v) {
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setText("Menu");
    }

    @Override
    protected void viewOnResume() {
        this.titleBar.setText(R.string.tab1Title);
    }

    @Override
    public void viewClick(View v) {
        if (v.getId() == R.id.saveBtn) {
            Person person = (Person) getBeanData(Person.class);
            tempTv.setText(person.toString());
            Tab2Fragment tab2 = (Tab2Fragment) getCYFragmentActivity().getFragment(R.id.contentContainer, "tab2");
            tab2.personList.add(person);
        } else if (v.getId() == R.id.leftBtn) {
            MainActivity mainActivity = (MainActivity) getCYFragmentActivity();
            mainActivity.showLeftMenu();
        }
    }
}
