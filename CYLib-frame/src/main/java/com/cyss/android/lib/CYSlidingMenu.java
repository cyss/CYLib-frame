package com.cyss.android.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.cyss.android.lib.utils.CYLog;
import com.cyss.android.lib.utils.ScreenUtils;

/**
 * Created by cyjss on 2015/9/1.
 */
public class CYSlidingMenu extends HorizontalScrollView {

    private View contentView;
    private View leftMenuView;
    private LinearLayout container;
    private FrameLayout leftMenuContainer;
    private LinearLayout contentContainer;

    private int mTouchSlop;

    private int paddingLeft = 100;
    private int leftMenuWidth;
    private Boolean leftMenuOpen = false;
    private float downX = 0;
    private float downY = 0;
    private float downOffsetX = 0;
    private boolean allowSlidingLeft = false;

    private boolean initFlag = false;

    public CYSlidingMenu(Context context) {
        super(context);
        this.init();
    }

    public CYSlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public CYSlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CYSlidingMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    private void init() {
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.container = new LinearLayout(getContext());
        this.container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.container.setOrientation(LinearLayout.HORIZONTAL);
        this.addView(this.container);
        this.setOverScrollMode(OVER_SCROLL_ALWAYS);
        this.setHorizontalScrollBarEnabled(false);

        this.contentContainer = new LinearLayout(getContext());
        this.container.addView(this.contentContainer);

        this.leftMenuContainer = new FrameLayout(getContext());
        this.container.addView(this.leftMenuContainer, 0);
    }

    private void addContentView() {
        this.contentContainer.addView(this.contentView);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.contentView.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.MATCH_PARENT;
        this.contentView.setLayoutParams(lp);
    }

    private void addLeftMenu() {
        this.leftMenuContainer.addView(this.leftMenuView);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.leftMenuView.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.MATCH_PARENT;
        this.leftMenuView.setLayoutParams(lp);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean flag = false;
        float x = ev.getX();
        float y = ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            this.downX = ev.getX();
            this.downY = ev.getY();
            this.downOffsetX = this.getScrollX();
            flag = leftMenuOpen;
            int offsetX = leftMenuWidth - this.getScrollX();
            if (ev.getX() < offsetX + paddingLeft && ev.getX() > offsetX) {
                allowSlidingLeft = true;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dy = y - this.downY;
            float dx = x - this.downX;
            boolean canScrollVertically = canScrollVertically(this.contentContainer, (int) dy, (int) x, (int) y);
            if (Math.abs(dy) >= mTouchSlop && canScrollVertically) {
                requestDisallowInterceptTouchEvent(true);
                return false;
            } else {
                boolean canScrollHorizontally = canScrollHorizontally(this.contentContainer, (int) dx, (int) x, (int) y);
                flag = Math.abs(dx) >= mTouchSlop && allowSlidingLeft && !canScrollHorizontally;
            }
        }
        return flag;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            this.downX = ev.getX();
            this.downY = ev.getY();
            int offsetX = leftMenuWidth - this.getScrollX();
            this.downOffsetX = this.getScrollX();
            if (this.downX < offsetX + paddingLeft && this.downX > offsetX) {
                allowSlidingLeft = true;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP
                || ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_OUTSIDE) {
            int offsetX = leftMenuWidth - this.getScrollX();
            if (Math.round(this.downX) == Math.round(ev.getX()) && leftMenuOpen && ((this.downX < offsetX + paddingLeft && this.downX > offsetX))) {
                this.closeLeftMenu();
            } else {
                if (this.getScrollX() < leftMenuWidth / 2) {
                    this.openLeftMenu();
                } else {
                    this.closeLeftMenu();
                }
            }
            allowSlidingLeft = false;
            getParent().requestDisallowInterceptTouchEvent(false);
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (allowSlidingLeft) {
                getParent().requestDisallowInterceptTouchEvent(true);
                scrollTo((int) (this.downX - ev.getX() + this.downOffsetX), 0);
            }
        }
        return true;
    }

    protected final boolean canScrollVertically(View v, int dy, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();

            final int childCount = viewGroup.getChildCount();
            for (int index = 0; index < childCount; index++) {
                View child = viewGroup.getChildAt(index);
                final int left = child.getLeft();
                final int top = child.getTop();
                if (x + scrollX >= left
                        && x + scrollX < child.getRight()
                        && y + scrollY >= top
                        && y + scrollY < child.getBottom()
                        && View.VISIBLE == child.getVisibility()
                        && canScrollVertically(
                        child, dy, x + scrollX - left, y + scrollY
                                - top)) {
                    return true;
                }
            }
        }
        return ViewCompat.canScrollVertically(v, -dy);
    }

    protected final boolean canScrollHorizontally(View v, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();

            final int childCount = viewGroup.getChildCount();
            for (int index = 0; index < childCount; index++) {
                View child = viewGroup.getChildAt(index);
                final int left = child.getLeft();
                final int top = child.getTop();
                if (x + scrollX >= left
                        && x + scrollX < child.getRight()
                        && y + scrollY >= top
                        && y + scrollY < child.getBottom()
                        && View.VISIBLE == child.getVisibility()
                        && canScrollHorizontally(
                        child, dx, x + scrollX - left, y + scrollY
                                - top)) {
                    return true;
                }
            }
        }
        return ViewCompat.canScrollHorizontally(v, -dx);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        if (wMode == MeasureSpec.UNSPECIFIED) {
            w = ScreenUtils.getScreenSize(getContext())[0];
        }
        LinearLayout.LayoutParams lp = null;

        lp = (LinearLayout.LayoutParams) this.contentContainer.getLayoutParams();
        lp.width = w;
        lp.height = LinearLayout.LayoutParams.MATCH_PARENT;

        lp = (LinearLayout.LayoutParams) this.leftMenuContainer.getLayoutParams();
        lp.width = w - paddingLeft;
        lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
        this.leftMenuWidth = lp.width;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!initFlag) {
            initFlag = !initFlag;
            this.scrollTo(leftMenuWidth, 0);
        }
    }

    public void openLeftMenu() {
        smoothScrollTo(0, 0);
        this.leftMenuOpen = true;
    }

    public void closeLeftMenu() {
        smoothScrollTo(leftMenuWidth, 0);
        this.leftMenuOpen = false;
    }

    public void setContentView(View view) {
        this.contentView = view;
        this.addContentView();
    }

    public void setContentView(int layout) {
        this.contentView = LayoutInflater.from(getContext()).inflate(layout, null);
        this.addContentView();
    }

    public void setLeftMenuView(View menuView) {
        this.leftMenuView = menuView;
        this.addLeftMenu();
    }

    public void setLeftMenuView(int layout) {
        this.leftMenuView = LayoutInflater.from(getContext()).inflate(layout, null);
        this.addLeftMenu();
    }
}
