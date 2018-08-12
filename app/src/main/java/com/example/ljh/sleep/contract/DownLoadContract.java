package com.example.ljh.sleep.contract;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.ljh.sleep.bean.DownLoadBean;

import java.util.List;

public class DownLoadContract {

    public interface DownLoadView{
        void showPopupWindow(View view,DownLoadBean downLoadBean);//显示"更多"popupWindow
        void showTip1();    //提示正在下载列表为空
        void showTip2();    //提示已下载列表为空
        void hideTip1();    //隐藏
        void hideTip2();    //隐藏
        void initRvAdapter();
        com.example.ljh.sleep.presenter.DownLoadPresenter getPresenter();
        Context getMyContext();
    }

    public interface DownLoadPresenter{
        void getDownLoaded();                               //获取已下载列表
        void getDownLoading();                              //获取正在下载列表
        void updateDownLoaded();                            //更新已下载列表
        void updateDownLoading();                           //更新正在下载列表
        void startOrPauseTask(DownLoadBean bean);           //开始或暂停下载任务
        void cancelTask(DownLoadBean downLoadBean);         //取消任务
        void reLoadTask(DownLoadBean downLoadBean);         //重新下载任务
        void downLoadFinish(DownLoadBean downLoadBean);     //任务下载完成
        void initRvAdapterDownLoaded(RecyclerView recyclerView);
        void initRvAdapterDownLoading(RecyclerView recyclerView);
    }

    public interface DownLoadModel{
        void getDownLoaded(Context context,GetDownLoadCallback callback);
        void getDownLoading(Context context,GetDownLoadCallback callback);
        void updateDownLoad(Context context,DownLoadBean downLoadBean);
    }

    public interface GetDownLoadCallback{
        void onSuccess(List<DownLoadBean> list);
        void onFailed(String error);
    }

    public interface showPopupListener{
        void showPopupWindow(View view,DownLoadBean downLoadBean);
    }


}
