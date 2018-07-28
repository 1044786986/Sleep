package com.example.ljh.sleep.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class ViewPagerAdapter_NetWorkStory extends FragmentStatePagerAdapter{
    private List<Fragment> fragmentList;
    private String[] tabItems;

    public ViewPagerAdapter_NetWorkStory(FragmentManager fm,List<Fragment> list,String[]tabItems) {
        super(fm);
        this.fragmentList = list;
        this.tabItems = tabItems;
    }

    @Override
    public Fragment getItem(int position) {
//        if(position != 0){
//            NetWorkStoryPresenter.pos = position - 1;
//        }else{
//            NetWorkStoryPresenter.pos = position;
//        }
//        NetWorkStoryPresenter.pos = position;
        
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabItems[position];
    }
}
