package com.example.miteto.placer.Adapters;

/**
 * Created by Miteto on 12/04/2015.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.miteto.placer.Fragment.DaytimeFragment;
import com.example.miteto.placer.Fragment.NightimeFragment;
import com.example.miteto.placer.Fragment.ViewAllFragment;

public class TabPagerAdapter extends FragmentPagerAdapter
{

    public TabPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int index)
    {

        switch (index)
        {
            case 0:
                return new DaytimeFragment();
            case 1:
                return new NightimeFragment();
            case 2:
                return new ViewAllFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}