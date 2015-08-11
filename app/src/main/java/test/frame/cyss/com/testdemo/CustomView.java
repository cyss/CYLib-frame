package test.frame.cyss.com.testdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyss.android.lib.impl.CYViewParent;

/**
 * Created by cyjss on 2015/8/7.
 */
public class CustomView extends LinearLayout implements CYViewParent<String> {

    private TextView label1;
    private TextView label2;
    private Object obj;

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        label1 = new TextView(getContext());
        label1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(label1);
        label2 = new TextView(getContext());
        label2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(label2);
    }

    @Override
    public void setCustomData(String obj) {
        this.obj = obj;
        label1.setText(obj.toString());
        label2.setText(obj.toString() + "2");
    }

    @Override
    public String getCustomData() {
        return "[" + label1.getText() + "," + label2.getText() + "]";
    }
}
