package com.example.ljh.sleep.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.ljh.sleep.utils.ScreenUtils;

/**
 * 右滑关闭
 */
public class SlideBack extends FrameLayout{
    private Activity mActivity;
    private ViewGroup mCurDecorView;
    private View mCurView;
    private ViewDragHelper mViewDragHelper;
    private Paint mPaint;
    private float mSlideWidth;
    private int mCloseWidth = 590;
    private int mScreenWidth = 1080;
    private int mScreenHeight = 1920;
    private boolean isTouchLeft = false;    //是否触碰屏幕边缘

    public SlideBack(@NonNull Context context) {
        this(context,null);
    }

    public SlideBack(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SlideBack(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mActivity = (Activity) context;
        mViewDragHelper = ViewDragHelper.create(this,new DragCallback());
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);

        mScreenWidth = ScreenUtils.getInstance(context).getScreenWidth();
        mScreenHeight = ScreenUtils.getInstance(context).getScreenHeight();
        mCloseWidth = mScreenWidth / 2;
    }

    public void attach(){
        mCurDecorView = (ViewGroup) mActivity.getWindow().getDecorView();
        mCurView = mCurDecorView.getChildAt(0);
        mCurDecorView.removeView(mCurView);
        this.addView(mCurView);
        mCurDecorView.addView(this);
    }

    private void drawShadow(Canvas canvas){
        canvas.save();
        Shader shader = new LinearGradient(mSlideWidth - 40,0,mSlideWidth,0,new int[]{Color.parseColor("#1edddddd"),
                Color.parseColor("#6e666666"), Color.parseColor("#9e666666")},
                null, Shader.TileMode.REPEAT);
        mPaint.setShader(shader);
        RectF rectF = new RectF(mSlideWidth - 40,0,mSlideWidth,mScreenHeight);
        canvas.drawRect(rectF,mPaint);
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(ev.getRawX() < 100){
                    isTouchLeft = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mViewDragHelper.processTouchEvent(ev);
                isTouchLeft = false;
                break;
        }

        if(!isTouchLeft){
//            Log.i("aaa","return false");
            return super.dispatchTouchEvent(ev);
        }

        mViewDragHelper.processTouchEvent(ev);
        return true;
    }

    /**
     * 使用settleCapturedViewAt方法是，必须重写computeScroll方法，传入true
        持续滚动期间，不断刷新ViewGroup
     */
    @Override
    public void computeScroll() {
        if(mViewDragHelper.continueSettling(true)){
            invalidate();
        }
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return mViewDragHelper.shouldInterceptTouchEvent(event);
    }

    /**
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawShadow(canvas);
        super.dispatchDraw(canvas);
    }

    class DragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return false;
        }

        /**
         * 手松开时触发
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int left = releasedChild.getLeft();
            if(left < mCloseWidth){
                mViewDragHelper.settleCapturedViewAt(0,0);
            }else{
                mViewDragHelper.settleCapturedViewAt(mScreenWidth,0);
            }
            invalidate();
        }

        /**
         * View位置改变时触发
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            mSlideWidth = left;
            invalidate();
            if(changedView == mCurView && left >= mScreenWidth){
                mActivity.finish();
            }
        }

        /**
         * 限制水平方向移动大小
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            left = left >= 0 ? left : 0;
            return left;
        }

        /**
         * 限制垂直方向移动大小
         * @param child
         * @param top
         * @param dy
         * @return
         */
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return 0;
        }

        /**
         * 触发边缘时，主动捕捉mCurView
         * @param edgeFlags
         * @param pointerId
         */
        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mViewDragHelper.captureChildView(mCurView,pointerId);
        }
    }

}
