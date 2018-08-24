package com.dannproductions.instaclone.Search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dannproductions.instaclone.DataModels.User;
import com.dannproductions.instaclone.Profile.ProfileActivity;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Utils.BottomNavigationViewHelper;
import com.dannproductions.instaclone.Utils.UserListAdapter;
import com.dannproductions.instaclone.Utils.ViewProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity{

    //Vars
    private static final String TAG = "SearchActivity";
    private Context mContext = SearchActivity.this;
    private static final int ACTIVITY_NUM = 1;
    private UserListAdapter listAdapter;
    private ArrayList<User> searchList;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: started.");

        mAuth = FirebaseAuth.getInstance();
        ListView listView = findViewById(R.id.searchList);
        EditText mSearch = findViewById(R.id.search);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        searchList = new ArrayList<>();
        listAdapter = new UserListAdapter(mContext,R.layout.layout_search_view,searchList);
        listView.setAdapter(listAdapter);


        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String keyword = s.toString().trim().toLowerCase();
                searchForMatch(keyword);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              if(searchList.get(position).getUser_id().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                  startActivity(new Intent(mContext, ProfileActivity.class));
              }else {
                  Intent intent = new Intent(mContext, ViewProfileActivity.class);
                  intent.putExtra(getString(R.string.users_id),searchList.get(position).getUser_id());
                  startActivity(intent);
              }
            }
        });

        setupBottomNavigationView();




    }




    private void searchForMatch(String keyword){

            searchList.clear();
            listAdapter.notifyDataSetChanged();

            if(keyword.length()>0) {
                progressBar.setVisibility(View.VISIBLE);
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                Query query = myRef.child(getString(R.string.users_node)).orderByChild(getString(R.string.usernameField))
                        .startAt(keyword).endAt(keyword + "\uf8ff");

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        searchList.clear();
                        listAdapter.notifyDataSetChanged();
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            searchList.add(singleSnapshot.getValue(User.class));
                            listAdapter.notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

    }







    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
