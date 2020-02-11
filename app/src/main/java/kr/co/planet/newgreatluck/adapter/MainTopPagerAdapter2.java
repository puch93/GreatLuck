package kr.co.planet.newgreatluck.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.fragment.MainTopBannerFrag;

public class MainTopPagerAdapter2 extends FragmentStatePagerAdapter {
    private ArrayList<String> contents_list;
    Activity act;

    public MainTopPagerAdapter2(Activity act, FragmentManager fm, ArrayList<String> contents_list) {
        super(fm);
        this.contents_list = contents_list;
        this.act = act;
    }

    @Override
    public Fragment getItem(int i) {
        MainTopBannerFrag currentFragment = new MainTopBannerFrag();
        currentFragment.setData(i, "",contents_list.get(i));

        return currentFragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}