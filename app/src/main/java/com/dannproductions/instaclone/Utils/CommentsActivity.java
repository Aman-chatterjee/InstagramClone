package com.dannproductions.instaclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dannproductions.instaclone.DataModels.Comment;
import com.dannproductions.instaclone.R;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity implements UtilityInterface {
    //Vars
    Context mContext = CommentsActivity.this;
    private final String TAG = "CommentsActivity";
    private String mediaId;
    private String mediaNode;
    private String profileImage;
    private ListView commentList;
    private ArrayList<Comment> list;
    private CommentListAdapter listAdapter;
    private boolean isCommentAdded = false;
    private long startLimit =-1;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    FirebaseDatabase database;
    private FirebaseMethods firebaseMethods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        firebaseMethods = new FirebaseMethods(CommentsActivity.this);

        Intent mediaIntent = getIntent();
        mediaId = mediaIntent.getStringExtra("mediaID");
        mediaNode = mediaIntent.getStringExtra("mediaNode");
        profileImage = mediaIntent.getStringExtra(getString(R.string.profilePhotoField));

        setCommentProfileImage(profileImage);
        addComment(mediaNode,mediaId);

        commentList = findViewById(R.id.comment_list);
        list = new ArrayList<>();
        listAdapter = new CommentListAdapter(CommentsActivity.this,R.layout.layout_comment,list);
        retrieveAllComments(mediaNode,mediaId,20);
        commentList.setAdapter(listAdapter);

        goBack();
    }


    private void retrieveAllComments(String mediaNode, String mediaId, final long endLimit){

        Query query = myRef.child(mediaNode).child(mediaId).child(getString(R.string.fieldComments)).orderByChild(getString(R.string.dateField));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long  commentsLength = 0;
                Log.d("startLimit",""+ startLimit);
                Log.d("endLimit",""+endLimit);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if(isCommentAdded&&commentsLength==dataSnapshot.getChildrenCount()-1){

                            Comment comment = snapshot.getValue(Comment.class);
                            list.add(0,new Comment(Objects.requireNonNull(comment).getComment(), comment.getDate_added(), comment.getUser_name(), comment.getProfile_image(), comment.getComment_likes()));
                            commentList.smoothScrollToPosition(0);
                            isCommentAdded = false;
                    }
                    else if (!isCommentAdded&&commentsLength <= endLimit && commentsLength > startLimit) {
                            Comment comment = snapshot.getValue(Comment.class);
                            list.add(new Comment(Objects.requireNonNull(comment).getComment(), comment.getDate_added(), comment.getUser_name(), comment.getProfile_image(), comment.getComment_likes()));
                        }
                    commentsLength++;
                }
                Log.d(TAG,"No of Comments : "+commentsLength);
                Log.d(TAG,"No of Comments_methodBased : "+dataSnapshot.getChildren());
                startLimit = endLimit;
                listAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void addComment(final String mediaNode, final String mediaId){

        TextView postComment = findViewById(R.id.post_comment);
        final EditText commentText = findViewById(R.id.comment);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentText.getText().toString().length()>0) {
                    isCommentAdded = true;
                    firebaseMethods.addNewComment(mediaNode,mediaId, commentText.getText().toString());
                }
            }
        });
    }

    private void setCommentProfileImage(String image){

        CircularImageView profileImage = findViewById(R.id.comment_profile_image);
        GlideImageLoader.loadImageWithOutTransition(mContext,image,profileImage);
    }



    private void goBack(){
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void loadMore(long limit) {
        retrieveAllComments(mediaNode,mediaId,limit);
    }


}
