package com.scheme.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.scheme.Tab1;
import com.scheme.Tab2;


public class SectionsPagerAdapter extends FragmentStateAdapter {


    public SectionsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new Tab1();
                break;
            case 1:
                fragment = new Tab2();
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
