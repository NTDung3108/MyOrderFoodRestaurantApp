package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.model.Rating;
import com.example.model.User;
import com.example.restaurant.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context mContext;
    ArrayList<Rating> ratings;

    public CommentAdapter(Context mContext, ArrayList<Rating> ratings) {
        this.mContext = mContext;
        this.ratings = ratings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Rating rating = ratings.get(position);

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(rating.getSenderphone()).getValue(User.class);
                holder.txtCommentUserName.setText(user.getName());
                if (user.getImageURL().equals("default")) {
                    Glide.with(mContext).load(R.mipmap.ic_launcher).into(holder.imgCommentAvatar);
                } else {
                    Glide.with(mContext).load(user.getImageURL()).into(holder.imgCommentAvatar);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.txtRating.setText(rating.getRateValue());
        holder.txtComment.setText(rating.getComment());

        if (!rating.getImage().equals("default")){
            holder.imgPicture.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(rating.getImage()).into(holder.imgPicture);
        }
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtCommentUserName, txtRating, txtComment;
        public CircleImageView imgCommentAvatar;
        public ImageView imgPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCommentUserName = itemView.findViewById(R.id.txtCommentUserName);
            txtRating = itemView.findViewById(R.id.txtRating);
            txtComment = itemView.findViewById(R.id.txtComment);
            imgCommentAvatar = itemView.findViewById(R.id.imgCommentAvatar);
            imgPicture = itemView.findViewById(R.id.imgPicture);
        }
    }
}
