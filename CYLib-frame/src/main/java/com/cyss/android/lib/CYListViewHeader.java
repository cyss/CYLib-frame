package com.cyss.android.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by cyjss on 2015/8/7.
 */
public class CYListViewHeader extends RelativeLayout {

    private TextView title;
//    private ImageView arrow;
//    private Spinner spinner;

    public CYListViewHeader(Context context) {
        super(context);
        this.defaultLayout(context);
    }

    public CYListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CYListViewHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CYListViewHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void defaultLayout(Context context) {
//        this.title = new TextView(context);
//        this.arrow = new ImageView(context);
//        this.spinner = new Spinner(context);
    }
}
