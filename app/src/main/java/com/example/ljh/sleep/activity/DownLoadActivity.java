package com.example.ljh.sleep.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.bean.EventBusBean;
import com.example.ljh.sleep.contract.DownLoadContract;
import com.example.ljh.sleep.presenter.DownLoadPresenter;
import com.example.ljh.sleep.ui.SlideBack;
import com.example.ljh.sleep.utils.CacheThreadPoolUtils;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DownLoadActivity extends AppCompatActivity implements DownLoadContract.DownLoadView,View.OnClickListener{
    private DownLoadPresenter mDownLoadPresenter;
    private RecyclerView mRvDownLoading; //正在下载列表
    private RecyclerView mRvDownLoaded;  //已下载列表
    private TextView tvTip1,tvTip2;     //显示是否有任务的提示

    private PopupWindow mPopupWindow;   //列表item显示的popup
    private TextView tvStartTask;
    private TextView tvCancelTask;
    private TextView tvReLoadTask;
    private View mPopupView;

    private PopupWindow mPopupWindowBottom;//底部显示的popup
    private View mPopupViewBottom;
    private TextView tvStart;
    private TextView tvPause;
    private TextView tvCancel;
    private TextView tvBack;

    private Button button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        init();
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("aaa","PoolSize = " + CacheThreadPoolUtils.getInstance().getPoolSize());
                Log.i("aaa","CorePoolSize = " + CacheThreadPoolUtils.getInstance().getCorePoolSize());
                Log.i("aaa","queue.size = " + CacheThreadPoolUtils.getInstance().getQueue());
                KLog.i("queue = " + CacheThreadPoolUtils.getInstance().getQueueList());
                Log.i("aaa","ActiveCount = " + CacheThreadPoolUtils.getInstance().getActiveCount());
                Log.i("aaa","CompletedTaskCount = " +
                        ""+CacheThreadPoolUtils.getInstance().getCompletedTaskCount());
                Log.i("aaa","LargestPoolSize = " + CacheThreadPoolUtils.getInstance().getLargestPoolSize());
                Log.i("aaa","TaskCount = " + CacheThreadPoolUtils.getInstance().getTaskCount());
                Log.i("aaa","MaximumPoolSize = " + CacheThreadPoolUtils.getInstance().getMaximumPoolSize());
                Log.i("aaa","-----------");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        mDownLoadPresenter.updateDownLoading();
        mDownLoadPresenter.updateDownLoaded();
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * MainPresenter.downLoad()发送
     * @param busBean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusBean busBean){
        if(busBean.getType().equals("onDowning")){
//            Log.i("aaa","DownLoadActivity.onEventBus.equals");
            mDownLoadPresenter.updateProgress((DownLoadBean) busBean.getObject());
        }else if(busBean.getType().equals("onDownFinish")){
            mDownLoadPresenter.downLoadFinish((DownLoadBean) busBean.getObject());
        }else if(busBean.getType().equals("download_error")){
            mDownLoadPresenter.downLoadFailed((DownLoadBean) busBean.getObject());
        }else if(busBean.getType().equals("onDownPause")){
            mDownLoadPresenter.downLoadPause((DownLoadBean) busBean.getObject());
        }
    }

    private void init(){
        mDownLoadPresenter = new DownLoadPresenter(this);

        mRvDownLoaded = findViewById(R.id.rvDownLoaded);
        mRvDownLoading = findViewById(R.id.rvDownLoading);
        tvTip1 = findViewById(R.id.tvTip1);
        tvTip2 = findViewById(R.id.tvTip2);
        initRvAdapter();
        initPopupWindow();

        SlideBack slideBack = new SlideBack(this);
        slideBack.attach();
    }

    private void initPopupWindow(){
        mPopupView = LayoutInflater.from(this).inflate(R.layout.popup_download,null);
        tvStartTask = mPopupView.findViewById(R.id.tvStart);
        tvCancelTask = mPopupView.findViewById(R.id.tvCancel);
        tvReLoadTask = mPopupView.findViewById(R.id.tvReLoad);
        int width = (int) getResources().getDimension(R.dimen.dp_80);
        mPopupWindow = new PopupWindow(mPopupView,ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT,true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        mPopupViewBottom = LayoutInflater.from(this).inflate(R.layout.popup_bottom_download,null);
        tvStart = mPopupViewBottom.findViewById(R.id.tvStart);
        tvPause = mPopupViewBottom.findViewById(R.id.tvPause);
        tvCancel = mPopupViewBottom.findViewById(R.id.tvCancel);
        tvBack = mPopupViewBottom.findViewById(R.id.tvBack);
//        mPopupWindowBottom = new PopupWindow(mPopupViewBottom,)
    }

    @Override
    public void showPopupWindow(View view, final DownLoadBean bean) {
        if(bean.getStatus() == DownLoadBean.DOWNLOAD_ING){
            tvStartTask.setText("暂停下载");
        }else{
            tvStartTask.setText("开始下载");
        }
        tvStartTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDownLoadPresenter.startOrPauseTask(bean);
                mPopupWindow.dismiss();
            }
        });

        tvCancelTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDownLoadPresenter.cancelTask(bean);
                mPopupWindow.dismiss();
            }
        });

        tvReLoadTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDownLoadPresenter.reLoadTask(bean);
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAsDropDown(view);
    }

    @Override
    public void showTip1() {
        tvTip1.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTip2() {
        tvTip2.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTip1() {
        tvTip1.setVisibility(View.GONE);
    }

    @Override
    public void hideTip2() {
        tvTip2.setVisibility(View.GONE);
    }

    @Override
    public void initRvAdapter() {
        mDownLoadPresenter.initRvAdapterDownLoaded(mRvDownLoaded);
        mDownLoadPresenter.initRvAdapterDownLoading(mRvDownLoading);
    }

    @Override
    public DownLoadPresenter getPresenter() {
        return null;
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDownLoadPresenter = null;
        mPopupView = null;
        mPopupViewBottom = null;
//        EventBus.getDefault().unregister(this);
    }
}
