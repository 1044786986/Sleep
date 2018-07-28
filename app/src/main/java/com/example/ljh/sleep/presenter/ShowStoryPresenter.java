package com.example.ljh.sleep.presenter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ljh.sleep.adapter.RvAdapter_ShowStory;
import com.example.ljh.sleep.bean.MusicInfoBean;
import com.example.ljh.sleep.bean.ShowStoryRequestBean;
import com.example.ljh.sleep.bean.ShowStoryResponseBean;
import com.example.ljh.sleep.callback.MyRetrofitCallback;
import com.example.ljh.sleep.contract.ShowStoryContract;
import com.example.ljh.sleep.model.ShowStoryModel;
import com.example.ljh.sleep.utils.ShowTipUtils;

import java.util.ArrayList;
import java.util.List;

public class ShowStoryPresenter implements ShowStoryContract.ShowStoryPresenter{
    private RvAdapter_ShowStory rvAdapter_showStory;
    private List<MusicInfoBean> dataList;
    private ShowStoryContract.ShowStoryView showStoryView;
    private ShowStoryContract.ShowStoryModel showStoryModel;

    public ShowStoryPresenter(ShowStoryContract.ShowStoryView view){
        this.showStoryView = view;
        showStoryModel = new ShowStoryModel();
    }

    /**
     * 进入页面时加载数据
     */
    @Override
    public synchronized void getData() {
        if(showStoryView.isViewCreate1() && showStoryView.isVisible1()){
            showStoryView.resetViewCreate();
            showStoryView.resetVisible();
            showStoryView.showProgressBar();

            ShowStoryRequestBean bean = new ShowStoryRequestBean();
            bean.setType(getPositionType(NetWorkStoryPresenter.pos));

            showStoryModel.getData(bean, new MyRetrofitCallback() {
                @Override
                public void onSuccess(Object o) {
                    showStoryView.hideProgressBar();
                    ShowStoryResponseBean bean = (ShowStoryResponseBean) o;
                    if(bean.isStatus()){
                        dataList.clear();
                        for(int i=0;i<bean.getData().size();i++){
                            MusicInfoBean bean1 = bean.getData().get(i);
                            dataList.add(bean1);
                        }
                        bean = null;
                        rvAdapter_showStory.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailed(String error) {
                    showStoryView.hideProgressBar();
                    ShowTipUtils.toastLong(showStoryView.getMyContext(),error);
                }
            });
        }
    }

    @Override
    public void upDate() {
        ShowStoryRequestBean bean = new ShowStoryRequestBean();
        bean.setType(getPositionType(NetWorkStoryPresenter.pos));   //设置内容类型

        showStoryModel.getData(bean, new MyRetrofitCallback() {
            @Override
            public void onSuccess(Object o) {
                showStoryView.hideRefreshLayout();
                ShowStoryResponseBean bean = (ShowStoryResponseBean) o;
                if(bean.isStatus()){
                    dataList.clear();
                    for(int i=0;i<bean.getData().size();i++){
                        MusicInfoBean bean1 = bean.getData().get(i);
                        dataList.add(bean1);
                    }
                    bean = null;
                    rvAdapter_showStory.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(String error) {
                showStoryView.hideProgressBar();
                ShowTipUtils.toastLong(showStoryView.getMyContext(),error);
            }
        });
    }


//    @Override
//    public void setMusicInfo(int i) {
//        MusicInfoBean bean = dataList.get(i);
//        SharedPreferencesUtils.setMusicInfo(showStoryView.getMyContext(),bean);
//    }

    @Override
    public void initRvAdapter(RecyclerView recyclerView) {
        dataList = new ArrayList<>();
        rvAdapter_showStory = new RvAdapter_ShowStory(showStoryView.getMyContext(),dataList,showStoryView.getPresenter());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(showStoryView.getMyContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvAdapter_showStory);
    }

    @Override
    public String getPositionType(int pos) {
        String string = null;
        switch (pos){
            case 0:
                string = "tuijian";
                break;
            case 1:
                string = "gushi";
                break;
            case 2:
                string = "jingsong";
                break;
            case 3:
                string = "tuokouxiu";
                break;
        }
        return string;
    }
}
