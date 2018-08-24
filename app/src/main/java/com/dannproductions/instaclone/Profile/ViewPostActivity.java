package com.dannproductions.instaclone.Profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dannproductions.instaclone.DataModels.Photo;
import com.dannproductions.instaclone.DataModels.User;
import com.dannproductions.instaclone.DataModels.Video;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Utils.BottomNavigationViewHelper;
import com.dannproductions.instaclone.Utils.CommentsActivity;
import com.dannproductions.instaclone.Utils.FirebaseMethods;
import com.dannproductions.instaclone.Utils.GlideImageLoader;
import com.dannproductions.instaclone.Utils.HeartAnimation;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.Serializable;
import java.util.Objects;

public class ViewPostActivity extends AppCompatActivity {
    //Vars
    private static final String TAG = "ViewPostActivity";
    private static final int ACTIVITY_NO = 4;
    private Context mContext = ViewPostActivity.this;
    private boolean mLikedByCurrentUser = false;
    private String profilePhotoUrl;
    private String userName;
    private Photo photo;
    private Video video;
    private String likeId;
    private boolean isPhoto;
    private GestureDetector detector;
    private StringBuilder mStringBuilder;
    private String mLikesString;
    private HeartAnimation heartAnimation;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    FirebaseDatabase database;
    private FirebaseMethods firebaseMethods;

    //Widgets
    RelativeLayout mediaViewsContainer;
    SimpleExoPlayerView exoPlayerView;
    ImageView postImage,profilePhoto,heartOutline,heartRed,comments;
    SimpleExoPlayer exoPlayer;
    ProgressBar progressBar;
    TextView caption,userNameTextView,dateAdded,likedBy,viewComments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

         mAuth = FirebaseAuth.getInstance();
         database = FirebaseDatabase.getInstance();
         myRef = database.getReference();
         mediaViewsContainer = findViewById(R.id.media_views);
         postImage = findViewById(R.id.photo_post);
         profilePhoto = findViewById(R.id.profile_photo);
         exoPlayerView = findViewById(R.id.video_post);
         progressBar = findViewById(R.id.videoProgress);
         comments = findViewById(R.id.comment);
         viewComments = findViewById(R.id.view_comments);
         userNameTextView = findViewById(R.id.user_name);
         heartRed = findViewById(R.id.heart_red);
         heartOutline = findViewById(R.id.heart_outline);
         caption = findViewById(R.id.caption_text);
         likedBy = findViewById(R.id.like_number);
         dateAdded = findViewById(R.id.date_added);
         heartAnimation =new HeartAnimation();

        detector = new GestureDetector(mContext,new GestureListener());
        setLikeListners();

        Intent intent = getIntent();
        loadProfileInfo(intent);
        Serializable serializable = intent.getSerializableExtra(getString(R.string.mediaData));
        if(serializable.getClass()==Photo.class){

            isPhoto = true;
            photo = (Photo)serializable;
            displayImage(photo.getImageUrl());
            caption.setText(photo.getCaption());
            dateAdded.setText(photo.getDate_added());
            likesSetup(getString(R.string.photos_node),photo.getPhoto_id());

        }else  {
            isPhoto = false;
            video = (Video) serializable;
            playVideo(video.getVideoUrl());
            caption.setText(video.getCaption());
            dateAdded.setText(video.getDate_added());
            likesSetup(getString(R.string.videos_node),video.getVideo_id());
        }

        //Launching comments activity
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mediaIntent = new Intent(mContext,CommentsActivity.class);
                if(isPhoto) {
                    mediaIntent.putExtra("mediaID", photo.getPhoto_id());
                    mediaIntent.putExtra("mediaNode",getString(R.string.photos_node));
                }else {
                    mediaIntent.putExtra("mediaID", video.getVideo_id());
                    mediaIntent.putExtra("mediaNode",getString(R.string.videos_node));
                }
                mediaIntent.putExtra(getString(R.string.profilePhotoField),profilePhotoUrl);
                startActivity(mediaIntent);
            }
        });

        viewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mediaIntent = new Intent(mContext,CommentsActivity.class);
                if(isPhoto) {
                    mediaIntent.putExtra("mediaID", photo.getPhoto_id());
                    mediaIntent.putExtra("mediaNode",getString(R.string.photos_node));
                }else {
                    mediaIntent.putExtra("mediaID", video.getVideo_id());
                    mediaIntent.putExtra("mediaNode",getString(R.string.videos_node));
                }
                mediaIntent.putExtra(getString(R.string.profilePhotoField),profilePhotoUrl);
                startActivity(mediaIntent);
            }
        });

        goBack();
        setupBottomNavigationView();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setLikeListners(){

      heartOutline.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             toggleLike();
          }
      });

      heartRed.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            toggleLike();
          }
      });

       mediaViewsContainer.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               return detector.onTouchEvent(event);
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
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NO);
        menuItem.setChecked(true);

    }

    private void playVideo(String path){

        postImage.setVisibility(View.GONE);
        exoPlayerView.setVisibility(View.VISIBLE);
        exoPlayerView.setUseController(false);
        //exoPlayerView.hideController();
        exoPlayerView.setResizeMode(AspectRatioFrameLayout.SYSTEM_UI_FLAG_FULLSCREEN);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        Uri videoURI = Uri.parse(path);
        Handler mainHandler = new Handler();
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, mainHandler, null);

        exoPlayerView.setPlayer(exoPlayer);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        exoPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);

               if(playbackState==ExoPlayer.STATE_BUFFERING){
                   progressBar.setVisibility(View.VISIBLE);
                }else {
                   progressBar.setVisibility(View.GONE);
               }

            }
        });
    }

    private void loadProfileInfo(Intent intent){

        userName = intent.getStringExtra(getString(R.string.usernameField));
        profilePhotoUrl = intent.getStringExtra(getString(R.string.profilePhoto));
        GlideImageLoader.loadImageWithOutTransition(mContext,profilePhotoUrl,profilePhoto);
        userNameTextView.setText(userName);
    }


    private  void displayImage(String path){

        exoPlayerView.setVisibility(View.GONE);
        postImage.setVisibility(View.VISIBLE);
        GlideImageLoader.loadImageWithTransition(mContext,path,postImage,progressBar);
    }

    private  void goBack(){
        ImageView back = (ImageView)findViewById(R.id.back_post);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(exoPlayer!=null) {
            exoPlayer.stop();
            exoPlayer.release();
        }
    }





    private void toggleLike(){

        if(isPhoto){

            if(!mLikedByCurrentUser){
                heartOutline.setVisibility(View.VISIBLE);
                heartRed.setVisibility(View.GONE);
                heartAnimation.toggleLike(heartOutline,heartRed);
                firebaseMethods.addNewLike(getString(R.string.photos_node),photo.getPhoto_id());
            }else {
                heartOutline.setVisibility(View.GONE);
                heartRed.setVisibility(View.VISIBLE);
                heartAnimation.toggleLike(heartOutline,heartRed);
                mLikedByCurrentUser = false;
                firebaseMethods.removeNewLike(getString(R.string.photos_node),photo.getPhoto_id(),likeId);
            }
        }

        else {
            if(!mLikedByCurrentUser){

                heartOutline.setVisibility(View.VISIBLE);
                heartRed.setVisibility(View.GONE);
                heartAnimation.toggleLike(heartOutline,heartRed);
                firebaseMethods.addNewLike(getString(R.string.videos_node),video.getVideo_id());
            }else {
                heartOutline.setVisibility(View.GONE);
                heartRed.setVisibility(View.VISIBLE);
                heartAnimation.toggleLike(heartOutline,heartRed);
                mLikedByCurrentUser = false;
                firebaseMethods.removeNewLike(getString(R.string.videos_node),video.getVideo_id(),likeId);
            }
        }

    }


    private void setUserLikes(DataSnapshot dataSnapshot){

        mStringBuilder = new StringBuilder();
        Query query = myRef.child(getString(R.string.users_node)).orderByChild(getString(R.string.users_id))
                .equalTo(dataSnapshot.child(getString(R.string.users_id)).getValue().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mStringBuilder.append(Objects.requireNonNull(singleSnapshot.getValue(User.class)).getUsername());
                    mStringBuilder.append(",");
                }
                String[] splitUsers = mStringBuilder.toString().split(",");

                int length = splitUsers.length;
                if(length == 1){
                    mLikesString = "Liked by " + splitUsers[0];
                }
                else if(length == 2){
                    mLikesString = "Liked by " + splitUsers[0]
                            + " and " + splitUsers[1];
                }
                else if(length == 3){
                    mLikesString = "Liked by " + splitUsers[0]
                            + ", " + splitUsers[1]
                            + " and " + splitUsers[2];

                }
                else if(length == 4){
                    mLikesString = "Liked by " + splitUsers[0]
                            + ", " + splitUsers[1]
                            + ", " + splitUsers[2]
                            + " and " + splitUsers[3];
                }
                else if(length > 4){
                    mLikesString = "Liked by " + splitUsers[0]
                            + ", " + splitUsers[1]
                            + ", " + splitUsers[2]
                            + " and " + (dataSnapshot.getChildrenCount() - 3) + " others";
                }
                likedBy.setText(mLikesString);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


   private void likesSetup(final String mediaNode, final String mediaId){

       firebaseMethods = new FirebaseMethods(ViewPostActivity.this);
       Query query = myRef.child(mediaNode).child(mediaId)
               .child(getString(R.string.fieldLikes));
       query.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                   setUserLikes(singleSnapshot);
                   if(singleSnapshot.child(getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())){
                       Log.d(TAG,"likeId :"+singleSnapshot.getKey());
                       likeId = singleSnapshot.getKey();
                       heartRed.setVisibility(View.VISIBLE);
                       heartOutline.setVisibility(View.GONE);
                       mLikedByCurrentUser = true;
                   }
               }

               if(!dataSnapshot.exists()){
                   mLikesString = "";
                   mLikedByCurrentUser = false;
                   heartRed.setVisibility(View.GONE);
                   heartOutline.setVisibility(View.VISIBLE);
                   likedBy.setText("");
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }

       });

   }


    class GestureListener extends android.view.GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {

            return true ;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            toggleLike();
            return true;
        }

    }












}
