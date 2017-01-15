package net.jackhallam.videocreator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MainActivity extends FragmentActivity {

    private static final int INITIAL_PAGE = 0;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;

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
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                hideDisplayFAB(position, fab1, fab2);
            }
        });
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

    public FloatingActionButton getFab(int whichFab){
        if(whichFab == 2){
            return fab2;
        } else {
            return fab1;
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
//            Uri videoUri = intent.getData();
//
//            View coordinator = findViewById(R.id.project_outer);
//            Snackbar snackbar = Snackbar.make(coordinator, "Got the video", Snackbar.LENGTH_LONG);
//            snackbar.show();
//            //TODO: do something with the Uri
//        }
//    }

}
