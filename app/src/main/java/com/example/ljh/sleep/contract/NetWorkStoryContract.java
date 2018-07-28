package com.example.ljh.sleep.contract;

import android.support.v4.view.ViewPager;

public class NetWorkStoryContract {
    public interface NetWorkStoryView{
    }

    public interface NetWorkStoryPresenter{
        void initViewPagerAdapter(ViewPager viewPager);
    }

    public interface NetWorkStoryModel{

    }
}
