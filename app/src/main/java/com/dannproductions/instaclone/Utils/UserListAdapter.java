package com.dannproductions.instaclone.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dannproductions.instaclone.DataModels.User;
import com.dannproductions.instaclone.DataModels.UserAccountSettings;
import com.dannproductions.instaclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class UserListAdapter extends ArrayAdapter<User>{
    private static final String TAG = "UserListAdapter";
    private Context mContext;
    private int layoutResource;
    private DatabaseReference reference;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        layoutResource = resource;
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(layoutResource, parent, false);
        }

        TextView userName = convertView.findViewById(R.id.username);
        TextView email = convertView.findViewById(R.id.email);
        final ImageView profileImage = convertView.findViewById(R.id.profile_image);
        User user = getItem(position);

        userName.setText(Objects.requireNonNull(user).getUsername());
        email.setText(user.getEmail());

        Query query = reference.child(mContext.getString(R.string.user_account_settings_node))
                .orderByChild(mContext.getString(R.string.usernameField))
                .equalTo(user.getUsername());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG,"Entered on Data Changed");
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    GlideImageLoader.loadImageWithOutTransition(mContext,
                    Objects.requireNonNull(ds.getValue(UserAccountSettings.class)).getProfile_photo(),profileImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return convertView;
    }
}
