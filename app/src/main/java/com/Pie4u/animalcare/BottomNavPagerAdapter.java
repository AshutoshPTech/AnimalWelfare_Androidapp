package com.Pie4u.animalcare;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BottomNavPagerAdapter extends FragmentStateAdapter {

    public BottomNavPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0) {
            return new RescueRequestsFragment();
        }
        else if(position==1)
            return new FeedSectionFragment();
        else
            return new VolunteerProfleFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
