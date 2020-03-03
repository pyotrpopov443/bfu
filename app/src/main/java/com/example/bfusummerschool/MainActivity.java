package com.example.bfusummerschool;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    MenuItem prevMenuItem;

    final static String RU = "RU";
    final static String EN = "EN";
    static String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        language = setLanguage();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.viewPager);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.schedule:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.administration:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.syllabi:
                                viewPager.setCurrentItem(2);
                            case R.id.settings:
                                viewPager.setCurrentItem(3);
                                break;
                        }
                        return false;
                    }
                });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);

                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        AdministrationFragment administrationFragment = new AdministrationFragment();
        SyllabiFragment syllabiFragment = new SyllabiFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        adapter.addFragment(scheduleFragment);
        adapter.addFragment(administrationFragment);
        adapter.addFragment(syllabiFragment);
        adapter.addFragment(settingsFragment);
        viewPager.setAdapter(adapter);
    }

    private String setLanguage(){
        String lang;
        if (Locale.getDefault().getDisplayLanguage().equals("русский")){
            lang = RU;
        }else {
            lang = EN;
        }
        return lang;
    }

}