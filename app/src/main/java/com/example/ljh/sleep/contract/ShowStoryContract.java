package com.example.ljh.sleep.contract;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.ljh.sleep.bean.ShowStoryRequestBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;

public class ShowStoryContract {

    public interface ShowStoryView{
        Context getMyContext();
        com.example.ljh.sleep.presenter.ShowStoryPresenter getPresenter();
        boolean isViewCreate1(); //页面是否已完成ViewCreate();
        boolean isVisible1();    //页面是否已对用户可见
        boolean isFirstOpen();
        void resetFirstOpen();   //重置 是否第一次打开
        void resetViewCreate();  //重置
        void resetVisible();     //重置
        void hideRefreshLayout();
        void showProgressBar();
        void hideProgressBar();
    }

    public interface ShowStoryPresenter{
        void getData();
        void upDate();
//        void setMusicInfo(int i);    //记录当前播放的音乐信息
        void initRvAdapter(RecyclerView recyclerView);
        String getPositionType(int pos);
    }

    public interface ShowStoryModel{
        void getData(ShowStoryRequestBean bean, MyRetrofitCallback callBack);
    }
}
