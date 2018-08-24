package com.dannproductions.instaclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dannproductions.instaclone.DataModels.MasterUserSettings;
import com.dannproductions.instaclone.DataModels.Photo;
import com.dannproductions.instaclone.DataModels.UserAccountSettings;
import com.dannproductions.instaclone.DataModels.Video;
import com.dannproductions.instaclone.Profile.ProfileActivity;
import com.dannproductions.instaclone.Profile.ViewPostActivity;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Settings.SettingsActivity;
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

public class ViewProfileActivity extends AppCompatActivity {

    private static final String TAG = "ViewProfileActivity";
    private static final int ACTIVITY_NO = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    private Photo photo;
    private Video video;
    private String userID;
    private Context mContext = ViewProfileActivity.this;
    private ArrayList<Serializable> dataList;
    private ArrayList<String> mediaUrls;
    private UserAccountSettings accountSettings;

    //widgets
    private ProgressBar progressBar;
    private ImageView profilePhoto;
    private Button unfolow,follow;


    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    FirebaseDatabase database;
    private FirebaseMethods firebaseMethods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        unfolow = findViewById(R.id.unfollow);
        follow = findViewById(R.id.follow);
        firebaseMethods = new FirebaseMethods(ViewProfileActivity.this);
        mediaUrls = new ArrayList<>();
        dataList = new ArrayList<>();
        photo = new Photo();
        video = new Video();
        progressBar = (ProgressBar) findViewById(R.id.profile_progress);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        userID = intent.getStringExtra(getString(R.string.users_id));
        isFollowing();
        setupFollowingListners();



        try {
            database.setPersistenceEnabled(true);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        myRef = database.getReference();
        setupToolbar();
        setupBottomNavigationView();
        setupFirebaseRetriever();

    }


    private void setupFirebaseRetriever() {


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                setupProfileData(firebaseMethods.retrieveUserData(dataSnapshot,userID, true, false));
                getGridData(getString(R.string.user_photos_node), false);
                getGridData(getString(R.string.user_videos_node), true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void setupProfileData(MasterUserSettings userSettings) {

        //User user = userSettings.getUser();
        accountSettings = userSettings.getAccountSettings();

        //setting up widgets
        TextView posts = (TextView) findViewById(R.id.tv_posts);
        TextView followers = (TextView) findViewById(R.id.tv_followers);
        TextView following = (TextView) findViewById(R.id.tv_following);
        TextView username = (TextView) findViewById(R.id.user_name);
        TextView displayName = (TextView) findViewById(R.id.display_name);
        TextView website = (TextView) findViewById(R.id.website);
        TextView description = (TextView) findViewById(R.id.description);
        profilePhoto = (ImageView) findViewById(R.id.profile_photo);

        //Setting data into the views
        username.setText(accountSettings.getUsername());
        displayName.setText(accountSettings.getDisplay_name());
        website.setText(accountSettings.getWebsite());
        description.setText(accountSettings.getDescription());
        firebaseMethods.setPostCount(userID,posts);
        firebaseMethods.setFollowersCount(userID,followers);
        firebaseMethods.setFollowingCount(userID,following);
        //GlideImageLoader.loadImage(mContext,accountSettings.getProfile_photo(),profilePhoto);
        UniversalImageLoader.setImage(accountSettings.getProfile_photo(), profilePhoto, progressBar, "");
    }

    private void getGridData(String node, final boolean isVideo) {

        Query query = myRef.child(node).child(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    if (isVideo) {
                        String videoUrl = Objects.requireNonNull(singleSnapshot.getValue(Video.class)).getVideoUrl();
                        video = Objects.requireNonNull(singleSnapshot.getValue(Video.class));
                        if (!mediaUrls.contains(videoUrl)) {
                            mediaUrls.add(0, videoUrl);
                        }
                        if (!dataList.contains(video)) {
                            dataList.add(0, video);
                        }
                    } else {
                        String photoUrl = Objects.requireNonNull(singleSnapshot.getValue(Photo.class)).getImageUrl();
                        photo = Objects.requireNonNull(singleSnapshot.getValue(Photo.class));
                        if (!mediaUrls.contains(photoUrl)) {
                            mediaUrls.add(0, photoUrl);
                        }
                        if (!dataList.contains(photo)) {
                            dataList.add(0, photo);
                        }
                    }
                    //Log.d(TAG,url);
                }
                setupImageGrid(mediaUrls, dataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to add imageUrls!!");
            }
        });


    }


    //Responsible for setting grid photos
    private void setupImageGrid(final ArrayList<String> mediaUrls, final ArrayList<Serializable> dataList) {

        GridView gridView = findViewById(R.id.profile_grid);
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);
        GridViewAdapter viewAdapter = new GridViewAdapter(mContext, R.layout.layout_grid_image_view, "", mediaUrls);
        gridView.setAdapter(viewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, ViewPostActivity.class);
                intent.putExtra(getString(R.string.mediaData), dataList.get(position));
                intent.putExtra(getString(R.string.usernameField), accountSettings.getUsername());
                intent.putExtra(getString(R.string.profilePhoto), accountSettings.getProfile_photo());
                startActivity(intent);
            }
        });
    }


    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {

        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
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
    }


    private void isFollowing(){
        Log.d(TAG, "isFollowing: checking if following this users.");

        myRef = database.getReference();
        Query query = myRef.child(getString(R.string.node_following))
                .child(mAuth.getCurrentUser().getUid())
                .orderByChild(getString(R.string.users_id)).equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              if(dataSnapshot.exists()){
                  follow.setVisibility(View.GONE);
                  unfolow.setVisibility(View.VISIBLE);
              }else {
                  follow.setVisibility(View.VISIBLE);
                  unfolow.setVisibility(View.GONE);
              }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void setupFollowingListners(){

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            firebaseMethods.addFollowingAndFollowers(userID);
            follow.setVisibility(View.GONE);
            unfolow.setVisibility(View.VISIBLE);
            }
        });

        unfolow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseMethods.removeFollowingAndFollowers(userID);
                follow.setVisibility(View.VISIBLE);
                unfolow.setVisibility(View.GONE);
            }
        });
    }




}
