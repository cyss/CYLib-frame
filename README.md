#CYLib-frame
android小工具,整理一下用到的东西,和一些小功能。

##目录
* 1.数据注入和获取
* 2.异步操作(例如http异步请求数据等)
* 3.一些Widget
	* CYFragment(做了一些简单封装)
	* CYListView(上拉刷新和下拽加载)
	* CYSlidingMenu(侧滑菜单)

##1. 数据注入和获取
**说明**
假如有这么个布局文件
```xml
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
```java
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
```java
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

##2. 异步操作
* 1.添加service到AndroidManifest.xml
```xml
<service android:name="com.cyss.android.lib.service.CYASyncService" />
```
* 2.创建一个异步行为
```java
public class TimeSleep extends CYASyncBehaviour {

    @Override
    public CYASyncResult run() {
	//处理一些异步行为
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int num = getArgs().getInt("send_num", 0);
	//return fail().addArg(...)为失败处理
        return success().addArg("test", "TimeSleep--" + getArgs().getString("input")).addArg("number", num);
    }
}
```
* 3.执行异步操作
```java
CYASyncTask.create(getContext()).addArg("input", "hello!").setCallBack(new CYASyncCallBack() {
	@Override
        public void success(Bundle bundle) {
            Toast.makeText(getContext(), bundle.getString("test") + "," + bundle.getString("number"), Toast.LENGTH_LONG).show();
        }

        @Override
        public void fail(Bundle bundle, Exception ex) {
            //do fail
        }

	@Override
        public void cancel(int reason) {
	    //do cancel
        }
}).execute();
```

execute()为立即执行，addQueue()为执行完上一个任务再执行
	关于Http的操作，可使用HttpUtils中（未测试）

##3.一些Widget
###CYFragment和CYFragmentActivity配合使用
	详见Demo/MainActivity
	对于CYFragment大概封装有
* 1.重写viewOnResume() viewOnPause()相当于activity的 onResume() onPause()
* 2.对onActivityResult(int requestCode, int resultCode, Intent data)的支持
* 3.简化Fragment切换操作
```java
createFragmentToContainer(containerId, tab2Tag, Tab2Fragment.class);//添加Fragment
showFragment(containerId, tab2Tag);//显示
```

###CYListView
*详见Demo/fragment/Tab2Fragment
```java
listView.setOnCYListLoadMoreListener(new CYListView.onCYListViewLoadMoreListener() {
     @Override
     public void onLoadMore() { listView.endLoadMore(); }
});
listView.setOnCYListRefreshListener(new CYListView.onCYListViewRefreshListener() {
     @Override
      public void onRefresh() { listView.endRefresh(); }
});
listView.setFooterEnable(false);
listView.setHeaderEnable(false);
```

###CYSlidingMenu
详见Demo/MainActivity
```java
//滑动响应区域为contentView的左侧
super.onCreate(savedInstanceState);
slidingMenu = new CYSlidingMenu(this);
slidingMenu.setContentView(R.layout.activity_main);
slidingMenu.setLeftMenuView(R.layout.layout_sliding_menu);
setContentView(slidingMenu);
```