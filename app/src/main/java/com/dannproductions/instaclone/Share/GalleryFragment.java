package com.dannproductions.instaclone.Share;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Utils.DirectoryScanner;
import com.dannproductions.instaclone.Utils.FirebaseMethods;
import com.dannproductions.instaclone.Utils.GlideImageLoader;
import com.dannproductions.instaclone.Utils.MediaFilesScanner;
import com.dannproductions.instaclone.Utils.GridViewAdapter;
import com.dannproductions.instaclone.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class GalleryFragment extends Fragment {


    //Widgets
    TextView nextScreen;
    ProgressBar progressBar;
    GridView gridView;
    ImageView camImage,close_gallery;
    ProgressDialog progressDialog;
    VideoView videoView;
    RelativeLayout rl;
    Spinner dirSpinner;

    //Variables
    private static final int NUM_GRID_COLUMNS = 4;
    String mPath;
    ArrayList<String> listPaths,dirPaths,directoryNames;
    GestureDetector gestureDetector;
    final String append = "file:/";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery,container,false);

        gridView = view.findViewById(R.id.galleryGrid);
        nextScreen = view.findViewById(R.id.gallery_next);
        progressBar = view.findViewById(R.id.galleryProgress);
        //progressBar.setVisibility(View.GONE);
        camImage = view.findViewById(R.id.camImage);
        videoView = view.findViewById(R.id.videoView);
        close_gallery = view.findViewById(R.id.gallery_close);
        dirSpinner = view.findViewById(R.id.spinnerGallery);
        rl = view.findViewById(R.id.gallery_rl);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupSpinner();
        hideImageView();

        //closing gallery
        close_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });


        //moving to next screen
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Objects.requireNonNull(getActivity()).getIntent().getBooleanExtra(getString(R.string.UploadProfilePhoto),false)) {

                    if(!MediaFilesScanner.isVideo(mPath)) {

                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setTitle(getString(R.string.updatingProfile));
                        progressDialog.setMessage(getString(R.string.Wait));
                        progressDialog.show();
                        new FirebaseMethods(getActivity()).uploadNewPhoto(null, 0, mPath,null,null,true);
                    }else {
                        Toast.makeText(getContext(), "Videos are not allowed as profile!!", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Intent nextIntent = new Intent(getActivity(),NextScreenActivity.class);
                    nextIntent.putExtra(getString(R.string.filePath), mPath);
                    startActivity(nextIntent);
                }
            }
        });


    }




    private void setupSpinner(){

            dirPaths = DirectoryScanner.getFileDirectories();
            directoryNames = DirectoryScanner.getDirectoryNames();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),android.R.layout.simple_list_item_1,directoryNames);
        //arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dirSpinner.setAdapter(arrayAdapter);

        dirSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(listPaths !=null){ listPaths.clear();}

                //setting up gridView for selected directory
                setupGridView(dirPaths.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




    private void setupGridView(final String selectedDirectory){

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);


                try {
                    listPaths = new MediaFilesScanner(progressBar).execute(selectedDirectory).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }


        GridViewAdapter adapter = new GridViewAdapter(Objects.requireNonNull(getActivity()),R.layout.layout_grid_image_view,append, listPaths);
        gridView.setAdapter(adapter);

        if(listPaths !=null) {

            final int  position = 0;
            mPath = listPaths.get(position);
            if(MediaFilesScanner.isVideo(mPath)) { playVideo(mPath); }
            else { displayImage(mPath); }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mPath = listPaths.get(position);
                if(MediaFilesScanner.isVideo(mPath)) { playVideo(mPath); }
                else { displayImage(mPath); }
            }
        });
    }




    private void playVideo(String path){

        camImage.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(path);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });
    }


    private  void displayImage(String path){

        videoView.setVisibility(View.GONE);
        camImage.setVisibility(View.VISIBLE);
        //GlideImageLoader.loadImageWithTransition(getContext(), path,camImage);
        UniversalImageLoader.setImage(path, camImage, progressBar, append);
    }



    @SuppressLint("ClickableViewAccessibility")
    private void hideImageView(){

        gestureDetector = new GestureDetector(getContext(),new com.dannproductions.instaclone.Utils.GestureDetector(rl,gridView));

        rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return gestureDetector.onTouchEvent(event);
            }
        });
    }




}
