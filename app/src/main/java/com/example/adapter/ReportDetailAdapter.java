package com.example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.common.Common;
import com.example.model.Request;
import com.example.restaurant.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReportDetailAdapter extends RecyclerView.Adapter<ReportDetailAdapter.ViewHolder> {
    Context mContext;
    ArrayList<Request> requests;

    public ReportDetailAdapter(Context mContext, ArrayList<Request> requests) {
        this.mContext = mContext;
        this.requests = requests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.report_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requests.get(position);

        Date date = new Date(request.getTimeStamp());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");

        holder.txtRDTime.setText(simpleDateFormat.format(date));
        holder.txtRDPhone.setText(request.getPhone());
        holder.txtRDAddress.setText(request.getAddress());
        holder.txtRDPrice.setText(request.getTotal()+"");
        if (request.getStatus() == -1){
            holder.txtRDTime.setText(Common.convertCodeToStatus(request.getStatus()));
            holder.txtRDStatus.setTextColor(Color.RED);
        }else {
            holder.txtRDTime.setText(Common.convertCodeToStatus(request.getStatus()));
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtRDTime, txtRDPhone, txtRDAddress, txtRDPrice, txtRDStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRDTime = itemView.findViewById(R.id.txtRDTime);
            txtRDPhone = itemView.findViewById(R.id.txtRDPhone);
            txtRDAddress = itemView.findViewById(R.id.txtRDAddress);
            txtRDPrice = itemView.findViewById(R.id.txtRDPrice);
            txtRDStatus = itemView.findViewById(R.id.txtRDStatus);
        }
    }
}
