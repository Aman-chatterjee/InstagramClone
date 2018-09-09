package com.dannproductions.instaclone.Login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dannproductions.instaclone.Home.HomeActivity;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.RegisterAcount.RegisterAccountActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Context mContext = LoginActivity.this;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressbar;
    private EditText mEmail,mPassword;
    TextView createAccount;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mProgressbar = (ProgressBar)findViewById(R.id.login_progress);
        mEmail = (EditText)findViewById(R.id.input_email);
        createAccount = (TextView)findViewById(R.id.create_account);
        mPassword = (EditText)findViewById(R.id.input_password);
        loginButton = (Button)findViewById(R.id.login_button);
        mProgressbar.setVisibility(View.GONE);


        firebaseLogin();

        //Navigating to RegisterAccountActivity
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterAccountActivity.class);
                startActivity(registerIntent);
            }
        });


    }



    private boolean isTextFieldEmpty(String text){

        return (text.length() == 0);
    }



   private void firebaseLogin(){

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(!isTextFieldEmpty(email)&&!isTextFieldEmpty(password)) {

                    mProgressbar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null && user.isEmailVerified()) {

                                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            Toast.makeText(mContext, "Login Success.",Toast.LENGTH_LONG).show();
                                        }else {
                                            Toast.makeText(mContext, "Login failed!! email not verified",Toast.LENGTH_LONG).show();
                                            mProgressbar.setVisibility(View.GONE);
                                            mAuth.signOut();
                                        }


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        mProgressbar.setVisibility(View.GONE);
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(mContext, "The email or password doesn't match",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }else {
                    Toast.makeText(mContext, "The text fields must not be empty!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
   }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


}
