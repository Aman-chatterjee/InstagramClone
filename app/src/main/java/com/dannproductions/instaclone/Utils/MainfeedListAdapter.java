package com.dannproductions.instaclone.Utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.dannproductions.instaclone.DataModels.Photo;
import com.dannproductions.instaclone.DataModels.User;
import com.dannproductions.instaclone.DataModels.UserAccountSettings;
import com.dannproductions.instaclone.DataModels.Video;
import com.dannproductions.instaclone.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.util.List;
import java.util.Objects;


public class MainfeedListAdapter extends ArrayAdapter<Object> {
    private int mResource;
    private Activity mContext;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private String profileImgUrl = "";
    private HeartAnimation heartAnimation;
    private boolean mLikedByCurrentUser = false;
    private String likeId;
    private String mLikesString;
    private FirebaseMethods firebaseMethods;
    private StringBuilder mStringBuilder;
    private Photo photo;
    private Video video;

    public MainfeedListAdapter(@NonNull Activity context, int resource, @NonNull List<Object> list) {
        super(context, resource, list);
        mResource = resource;
        mContext = context;
        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(mContext);
        heartAnimation = new HeartAnimation();
    }


    private static class ViewHolder{
        ImageView post;
        ProgressBar progressBar;
        TextView likedBy;
        CircularImageView profileImage;
        TextView userName;
        ImageView vidInd;
        ImageView heartRed;
        ImageView heartOutline;
        TextView caption;
        TextView date;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            holder = new ViewHolder();

            holder.post = convertView.findViewById(R.id.media_post);
            holder.progressBar = convertView.findViewById(R.id.progressBar);
            holder.likedBy = convertView.findViewById(R.id.like_number);
            holder.profileImage = convertView.findViewById(R.id.profile_photo);
            holder.userName = convertView.findViewById(R.id.user_name);
            holder.vidInd = convertView.findViewById(R.id.video_indicater);
            holder.heartRed = convertView.findViewById(R.id.heart_red);
            holder.heartOutline = convertView.findViewById(R.id.heart_outline);
            holder.caption = convertView.findViewById(R.id.caption_text);
            holder.date = convertView.findViewById(R.id.date_added);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder)convertView.getTag();
        }


        Object object = getItem(position);
        setLikeListeners(holder.heartOutline,holder.heartRed,object,holder.likedBy);

        if(Objects.requireNonNull(object).getClass()==Photo.class){
            photo = (Photo)object;
            setUserLikes(holder.heartOutline,holder.heartRed,mContext.getString(R.string.photos_node),photo.getPhoto_id(),holder.likedBy);
            holder.vidInd.setVisibility(View.GONE);
            setProfileInfo(photo.getUser_id(),holder.profileImage, holder.userName);
            launchComment(mContext.getString(R.string.photos_node),photo.getPhoto_id(),convertView);
            GlideImageLoader.loadImageWithTransition(mContext,photo.getImageUrl(),holder.post,holder.progressBar);
            holder.caption.setText(photo.getCaption());
            holder.date.setText(photo.getDate_added());
        }
        else if(Objects.requireNonNull(object).getClass()==Video.class){
            video = (Video)object;
            setUserLikes(holder.heartOutline,holder.heartRed,mContext.getString(R.string.videos_node),video.getVideo_id(),holder.likedBy);
            holder.vidInd.setVisibility(View.VISIBLE);
            setProfileInfo(video.getUser_id(), holder.profileImage, holder.userName);
            launchComment(mContext.getString(R.string.videos_node),video.getVideo_id(),convertView);
            GlideImageLoader.loadImageWithTransition(mContext,video.getVideoUrl(),holder.post,holder.progressBar);
            holder.caption.setText(video.getCaption());
            holder.date.setText(video.getDate_added());
        }
        return convertView;
    }


    private void setProfileInfo(final String userId, final CircularImageView profileImage, final TextView userName) {

        Query query = reference.child(mContext.getString(R.string.user_account_settings_node))
                .orderByKey().equalTo(Objects.requireNonNull(photo).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (userId.equals(mAuth.getCurrentUser().getUid())) {
                        profileImgUrl = Objects.requireNonNull(ds.getValue(UserAccountSettings.class)).getProfile_photo();
                    }
                    GlideImageLoader.loadImageWithOutTransition(mContext, ds.getValue(UserAccountSettings.class).getProfile_photo(), profileImage);
                    userName.setText(Objects.requireNonNull(ds.getValue(UserAccountSettings.class)).getUsername());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void launchComment(String mediaNode,String mediaId,View view) {

        ImageView comment = view.findViewById(R.id.comment);
        TextView viewComments = view.findViewById(R.id.view_comments);
        final Intent mediaIntent = new Intent(mContext, CommentsActivity.class);
        mediaIntent.putExtra("mediaID", mediaId);
        mediaIntent.putExtra("mediaNode", mediaNode);
        mediaIntent.putExtra(mContext.getString(R.string.profilePhotoField), profileImgUrl);

        viewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(mediaIntent);
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(mediaIntent);
            }
        });
    }


    private void setLikeListeners(final ImageView heartOutline, final ImageView heartRed, final Object object, final TextView likedBy){

        heartOutline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               toggleLike(heartOutline,heartRed,object,likedBy);
            }
        });
        heartRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               toggleLike(heartOutline,heartRed,object,likedBy);
            }
        });
    }


    private void setUserLikes(final ImageView heartOutline, final ImageView heartRed,String mediaNode, String mediaId,final TextView likedBy){

            Query query = reference.child(mediaNode).child(mediaId)
                    .child(mContext.getString(R.string.fieldLikes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.exists()){
                        heartOutline.setVisibility(View.VISIBLE);
                        heartRed.setVisibility(View.GONE);
                        likedBy.setText("");
                    }else {
                            heartRed.setVisibility(View.GONE);
                            heartOutline.setVisibility(View.VISIBLE);
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                if (ds.child(mContext.getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                                    heartOutline.setVisibility(View.GONE);
                                    heartRed.setVisibility(View.VISIBLE);
                                }
                                setLikeText(ds,likedBy);
                            }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
    }


    private void toggleLike(final ImageView heartOutline, final ImageView heartRed, Object object, final TextView likedBy) {

        mLikedByCurrentUser  = false;
        if (Objects.requireNonNull(object).getClass()==Photo.class) {
            photo = (Photo)object;

            Query query = reference.child(mContext.getString(R.string.photos_node)).child(photo.getPhoto_id()).child(mContext.getString(R.string.fieldLikes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.exists()){
                        mLikesString = "";
                        mLikedByCurrentUser = false;
                        heartRed.setVisibility(View.GONE);
                        heartOutline.setVisibility(View.VISIBLE);
                    }else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            if (ds.child(mContext.getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                                mLikedByCurrentUser = true;
                                likeId = ds.getKey();
                                Log.d("TAG", "LikeID = " + likedBy);
                                heartOutline.setVisibility(View.GONE);
                                heartRed.setVisibility(View.VISIBLE);
                                heartAnimation.toggleLike(heartOutline, heartRed);
                                firebaseMethods.removeNewLike(mContext.getString(R.string.photos_node), photo.getPhoto_id(), likeId);
                                setUserLikes(heartOutline,heartRed,mContext.getString(R.string.photos_node),photo.getPhoto_id(),likedBy);
                            }
                        }
                    }

                        if(!mLikedByCurrentUser){
                            Log.d("TAG","Datasnapshot doesn't exists");
                            heartOutline.setVisibility(View.VISIBLE);
                            heartRed.setVisibility(View.GONE);
                            heartAnimation.toggleLike(heartOutline, heartRed);
                            firebaseMethods.addNewLike(mContext.getString(R.string.photos_node),photo.getPhoto_id());
                            setUserLikes(heartOutline,heartRed,mContext.getString(R.string.photos_node),photo.getPhoto_id(),likedBy);
                        }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

        } else {
            video = (Video)object;

            Query query = reference.child(mContext.getString(R.string.videos_node)).child(video.getVideo_id())
                    .child(mContext.getString(R.string.fieldLikes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.exists()){
                        mLikedByCurrentUser = false;
                        mLikesString = "";
                        heartRed.setVisibility(View.GONE);
                        heartOutline.setVisibility(View.VISIBLE);
                    }else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.child(mContext.getString(R.string.users_id)).getValue().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                            mLikedByCurrentUser = true;
                            likeId = ds.getKey();
                            Log.d("TAG", "LikeID = " + likedBy);
                            heartOutline.setVisibility(View.GONE);
                            heartRed.setVisibility(View.VISIBLE);
                            heartAnimation.toggleLike(heartOutline, heartRed);
                            firebaseMethods.removeNewLike(mContext.getString(R.string.videos_node), video.getVideo_id(), likeId);
                            setUserLikes(heartOutline,heartRed,mContext.getString(R.string.videos_node),video.getVideo_id(),likedBy);
                        }
                    }
                }
                    if(!mLikedByCurrentUser){
                        heartOutline.setVisibility(View.VISIBLE);
                        heartRed.setVisibility(View.GONE);
                        heartAnimation.toggleLike(heartOutline, heartRed);
                        firebaseMethods.addNewLike(mContext.getString(R.string.videos_node),video.getVideo_id());
                        setUserLikes(heartOutline,heartRed,mContext.getString(R.string.videos_node),video.getVideo_id(),likedBy);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }





    private void setLikeText(DataSnapshot dataSnapshot, final TextView likedBy){

        mStringBuilder = new StringBuilder();
        Query query = reference.child(mContext.getString(R.string.users_node)).orderByChild(mContext.getString(R.string.users_id))
                .equalTo(dataSnapshot.child(mContext.getString(R.string.users_id)).getValue().toString());
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



}
