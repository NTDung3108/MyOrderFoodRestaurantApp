package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.OrderDetailAdapter;
import com.example.model.Food;
import com.example.model.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetailActivity extends AppCompatActivity {
    Button btnDelivery, btnCancel;
    TextView txtOrderID2;
    ImageView imgBack;
    RecyclerView rvODFoods;

    DatabaseReference requestRef, foodRef;

    ArrayList<Order> orders = new ArrayList<>();
    ArrayList<Food> foods = new ArrayList<>();

    OrderDetailAdapter orderDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initView();

        String id = getIntent().getStringExtra("OrderID");
        int status = getIntent().getIntExtra("status", 0);
        if (status == -1){
            btnCancel.setVisibility(View.GONE);
            btnDelivery.setVisibility(View.GONE);
        }else if (status == 1){
            btnDelivery.setVisibility(View.GONE);
        }else if (status == 2){
            btnCancel.setVisibility(View.GONE);
            btnDelivery.setVisibility(View.GONE);
        }

        txtOrderID2.setText(id);

        requestRef = FirebaseDatabase.getInstance().getReference("Requests").child(id);
        requestRef.child("foods").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders.clear();
                foods.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Order order = snapshot.getValue(Order.class);
                    orders.add(order);
                }
                orderDetailAdapter = new OrderDetailAdapter(OrderDetailActivity.this, orders);
                rvODFoods.setHasFixedSize(true);
                rvODFoods.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this, RecyclerView.VERTICAL, false));
                rvODFoods.setAdapter(orderDetailAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imgBack.setOnClickListener(v -> finish());

        btnCancel.setOnClickListener(v -> showCancelDialog(btnCancel, btnDelivery));

        btnDelivery.setOnClickListener(v -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", 1);

            requestRef.updateChildren(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    btnCancel.setVisibility(View.GONE);
                    btnDelivery.setVisibility(View.GONE);
                    Toast.makeText(OrderDetailActivity.this, "Đơn hàng đang được chuyển đi hãy đợi xác nhận từ người đặt", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(OrderDetailActivity.this, "Có lỗi xảy ra vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void showCancelDialog(Button btnCancel, Button btnDelivery) {
        LayoutInflater layoutInflaterAndroid = getLayoutInflater();
        View mView = layoutInflaterAndroid.inflate(R.layout.report_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText edtReason =  mView.findViewById(R.id.edtReason);
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setTitle("Hủy Đơn");
        alert.setView(mView);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alert.setPositiveButton("Hủy đơn", (dialog, which) -> {
            // code for matching password
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", -1);
            hashMap.put("reason", edtReason.getText().toString());
            requestRef.updateChildren(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    btnCancel.setVisibility(View.GONE);
                    btnDelivery.setVisibility(View.GONE);
                    Toast.makeText(OrderDetailActivity.this, "Yêu cần của bạn đã được gửi đi", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Có lỗi xảy ra vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            });
        });
        android.app.AlertDialog dialog = alert.create();
        dialog.show();
    }


    private void initView() {
        btnDelivery = findViewById(R.id.btnDelivery);
        btnCancel = findViewById(R.id.btnCancel);
        txtOrderID2 = findViewById(R.id.txtOrderID2);
        imgBack = findViewById(R.id.imgBack);
        rvODFoods = findViewById(R.id.rvODFoods);
    }
}