package com.dannproductions.instaclone.Settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dannproductions.instaclone.DataModels.MasterUserSettings;
import com.dannproductions.instaclone.DataModels.User;
import com.dannproductions.instaclone.DataModels.UserAccountSettings;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Share.ShareActivity;
import com.dannproductions.instaclone.Utils.FirebaseMethods;
import com.dannproductions.instaclone.Utils.StringManipulation;
import com.dannproductions.instaclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class EditProfileFragment extends Fragment {
    View view;
    private static final String TAG = "EditProfileFragment";
    //Firebase
    private FirebaseMethods firebaseMethods;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;

    //variables
    User user;
    String DisplayName;
    long PhoneNo;
    String Website;
    String Description;
    String Email;
    String UserName;
    UserAccountSettings accountSettings;

    //Widgets
    EditText username,description,email,phoneNo,website,displayName;
    TextView change_photo;
    ProgressBar progressBar;
    ImageView profilePhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference();
        firebaseMethods = new FirebaseMethods(getActivity());
        setupFirebaseDataRetriever();
        onTick();
        changePhoto();
        actionGoBack();
    }

    private void setupProfileData(MasterUserSettings userSettings){

     user = userSettings.getUser();
     accountSettings = userSettings.getAccountSettings();

        //setting up widgets
        username = (EditText) view.findViewById(R.id.user_name);
        displayName = (EditText)view.findViewById(R.id.display_name);
        website = (EditText)view.findViewById(R.id.website);
        phoneNo = (EditText)view.findViewById(R.id.phone_no);
        email = (EditText)view.findViewById(R.id.email_id);
        description = (EditText)view.findViewById(R.id.description);
        profilePhoto = (ImageView)view.findViewById(R.id.profile_photo);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //Setting data into the views
        username.setText(accountSettings.getUsername());
        displayName.setText(accountSettings.getDisplay_name());
        website.setText(accountSettings.getWebsite());
        description.setText(accountSettings.getDescription());
        email.setText(user.getEmail());
        phoneNo.setText(String.valueOf(user.getPhone_number()));
        //GlideImageLoader.loadImage(getContext(),accountSettings.getProfile_photo(),profilePhoto);
        UniversalImageLoader.setImage(accountSettings.getProfile_photo(),profilePhoto,progressBar,"");

    }


    private  void setupFirebaseDataRetriever(){

       try {
           database.setPersistenceEnabled(true);
       }catch (RuntimeException e){
           e.printStackTrace();
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                setupProfileData(firebaseMethods.retrieveUserData(dataSnapshot,mAuth.getCurrentUser().getUid(),true,true));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    private void setupFirebaseDataUpdater() {

        //Getting data from widgets
        UserName = username.getText().toString();
        DisplayName = displayName.getText().toString();
        Email = email.getText().toString();
        Website = website.getText().toString();
        Description = description.getText().toString();
        PhoneNo = Long.parseLong(phoneNo.getText().toString());



        //-------------------------------Checking if Username is changed------------------------------------
        if (!accountSettings.getUsername().equals(StringManipulation.condenseUserName(UserName.toLowerCase()))) {
            //Checking if Username already exists in the database
            checkIfUsernameExists(UserName.toLowerCase());
        }

        //------------------------------------If displayName is changed--------------------------------------
        if (!accountSettings.getDisplay_name().toLowerCase().equals(DisplayName.toLowerCase())) {

           firebaseMethods.updateDisplayname(DisplayName);
        }

        //------------------------------------If description is changed---------------------------------------
        if (!accountSettings.getDescription().toLowerCase().equals(Description.toLowerCase())) {

            firebaseMethods.updateDescription(Description);
        }

        //------------------------------------If website is changed-------------------------------------------
        if (!accountSettings.getWebsite().toLowerCase().equals(Website.toLowerCase())) {

            firebaseMethods.updateWebsite(Website);
        }


        //------------------------------------If phoneNo is changed-------------------------------------------
        if (user.getPhone_number()!=PhoneNo) {

            firebaseMethods.updatePhoneNo(PhoneNo);
            Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
        }

        //------------------------------Checking if Email is changed------------------------------------------
        if (!user.getEmail().equals(Email.toLowerCase())) {

            final View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_password, null);
            new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                    .setTitle(getString(R.string.ConfirmPassword))
                    .setView(view).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Getting the password for reAuthentication
                    final EditText password = (EditText) view.findViewById(R.id.input_confirm_password);

                    //Checking if the email already in use
                    FirebaseAuth.getInstance().fetchProvidersForEmail(Email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            if (task.isSuccessful()) {

                                if (Objects.requireNonNull(task.getResult().getProviders()).size() == 1) {
                                    Log.d(TAG, "Email address already in use");
                                    Toast.makeText(getContext(), "Email address already in use!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    //ReAuthenticating user and updating data
                                    firebaseMethods.reAuthenticateUser(Email.toLowerCase(), password.getText().toString());
                                }
                            }
                        }
                    });

                }
            }).setNegativeButton("Cancel", null).show();

        }


    }






    private void checkIfUsernameExists(final String username) {

        Query query = myRef.child(getString(R.string.users_node)).orderByChild(getString(R.string.usernameField)).equalTo(StringManipulation.condenseUserName(username));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if(!dataSnapshot.exists()) {

                   firebaseMethods.updateUsername(StringManipulation.condenseUserName(username));
               } else {
                Log.d(TAG, "Username already exists!!");
                Toast.makeText(getContext(), "Username already exists!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void changePhoto(){

        change_photo =(TextView)view.findViewById(R.id.change_photo);
        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ShareActivity.class);
                intent.putExtra(getString(R.string.UploadProfilePhoto),true);
                startActivity(intent);
            }
        });

    }



    //Save changes to the database
    private void onTick(){

        ImageView tick = (ImageView)view.findViewById(R.id.save_changes);
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupFirebaseDataUpdater();
                Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void actionGoBack(){

        ImageView back = (ImageView) Objects.requireNonNull(getActivity()).findViewById(R.id.setting_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }



    }
