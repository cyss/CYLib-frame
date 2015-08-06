#CYLib-frame
android小工具,整理一下用到的东西,和一些小功能。

**1. 目前添加一个数据填充功能**
**说明**
假如有这么个布局文件
```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:id="@+id/hw"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/hello_world" />

    <EditText
        android:id="@+id/et"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content" />

    <Button
        android:id="@+id/name"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content" />

    <EditText
        android:id="@+id/age"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content" />

</LinearLayout>

```
来一个javabean吧
```
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
```

其对应的Activity继承CYActivity
```
public class MainActivity extends CYActivity {

    //获取View和绑定click这里已经很常见了
    @BindView(id = R.id.hw, click = true)
    private TextView tv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //根据私有变量的名称(其对应要有get set方法)，与layout中的id对应进行填充
        fillBeanData(new Person());
    }

    //绑定的点击事件,点击的是TextView
    @Override
    public void viewClick(View v) {
        //getBeanData(..)就是把信息注入到Person变量了
        Toast.makeText(this, getBeanData(Person.class).toString(), Toast.LENGTH_LONG).show();
    }
}
```
当然填充数据和获取也支持Map