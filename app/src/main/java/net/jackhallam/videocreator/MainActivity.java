package net.jackhallam.videocreator;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends FragmentActivity {

    private static final int INITIAL_PAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
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
            case 2:
                fab1.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings_black_48dp));
                fab1.show();
                fab2.hide();
                break;
            default:
                fab1.hide();
                fab2.hide();
        }
    }
}
