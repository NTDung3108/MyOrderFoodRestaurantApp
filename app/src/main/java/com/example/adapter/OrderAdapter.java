package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.common.Common;
import com.example.model.Request;
import com.example.restaurant.OrderDetailActivity;
import com.example.restaurant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Request> requests;

    public OrderAdapter(Context mContext, ArrayList<Request> requests) {
        this.mContext = mContext;
        this.requests = requests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.order_item, parent, false);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }
    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requests.get(position);

        if (request != null){
            holder.txtOrderID.setText(request.getId());
            holder.txtOderAddress.setText(request.getAddress());
            holder.txtOrderStatus.setText(Common.convertCodeToStatus(request.getStatus()));
            holder.txtPhoneOrder.setText(request.getPhone());

            long l = request.getTimeStamp();
            Date date = new Date(l);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd-MM-yyyy");

            holder.txtTimeStamp.setText(simpleDateFormat.format(date));

            holder.txtOderDetail.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                intent.putExtra("OrderID", request.getId());
                intent.putExtra("status", request.getStatus());
                mContext.startActivity(intent);
            });

            holder.txtReport.setOnClickListener(v -> showReportDialog(request.getId()));

            holder.txtUserPhone.setOnClickListener(v -> {
                Uri uri = Uri.parse("tel:"+request.getPhone());
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(uri);
                mContext.startActivity(intent);
            });
        }
    }



    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtOrderID, txtOderAddress, txtPhoneOrder, txtOrderStatus, txtOderDetail,
                txtTimeStamp, txtReport, txtUserPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderID = itemView.findViewById(R.id.txtOrderID);
            txtOderAddress = itemView.findViewById(R.id.txtOderAddress);
            txtPhoneOrder = itemView.findViewById(R.id.txtPhoneOrder);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtOderDetail = itemView.findViewById(R.id.txtOderDetail);
            txtTimeStamp = itemView.findViewById(R.id.txtTimeStamp);
            txtReport = itemView.findViewById(R.id.txtReport);
            txtUserPhone = itemView.findViewById(R.id.txtUserPhone);
        }
    }
    private void showReportDialog(String id) {
        LayoutInflater layoutInflaterAndroid = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = layoutInflaterAndroid.inflate(R.layout.report_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
        alertDialogBuilderUserInput.setView(mView);

        final EditText edtReason = (EditText) mView.findViewById(R.id.edtReason);
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(mContext);
        alert.setTitle("Report");
        alert.setView(mView);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton("Report", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // code for matching password
                DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", Common.ID);
                hashMap.put("oderReport", id);
                hashMap.put("reason", edtReason.getText().toString());

                reportRef.child("Reports").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Lý do tố cáo của bạn đã được gửi đi", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(mContext, "Có lỗi xảy ra vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        android.app.AlertDialog dialog = alert.create();
        dialog.show();
    }
//    private static void timeStamp(TextView textView, Date date){
//
//        SimpleDateFormat getSeconds = new SimpleDateFormat("ss");
//        SimpleDateFormat getMinutes = new SimpleDateFormat("mm");
//        SimpleDateFormat getHours = new SimpleDateFormat("HH");
//
//        String seconds = getSeconds.format(date);
//        String minutes  = getMinutes.format(date);
//        String hours = getHours.format(date);
//
//    }


}
