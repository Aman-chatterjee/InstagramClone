package com.dannproductions.instaclone.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.dannproductions.instaclone.Home.HomeActivity;
import com.dannproductions.instaclone.Like.LikesActivity;
import com.dannproductions.instaclone.Profile.ProfileActivity;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Search.SearchActivity;
import com.dannproductions.instaclone.Share.ShareActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, final Activity callingActivity,BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.home:
                        if(callingActivity.getClass()!=HomeActivity.class) {
                            Intent intent1 = new Intent(context, HomeActivity.class);//ACTIVITY_NUM = 0
                            //intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                            callingActivity.overridePendingTransition(0, 0);
                        }
                        break;

                    case R.id.search:
                        if(callingActivity.getClass()!=SearchActivity.class) {
                            Intent intent2 = new Intent(context, SearchActivity.class);//ACTIVITY_NUM = 1
                            //intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent2);
                            callingActivity.overridePendingTransition(0, 0);
                        }
                        break;

                    case R.id.share:
                        if(callingActivity.getClass()!=ShareActivity.class) {
                            Intent intent3 = new Intent(context, ShareActivity.class);//ACTIVITY_NUM = 2
                            context.startActivity(intent3);
                            callingActivity.overridePendingTransition(0, 0);
                        }
                        break;

                    case R.id.like:
                        if(callingActivity.getClass()!=LikesActivity.class) {
                            Intent intent4 = new Intent(context, LikesActivity.class);//ACTIVITY_NUM = 3
                            //intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent4);
                            callingActivity.overridePendingTransition(0, 0);
                        }
                        break;

                    case R.id.profile:
                        if(callingActivity.getClass()!=ProfileActivity.class) {
                            Intent intent5 = new Intent(context, ProfileActivity.class);//ACTIVITY_NUM = 4
                            //intent5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent5);
                            callingActivity.overridePendingTransition(0, 0);
                        }
                        break;
                }


                return false;
            }
        });
    }
}
