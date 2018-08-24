package com.dannproductions.instaclone.RegisterAcount;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterAccountActivity extends AppCompatActivity {
    private static final String TAG = "RegisterAccountActivity";
    private Context mContext = RegisterAccountActivity.this;
    private ProgressBar mProgressbar;
    private EditText mUsername,mEmail,mPassword,mConfirmPassword;
    private String email,password,confirmPassword,userName,append="";
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth mAuth;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(RegisterAccountActivity.this);
        mProgressbar = (ProgressBar)findViewById(R.id.register_progress);
        mUsername = (EditText)findViewById(R.id.input_userName);
        mEmail = (EditText)findViewById(R.id.input_email);
        mPassword = (EditText)findViewById(R.id.input_password);
        mConfirmPassword = (EditText)findViewById(R.id.input_confirm_password);
        registerButton = (Button)findViewById(R.id.register_button);
        mProgressbar.setVisibility(View.GONE);


        firebaseRegister();

    }












    private boolean isTextFieldEmpty(String text){

        return (text.length() == 0);
    }

    private boolean isEmailValid(String email){
        if(!isTextFieldEmpty(email)&&android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return true;
        }else {
            Toast.makeText(mContext,"The email "+email+" is not valid",Toast.LENGTH_SHORT).show();
            return  false;
        }
    }

    private boolean isPasswordValid(String password,String confirmPassword){

        if(password.length()>=6){
            if(password.equals(confirmPassword)){
                return true;
            }else {
                Toast.makeText(mContext, "Your passwords doesn't match!!", Toast.LENGTH_LONG).show();
                return false;
            }
        }else {
            Toast.makeText(mContext, "Your password must be of six or more characters!!", Toast.LENGTH_LONG).show();
            return false;
        }
    }


    private void firebaseRegister(){

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = mUsername.getText().toString().trim();
                email = mEmail.getText().toString().toLowerCase().trim();
                password = mPassword.getText().toString().trim();
                confirmPassword = mConfirmPassword.getText().toString().trim();


                if(!isTextFieldEmpty(userName)&&isEmailValid(email)&&isPasswordValid(password,confirmPassword)) {

                    firebaseMethods.signUp(email,password,userName,mProgressbar);
                }
            }
        });

    }


}
