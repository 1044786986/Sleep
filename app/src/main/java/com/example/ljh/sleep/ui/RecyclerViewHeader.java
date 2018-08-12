package com.example.ljh.sleep.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ljh.sleep.R;

public class RecyclerViewHeader extends LinearLayout{
    public final static int STATUS_NORMAL = 0;
    public final static int STATUS_READY = 1;
    public final static int STATUS_REFRESH = 2;
    private final int ANIM_DURATION = 200;
    private int mStatus = STATUS_NORMAL;

    private LinearLayout mLinearLayout;
    private ImageView imageView;
    private TextView tvPull,tvRefresh;
    private LinearLayout linearLayout_pull_refresh;

    private ObjectAnimator rotationAnimation;

    public RecyclerViewHeader(Context context) {
        this(context,null);
    }

    public RecyclerViewHeader(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RecyclerViewHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setStatus(int status){
        if(mStatus == status){
            return;
        }
        if(status == STATUS_REFRESH){

        }else{

        }
        switch (status){
            case STATUS_NORMAL:
                tvPull.setText("下拉刷新");
                break;
            case STATUS_READY:
                if(mStatus != status){
                    tvPull.setText("松开刷新");
                }
                break;
            case STATUS_REFRESH:
                tvPull.setText("正在加载");
                rotationAnimation.start();
                break;
        }
        mStatus = status;
    }

    public int getRealityHeight(){
        return linearLayout_pull_refresh.getHeight();
    }

    public int getInVisibleHeight(){
        return mLinearLayout.getLayoutParams().height;
    }

    public void setInVisibleHeight(int height){
        if(height < 0){
            height = 0;
        }
        LayoutParams lp = (LayoutParams) mLinearLayout.getLayoutParams();
        lp.height = height;
        mLinearLayout.setLayoutParams(lp);
    }

    private void init(Context context){
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
        mLinearLayout = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.rv_pull_refresh_content,(ViewGroup) getParent(),true);
        addView(mLinearLayout,lp);
        setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        linearLayout_pull_refresh = mLinearLayout.findViewById(R.id.linearLayout_refresh_content);
        imageView = mLinearLayout.findViewById(R.id.imageView);
        tvPull = mLinearLayout.findViewById(R.id.tvPull);
        tvRefresh = mLinearLayout.findViewById(R.id.tvRefresh);

        rotationAnimation = ObjectAnimator.ofFloat(imageView,"rotation",0,720);
        rotationAnimation.setDuration(1500);
        rotationAnimation.setRepeatCount(-1);
    }
}
