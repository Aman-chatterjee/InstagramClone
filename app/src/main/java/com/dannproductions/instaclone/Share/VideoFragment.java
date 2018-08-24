package com.dannproductions.instaclone.Share;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dannproductions.instaclone.R;

import java.util.Objects;

public class VideoFragment extends Fragment {
    private final int requestCode = 6;
    String TAG = "Video Fragment";
    final String outputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera";
    String videoUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video,container,false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageView camcorder = (ImageView) Objects.requireNonNull(getView()).findViewById(R.id.launch_camcorder);
        ImageView close = (ImageView)getView().findViewById(R.id.video_close);


        camcorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,30); //Recording duration to 30sec
                startActivityForResult(cameraIntent, requestCode);
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (this.requestCode == requestCode && data != null) {

            try {
                videoUri = getRealPathFromUri(getContext(), data.getData());
                if (videoUri != null) {
                    if (Objects.requireNonNull(getActivity()).getIntent().getBooleanExtra(getString(R.string.UploadProfilePhoto), false)) {
                        Toast.makeText(getContext(), "Videos are not allowed as profile!!", Toast.LENGTH_LONG).show();

                    } else {
                        Intent nextIntent = new Intent(getActivity(), NextScreenActivity.class);
                        nextIntent.putExtra(getString(R.string.filePath), videoUri);
                        startActivity(nextIntent);
                    }
                }
            }catch (RuntimeException e){
                e.printStackTrace();
            }
        }
    }


    public  String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = Objects.requireNonNull(cursor).getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



}
