package com.example.ljh.sleep.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.contract.NetWorkStoryContract;
import com.example.ljh.sleep.presenter.NetWorkStoryPresenter;

public class NetWorkStoryFragment extends Fragment implements NetWorkStoryContract.NetWorkStoryView{
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private NetWorkStoryPresenter netWorkStoryPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_networkstory,null);
        init(view);
        return view;
    }

    private void init(View view){
        tabLayout = view.findViewById(R.id.tabLayout_NetWorkStory);
        viewPager = view.findViewById(R.id.viewPager_NetWorkStory);
//        pbShowStory = view.findViewById(R.id.pbShowStory);
        netWorkStoryPresenter = new NetWorkStoryPresenter(this);
        netWorkStoryPresenter.initViewPagerAdapter(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }
}
