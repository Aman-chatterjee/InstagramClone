package com.dannproductions.instaclone.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Share.NextScreenActivity;
import com.dannproductions.instaclone.Utils.FirebaseMethods;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CameraFragment extends Fragment {
    private final int requestCode = 5;
    String TAG = "CameraFragment";
    final String outputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera";
    File imageFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_camera,container,false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageView camera = (ImageView) Objects.requireNonNull(getView()).findViewById(R.id.launch_camera);
        ImageView close = (ImageView)getView().findViewById(R.id.photo_close);
        //Ignoring FileUri expose exception
        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateAdded = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                imageFile = new File(outputDirectory,"insta_cam_"+dateAdded+".jpg");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(cameraIntent, requestCode);
                Log.d(TAG,"CameraIntent launched");
            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(this.requestCode==requestCode){

            Log.d(TAG,imageFile.getAbsolutePath());

            if(imageFile.exists()) {

                if(Objects.requireNonNull(getActivity()).getIntent().getBooleanExtra(getString(R.string.UploadProfilePhoto),false)) {
                    ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle(R.string.updatingProfile);
                    progressDialog.setMessage(getString(R.string.Wait));
                    progressDialog.show();
                    new FirebaseMethods(getActivity()).uploadNewPhoto(null,0,imageFile.getAbsolutePath(),null,null,true);
                }else {

                    Intent shareIntent = new Intent(getActivity(), NextScreenActivity.class);
                    shareIntent.putExtra(getString(R.string.filePath), imageFile.getAbsolutePath());
                    startActivity(shareIntent);
                }
            }else {
                Toast.makeText(getContext(), "Failed to save photo!!", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
