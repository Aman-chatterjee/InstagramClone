package com.dannproductions.instaclone.Share;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dannproductions.instaclone.Home.CameraFragment;
import com.dannproductions.instaclone.Home.HomeFragment;
import com.dannproductions.instaclone.Home.MessagingFragment;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Utils.BottomNavigationViewHelper;
import com.dannproductions.instaclone.Utils.CheckPermissions;
import com.dannproductions.instaclone.Utils.DirectoryScanner;
import com.dannproductions.instaclone.Utils.MediaFilesScanner;
import com.dannproductions.instaclone.Utils.SectionPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.Objects;

public class ShareActivity extends AppCompatActivity{
    private static final String TAG = "ShareActivity";
    public TabLayout tabLayout;
    private Context mContext;

    private final int requestCode = 3;
    private final String[] permissionList =  {
            Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mContext = ShareActivity.this;

       if(Build.VERSION.SDK_INT >= 23&&!CheckPermissions.hasPermissions(mContext,permissionList)) {

            //Toast.makeText(mContext, "Please give the required permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,permissionList, requestCode);
        }else {
            //Loading all image and video directories
            new DirectoryScanner().execute(Environment.getExternalStorageDirectory().getAbsolutePath());
            setupViewPager();
            Log.d(TAG,"Scanning Files");
        }


    }


    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment()); //index 0
        adapter.addFragment(new PhotoFragment());  //index 1
        adapter.addFragment(new VideoFragment()); //index 2
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.GalleryFragment));
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(getString(R.string.PhotoFragment));
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText(getString(R.string.VideoFragment));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case 3 :
                if(CheckPermissions.hasPermissions(mContext,permissionList)){
                    //Loading all image and video directories
                    new DirectoryScanner().execute(Environment.getExternalStorageDirectory().getAbsolutePath());
                    setupViewPager();
                    Log.d(TAG,"Scanning Files");
                }else {
                    finish();
                }
                break;
        }
    }




}