package com.dannproductions.instaclone.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dannproductions.instaclone.Home.HomeActivity;
import com.dannproductions.instaclone.Login.LoginActivity;
import com.dannproductions.instaclone.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignOutFragment extends Fragment {
    View view;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseAuth.AuthStateListener authStateListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sign_out, container, false);
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button signOut = (Button)view.findViewById(R.id.signoutButton);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        setupAuthListner();


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.addAuthStateListener(authStateListener);
                mAuth.signOut();
            }
        });

    }

    private void setupAuthListner(){

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()!=null){
                    Toast.makeText(getContext(),"Signout failed!!",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }else {
                    Toast.makeText(getContext(),"Signout success!!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    progressBar.setVisibility(View.GONE);
                }

            }
        };

    }



    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }


}
