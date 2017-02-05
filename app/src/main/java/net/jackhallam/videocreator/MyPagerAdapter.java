package net.jackhallam.videocreator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    private MainActivity mainActivity;
    private FirebaseUser firebaseUser;
    private ViewPager viewPager;

    private boolean isUserLoggedIn = false;

    private List<Fragment> pages = new ArrayList<>();

    MyPagerAdapter(FragmentManager fm, MainActivity mainActivity, ViewPager viewPager) {
        super(fm);
        this.mainActivity = mainActivity;
        this.viewPager = viewPager;

        LoginFragment loginFragment = new LoginFragment();
        ImportFragment importFragment = new ImportFragment();
        EditFragment editFragment = new EditFragment();
        ExportFragment exportFragment = new ExportFragment();

        pages.add(loginFragment);
        pages.add(importFragment);
        pages.add(editFragment);
        pages.add(exportFragment);
    }

    @Override
    public Fragment getItem(int pos) {
        return pages.get(pos);
    }

    @Override
    public int getCount() {
        switch (mainActivity.getUserState()) {
            case NOT_LOGGED_IN:
                return 1;
            case NO_PROJECT:
                return 2;
            case PROJECT:
                return 4;
            default:
                return pages.size();
        }
    }

    public void userLoggedIn() {
        isUserLoggedIn = true;
        firebaseUser = mainActivity.getFirebaseUser();
        notifyDataSetChanged();
    }

    public void userLoggedOut() {
        isUserLoggedIn = false;
    }
}