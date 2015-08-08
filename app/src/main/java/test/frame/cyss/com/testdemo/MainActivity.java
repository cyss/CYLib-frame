package test.frame.cyss.com.testdemo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyss.android.lib.CYActivity;
import com.cyss.android.lib.annotation.BindView;
import com.cyss.android.lib.utils.CYLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends CYActivity {

    @BindView(id = R.id.btn, click = true)
    private TextView btn;

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
        startActivity(new Intent(this, ListViewActivity.class));
    }

    //内部类需要加上public static(getBeanData使用提示)
    public static class Person {
        private String name = "cyss";
        private Integer age = 21;
        private Boolean isGirl = false;

        @Override
        public String toString() {
            return new StringBuffer(name).append(",").append(age).append(",").append(isGirl).toString();
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
    }
}
