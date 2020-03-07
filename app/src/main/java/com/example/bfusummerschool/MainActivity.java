package com.example.bfusummerschool;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ScheduleExpandableListAdapter.OnEventClickCallback {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;

    final static String RU = "RU";
    final static String EN = "EN";
    static String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        language = setLanguage();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);

        viewPager.setOffscreenPageLimit(3);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    int action = 0;
                    switch (item.getItemId()) {
                        case R.id.schedule:
                            action = 0;
                            break;
                        case R.id.administration:
                            action = 1;
                            break;
                        case R.id.syllabi:
                            action = 2;
                            break;
                        case R.id.settings:
                            action = 3;
                    }
                    viewPager.setCurrentItem(action);
                    return false;
                });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                MenuItem item = bottomNavigationView.getMenu().getItem(position);
                bottomNavigationView.setSelectedItemId(item.getItemId());
                item.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onEventClick(String event) {
        new AlertDialog.Builder(this).setMessage(event).create().show();
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                return new AdministrationFragment();
            } else if (position == 2) {
                return new SyllabiFragment();
            } else if (position == 3) {
                return new SettingsFragment();
            } else {
                return new ScheduleFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Object fragment = super.instantiateItem(container, position);
            if (fragment instanceof ScheduleFragment) {
                ((ScheduleFragment) fragment).setCallback(MainActivity.this);
            }
            return fragment;
        }
    }

    private String setLanguage() {
        String lang;
        if (Locale.getDefault().getDisplayLanguage().equals("русский")) {
            lang = RU;
        } else {
            lang = EN;
        }
        return lang;
    }

}