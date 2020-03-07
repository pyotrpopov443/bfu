package com.example.bfusummerschool;

import android.content.SharedPreferences;
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

    final String RU = "RU";
    final String EN = "EN";
    final String DARK_MODE = "darkMode";
    private String language;
    private boolean isDarkMode;
    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = getPreferences(MODE_PRIVATE);
        isDarkMode = sPref.getBoolean(DARK_MODE, false);
        setTheme(isDarkMode ? R.style.DarkTheme : R.style.AppTheme);
        setContentView(R.layout.activity_main);
        language = setLanguage();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int action = 0;
            switch (menuItem.getItemId()) {
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
        if(getIntent().getBooleanExtra("settings", false)) {
            viewPager.setCurrentItem(3);
        }
    }

    @Override
    public void onThemeChanged(boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
        sPref.edit().putBoolean(DARK_MODE, isDarkMode).apply();
        finish();
        getIntent().putExtra("settings", true);
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
                return new AdministrationFragment();
            } else if (position == 2) {
                return new SyllabiFragment();
            } else if (position == 3) {
                return new SettingsFragment(isDarkMode);
            } else {
                return new ScheduleFragment(language);
            }
        }

        @Override
        public int getCount() {
            return 4;
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