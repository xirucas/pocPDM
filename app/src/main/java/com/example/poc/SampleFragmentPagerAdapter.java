package com.example.poc;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.poc.CameraFragment;
import com.example.poc.InternetFragment;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 5; // Number of tabs
    private final Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Return the appropriate fragment for each tab
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new CameraFragment();
            case 2:
                return new InternetFragment();
            case 3:
                return new LocalDatabaseFragment();
            case 4:
                return new WebServicesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Return the title for each tab
        switch (position) {
            case 0:
                return "⌂";
            case 1:
                return "Camera";
            case 2:
                return "Internet";
            case 3:
                return "Local Database";
            case 4:
                return "Web Services";
            default:
                return null;
        }
    }
}
