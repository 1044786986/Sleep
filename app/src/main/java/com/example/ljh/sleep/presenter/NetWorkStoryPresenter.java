package com.example.ljh.sleep.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.example.ljh.sleep.activity.MainActivity;
import com.example.ljh.sleep.adapter.ViewPagerAdapter_NetWorkStory;
import com.example.ljh.sleep.contract.NetWorkStoryContract;
import com.example.ljh.sleep.fragment.ShowStoryFragment;
import com.example.ljh.sleep.model.NetWorkStoryModel;

import java.util.ArrayList;
import java.util.List;

public class NetWorkStoryPresenter implements NetWorkStoryContract.NetWorkStoryPresenter{
    private NetWorkStoryContract.NetWorkStoryView netWorkStoryView;
    private NetWorkStoryContract.NetWorkStoryModel netWorkStoryModel;
    private ViewPagerAdapter_NetWorkStory viewPagerAdapter;
    private Fragment fragment_recommend;//推荐
    private Fragment fragment_story;//童话故事
    private Fragment fragment_jingsong;
    private Fragment fragment_tuokouxiu;
    private List<Fragment> fragmentList;
    private final String[] tabItems = {"推荐","故事","惊悚","脱口秀"};
    public static int pos; //当前展示第几个fragment

    public NetWorkStoryPresenter(NetWorkStoryContract.NetWorkStoryView view){
        this.netWorkStoryView = view;
        netWorkStoryModel = new NetWorkStoryModel();
    }

    @Override
    public void initViewPagerAdapter(ViewPager viewPager) {
        fragmentList = new ArrayList<>();
        fragment_recommend = new ShowStoryFragment();
        fragment_story = new ShowStoryFragment();
        fragment_jingsong = new ShowStoryFragment();
        fragment_tuokouxiu = new ShowStoryFragment();
        fragmentList.add(fragment_recommend);
        fragmentList.add(fragment_story);
        fragmentList.add(fragment_jingsong);
        fragmentList.add(fragment_tuokouxiu);
        viewPagerAdapter = new ViewPagerAdapter_NetWorkStory(MainActivity.getPresenter().getFragmentManager(),
                fragmentList,tabItems);
        viewPager.setAdapter(viewPagerAdapter);
        /**
         * 获取当前显示的fragment的position
         */
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
