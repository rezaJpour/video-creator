package net.jackhallam.videocreator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.jackhallam.videocreator.pages.CameraFragment;
import net.jackhallam.videocreator.pages.EditFragment;
import net.jackhallam.videocreator.pages.ExportFragment;
import net.jackhallam.videocreator.pages.ImportFragment;

/**
 * Created by jackhallam on 12/24/16.
 */

class MyPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] pages = new Fragment[4];

    MyPagerAdapter(FragmentManager fm) {
        super(fm);
        pages[0] = new ImportFragment();
        pages[1] = new CameraFragment();
        pages[2] = new EditFragment();
        pages[3] = new ExportFragment();
    }

    @Override
    public Fragment getItem(int pos) {
        return pages[pos];
    }

    @Override
    public int getCount() {
        return pages.length;
    }
}