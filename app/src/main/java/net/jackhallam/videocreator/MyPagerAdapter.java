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

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos) {
            case 0:
                return new ImportFragment();
            case 1:
                return new CameraFragment();
            case 2:
                return new EditFragment();
            case 3:
                return new ExportFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}