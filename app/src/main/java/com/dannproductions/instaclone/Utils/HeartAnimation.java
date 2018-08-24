package com.dannproductions.instaclone.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class HeartAnimation {
    private static final String TAG = "HeartAnimation";
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();


    public void toggleLike(ImageView heartOutline,ImageView heartRed){
        Log.d(TAG,"Toggling Like");

        AnimatorSet animatorSet = new AnimatorSet();
        if(heartRed.getVisibility()== View.VISIBLE){
            Log.d(TAG,"Toggling red heart off");


            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartRed,"scaleY",1f,0.1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartRed,"scaleX",1f,0.1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            heartRed.setVisibility(View.GONE);
            heartOutline.setVisibility(View.VISIBLE);
            animatorSet.playTogether(scaleDownY,scaleDownX);

        }else  if(heartRed.getVisibility()== View.GONE){
            Log.d(TAG,"Toggling red heart On");

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartRed,"scaleY",0.1f,1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartRed,"scaleX",0.1f,1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            heartRed.setVisibility(View.VISIBLE);
            heartOutline.setVisibility(View.GONE);
            animatorSet.playTogether(scaleDownY,scaleDownX);
        }

        animatorSet.start();

    }




}
