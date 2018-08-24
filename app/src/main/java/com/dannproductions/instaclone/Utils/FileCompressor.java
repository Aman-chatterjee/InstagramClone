package com.dannproductions.instaclone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;


public class FileCompressor {
    private Context mContext;
//    private final String IMAGE_DESTINATION_FILE = Environment.getExternalStorageDirectory().getPath()+"/InstagramClone/Images";
//    private final String VIDEO_DESTINATION_PATH = Environment.getExternalStorageDirectory().getPath()+"/InstagramClone/Videos";

    FileCompressor(Context mContext) {
        this.mContext = mContext;
    }

    public File compressImage(String imageUrl){

        File imageFile = new File(imageUrl);
        try {
            return new Compressor(mContext).compressToFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return new File(imageUrl);
        }
    }



}
