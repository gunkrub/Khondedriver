package com.tny.khondedriver;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by Thitiphat on 4/11/2016.
 */
public class RegisterFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    register_tab1 r1 = new register_tab1();
    register_tab2 r2 = new register_tab2();
    register_tab3 r3 = new register_tab3();
    register_tab4 r4 = new register_tab4();


    public RegisterFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return r1;
            case 1:
                return r2;
            case 2:
                return r3;
            case 3:
                return r4;
            default:
                return r1;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
