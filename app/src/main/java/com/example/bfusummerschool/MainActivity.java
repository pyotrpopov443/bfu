package com.example.bfusummerschool;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SettingsFragment.SettingsCallback {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;

    private boolean isDarkMode;
    private String cohort;
    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sPref = getPreferences(MODE_PRIVATE);
        isDarkMode = sPref.getBoolean(Constants.DARK_MODE, false);
        setTheme(isDarkMode ? R.style.DarkTheme : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        DBHelper.getInstance().init(this);
        cohort = sPref.getString(Constants.COHORT, (setLanguage().equals(Constants.RU) ? "Юристы, Английская группа" : "Lawyers, English Cohort" ));
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int action = 0;
            switch (menuItem.getItemId()) {
                case R.id.schedule_expandable_list_view:
                    action = 0;
                    break;
                case R.id.administration:
                    action = 1;
                    break;
                case R.id.syllabi_expandable_list_view:
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
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                MenuItem item = bottomNavigationView.getMenu().getItem(position);
                bottomNavigationView.setSelectedItemId(item.getItemId());
                item.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        if(getIntent().getBooleanExtra(Constants.SETTINGS_CHANGED, false)) {
            viewPager.setCurrentItem(3);
        }
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        sPref.edit().putBoolean(Constants.DARK_MODE, isDarkMode).apply();
        finish();
        getIntent().putExtra(Constants.SETTINGS_CHANGED, true);
        startActivity(getIntent());
    }

    @Override
    public void onCohortChanged(String cohort) {
        sPref.edit().putString(Constants.COHORT, cohort).apply();
        finish();
        getIntent().putExtra(Constants.SETTINGS_CHANGED, true);
        startActivity(getIntent());
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                return new AdministrationFragment(setLanguage());
            } else if (position == 2) {
                return new SyllabiFragment(setLanguage());
            } else if (position == 3) {
                return new SettingsFragment(isDarkMode, cohort);
            } else {
                return new ScheduleFragment(cohort);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

    }

    private String setLanguage(){
        String lang;
        if (Locale.getDefault().getDisplayLanguage().equals("русский")){
            lang = Constants.RU;
        }else {
            lang = Constants.EN;
        }
        return lang;
    }

}