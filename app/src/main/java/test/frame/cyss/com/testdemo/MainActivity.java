package test.frame.cyss.com.testdemo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cyss.android.lib.CYActivity;
import com.cyss.android.lib.annotation.BindView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends CYActivity {

    @BindView(id = R.id.hw, click = true)
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map<String, Object> map = new HashMap<>();
        map.put("hw", "test fill data");
        map.put("et", "test fill data212");
        fillMapData(map);
        fillBeanData(new Person());
    }

    @Override
    public void viewClick(View v) {
    }

    public class Person {
        private String name = "cyss";
        private Integer age = 21;
        private Boolean isGirl = false;

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
