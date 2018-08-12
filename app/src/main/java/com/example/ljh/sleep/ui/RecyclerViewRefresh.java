package com.example.ljh.sleep.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class RecyclerViewRefresh extends RecyclerView{
    public final static int SCROLL_DURATION = 200;
    public static final float OFFSET_RADIO = 1.5f;
    public final static int PULL_LOAD_MORE_DELTA = 50;

    private float lastY = -1;
    private float moveY = 0;
    private float distanceY = 0;

    private boolean isAutoLoading = false;
    private boolean isLoading = false;
    private boolean isRefreshing = false;

    private Scroller scroller;
    private RecyclerViewHeader recyclerViewHeader;
    private RecyclerViewFooter recyclerViewFooter;
    private HeaderAndFooter headerAndFooter;
    private Adapter adapter;
    private OnRefreshListener onRefreshListener;

    public interface OnRefreshListener{
        void onRefresh();
        void onLoadMore();
    }

    public RecyclerViewRefresh(Context context) {
        this(context,null);
    }

    public RecyclerViewRefresh(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RecyclerViewRefresh(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(Context context){
        scroller = new Scroller(context,new DecelerateInterpolator());
        recyclerViewHeader = new RecyclerViewHeader(context);
        recyclerViewHeader.setLayoutParams
                (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerViewFooter = new RecyclerViewFooter(context);
        recyclerViewFooter.setLayoutParams
                (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        headerAndFooter = new HeaderAndFooter(adapter);
        super.setAdapter(adapter);

        headerAndFooter.addHeaderView(recyclerViewHeader);
        headerAndFooter.addFooterView(recyclerViewFooter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = e.getRawY();
                distanceY = moveY - lastY;
                lastY = moveY;

                if((((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0 ||
                        ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 1) &&
                        (recyclerViewHeader.getInVisibleHeight() > 0 || distanceY > 0)){
                    updateHeaderHeight(distanceY / OFFSET_RADIO);
                }else if(isSlideBottom() && (recyclerViewFooter.getBottomMargin() > 0 || distanceY < 0)){
                    updateFooterHeight(-distanceY / OFFSET_RADIO);
                }
                break;
            case MotionEvent.ACTION_UP:
                lastY = -1;
                if(((LinearLayoutManager)getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0 ||
                        ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 1){
                    if(recyclerViewHeader.getInVisibleHeight() > recyclerViewHeader.getRealityHeight()){
                        isRefreshing = true;
                        recyclerViewHeader.setStatus(RecyclerViewHeader.STATUS_REFRESH);
                        if(onRefreshListener != null){
                            onRefreshListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                }else if(isSlideBottom()){
                    if(recyclerViewFooter.getBottomMargin() > PULL_LOAD_MORE_DELTA && !isLoading){
                        isLoading = true;
                        recyclerViewFooter.setStatus(RecyclerViewFooter.STATUS_REFRESH);
                        if(onRefreshListener != null){
                            onRefreshListener.onLoadMore();
                        }
                    }
                    resetFooterHeight();
                }else{
                    resetHeaderHeight();
                }
                break;
        }
        return true;
    }

    private void updateHeaderHeight(float distance){
        recyclerViewHeader.setInVisibleHeight((int) distance + recyclerViewHeader.getInVisibleHeight());
        if(!isRefreshing){
            if(recyclerViewHeader.getInVisibleHeight() > recyclerViewHeader.getRealityHeight()){
                recyclerViewHeader.setStatus(RecyclerViewHeader.STATUS_READY);
            }else{
                recyclerViewHeader.setStatus(RecyclerViewHeader.STATUS_NORMAL);
            }
        }
        smoothScrollBy(0,0);
    }

    private void updateFooterHeight(float distance){
        int height = (int) (recyclerViewFooter.getBottomMargin() + distance);
        if(!isLoading){
            if(height > PULL_LOAD_MORE_DELTA){
                recyclerViewFooter.setStatus(RecyclerViewFooter.STATUS_READY);
            }else{
                recyclerViewFooter.setStatus(RecyclerViewFooter.STATUS_NORMAL);
            }
        }
        recyclerViewFooter.setBottomMargin(height);
    }

    private void resetHeaderHeight(){
        int height = recyclerViewHeader.getInVisibleHeight();
        if(height == 0){
            return;
        }

        if(isRefreshing && height <= recyclerViewHeader.getRealityHeight()){
            return;
        }

        int finalHeight = 0;
        if(isRefreshing && height > recyclerViewHeader.getRealityHeight()){

        }
    }

    private void resetFooterHeight(){

    }

    private boolean isSlideBottom(){
        return computeVerticalScrollExtent() + computeVerticalScrollOffset() >= computeVerticalScrollRange();
    }
}
