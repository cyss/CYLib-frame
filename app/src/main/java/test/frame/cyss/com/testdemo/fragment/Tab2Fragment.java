package test.frame.cyss.com.testdemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cyss.android.lib.CYFragment;
import com.cyss.android.lib.CYListView;
import com.cyss.android.lib.annotation.BindView;

import java.util.ArrayList;
import java.util.List;

import test.frame.cyss.com.testdemo.R;
import test.frame.cyss.com.testdemo.pojo.Person;

/**
 * Created by cyjss on 2015/8/21.
 */
public class Tab2Fragment extends CYFragment implements CYListView.onCYListViewRefreshListener, CYListView.onCYListViewLoadMoreListener {

    @BindView(id = R.id.listView)
    private CYListView listView;
    private ListViewAdapter adapter;

    public static List<Person> personList = new ArrayList<>();

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab2, null);
    }

    @Override
    protected void viewLoaded(View v) {
        this.listView.setOnCYListLoadMoreListener(this);
        this.listView.setOnCYListRefreshListener(this);
        this.adapter = new ListViewAdapter();
        this.listView.setAdapter(this.adapter);
        for (int i = 0; i < 100; i++) {
            personList.add(new Person());
        }
    }

    @Override
    protected void viewOnResume() {
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        listView.endRefresh();
    }

    @Override
    public void onLoadMore() {
        listView.endLoadMore(2000);
    }

    private class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return personList.size();
        }

        @Override
        public Object getItem(int position) {
            return personList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Person person = (Person) getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(Tab2Fragment.this.getActivity()).inflate(R.layout.listview_item, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.joker);
            tv.setText(person.toString());
            return convertView;
        }
    }
}
