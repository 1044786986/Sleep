package com.example.ljh.sleep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.contract.ShowStoryContract;
import com.example.ljh.sleep.presenter.ShowStoryPresenter;
import com.example.ljh.sleep.utils.MyApplication;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class ShowStoryFragment extends Fragment implements ShowStoryContract.ShowStoryView{
    private RecyclerView recyclerView;
    private WaveSwipeRefreshLayout refreshLayout;
//    public static TextView textView;
    private ProgressBar pbShowStory;
    private ShowStoryPresenter showStoryPresenter;
    private boolean isFirstOpen = true;       //第一次打开页面
    private boolean isViewCreate = false;   //视图已创建
    private boolean isVisible = false;      //页面已对用户可见
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_showstory,null);
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreate = true;
        showStoryPresenter.getData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            isVisible = true;
            if(isViewCreate){   //防止ViewCreate完成前，还没创建对象
            showStoryPresenter.getData();
            }
        }else{
            isVisible = false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView_ShowStory);
        pbShowStory = view.findViewById(R.id.pbShowStory);
//        textView = view.findViewById(R.id.textView);
        showStoryPresenter = new ShowStoryPresenter(this);
        showStoryPresenter.initRvAdapter(recyclerView);
        refreshLayout = view.findViewById(R.id.refresh_ShowStory);
        refreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("aaa","ShowStoryFragment.onRefresh()");
                showStoryPresenter.upDate();
            }
        });
    }

    @Override
    public Context getMyContext() {
        if(getActivity() == null){
            return MyApplication.getInstance();
        }
        return getActivity();
    }

    @Override
    public ShowStoryPresenter getPresenter() {
        return showStoryPresenter;
    }

    @Override
    public boolean isViewCreate1() {
        return isViewCreate;
    }

    @Override
    public boolean isVisible1() {
        return isVisible;
    }

    @Override
    public boolean isFirstOpen() {
        return isFirstOpen;
    }

    @Override
    public void resetFirstOpen() {
        isFirstOpen = false;
    }

    @Override
    public void resetViewCreate() {
        isViewCreate = false;
    }

    @Override
    public void resetVisible() {
        isVisible = false;
    }

    @Override
    public void hideRefreshLayout() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showProgressBar() {
        pbShowStory.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        pbShowStory.setVisibility(View.GONE);
    }

}
