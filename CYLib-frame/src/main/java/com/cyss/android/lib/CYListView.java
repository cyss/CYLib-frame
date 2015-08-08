package com.cyss.android.lib;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;

import com.cyss.android.lib.utils.CYLog;

import java.util.zip.Inflater;

/**
 * Created by cyjss on 2015/8/6.
 */
public class CYListView extends ListView {

    private Boolean isRefreshEnable = true;

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

    private LinearLayout footerContainer;
    private RelativeLayout footerView;
    private TextView footerTitle;
    private int footerHeight = 0;
    private boolean footerEnable = true;
    private CYListViewState footerState = CYListViewState.DRAG_TO_LOAD_MORE;

    private float downY = 0;
    private float scrollHeaderHeight = 0;
    private float scrollFooterHeight = 0;
    private float marginTop = 0;
    private float marginBottom = 0;
    private Scroller headerScroller;
    private Scroller footerScroller;

    private onCYListViewRefreshListener refreshListener;

    private onCYListViewLoadMoreListener loadMoreListener;


    enum CYListViewState {
        PULL_TO_REFRESH,
        RELEASE_TO_REFRESH,
        REFRESHING,
        REFRESH_FINISH,

        DRAG_TO_LOAD_MORE,
        RELEASE_TO_LOAD_MORE,
        LOADING_MORE,
        LOAD_FINISH
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

    public void endRefresh() {
        this.endRefresh(280);
    }

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

    public void setHeaderEnable(boolean flag) {
        this.headerEnable = flag;
        setHeaderMarginTop(-headerHeight);
        setHeaderContent(CYListViewState.PULL_TO_REFRESH);
    }

    public void setOnCYListRefreshListener(onCYListViewRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public void endLoadMore() {
        setFooterContent(CYListViewState.LOAD_FINISH);
        bounceBackFooter();
    }

    public void setFooterEnable(boolean flag) {
        this.footerEnable = flag;
    }

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
        setFooterMarginBottom(-footerHeight);
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
        if (!isRefreshEnable) {
            return true;
        }
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
                if ((getLastVisiblePosition() == getAdapter().getCount() - 1 || footerState == CYListViewState.RELEASE_TO_LOAD_MORE) && footerEnable) {
                    if (footerState != CYListViewState.LOADING_MORE) {
                        if (marginBottom >= 0) {
                            setFooterContent(CYListViewState.LOADING_MORE);
                        } else {
                            setFooterContent(CYListViewState.DRAG_TO_LOAD_MORE);
                        }
                    }
                    if (marginBottom < -footerHeight) {
                        setHeaderMarginTop(-footerHeight);
                        if (footerState == CYListViewState.LOADING_MORE) {
                            this.bounceBackFooter();
                        }
                    } else {
                        this.bounceBackFooter();
                    }
                    if (footerState == CYListViewState.LOADING_MORE && this.loadMoreListener != null) {
                        this.loadMoreListener.onLoadMore();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (getFirstVisiblePosition() == 0 && headerEnable) {
                    float y = ev.getY();
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
                if (getLastVisiblePosition() == getAdapter().getCount() - 1 && footerEnable) {
                    float y = ev.getY();
                    float gap = downY - y + scrollFooterHeight;
                    int t = Math.round(gap);
                    setFooterMarginBottom(t);
                    if (footerState != CYListViewState.LOADING_MORE) {
                        if (t >= 0) {
                            setFooterContent(CYListViewState.RELEASE_TO_LOAD_MORE);
                        } else if (t < 0) {
                            setFooterContent(CYListViewState.DRAG_TO_LOAD_MORE);
                        }
                    }
                }
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
        if (state == CYListViewState.DRAG_TO_LOAD_MORE) {
            footerTitle.setText("上拽加载更多");
        } else if (state == CYListViewState.RELEASE_TO_LOAD_MORE) {
            footerTitle.setText("放手加载更多");
        } else if (state == CYListViewState.LOADING_MORE) {
            footerTitle.setText("正在加载数据");
        } else if (state == CYListViewState.LOAD_FINISH) {
            footerTitle.setText("成功加载数据");
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
                if (headerState == CYListViewState.REFRESH_FINISH) {
                    setHeaderContent(CYListViewState.PULL_TO_REFRESH);
                } else if (headerState == CYListViewState.REFRESHING) {
                    setSelection(0);
                }
            }
        }
        if (footerScroller.computeScrollOffset()) {
            int gap = footerState == CYListViewState.LOADING_MORE
                    ? footerScroller.getFinalY() - footerScroller.getCurrY()
                    : footerScroller.getFinalY() - footerScroller.getCurrY() - footerHeight;
            setFooterMarginBottom(gap);
            if (footerScroller.isFinished() && footerScroller.getFinalY() == footerScroller.getCurrY()) {
                if (footerState == CYListViewState.LOAD_FINISH) {
                    setFooterContent(CYListViewState.DRAG_TO_LOAD_MORE);
                }
            }
        }
    }

    private void bounceBackHeader() {
        int dy = headerState == CYListViewState.REFRESHING ? 0 : -headerHeight;
        headerScroller.startScroll(0, 0, 0, (int) marginTop - dy, 600);
    }

    private void bounceBackFooter() {
        int dy = footerState == CYListViewState.LOADING_MORE ? 0 : -footerHeight;
        footerScroller.startScroll(0, 0, 0, (int) marginBottom - dy, 600);
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
        final float marginTop = 4;
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
