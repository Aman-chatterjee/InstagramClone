package com.dannproductions.instaclone.Utils;

import android.os.AsyncTask;
import android.support.v4.content.MimeTypeFilter;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class MediaFilesScanner extends AsyncTask<String,Void,ArrayList> {

    private ProgressBar progressBar;
    private ArrayList<String> filePaths = new ArrayList<String>();

    public MediaFilesScanner(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    protected ArrayList<String> doInBackground(String[] directoryName) {

        File directory = new File(directoryName[0]);
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile() && isPhotoOrVideo(file.getName())) {
                //Log.d("filePath","path : "+file.getAbsolutePath());
                filePaths.add(0,file.getAbsolutePath());
            }
        }
        return filePaths;
    }

    @Override
    protected void onPostExecute(ArrayList arrayList) {
        super.onPostExecute(arrayList);
        if(progressBar!=null)
        progressBar.setVisibility(View.GONE);
    }

    //Checking file extension
    public boolean isPhotoOrVideo(String fileName) {

        return fileName.endsWith(".jpg") ||  fileName.endsWith(".png") ||
                fileName.endsWith(".gif") || fileName.endsWith(".mp4") ||
                fileName.endsWith(".3gp") || fileName.endsWith(".mkv")
                ||fileName.endsWith("bmp");
    }

    public static boolean isVideo(String fileName) {

        return  fileName.endsWith(".mp4") || fileName.endsWith(".3gp") || fileName.endsWith(".mkv");
    }



}