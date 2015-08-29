package test.frame.cyss.com.testdemo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyss.android.lib.CYActivity;
import com.cyss.android.lib.annotation.BindView;
import com.cyss.android.lib.service.CYSyncCallBack;
import com.cyss.android.lib.service.CYSyncTask;
import com.cyss.android.lib.utils.CYLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.frame.cyss.com.testdemo.impl.RequestHttp;
import test.frame.cyss.com.testdemo.impl.TimeSleep;

public class MainActivity extends CYActivity {

    @BindView(id = R.id.btn, click = true)
    private Button btn;
    @BindView(id = R.id.toFragment, click = true)
    private Button toFragment;
    @BindView(id = R.id.hw)
    private TextView hw;
    @BindView(id = R.id.showInjection, click = true)
    private Button showInjection;
    @BindView(id = R.id.executeSync, click = true)
    private Button executeSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map<String, Object> map = new HashMap<>();
        map.put("hw", "test fill data");
        map.put("et", "test fill data212");
        fillMapData(map);
        fillBeanData(new Person());
        map = getMapData();
        for (String key : map.keySet()) {
            CYLog.d(this, key + "===" + map.get(key));
        }
        ListView lv = null;
    }

    @Override
    public void viewClick(View v) {
        if (v.getId() == R.id.btn) {
            startActivity(new Intent(this, ListViewActivity.class));
        } else if (v.getId() == R.id.toFragment) {
            startActivity(new Intent(this, FragmentActivity.class));
        } else if (v.getId() == R.id.showInjection) {
            hw.setText(getBeanData(Person.class).toString());
        } else if (v.getId() == R.id.executeSync) {
            CYSyncTask task = CYSyncTask.create(this).setBehaviour(RequestHttp.class).addArg("send_num", 12).setCallBack(new CYSyncCallBack() {
                @Override
                public void success(Bundle bundle) {
                    appendLog(bundle.getString("test") + ":" + bundle.getInt("number"));
                }

                @Override
                public void fail(Bundle bundle, Exception ex) {

                }

                @Override
                public void cancel(int reason) {

                }
            }).execute();

            CYSyncTask.create(this).setBehaviour(TimeSleep.class).addArg("send_num", 2).setCallBack(new CYSyncCallBack() {
                @Override
                public void success(Bundle bundle) {
                    appendLog(bundle.getString("test") + ":" + bundle.getInt("number"));
                }

                @Override
                public void fail(Bundle bundle, Exception ex) {

                }

                @Override
                public void cancel(int reason) {

                }
            }).execute();
        }
    }

    private void appendLog(String txt) {
        hw.setText(hw.getText() + "\n" + txt);
    }


    //内部类需要加上public static(getBeanData使用提示)
    public static class Person {
        private String name = "cyss";
        private Integer age = 21;
        private Boolean isGirl = false;
        private String comment = "备注";

        @Override
        public String toString() {

            return new StringBuffer(name).append(",").append(age).append(",").append(isGirl).append(",").append(comment).toString();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Boolean getIsGirl() {
            return isGirl;
        }

        public void setIsGirl(Boolean isGirl) {
            this.isGirl = isGirl;
        }


        public String getComment() {
            return getIsGirl() ? "女" : "男" + comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
