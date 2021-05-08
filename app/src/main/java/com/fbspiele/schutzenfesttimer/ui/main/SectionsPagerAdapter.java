package com.fbspiele.schutzenfesttimer.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fbspiele.schutzenfesttimer.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {



    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        //Log.v("sectionpager","adapter");
    }

    public static class PlaceHolderFragmentEintrag{
        int position;
        Fragment fragment;
        PlaceHolderFragmentEintrag(Fragment fragment, int position){
            this.position = position;
            this.fragment = fragment;
            //Log.v("PlaceHolderFragmentEintrag","position "+position);
        }
    }

    private List<PlaceHolderFragmentEintrag> placeHolderFragmentList = new ArrayList<>();


    public Fragment getFragmentByPosition(int position){
        for(int i = 0; i<placeHolderFragmentList.size();i++){
            if(placeHolderFragmentList.get(i).position==position){
                return placeHolderFragmentList.get(i).fragment;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //Log.v("getItem","position "+position);
        Fragment fragment = PlaceholderFragment.newInstance(position + 1);
        placeHolderFragmentList.add(new PlaceHolderFragmentEintrag(fragment, position));
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(MainActivity.myAngezeigteCalendarList.get(position).getName());
    }

    @Override
    public int getCount() {
        return MainActivity.myAngezeigteCalendarList.size();
    }
}