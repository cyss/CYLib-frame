package test.frame.cyss.com.testdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cyss.android.lib.CYActivity;
import com.cyss.android.lib.CYListView;
import com.cyss.android.lib.annotation.BindView;
import com.cyss.android.lib.utils.CYLog;

/**
 * Created by cyjss on 2015/8/7.
 */
public class ListViewActivity extends CYActivity {

    @BindView(id = R.id.listView)
    private CYListView listView;
    @BindView(id = R.id.btn, click = true)
    private Button btn;

    private boolean headerEnable = true;
    private ListViewAdapter adapter = new ListViewAdapter();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                listView.endRefresh();
            } else {
                listView.endLoadMore();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        listView.setAdapter(adapter);
        listView.setOnCYListRefreshListener(new CYListView.onCYListViewRefreshListener() {
            @Override
            public void onRefresh() {
                CYLog.d(this, "==->");
//                listView.endRefresh();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });
        listView.setOnCYListLoadMoreListener(new CYListView.onCYListViewLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(1);
                    }
                }).start();
            }
        });
    }

    @Override
    public void viewClick(View v) {
        CYLog.d(this, "====>" + listView.getLastVisiblePosition());
        headerEnable = !headerEnable;
        listView.setHeaderEnable(headerEnable);
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
                convertView = LayoutInflater.from(ListViewActivity.this).inflate(R.layout.listview_item, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.joker);
            tv.setText("hehehehehshshshehsehshe" + position);
            return convertView;
        }
    }
}
