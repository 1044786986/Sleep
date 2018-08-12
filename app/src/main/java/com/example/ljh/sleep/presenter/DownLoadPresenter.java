package com.example.ljh.sleep.presenter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.ljh.sleep.activity.MainActivity;
import com.example.ljh.sleep.adapter.RvAdapterDownLoaded;
import com.example.ljh.sleep.adapter.RvAdapterDownLoading;
import com.example.ljh.sleep.application.ErrorTipApp;
import com.example.ljh.sleep.bean.DownLoadBean;
import com.example.ljh.sleep.contract.DownLoadContract;
import com.example.ljh.sleep.model.DownLoadModel;
import com.example.ljh.sleep.utils.DownLoadUtils;
import com.example.ljh.sleep.utils.DownLoadUtils2;
import com.example.ljh.sleep.utils.ShowTipUtils;

import java.util.ArrayList;
import java.util.List;

public class DownLoadPresenter implements DownLoadContract.DownLoadPresenter,DownLoadUtils.UpdateProgress,
DownLoadContract.showPopupListener{
    private DownLoadContract.DownLoadView mDownLoadView;
    private DownLoadContract.DownLoadModel mDownLoadModel;

    private RvAdapterDownLoaded mRvAdapterDownLoaded;
    private RvAdapterDownLoading mRvAdapterDownLoading;
    private List<DownLoadBean> mDownLoadedList;
    private List<DownLoadBean> mDownLoadingList;

    public DownLoadPresenter(DownLoadContract.DownLoadView downLoadView){
        mDownLoadView = downLoadView;
        mDownLoadModel = new DownLoadModel();

        mDownLoadedList = new ArrayList<>();
        mDownLoadingList = new ArrayList<>();
    }

    /**
     * 更新下载进度条
     * @param bean
     */
    @Override
    public void updateProgress(DownLoadBean bean) {
        int id = bean.getId();
        for(int i = 0;i < mDownLoadingList.size();i++){
            if(id == mDownLoadingList.get(i).getId()){
               mDownLoadingList.set(i,bean);
               updateDownLoading();
               break;
            }
        }
    }

    /**
     * 获取已下载列表
     */
    @Override
    public void getDownLoaded() {
        mDownLoadModel.getDownLoaded(mDownLoadView.getMyContext(),new DownLoadContract.GetDownLoadCallback() {
            @Override
            public void onSuccess(List<DownLoadBean> list) {
                Log.i("aaa","DownLoadPresenter.getDownLoading().list.size = " + list.size());
                mDownLoadedList.clear();
                for(int i=0;i<list.size();i++){
                    DownLoadBean bean = list.get(i);
                    mDownLoadedList.add(bean);
                }
                updateDownLoading();
            }

            @Override
            public void onFailed(String error) {
                updateDownLoaded();
            }
        });
    }

    /**
     * 获取正在下载列表
     */
    @Override
    public void getDownLoading() {
        mDownLoadModel.getDownLoading(mDownLoadView.getMyContext(),new DownLoadContract.GetDownLoadCallback() {
            @Override
            public void onSuccess(List<DownLoadBean> list) {
                Log.i("aaa","DownLoadPresenter.getDownLoading().list.size = " + list.size());
                mDownLoadingList.clear();
                for(int i=0;i<list.size();i++){
                    DownLoadBean bean = list.get(i);
                    mDownLoadingList.add(bean);
                }
                updateDownLoading();
            }

            @Override
            public void onFailed(String error) {
                updateDownLoading();
            }
        });
    }

    /**
     * 更新已下载列表
     */
    @Override
    public void updateDownLoaded() {
        //列表为空时提示
        if(mDownLoadedList == null || mDownLoadedList.size() == 0){
            mDownLoadView.showTip2();
        }else{
            mDownLoadView.hideTip2();
        }
        if(mRvAdapterDownLoaded != null){
            mRvAdapterDownLoaded.notifyDataSetChanged();
        }
    }

    /**
     * 更新正在下载列表
     */
    @Override
    public void updateDownLoading() {
        //列表为空时提示
        if(mDownLoadingList == null || mDownLoadingList.size() == 0){
            mDownLoadView.showTip1();
        }else{
            mDownLoadView.hideTip1();
        }

        if(mRvAdapterDownLoading != null){
            mRvAdapterDownLoading.notifyDataSetChanged();
        }
    }

    /**
     * 开始或暂停任务
     * @param bean
     */
    @Override
    public void startOrPauseTask(DownLoadBean bean) {
        for(int i=0;i<mDownLoadingList.size();i++){
            if(bean.getId() == mDownLoadingList.get(i).getId()){
                int status = bean.getStatus();
                if(status == DownLoadBean.DOWNLOAD_ING){
                    DownLoadUtils2.getInstance().stopTask(bean);
                    mDownLoadingList.get(i).setStatus(DownLoadBean.DOWNLOAD_PAUSE);
                    updateDownLoading();
                    break;
                }else{

                }
            }
        }
    }

    /**
     * 取消任务
     * @param downLoadBean
     */
    @Override
    public void cancelTask(final DownLoadBean downLoadBean) {
        ShowTipUtils.showAlertDialog(mDownLoadView.getMyContext(), ErrorTipApp.SURE_CANCEL_TASK, 2,
                new ShowTipUtils.AlertDialogCallback() {
                    @Override
                    public void positive() {
                        for(int i=0;i< DownLoadUtils2.mTaskMap.size();i++){
                            if(downLoadBean.getId() == DownLoadUtils2.mTaskMap.keyAt(i)){
                                DownLoadUtils2.getInstance().stopTask(downLoadBean);
                                DownLoadUtils2.mTaskMap.remove(i);
                                break;
                            }
                        }
                        for(int i=0;i<mDownLoadingList.size();i++){
                            if(downLoadBean.getId() == mDownLoadingList.get(i).getId()){
                                mDownLoadingList.remove(i);
                                updateDownLoading();
                                break;
                            }
                        }
                    }

                    @Override
                    public void negative() {

                    }
                });
    }

    /**
     * 重新下载任务
     * @param downLoadBean
     */
    @Override
    public void reLoadTask(final DownLoadBean downLoadBean) {
        ShowTipUtils.showAlertDialog(mDownLoadView.getMyContext(), ErrorTipApp.SURE_RELOAD_TASK, 2,
                new ShowTipUtils.AlertDialogCallback() {
                    @Override
                    public void positive() {
                        for(int i=0;i<mDownLoadingList.size();i++){
                            if(mDownLoadingList.get(i).getId() == downLoadBean.getId()){
                                mDownLoadingList.get(i).setSpeed(0);
                                mDownLoadingList.get(i).setStatus(DownLoadBean.DOWNLOAD_ING);
                                updateDownLoading();
                                break;
                            }
                        }
                        MainActivity.getPresenter().reLoad(downLoadBean);
                    }

                    @Override
                    public void negative() {

                    }
                });
    }

    /**
     * 下载完成回调
     * @param downLoadBean
     */
    @Override
    public void downLoadFinish(DownLoadBean downLoadBean) {
        downLoadBean.setStatus(3);
        for(int i=0;i < mDownLoadingList.size();i++){
            if(downLoadBean.getId() == mDownLoadingList.get(i).getId()){
                mDownLoadingList.remove(i);
                mDownLoadedList.add(0,downLoadBean);
//                mDownLoadModel.updateDownLoad(mDownLoadView.getMyContext(),downLoadBean);
//                mDownLoadModel.insertMusicInfo(mDownLoadView.getMyContext(),downLoadBean);
                updateDownLoaded();
                updateDownLoading();
                break;
            }
        }
    }

    /**
     * 初始化已下载列表
     * @param recyclerView
     */
    @Override
    public void initRvAdapterDownLoaded(RecyclerView recyclerView) {
        mRvAdapterDownLoaded = new RvAdapterDownLoaded(mDownLoadView.getMyContext(),mDownLoadedList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mDownLoadView.getMyContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mRvAdapterDownLoaded);
        getDownLoaded();
    }

    /**
     * 初始化正在下载列表
     * @param recyclerView
     */
    @Override
    public void initRvAdapterDownLoading(RecyclerView recyclerView) {
        mRvAdapterDownLoading = new RvAdapterDownLoading(mDownLoadView.getMyContext(),this,mDownLoadingList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mDownLoadView.getMyContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mRvAdapterDownLoading);
        getDownLoading();
    }

    @Override
    public void showPopupWindow(View view, DownLoadBean bean) {
        mDownLoadView.showPopupWindow(view,bean);
    }
}
