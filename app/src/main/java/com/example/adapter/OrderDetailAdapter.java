package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.model.Food;
import com.example.model.Order;
import com.example.restaurant.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Order> orders;

    public OrderDetailAdapter(Context mContext, ArrayList<Order> orders) {
        this.mContext = mContext;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.foods_order_detail, parent, false);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods")
                .child(order.getProductId());

        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Food food = snapshot.getValue(Food.class);
                assert food != null;
                Glide.with(mContext).load(food.getImage()).into(holder.imgItemFood);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.txtItemFoodName.setText(order.getProductName());
        holder.txtQuantity.setText(order.getQuantity()+"");

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgItemFood;
        public TextView txtItemFoodName, txtQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgItemFood = itemView.findViewById(R.id.imgItemFood);
            txtItemFoodName = itemView.findViewById(R.id.txtItemFoodName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
        }
    }
}
