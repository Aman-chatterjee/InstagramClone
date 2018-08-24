package com.dannproductions.instaclone.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dannproductions.instaclone.DataModels.Photo;
import com.dannproductions.instaclone.DataModels.Video;
import com.dannproductions.instaclone.R;
import com.dannproductions.instaclone.Utils.FirebaseMethods;
import com.dannproductions.instaclone.Utils.MainfeedListAdapter;
import com.dannproductions.instaclone.Utils.VideoPlayerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class HomeFragment extends Fragment{
    View view;
    private Context mContext;
    private ListView mainList;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private  ArrayList<Object> mediaList;
    private MainfeedListAdapter adapter;
    private ProgressBar pb;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.fragment_home,container,false);
         mainList = view.findViewById(R.id.mainListView);
         pb = view.findViewById(R.id.progress);
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
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mediaList = new ArrayList<>();
        myRef = database.getReference();
        setSelfFollowing();

        addContent();
        adapter = new MainfeedListAdapter(Objects.requireNonNull(getActivity()),R.layout.layout_mainfeed_item_view,mediaList);
        mainList.setAdapter(adapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mediaList.get(position).getClass()==Video.class){

                    Intent intent = new Intent(getActivity(),VideoPlayerActivity.class);
                    intent.putExtra("VideoPath",((Video)mediaList.get(position)).getVideoUrl());
                    startActivity(intent);
                }
            }
        });


    }


    private void addContent(){

            Query query = myRef.child(mContext.getString(R.string.node_following))
                    .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mediaList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            getPhoto(ds);
                            getVideo(ds);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
    }




    private void getPhoto(DataSnapshot ds){

        Query query = myRef.child(mContext.getString(R.string.photos_node)).orderByChild(mContext.getString(R.string.users_id)).equalTo(ds.getKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Photo photo = ds.getValue(Photo.class);
                    mediaList.add(0,photo);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void getVideo(DataSnapshot ds){

        Query query = myRef.child(mContext.getString(R.string.videos_node)).orderByChild(mContext.getString(R.string.users_id)).equalTo(ds.getKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Video video = ds.getValue(Video.class);
                    mediaList.add(video);
                    adapter.notifyDataSetChanged();
                }
//                Collections.sort(mediaList, new Comparator<Object>() {
//                    @Override
//                    public int compare(Object o1, Object o2) {
//                        Log.d("TAG","Comparing...");
//                        if (o1.getClass() == Video.class && o2.getClass() == Video.class) {
//                            Video p1 = (Video) o1;
//                            Video p2 = (Video) o2;
//                            return p2.getDate_added().compareTo(p1.getDate_added());
//                        }
//                        return 0;
//                    }
//                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void setSelfFollowing(){

       Query query = myRef.child(mContext.getString(R.string.node_following)).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
               .orderByChild(mContext.getString(R.string.users_id)).equalTo(mAuth.getCurrentUser().getUid());

       query.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if(!dataSnapshot.exists()){
                   new FirebaseMethods(getActivity()).addFollowingAndFollowers(mAuth.getCurrentUser().getUid());
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) { }
       });

    }



}
