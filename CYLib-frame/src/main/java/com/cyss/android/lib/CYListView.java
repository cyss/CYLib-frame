package com.cyss.android.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.cyss.android.lib.utils.CYLog;

import java.util.zip.Inflater;

/**
 * 支持上拉刷新和加载更多的ListView
 * To do: 支持自定义header和footer,整理代码
 * Created by cyjss on 2015/8/6.
 */
public class CYListView extends ListView {

    //关于上拉刷新头
    private LinearLayout headerContainer;
    private RelativeLayout headerView;
    private TextView headerTitle;
    private ImageView headerArrow;
    private RotateAnimation headerArrowAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    private RotateAnimation headerArrowResetAnimation = new RotateAnimation(180f, 0, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    private float arrowDegree = 0;
    private ProgressBar headerSpinner;
    private Bitmap arrowBitmap = getArrow(16, 20);
    private int headerHeight = 0;
    private boolean headerEnable = true;
    private CYListViewState headerState = CYListViewState.PULL_TO_REFRESH;

    //关于加载更多的Footer
    private LinearLayout footerContainer;
    private RelativeLayout footerView;
    private TextView footerTitle;
    private ProgressBar footerSpinner;
    private int footerHeight = 0;
    private boolean footerEnable = true;
    private CYListViewState footerState = CYListViewState.CLICK_TO_LOAD_MORE;
    private int footerIdleDis = 30;

    //计算存储参数
    private float downY = 0;
    private float scrollHeaderHeight = 0;
    private float scrollFooterHeight = 0;
    private float marginTop = 0;
    private float marginBottom = 0;
    private Scroller headerScroller;
    private Scroller footerScroller;

    private float prevY = -1;
    private boolean isBounce = false;
    private onCYListViewRefreshListener refreshListener;
    private onCYListViewLoadMoreListener loadMoreListener;


    enum CYListViewState {
        PULL_TO_REFRESH,
        RELEASE_TO_REFRESH,
        REFRESHING,
        REFRESH_FINISH,

        CLICK_TO_LOAD_MORE,
        LOADING_MORE,
        LOAD_FINISH,
        LOAD_NO_MORE
    }

    public CYListView(Context context) {
        super(context);
        this.init(context);
    }

    public CYListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public CYListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CYListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    /**
     * 停止上拉刷新
     */
    public void endRefresh() {
        this.endRefresh(280);
    }

    /**
     * 停止上拉刷新
     *
     * @param stayTime 停止在提示刷新成功提示的时间
     */
    public void endRefresh(long stayTime) {
        if (headerState == CYListViewState.REFRESH_FINISH) {
            return;
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setHeaderContent(CYListViewState.REFRESH_FINISH);
                bounceBackHeader();
            }
        }, stayTime);
        showHeaderSuccess();
    }

    /**
     * 设置上拉刷新是否可用
     *
     * @param flag
     */
    public void setHeaderEnable(boolean flag) {
        this.headerEnable = flag;
        setHeaderMarginTop(-headerHeight);
        setHeaderContent(CYListViewState.PULL_TO_REFRESH);
    }

    /**
     * 设置上拉刷新后的事件
     *
     * @param refreshListener
     */
    public void setOnCYListRefreshListener(onCYListViewRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    /**
     * 停止加载更多
     */
    public void endLoadMore() {
        setFooterContent(CYListViewState.CLICK_TO_LOAD_MORE);
        bounceBackFooter();
    }

    /**
     * 设置没有更多内容
     */
    public void endNoMoreData() {
        setFooterContent(CYListViewState.LOAD_NO_MORE);
        bounceBackFooter();
    }

    /**
     * 重置加载更多的Footer
     */
    public void resetFooter() {
        this.footerEnable = true;
        setFooterMarginBottom(-footerHeight);
        setFooterContent(CYListViewState.CLICK_TO_LOAD_MORE);
    }

    /**
     * 设置是否可以加载更多
     *
     * @param flag
     */
    public void setFooterEnable(boolean flag) {
        this.footerEnable = flag;
        setFooterMarginBottom(flag ? 0 : -footerHeight);
        setFooterContent(CYListViewState.CLICK_TO_LOAD_MORE);
    }

    /**
     * 加载更多的事件监听
     *
     * @param loadMoreListener
     */
    public void setOnCYListLoadMoreListener(onCYListViewLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    private void init(Context context) {
        headerContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.cy_listview_header, null);
        headerView = (RelativeLayout) headerContainer.findViewById(R.id.cy_listview_header);
        headerTitle = (TextView) headerView.findViewById(R.id.cy_listview_header_title);
        headerSpinner = (ProgressBar) headerView.findViewById(R.id.cy_listview_header_spinner);
        headerArrow = (ImageView) headerView.findViewById(R.id.cy_listview_header_image);
        headerArrow.setImageBitmap(arrowBitmap);
        headerArrowAnimation.setDuration(300);
        headerArrowAnimation.setFillAfter(true);
        headerArrowResetAnimation.setDuration(300);
        headerArrowResetAnimation.setFillAfter(true);
        addHeaderView(headerContainer);

        footerContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.cy_listview_header, null);
        footerView = (RelativeLayout) footerContainer.findViewById(R.id.cy_listview_header);
        footerTitle = (TextView) footerView.findViewById(R.id.cy_listview_header_title);
        footerSpinner = (ProgressBar) footerView.findViewById(R.id.cy_listview_header_spinner);
        footerContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (footerState == CYListViewState.CLICK_TO_LOAD_MORE) {
                    setFooterContent(CYListViewState.LOADING_MORE);
                }
            }
        });
        addFooterView(footerContainer);

        headerScroller = new Scroller(context);
        footerScroller = new Scroller(context);
        headerContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = headerContainer.getHeight();
                if (height > 0) {
                    headerHeight = height;
                    setHeaderMarginTop(-headerHeight);
                    requestLayout();
                }
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        footerContainer.measure(w, h);
        int height = footerContainer.getMeasuredHeight();
        footerHeight = height;
        footerIdleDis = footerHeight / 2;
        footerContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getFirstVisiblePosition() == 0 && getLastVisiblePosition() == getAdapter().getCount() - 1) {
                    setFooterMarginBottom(0);
                } else {
                    setFooterMarginBottom(-footerHeight);
                }
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    private void setHeaderMarginTop(int top) {
        MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) headerView.getLayoutParams();
        mlp.setMargins(0, top, 0, 0);
        headerView.setLayoutParams(mlp);
        marginTop = top;
    }

    private void setFooterMarginBottom(int bottom) {
        MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) footerView.getLayoutParams();
        mlp.setMargins(0, 0, 0, bottom);
        footerView.setLayoutParams(mlp);
        marginBottom = bottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                scrollHeaderHeight = marginTop;
                scrollFooterHeight = marginBottom;
                break;
            case MotionEvent.ACTION_UP:
                if ((getFirstVisiblePosition() == 0 || headerState == CYListViewState.RELEASE_TO_REFRESH) && headerEnable) {
                    if (headerState != CYListViewState.REFRESHING) {
                        if (marginTop >= 0) {
                            setHeaderContent(CYListViewState.REFRESHING);
                        } else {
                            setHeaderContent(CYListViewState.PULL_TO_REFRESH);
                        }
                    }
                    if (marginTop < -headerHeight) {
                        setHeaderMarginTop(-headerHeight);
                        if (headerState == CYListViewState.REFRESHING) {
                            this.bounceBackHeader();
                        }
                    } else {
                        this.bounceBackHeader();
                    }
                    if (headerState == CYListViewState.REFRESHING && this.refreshListener != null) {
                        this.refreshListener.onRefresh();
                    }
                }
                if ((getLastVisiblePosition() >= getAdapter().getCount() - 2) && footerEnable) {
                    bounceBackFooter();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float y = ev.getY();
                if (isBounce) {
                    downY = y;
                }
                if (getFirstVisiblePosition() == 0 && headerEnable && !isBounce) {
                    float gap = y - downY + scrollHeaderHeight;
                    int t = Math.round(gap);
                    setHeaderMarginTop(t);
                    if (headerState != CYListViewState.REFRESHING) {
                        if (t >= 0) {
                            setHeaderContent(CYListViewState.RELEASE_TO_REFRESH);
                        } else if (t < 0) {
                            setHeaderContent(CYListViewState.PULL_TO_REFRESH);
                        }
                    }
                }
                if (getLastVisiblePosition() == getAdapter().getCount() - 1 && footerEnable && !isBounce) {
                    float gap = downY - y + scrollFooterHeight;
                    if (getFirstVisiblePosition() == 0) {
                        gap += headerHeight + marginTop;
                    }
                    int t = Math.round(gap);
                    setFooterMarginBottom(t);
                    if (footerHeight + t > footerIdleDis && y - downY < -footerIdleDis && footerState != CYListViewState.LOAD_NO_MORE && footerState != CYListViewState.LOADING_MORE) {
                        setFooterContent(CYListViewState.LOADING_MORE);
                    }
                }
                prevY = y;
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void setHeaderContent(CYListViewState state) {
        if (state == headerState) {
            return;
        }
        if (state == CYListViewState.PULL_TO_REFRESH) {
            headerSpinner.setVisibility(View.GONE);
            headerArrow.setVisibility(View.VISIBLE);
            headerArrow.startAnimation(headerArrowResetAnimation);
            headerTitle.setText("下拉刷新数据");
        } else if (state == CYListViewState.RELEASE_TO_REFRESH) {
            headerSpinner.setVisibility(View.GONE);
            headerArrow.setVisibility(View.VISIBLE);
            headerArrow.startAnimation(headerArrowAnimation);
            headerTitle.setText("放手刷新数据");
        } else if (state == CYListViewState.REFRESHING) {
            headerArrow.clearAnimation();
            headerSpinner.setVisibility(View.VISIBLE);
            headerArrow.setVisibility(View.GONE);
            headerTitle.setText("正在刷新数据");
        } else if (state == CYListViewState.REFRESH_FINISH) {
            showHeaderSuccess();
        }
        headerState = state;
    }

    private void showHeaderSuccess() {
        headerSpinner.setVisibility(View.GONE);
        headerArrow.setVisibility(View.GONE);
        headerTitle.setText("成功刷新数据");
    }


    private void setFooterContent(CYListViewState state) {
        if (state == footerState) {
            return;
        }
        if (state == CYListViewState.CLICK_TO_LOAD_MORE) {
            footerSpinner.setVisibility(View.GONE);
            footerTitle.setText("点击加载更多");
        } else if (state == CYListViewState.LOADING_MORE) {
            footerSpinner.setVisibility(View.VISIBLE);
            footerTitle.setText("正在加载数据");
            if (this.loadMoreListener != null) {
                this.loadMoreListener.onLoadMore();
            }
        } else if (state == CYListViewState.LOAD_FINISH) {
            setFooterContent(CYListViewState.CLICK_TO_LOAD_MORE);
        } else if (state == CYListViewState.LOAD_NO_MORE) {
            footerSpinner.setVisibility(View.GONE);
            footerTitle.setText("没有更多数据");
        }
        footerState = state;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (headerScroller.computeScrollOffset()) {
            int gap = headerState == CYListViewState.REFRESHING
                    ? headerScroller.getFinalY() - headerScroller.getCurrY()
                    : headerScroller.getFinalY() - headerScroller.getCurrY() - headerHeight;
            setHeaderMarginTop(gap);
            if (headerScroller.isFinished() && headerScroller.getFinalY() == headerScroller.getCurrY()) {
                isBounce = false;
                if (headerState == CYListViewState.REFRESH_FINISH) {
                    setHeaderContent(CYListViewState.PULL_TO_REFRESH);
                } else if (headerState == CYListViewState.REFRESHING) {
                    if (getLastVisiblePosition() != getAdapter().getCount() - 1) {
                        setSelection(0);
                    }
                }
            }
        }
        if (footerScroller.computeScrollOffset()) {
            int gap = footerScroller.getFinalY() - footerScroller.getCurrY();
            setFooterMarginBottom(gap);
            if (footerScroller.isFinished() && footerScroller.getFinalY() == footerScroller.getCurrY()) {
                isBounce = false;
            }
        }
    }

    private void bounceBackHeader() {
        isBounce = true;
        int dy = headerState == CYListViewState.REFRESHING ? 0 : -headerHeight;
        headerScroller.startScroll(0, 0, 0, (int) marginTop - dy, 600);
    }

    private void bounceBackFooter() {
        isBounce = true;
        footerScroller.startScroll(0, 0, 0, (int) marginBottom, 600);
    }

    public interface onCYListViewRefreshListener {
        public void onRefresh();
    }

    public interface onCYListViewLoadMoreListener {
        public void onLoadMore();
    }

    private Bitmap getArrow(int w, int h) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1.5f);
        Bitmap arrow = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        arrow.prepareToDraw();
        Canvas canvas = new Canvas();
        canvas.setBitmap(arrow);
        canvas.drawLine(w / 2, h, 0, h / 2, paint);
        canvas.drawLine(w / 2, h, w, h / 2, paint);
        paint.setStrokeWidth(1.2f);
        canvas.drawLine(w / 2, h, w / 2, 0, paint);
        canvas.save();
        return arrow;
    }
}
