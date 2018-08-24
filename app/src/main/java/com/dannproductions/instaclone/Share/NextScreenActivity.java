package com.dannproductions.instaclone.Share;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Utils.FirebaseMethods;
import com.dannproductions.instaclone.Utils.GlideImageLoader;
import com.dannproductions.instaclone.Utils.MediaFilesScanner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class NextScreenActivity extends AppCompatActivity {

    //Variables
    Activity mActivity = NextScreenActivity.this;
    private static final String TAG = "NEXT_SCREEN_ACTIVITY";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String path;
    private long image_count,video_count;

    //Widgets
    private ImageView previewImage,speak_now;
    private ProgressBar progressBar;
    TextView sharePost,uploadText;
    private EditText caption;

    //Fiirebase
    private String userID;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_screen);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        previewImage = (ImageView)findViewById(R.id.preview_image);
        sharePost =(TextView) findViewById(R.id.share_post);
        uploadText = (TextView)findViewById(R.id.upload_text);
        uploadText.setVisibility(View.GONE);
        speak_now = (ImageView)findViewById(R.id.speak_now);
        progressBar = (ProgressBar)findViewById(R.id.upload_progress);
        progressBar.setVisibility(View.GONE);
        caption = (EditText)findViewById(R.id.caption_text);

        //Getting file path and setting the image
        Intent intent = getIntent();
        path = intent.getStringExtra(getString(R.string.filePath));
        GlideImageLoader.loadImageWithOutTransition(getApplicationContext(),path,previewImage);

        promptSpeechInput();
        sharePost();
        goBack();
    }


    //-------------------------------------------------------Firebase stuffs-------------------------------------------------------------

    private void setupFirebaseUploads(){
        final FirebaseMethods firebaseMethods = new FirebaseMethods(mActivity);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(MediaFilesScanner.isVideo(path)){
                    if(checkVideoDuration()) {
                        progressBar.setVisibility(View.VISIBLE);
                        uploadText.setVisibility(View.VISIBLE);
                        video_count = firebaseMethods.getVideoCount(dataSnapshot);
                        firebaseMethods.uploadNewVideo(caption.getText().toString(), video_count, path, progressBar, uploadText);
                    }else {
                        Toast.makeText(mActivity, "The video length should exceed the 30 seconds!!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    uploadText.setVisibility(View.VISIBLE);
                    image_count = firebaseMethods.getImageCount(dataSnapshot);
                    firebaseMethods.uploadNewPhoto(caption.getText().toString(), image_count, path,progressBar,uploadText, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    //Setting up speech recognition
    private void promptSpeechInput() {

        speak_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        getString(R.string.speech_prompt));
                intent.putExtra("android.speech.extra.DICTATION_MODE",true);
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(mActivity, "speech_not_supported", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private boolean checkVideoDuration(){

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        String time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInSec = Long.parseLong(time)/1000;
        return timeInSec <=30;
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String existingText = caption.getText().toString()+" ";
                    caption.setText(String.valueOf(existingText+result.get(0)));
                }
                break;
            }
        }
    }

    private void sharePost(){

        sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    setupFirebaseUploads();
            }
        });

    }



    private void goBack(){
        ImageView back_view = (ImageView) findViewById(R.id.share_back);
        back_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }




}
