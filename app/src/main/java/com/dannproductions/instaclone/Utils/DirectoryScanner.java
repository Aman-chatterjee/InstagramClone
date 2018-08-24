package com.dannproductions.instaclone.Utils;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;

public class DirectoryScanner extends AsyncTask<String,Void,Object>{
    private static ArrayList<String> fileDirectories = new ArrayList<String>();
    private static ArrayList<String> directoryNames = new ArrayList<String>();
    private MediaFilesScanner mfs = new MediaFilesScanner(null);


    //Loading directories containing video and image files
    private void loadFileDirectory(final String directoryPath){
        try {
            File directory = new File(directoryPath);
            File[] fList = directory.listFiles();
            for (File file : fList) {
                if (file.isFile() && mfs.isPhotoOrVideo(file.getName())) {

                    String parentDirectory = file.getParent();
                    if (!fileDirectories.contains(parentDirectory)) {
                        if (parentDirectory.contains("Camera")) {
                            fileDirectories.add(0, parentDirectory);
                            directoryNames.add(0, parentDirectory.substring((parentDirectory.lastIndexOf("/") + 1), parentDirectory.length()));
                        } else {
                            //Log.d("filePath", "path : " + file.getParent());
                            fileDirectories.add(parentDirectory);
                            directoryNames.add(parentDirectory.substring((parentDirectory.lastIndexOf("/") + 1), parentDirectory.length()));
                        }
                    }
                } else if (file.isDirectory() && !(file.getName().startsWith(".")) && !(file.getName().startsWith("Android"))) {
                    loadFileDirectory(file.getAbsolutePath());
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    //Returning directory paths
    public static ArrayList<String> getFileDirectories() {
        return fileDirectories;
    }

    //Returning directory names
    public static ArrayList<String> getDirectoryNames() {
        return directoryNames;
    }


    @Override
    protected Object doInBackground(String[] dir) {
        loadFileDirectory(dir[0]);
        return null;
    }
}
