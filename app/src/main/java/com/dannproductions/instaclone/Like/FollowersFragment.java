package com.dannproductions.instaclone.Like;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dannproductions.instaclone.DataModels.User;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Utils.UserListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FollowersFragment extends Fragment{
    final String TAG = "FollowersFragment";
    Context mContext;
    ListView listView;
    ArrayList<User> userList;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    UserListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_followers,container,false);
        listView = view.findViewById(R.id.follower_list);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth =  FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        userList = new ArrayList<User>();
        addFollowers();

    }


    private void addFollowers(){

        Query query = myRef.child(getString(R.string.node_followers))
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    addUsers(ds);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void addUsers(DataSnapshot ds) {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef.child(getString(R.string.users_node)).orderByChild(getString(R.string.users_id)).equalTo(ds.getKey());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userList.add(0, ds.getValue(User.class));
                }
                adapter = new UserListAdapter(mContext, R.layout.layout_search_view, userList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
