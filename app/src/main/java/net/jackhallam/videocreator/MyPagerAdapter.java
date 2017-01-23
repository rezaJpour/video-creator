package net.jackhallam.videocreator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import net.jackhallam.videocreator.pages.EditFragment;
import net.jackhallam.videocreator.pages.ExportFragment;
import net.jackhallam.videocreator.pages.ImportFragment;
import net.jackhallam.videocreator.pages.LoginFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackhallam on 12/24/16.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> pages = new ArrayList<>();


    private ImportFragment importFragment;

    MyPagerAdapter(FragmentManager fm, ViewPager viewPager) {
        super(fm);
        pages.add(new LoginFragment());
        importFragment = new ImportFragment();
        importFragment.setPager(viewPager);
        pages.add(importFragment);
        pages.add(new EditFragment());
        pages.add(new ExportFragment());
    }

    @Override
    public Fragment getItem(int pos) {
        return pages.get(pos);
    }

    @Override
    public int getCount() {
        return pages.size();
    }
}