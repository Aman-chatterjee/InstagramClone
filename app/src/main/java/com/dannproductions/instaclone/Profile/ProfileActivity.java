
package com.dannproductions.instaclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dannproductions.instaclone.DataModels.MasterUserSettings;
import com.dannproductions.instaclone.DataModels.Photo;
import com.dannproductions.instaclone.DataModels.UserAccountSettings;
import com.dannproductions.instaclone.DataModels.Video;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Settings.SettingsActivity;
import com.dannproductions.instaclone.Utils.BottomNavigationViewHelper;
import com.dannproductions.instaclone.Utils.FirebaseMethods;
import com.dannproductions.instaclone.Utils.GridViewAdapter;
import com.dannproductions.instaclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NO = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    private Photo photo;
    private  Video video;
    private Context mContext = ProfileActivity.this;
    private ArrayList<Serializable> dataList;
    private ArrayList<String> mediaUrls;
    private UserAccountSettings accountSettings;

    //widgets
    private ProgressBar progressBar;
    private ImageView profilePhoto;


    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    FirebaseDatabase  database;
    private FirebaseMethods firebaseMethods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mediaUrls = new ArrayList<>();
        dataList = new ArrayList<>();
        photo = new Photo();
        video = new Video();
        progressBar = (ProgressBar)findViewById(R.id.profile_progress);
        firebaseMethods = new FirebaseMethods(ProfileActivity.this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        myRef=database.getReference();

        setupToolbar();
        setupBottomNavigationView();
        setupFirebaseRetriever();

    }



    private  void setupFirebaseRetriever(){


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            setupProfileData(firebaseMethods.retrieveUserData(dataSnapshot,mAuth.getCurrentUser().getUid(),true,false));
            getGridData(getString(R.string.user_photos_node),false);
            getGridData(getString(R.string.user_videos_node),true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void setupProfileData(MasterUserSettings userSettings){

        //User user = userSettings.getUser();
        accountSettings = userSettings.getAccountSettings();

        //setting up widgets
        TextView posts = (TextView)findViewById(R.id.tv_posts);

        TextView followers = (TextView)findViewById(R.id.tv_followers);
        TextView following = (TextView)findViewById(R.id.tv_following);
        TextView username = (TextView)findViewById(R.id.user_name);
        TextView displayName = (TextView)findViewById(R.id.display_name);
        TextView website = (TextView)findViewById(R.id.website);
        TextView description = (TextView)findViewById(R.id.description);
        profilePhoto = (ImageView)findViewById(R.id.profile_photo);

        //Setting data into the views
        username.setText(accountSettings.getUsername());
        displayName.setText(accountSettings.getDisplay_name());
        website.setText(accountSettings.getWebsite());
        description.setText(accountSettings.getDescription());
        firebaseMethods.setPostCount(mAuth.getCurrentUser().getUid(),posts);
        firebaseMethods.setFollowersCount(mAuth.getCurrentUser().getUid(),followers);
        firebaseMethods.setFollowingCount(mAuth.getCurrentUser().getUid(),following);
        //GlideImageLoader.loadImage(mContext,accountSettings.getProfile_photo(),profilePhoto);
        UniversalImageLoader.setImage(accountSettings.getProfile_photo(),profilePhoto,progressBar,"");
    }

    private  void getGridData(String node, final boolean isVideo){

        Query query = myRef.child(node).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                 if(isVideo){
                     String videoUrl = Objects.requireNonNull(singleSnapshot.getValue(Video.class)).getVideoUrl();
                     video = Objects.requireNonNull(singleSnapshot.getValue(Video.class));
                     if(!mediaUrls.contains(videoUrl)){ mediaUrls.add(0,videoUrl); }
                     if(!dataList.contains(video)){ dataList.add(0,video); }
                 }else {
                     String photoUrl = Objects.requireNonNull(singleSnapshot.getValue(Photo.class)).getImageUrl();
                     photo = Objects.requireNonNull(singleSnapshot.getValue(Photo.class));
                     if(!mediaUrls.contains(photoUrl)){ mediaUrls.add(0,photoUrl); }
                     if(!dataList.contains(photo)){ dataList.add(0,photo); }
                 }
                    //Log.d(TAG,url);
                }
                setupImageGrid(mediaUrls,dataList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Failed to add imageUrls!!");
            }
        });

    }



    //Responsible for setting grid photos
    private void setupImageGrid(final ArrayList<String> mediaUrls, final ArrayList<Serializable> dataList){

        GridView gridView = findViewById(R.id.profile_grid);
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);
        GridViewAdapter viewAdapter = new GridViewAdapter(ProfileActivity.this,R.layout.layout_grid_image_view,"",mediaUrls);
        gridView.setAdapter(viewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext,ViewPostActivity.class);
                intent.putExtra(getString(R.string.mediaData),dataList.get(position));
                intent.putExtra(getString(R.string.usernameField),accountSettings.getUsername());
                intent.putExtra(getString(R.string.profilePhoto),accountSettings.getProfile_photo());
                startActivity(intent);
            }
        });
    }


     /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){

        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NO);
        menuItem.setChecked(true);
    }


    /**
     * Responsible for setting up the profile toolbar
     */
    private void setupToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.option :

                        Intent settingIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
                        startActivity(settingIntent);
                        break;

                    case R.id.addFriends :
                        break;
                }
                return false;
            }
        });
    }


    public void editProfileClicked(View view){
        Intent intent = new Intent(ProfileActivity.this,SettingsActivity.class);
        intent.putExtra("formProfile",true);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



}
