package com.dannproductions.instaclone.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<Fragment>();
    private final HashMap<Fragment,Integer> mFragments = new HashMap<>();
    private final HashMap<String,Integer> mFragmentNumbers = new HashMap<>();
    private final HashMap<Integer,String> mFragmentNames = new HashMap<>();

    public SectionStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragments(Fragment fm,String fragmentName){
    fragmentList.add(fm);
    mFragments.put(fm,fragmentList.size()-1);
    mFragmentNumbers.put(fragmentName,fragmentList.size()-1);
    mFragmentNames.put(fragmentList.size()-1,fragmentName);
    }


        public Integer getFragmentNumber(Fragment fragment){

        if(mFragments.containsKey(fragment)){

            return mFragments.get(fragment);
        }else {
            return null;
        }
    }


        public Integer getFragmentNumber(String fragmentName){

        if(mFragmentNumbers.containsKey(fragmentName)){

            return mFragmentNumbers.get(fragmentName);
        }else {
            return null;
        }
    }

    public String getFragmentNames(Integer fragmentNumber){

        if(mFragmentNames.containsKey(fragmentNumber)){

            return mFragmentNames.get(fragmentNumber);
        }else {
            return null;
        }
    }


}
