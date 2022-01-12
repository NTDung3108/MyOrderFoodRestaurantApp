package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.model.Food;
import com.example.restaurant.FoodDetailActivity;
import com.example.restaurant.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Food> foods;

    public FoodAdapter(Context mContext, ArrayList<Food> foods) {
        this.mContext = mContext;
        this.foods = foods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.food_item, parent, false);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foods.get(position);

        holder.txtFoodName.setText(food.getName());
        holder.txtRatting.setText(food.getRatting()+"");

        Glide.with(mContext).load(food.getImage()).into(holder.imgFood);

        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods").child(food.getId());

        holder.btnDelete.setOnClickListener(v -> {
            foodRef.removeValue();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, FoodDetailActivity.class);
            intent.putExtra("Food", food);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgFood;
        public TextView txtFoodName, txtRatting;
        public RelativeLayout btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtRatting = itemView.findViewById(R.id.txtRatting);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
