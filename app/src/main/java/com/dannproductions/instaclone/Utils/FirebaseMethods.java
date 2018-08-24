package com.dannproductions.instaclone.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dannproductions.instaclone.DataModels.Comment;
import com.dannproductions.instaclone.DataModels.MasterUserSettings;
import com.dannproductions.instaclone.DataModels.Photo;
import com.dannproductions.instaclone.DataModels.User;
import com.dannproductions.instaclone.DataModels.UserAccountSettings;
import com.dannproductions.instaclone.DataModels.Video;
import com.dannproductions.instaclone.Home.HomeActivity;
import com.dannproductions.instaclone.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;


public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";
    private Activity mActivity;
    private String userID;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private long mediaCount = 0;

    public FirebaseMethods(Activity activity) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        myRef = database.getReference();
        mStorageRef = mStorage.getReference();
        mActivity = activity;
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }


//    private boolean checkIfUsernameExists(String userName, DataSnapshot dataSnapshot){
//        //Checking if user_name already exists.
//        User user = new User();
//        Log.d(TAG,"Username checking");
//        for(DataSnapshot ds: dataSnapshot.child(mActivity.getString(R.string.users_node)).getChildren()){
//
//            user.setUsername(Objects.requireNonNull(ds.getValue(User.class)).getUsername());
//            Log.d(TAG,"Username entered :"+userName+", Username fetched :"+user.getUsername());
//            if(StringManipulation.expandUserName(user.getUsername()).equals(userName.toLowerCase())){
//                Log.d(TAG,"Username exists");
//                return true;
//            }
//        }
//        return false;
//    }


    public void signUp(final String email, String password, final String userName, final ProgressBar progressBar){

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                final FirebaseUser user = mAuth.getCurrentUser();
                                userID = Objects.requireNonNull(user).getUid();

                                Query query = myRef.child(mActivity.getString(R.string.users_node)).orderByChild(mActivity.getString(R.string.usernameField)).equalTo(StringManipulation.condenseUserName(userName));
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(!dataSnapshot.exists()) {
                                            addNewUserData(email, userName, userName, "Your bio", "Your website", 0, "");
                                        } else {
                                            String name = userName + ".";
                                            name += Objects.requireNonNull(myRef.push().getKey()).substring(3, 10);
                                            addNewUserData(email, name, userName, "Your bio", "Your website", 0, "");
                                            Toast.makeText(mActivity, "Username already exists!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(mActivity, "Authentication Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }


    private void sendVerificationEmail(final Boolean isRequired){

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG,"Verification code sent");
                        Toast.makeText(mActivity, "Verification email sent, please check your inbox", Toast.LENGTH_SHORT).show();

                        if(isRequired) {
                            mAuth.signOut();
                            mActivity.finish();
                        }
                    }else {
                        Toast.makeText(mActivity, "Couldn't send verification email !!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public void reAuthenticateUser(final String newEmail, String password){

        try {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(mAuth.getCurrentUser().getEmail(), password);

            // Prompt the user to re-provide their sign-in credentials
            Objects.requireNonNull(mAuth.getCurrentUser()).reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.d(TAG, "User re-authenticated.");
                                sendVerificationEmail(false);
                                updateEmail(newEmail);
                            }else {
                                Log.d(TAG, "User re-authentication failed.");
                                Toast.makeText(mActivity, "Failed!! Your password doesn't match!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    private void updateEmail(final String newEmail){

        FirebaseUser user = mAuth.getCurrentUser();

        Objects.requireNonNull(user).updateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            myRef.child(mActivity.getString(R.string.users_node))
                                    .child(userID)
                                    .child(mActivity.getString(R.string.emailField)).setValue(newEmail);
                            Toast.makeText(mActivity, "Email updated", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "User email address updated.");
                        }else {
                            Toast.makeText(mActivity, "Failed!!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    public void updateUsername(String username){

        //In users node
        myRef.child(mActivity.getString(R.string.users_node))
                .child(userID)
                .child(mActivity.getString(R.string.usernameField)).setValue(username);

        //In user_account_settings node
        myRef.child(mActivity.getString(R.string.user_account_settings_node))
                .child(userID)
                .child(mActivity.getString(R.string.usernameField)).setValue(username);
    }

    public void updateDisplayname(String displayName){

        //In user_account_settings node
        myRef.child(mActivity.getString(R.string.user_account_settings_node))
                .child(userID)
                .child(mActivity.getString(R.string.displayNameField)).setValue(displayName);
    }

    public void updateDescription(String description){

        //In user_account_settings node
        myRef.child(mActivity.getString(R.string.user_account_settings_node))
                .child(userID)
                .child(mActivity.getString(R.string.descriptionField)).setValue(description);
    }

    public void updateWebsite(String website){

        //In user_account_settings node
        myRef.child(mActivity.getString(R.string.user_account_settings_node))
                .child(userID)
                .child(mActivity.getString(R.string.websiteField)).setValue(website);
    }

    private void updateProfilePhoto(String photoUri){

        //In user_account_settings node
        myRef.child(mActivity.getString(R.string.user_account_settings_node))
                .child(userID)
                .child(mActivity.getString(R.string.profilePhotoField)).setValue(photoUri);
    }


    public void updatePhoneNo(long phoneNo){

        //In users node
        myRef.child(mActivity.getString(R.string.users_node))
                .child(userID)
                .child(mActivity.getString(R.string.phoneNoField)).setValue(phoneNo);
    }

    public void addFollowingAndFollowers(String uid){

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.node_following))
                .child(userID)
                .child(uid)
                .child(mActivity.getString(R.string.users_id))
                .setValue(uid);

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.node_followers))
                .child(uid)
                .child(userID)
                .child(mActivity.getString(R.string.users_id))
                .setValue(userID);
    }


    public void removeFollowingAndFollowers(String uid){

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.node_following))
                .child(userID)
                .child(uid)
                .removeValue();

        FirebaseDatabase.getInstance().getReference()
                .child(mActivity.getString(R.string.node_followers))
                .child(uid)
                .child(userID)
                .removeValue();
    }



    public MasterUserSettings retrieveUserData(DataSnapshot dataSnapshot,String userID,boolean needAccountSettings, boolean needUser){

        User user = new User();
        UserAccountSettings settings = new UserAccountSettings();

        for (DataSnapshot ds: dataSnapshot.getChildren()) {

            try {
                //Getting data from the UserAccountSettings node
                if(needAccountSettings&&Objects.requireNonNull(ds.getKey()).equals(mActivity.getString(R.string.user_account_settings_node))) {

                    settings.setUsername(Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getUsername());
                    settings.setDisplay_name(Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getDisplay_name());
                    settings.setDscription(Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getDescription());
                    settings.setProfile_photo(Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getProfile_photo());
                    settings.setWebsite(Objects.requireNonNull(ds.child(userID).getValue(UserAccountSettings.class)).getWebsite());
                }
                //Getting data from the Users node
                if(needUser&&Objects.requireNonNull(ds.getKey()).equals(mActivity.getString(R.string.users_node))){

                    user.setUsername(Objects.requireNonNull(ds.child(userID).getValue(User.class)).getUsername());
                    user.setEmail(Objects.requireNonNull(ds.child(userID).getValue(User.class)).getEmail());
                    user.setPhone_number(Objects.requireNonNull(ds.child(userID).getValue(User.class)).getPhone_number());
                    user.setUser_id(Objects.requireNonNull(ds.child(userID).getValue(User.class)).getUser_id());
                }
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        return new MasterUserSettings(user,settings);
    }


    public void uploadNewPhoto(final String caption, long count, String imageUrl, final ProgressBar progressBar, final TextView uploadProgress, boolean isProfilePhoto){

        final String FIREBASE_IMAGE_STORAGE = "photos/users/";
        FileCompressor compressor = new FileCompressor(mActivity);
        final StorageReference storageReference;

        //If it is not a profile photo
        if(!isProfilePhoto){
            storageReference =  mStorageRef.child(FIREBASE_IMAGE_STORAGE+userID+"/photo"+(count+1));
            final UploadTask uploadTask = storageReference.putFile(Uri.fromFile(compressor.compressImage(imageUrl)));

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        addPhotoToDatabase(caption,downloadUri.toString());
                        progressBar.setVisibility(View.GONE);
                        uploadProgress.setVisibility(View.GONE);
                        mActivity.finish();
                        mActivity.startActivity(new Intent(mActivity, HomeActivity.class));
                        Toast.makeText(mActivity, "Photo uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        uploadProgress.setVisibility(View.GONE);
                        Toast.makeText(mActivity, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //Tracking progress
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    long uploadPercentage = (taskSnapshot.getBytesTransferred()*100)/taskSnapshot.getTotalByteCount();
                    uploadProgress.setText(String.valueOf("Uploading "+uploadPercentage+"%"));
                }
            });
        //If it is a profile photo
        }else {
            storageReference =  mStorageRef.child(FIREBASE_IMAGE_STORAGE+userID+"/profile_photo");
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(compressor.compressImage(imageUrl)));

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        updateProfilePhoto(downloadUri.toString());
                        mActivity.finish();
                        Toast.makeText(mActivity, "Photo uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mActivity, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }





    public void uploadNewVideo(final String caption, final long count, final String videoUrl, final ProgressBar progressBar, final TextView uploadProgress){

        final String FIREBASE_VIDEO_STORAGE = "videos/users/";
        final StorageReference storageReference;

            storageReference =  mStorageRef.child(FIREBASE_VIDEO_STORAGE+userID+"/video"+(count+1));
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(new File(videoUrl)));

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        addVideoToDatabase(caption,downloadUri.toString());
                        progressBar.setVisibility(View.GONE);
                        uploadProgress.setVisibility(View.GONE);
                        mActivity.finish();
                        Toast.makeText(mActivity, "Video uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        uploadProgress.setVisibility(View.GONE);
                        Toast.makeText(mActivity, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //Tracking progress
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    long uploadPercentage = (taskSnapshot.getBytesTransferred()*100)/taskSnapshot.getTotalByteCount();
                    uploadProgress.setText(String.valueOf("Uploading "+uploadPercentage+"%"));
                }
            });
    }

    public void setFollowingCount(String uid, final TextView followingCount){

        Query query = myRef.child(mActivity.getString(R.string.node_following))
                .child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0) {
                    followingCount.setText(String.valueOf(dataSnapshot.getChildrenCount() - 1));
                }else {
                    followingCount.setText(String.valueOf(0));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setFollowersCount(String uid, final TextView followerCount){

        Query query = myRef.child(mActivity.getString(R.string.node_followers))
                .child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0) {
                    followerCount.setText(String.valueOf(dataSnapshot.getChildrenCount() - 1));
                }else {
                    followerCount.setText(String.valueOf(0));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setPostCount(final String uid, final TextView postCount){

        Query query = myRef.child(mActivity.getString(R.string.user_photos_node)).child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mediaCount = dataSnapshot.getChildrenCount();
                Query query = myRef.child(mActivity.getString(R.string.user_videos_node)).child(uid);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mediaCount+=dataSnapshot.getChildrenCount();
                        postCount.setText(String.valueOf(mediaCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public long getImageCount(DataSnapshot dataSnapshot) {
        Log.d(TAG,"image_count: "+dataSnapshot.getChildrenCount());
        return dataSnapshot.getChildrenCount();
    }


    public long getVideoCount(DataSnapshot dataSnapshot) {

        Log.d(TAG,"video_count: "+dataSnapshot.getChildrenCount());
        return dataSnapshot.getChildrenCount();
    }


    private void addPhotoToDatabase(String caption, String imageUrl){

        String photoId = myRef.push().getKey();
        String dateAdded = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String tags = StringManipulation.getTags(caption);
        Photo photo = new Photo(caption,imageUrl,dateAdded,photoId,tags,userID);
        myRef.child(mActivity.getString(R.string.user_photos_node)).child(userID).child(photoId).setValue(photo);
        myRef.child(mActivity.getString(R.string.photos_node)).child(photoId).setValue(photo);
    }


    private void addVideoToDatabase(String caption, String videoUrl){

        String videoId = myRef.push().getKey();
        String dateAdded = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String tags = StringManipulation.getTags(caption);
        Video video = new Video(caption,videoUrl,dateAdded,videoId,tags,userID);
        myRef.child(mActivity.getString(R.string.user_videos_node)).child(userID).child(videoId).setValue(video);
        myRef.child(mActivity.getString(R.string.videos_node)).child(videoId).setValue(video);
    }


    public void addNewLike(String node,String mediaId){

        String likesId = myRef.push().getKey();
        myRef.child(node).child(mediaId).child(mActivity.getString(R.string.fieldLikes))
        .child(likesId).child(mActivity.getString(R.string.users_id)).setValue(userID);
    }


    public void removeNewLike(String node,String mediaId,String likesId){

        myRef.child(node).child(mediaId).child(mActivity.getString(R.string.fieldLikes))
                .child(likesId).removeValue();
    }


    public void addNewComment(final String node, final String mediaId, final String comment){

        final String commentId = myRef.push().getKey();
        final String dateAdded = new SimpleDateFormat("dd-MM-yyyy ",Locale.getDefault()).format(Calendar.getInstance().getTime());
        Query query = myRef.child(mActivity.getString(R.string.user_account_settings_node)).child(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userName = Objects.requireNonNull(dataSnapshot.child(mActivity.getString(R.string.usernameField)).getValue()).toString();
                String profileImage = Objects.requireNonNull(dataSnapshot.child(mActivity.getString(R.string.profilePhotoField)).getValue()).toString();

                Comment comment_model = new Comment(comment, dateAdded, userName, profileImage, 0);
                myRef.child(node).child(mediaId).child(mActivity.getString(R.string.fieldComments))
                        .child(Objects.requireNonNull(commentId)).setValue(comment_model);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void addNewUserData(String email, String username, String displayName, String description, String website, long phoneNo, String profile_photo){

        User user = new User( userID, phoneNo, email, StringManipulation.condenseUserName(username).toLowerCase());
        final UserAccountSettings settings = new UserAccountSettings(description, displayName,
                profile_photo, StringManipulation.condenseUserName(username).toLowerCase(), website );

            myRef.child(mActivity.getString(R.string.users_node))
                .child(userID)
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                myRef.child(mActivity.getString(R.string.user_account_settings_node))
                        .child(userID)
                        .setValue(settings).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(!Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()){
                        sendVerificationEmail(true);}
                        Log.d(TAG,"new user added");
                    }
                });

            }
        });

    }



}
