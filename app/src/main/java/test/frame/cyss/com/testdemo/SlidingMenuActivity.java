package test.frame.cyss.com.testdemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cyss.android.lib.CYActivity;
import com.cyss.android.lib.CYListView;
import com.cyss.android.lib.CYSlidingMenu;
import com.cyss.android.lib.annotation.BindView;

/**
 * Created by cyjss on 2015/9/1.
 */
public class SlidingMenuActivity extends CYActivity {
    @BindView(id = R.id.btn, click = true)
    private Button btn;

    @BindView(id = R.id.listView)
    private CYListView listView;
    private ListViewAdapter adapter = new ListViewAdapter();
    private CYSlidingMenu slidingMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slidingMenu = new CYSlidingMenu(this);
        slidingMenu.setContentView(R.layout.activity_listview);
        slidingMenu.setLeftMenuView(R.layout.listview_item);
        setContentView(slidingMenu);
        listView.setAdapter(adapter);
        listView.setOnCYListRefreshListener(new CYListView.onCYListViewRefreshListener() {
            @Override
            public void onRefresh() {
                listView.endRefresh();
            }
        });
        listView.setOnCYListLoadMoreListener(new CYListView.onCYListViewLoadMoreListener() {
            @Override
            public void onLoadMore() {
                listView.endLoadMore();
            }
        });
    }

    @Override
    public void viewClick(View v) {
        if (v.getId() == R.id.btn) {
            slidingMenu.smoothScrollTo(100, 0);
        }
    }

    private class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public Object getItem(int position) {
            return "";
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SlidingMenuActivity.this).inflate(R.layout.listview_item, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.joker);
            tv.setText("hehehe" + position);
            return convertView;
        }
    }
}
