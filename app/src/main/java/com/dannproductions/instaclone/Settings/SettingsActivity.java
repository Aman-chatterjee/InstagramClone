package com.dannproductions.instaclone.Settings;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.dannproductions.instaclone.Profile.ProfileActivity;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Utils.BottomNavigationViewHelper;
import com.dannproductions.instaclone.Utils.SectionStatePagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private static final int ACTIVITY_NO = 4;
    private static final int EDIT_PROFILE_SETTING_POSITION = 0;
    private Context mContext = SettingsActivity.this;
    private SectionStatePagerAdapter sectionStatePagerAdapter;
    private ViewPager viewPager;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        relativeLayout = (RelativeLayout)findViewById(R.id.rl_settings);
        viewPager = (ViewPager)findViewById(R.id.viewPager);


        setupBottomNavigationView();
        setupSettingsList();
        setupFragments();
        actionGoBack();

    }



    private void setupFragments(){

        sectionStatePagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        sectionStatePagerAdapter.addFragments(new EditProfileFragment(),getString(R.string.EditProfile));
        sectionStatePagerAdapter.addFragments(new SignOutFragment(),getString(R.string.SignOut));
    }

    private void setupViewPager(int fragmentNumber){

        relativeLayout.setVisibility(View.GONE);
        viewPager.setAdapter(sectionStatePagerAdapter);
        viewPager.setCurrentItem(fragmentNumber);
    }


    private void setupSettingsList(){

        ListView listView = (ListView)findViewById(R.id.setting_list);
        ArrayList<String> settingList = new ArrayList<>();
        settingList.add(getString(R.string.EditProfile));
        settingList.add(getString(R.string.SignOut));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,settingList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Navigating to item_Settings
                setupViewPager(position);
            }
        });

    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
      //  Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NO);
        menuItem.setChecked(true);

    }



    private void actionGoBack(){

        ImageView back = (ImageView)findViewById(R.id.setting_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().getBooleanExtra("formProfile",false)){ setupViewPager(EDIT_PROFILE_SETTING_POSITION);}
    }
}
