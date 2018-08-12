package com.example.ljh.sleep.contract;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.ljh.sleep.bean.ShowStoryRequestBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;

public class ShowStoryContract {

    public interface ShowStoryView{
        Context getMyContext();
        com.example.ljh.sleep.presenter.ShowStoryPresenter getPresenter();
        boolean isViewCreate1();            //页面是否已完成ViewCreate();
        boolean isVisible1();               //页面是否已对用户可见
        boolean isFirstOpen();
        void resetFirstOpen();              //重置 是否第一次打开
        void resetViewCreate();             //重置 已ViewCreate()
        void resetVisible();                //重置 是否已对用户可见
        void hideRefreshLayout();
        void showProgressBar();
        void hideProgressBar();
    }

    public interface ShowStoryPresenter{
        void getData();                                 //获取数据
        void upDate();                                  //更新页面
//        void setMusicInfo(int i);                     //记录当前播放的音乐信息
        void initRvAdapter(RecyclerView recyclerView);  //初始化并绑定adapter
        String getPositionType(int pos);                //获取页面类型
    }

    public interface ShowStoryModel{
        void getData(ShowStoryRequestBean bean, MyRetrofitCallback callBack);
    }
}
