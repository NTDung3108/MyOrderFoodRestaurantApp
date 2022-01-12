package com.example.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adapter.OrderAdapter;
import com.example.common.Common;
import com.example.model.Request;
import com.example.restaurant.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class OrderFragment extends Fragment {

    RecyclerView rvOrder;
    DatabaseReference requestsRef;
    ArrayList<Request> requests = new ArrayList<>();
    OrderAdapter orderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        rvOrder = view.findViewById(R.id.rvOrder);

        requestsRef = FirebaseDatabase.getInstance().getReference("Requests");
        Query order = requestsRef.orderByChild("restaurant").equalTo(Common.ID);
        order.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requests.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Request request = snapshot.getValue(Request.class);
                    requests.add(request);
                }

                Collections.sort(requests, new Comparator<Request>() {
                    @Override
                    public int compare(Request o1, Request o2) {
                        if (o1.getTimeStamp() < o2.getTimeStamp()) {
                            return 1;
                        } else {
                            if (o1.getTimeStamp() < o2.getTimeStamp()) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }
                    }
                });

                orderAdapter = new OrderAdapter(getContext(), requests);
                rvOrder.setHasFixedSize(true);
                rvOrder.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                rvOrder.setAdapter(orderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}