package com.dannproductions.instaclone.Utils;

import android.util.Log;

import java.util.ArrayList;

public class StringManipulation {

    public static String expandUserName(String name){
        return name.replace("."," ");
    }

    public static String condenseUserName(String name){
        return name.replace(" ",".");
    }

//    public static String getTags(String caption){
//
//        StringBuilder tags = new StringBuilder();
//        if(caption!=null&&caption.length()!=0&&caption.contains("#")) {
//            int startIndex = caption.indexOf('#');
//            while (startIndex != caption.length()) {
//
//                for (int i = startIndex; i < caption.length(); i++) {
//                    char c = caption.charAt(i);
//                    if (i != startIndex && (c == '#' || c == ' ' || c == '\n')) {
//                        if (caption.charAt(startIndex) == '#') {
//                          tags.append(caption.substring(startIndex, i));
//                        }
//                        startIndex = i;
//                        break;
//                    }
//                }
//            }
//        }
//        return tags.toString();
//    }

    public static String getTags(String string){
        if(string.indexOf("#") > 0){
            StringBuilder sb = new StringBuilder();
            char[] charArray = string.toCharArray();
            boolean foundWord = false;
            for( char c : charArray){
                if(c == '#'){
                    foundWord = true;
                    sb.append(c);
                }else{
                    if(foundWord){
                        sb.append(c);
                    }
                }
                if(c == ' ' ||c=='\n'){
                    foundWord = false;
                }
            }
            String s = sb.toString().replace(" ", "").replace("#", ",#");
            return s.substring(1, s.length());
        }
        return string;
    }



}
