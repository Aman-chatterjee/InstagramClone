package com.dannproductions.instaclone.Home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dannproductions.instaclone.Login.LoginActivity;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Utils.BottomNavigationViewHelper;
import com.dannproductions.instaclone.Utils.CheckPermissions;
import com.dannproductions.instaclone.Utils.DirectoryScanner;
import com.dannproductions.instaclone.Utils.MediaFilesScanner;
import com.dannproductions.instaclone.Utils.SectionPagerAdapter;
import com.dannproductions.instaclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NO = 0;
    private Context mContext = HomeActivity.this;
    private final int camFragmentPos = 0;
    private final String[] permissionList =  {
            Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.CAMERA
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();

    }


    /**
     * Responsible for adding the 3 tabs: Camera, Home, Messages
     */
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(HomeActivity.this.getSupportFragmentManager());
        adapter.addFragment(new CameraFragment()); //index 0
        adapter.addFragment(new HomeFragment());  //index 1
        adapter.addFragment(new MessagingFragment()); //index 2
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_action_camera);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.instagram_logo);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_action_message);
        viewPager.setCurrentItem(1,false);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==camFragmentPos){

                    if(Build.VERSION.SDK_INT >= 23&&!CheckPermissions.hasPermissions(mContext,permissionList)) {
                        ActivityCompat.requestPermissions(HomeActivity.this,permissionList, 0);
                    }
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

    }



    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NO);
        menuItem.setChecked(true);

    }


    private void initImageLoader(){

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(HomeActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

                if (requestCode==0&&!CheckPermissions.hasPermissions(mContext, permissionList)) {
                    finish();
                }
    }



}
