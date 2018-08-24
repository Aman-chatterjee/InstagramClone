package com.dannproductions.instaclone.Utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dannproductions.instaclone.DataModels.Comment;
import com.dannproductions.instaclone.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Objects;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private Activity mContext;
    private int layoutResource;
    private long limit=20;
    private UtilityInterface utilityInterface;

    CommentListAdapter(@NonNull Activity context, int resource, ArrayList<Comment> comments) {
        super(context,resource,comments);
        mContext = context;
        layoutResource = resource;
        utilityInterface = (UtilityInterface)mContext;
    }

    private static class ViewHolder{

        ImageView profileImage;
        TextView comment;
        //TextView commentLike;
        TextView dateAdded;
        ImageView addLike;
        TextView commentReply;
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.profileImage = convertView.findViewById(R.id.comment_profile);
            holder.comment = convertView.findViewById(R.id.comment);
            //holder.commentLike = convertView.findViewById(R.id.commentLike);
            holder.dateAdded = convertView.findViewById(R.id.date_added);
            holder.addLike = convertView.findViewById(R.id.comment_heart);
            holder.commentReply = convertView.findViewById(R.id.commentReply);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }


        Comment commentData = getItem(position);


        //Setting profile image
        GlideImageLoader.loadImageWithOutTransition(mContext, Objects.requireNonNull(commentData).getProfile_image(),holder.profileImage);
        //Setting userName and comment
        String userName = Objects.requireNonNull(commentData).getUser_name();
        SpannableStringBuilder str = new SpannableStringBuilder(userName+" "+commentData.getComment());
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.comment.setText(str);
        //Setting date
        holder.dateAdded.setText(commentData.getDate_added());

        holder.addLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "This feature is not added yet!!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.commentReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "This feature is not added yet!!", Toast.LENGTH_SHORT).show();
            }
        });


        if(position>=limit-1) {
            limit+=20;
            utilityInterface.loadMore(limit);
        }

        return  convertView;
    }




}
