package test.frame.cyss.com.testdemo.pojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * Created by cyjss on 2015/8/31.
 */
public class Person {

    private String name = "cyss";
    private Integer age;
    private String gender = "male";
    private Date birthday;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
