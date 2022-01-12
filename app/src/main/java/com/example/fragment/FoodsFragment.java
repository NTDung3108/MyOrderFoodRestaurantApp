package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.adapter.FoodAdapter;
import com.example.common.Common;
import com.example.model.Food;
import com.example.restaurant.AddNewFoodActivity;
import com.example.restaurant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class FoodsFragment extends Fragment {

    RelativeLayout btnNewFood;
    RecyclerView rvFoods;

    DatabaseReference foodRef;

    ArrayList<Food> foods = new ArrayList<>();

    FoodAdapter foodAdapter;

    int stt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_foods, container, false);


        btnNewFood = view.findViewById(R.id.btnNewFood);
        rvFoods = view.findViewById(R.id.rvFoods);
        rvFoods.setHasFixedSize(true);
        rvFoods.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        foodRef = FirebaseDatabase.getInstance().getReference("Foods");
        Query foodQuery = foodRef.orderByChild("restaurants").equalTo(Common.ID);

        foodQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foods.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Food food = snapshot.getValue(Food.class);
                    foods.add(food);
                }
                stt = foods.size();
                foodAdapter = new FoodAdapter(getContext(), foods);
                rvFoods.setAdapter(foodAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnNewFood.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddNewFoodActivity.class);
            intent.putExtra("STT", stt);
            startActivity(intent);
        });

        return view;
    }
}