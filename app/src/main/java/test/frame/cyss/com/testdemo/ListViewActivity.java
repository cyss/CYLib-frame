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

import java.util.Random;

/**
 * Created by cyjss on 2015/8/7.
 */
public class ListViewActivity extends CYActivity {

    @BindView(id = R.id.listView)
    private CYListView listView;
    @BindView(id = R.id.btn, click = true)
    private Button btn;
    private int count = 10;

    private String preStr = "hehehehehshshshehsehshe";

    private boolean headerEnable = true;
    private ListViewAdapter adapter = new ListViewAdapter();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                listView.endRefresh();
                adapter.notifyDataSetChanged();
            } else {
                listView.endLoadMore();
                adapter.notifyDataSetChanged();
            }
        }
    };

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        listView.setAdapter(adapter);
        listView.setOnCYListRefreshListener(new CYListView.onCYListViewRefreshListener() {
            @Override
            public void onRefresh() {
                CYLog.d(this, "==-> refresh");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        preStr = getRandomString(15);
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
                        count += new Random().nextInt(10);
                        handler.sendEmptyMessage(1);
                    }
                }).start();
            }
        });
//        listView.endNoMoreData();
    }

    @Override
    public void viewClick(View v) {
        headerEnable = !headerEnable;
        listView.setFooterEnable(headerEnable);
//        CYLog.d(this, "====>" + listView.getLastVisiblePosition());
//        headerEnable = !headerEnable;
//        listView.setHeaderEnable(headerEnable);
//
//        listView.resetFooter();
    }

    private class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return count;
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
            tv.setText(preStr + position);
            return convertView;
        }
    }
}
