package kr.co.planet.newgreatluck.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import kr.co.planet.newgreatluck.fragment.Menu01Frag;
import kr.co.planet.newgreatluck.fragment.Menu02Frag;
import kr.co.planet.newgreatluck.fragment.Menu03Frag;
import kr.co.planet.newgreatluck.fragment.Menu04Frag;

public class MainBottomPagerAdapter extends FragmentStatePagerAdapter {

    public MainBottomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment currentFragment = null;
        switch (i) {
            case 0:
                currentFragment = new Menu01Frag();
                break;
            case 1:
                currentFragment = new Menu02Frag();
                break;
            case 2:
                currentFragment = new Menu03Frag();
                break;
            case 3:
                currentFragment = new Menu04Frag();
                break;
        }
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