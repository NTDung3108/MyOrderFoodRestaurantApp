package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.adapter.ReportDetailAdapter;
import com.example.model.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReportDetailActivity extends AppCompatActivity {

    ImageView imgBack;
    RecyclerView rvReportDetail;

    DatabaseReference reference;

    ArrayList<Request> requests = new ArrayList<>();

    ReportDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        long startDate = getIntent().getLongExtra("startDate", 0);
        long endDate = getIntent().getLongExtra("endDate", 0);

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        rvReportDetail = findViewById(R.id.rvReportDetail);
        rvReportDetail.setHasFixedSize(true);
        rvReportDetail.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        reference = FirebaseDatabase.getInstance().getReference("Requests");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Request request = snapshot.getValue(Request.class);
                    long timeStamp = request.getTimeStamp();
                    if (startDate <= timeStamp && timeStamp <= endDate){
                        requests.add(request);
                    }
                    adapter = new ReportDetailAdapter(ReportDetailActivity.this, requests);
                    rvReportDetail.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}