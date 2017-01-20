package net.jackhallam.videocreator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.jackhallam.videocreator.model.VideoProject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private static final int INITIAL_PAGE = 0;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;

    private static List<VideoProject> videoProjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        hideDisplayFAB(INITIAL_PAGE, fab1, fab2);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(myPagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                hideDisplayFAB(position, fab1, fab2);
            }
        });

        addSampleProject();
    }

    private void hideDisplayFAB(int position, FloatingActionButton fab1, FloatingActionButton fab2) {
        switch (position) {
            case 0:
                fab1.setImageDrawable(getResources().getDrawable(R.drawable.ic_help_black_48dp));
                fab1.show();
                fab2.hide();
                break;
            case 1:
                fab1.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_48dp));
                fab1.show();
                fab2.show();
                break;
            default:
                fab1.hide();
                fab2.hide();
        }
    }

    public FloatingActionButton getFab(int whichFab) {
        if (whichFab == 2) {
            return fab2;
        } else {
            return fab1;
        }
    }

    //TODO: just a sample
    private static void addSampleProject() {
        VideoProject sampleVideoProject = new VideoProject();
        sampleVideoProject.setTitle("Sample Video Project");
        videoProjects.add(sampleVideoProject);
    }

    public static List<VideoProject> getVideoProjects() {
        return videoProjects;
    }
}
