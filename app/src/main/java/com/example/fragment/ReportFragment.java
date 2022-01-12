package com.example.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common.Common;
import com.example.model.Request;
import com.example.restaurant.R;
import com.example.restaurant.ReportDetailActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReportFragment extends Fragment {

    EditText edtDay1, edtDay2;
    TextView txtSum, txtSum2, txtSum3, txtTotalPrice, txtDetail;

    LineChart lineChartOrder, lineChartTotalPrice;

    Button btnGetReport;

    LinearLayout linearReport;

    DatePickerDialog datePickerDialog;

    DatabaseReference reference;

    ArrayList<Request> requests = new ArrayList<>();
    ArrayList<Entry> orderDataList = new ArrayList<>();
    ArrayList<Entry> priceDataList = new ArrayList<>();

    Date date1, date2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        initView(view);

        addEvent();

        return view;
    }

    private void initView(View view) {
        edtDay1 = view.findViewById(R.id.edtDay1);
        edtDay2 = view.findViewById(R.id.edtDay2);
        txtSum = view.findViewById(R.id.txtSum);
        txtSum2 = view.findViewById(R.id.txtSum2);
        txtSum3 = view.findViewById(R.id.txtSum3);
        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
        txtDetail = view.findViewById(R.id.txtDetail);
        btnGetReport = view.findViewById(R.id.btnGetReport);
        lineChartOrder = view.findViewById(R.id.lineChartOrder);

        XAxis xAxis = lineChartOrder.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @SuppressLint("SimpleDateFormat")
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM");

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value;
                return mFormat.format(new Date(millis));
            }
        });

        lineChartTotalPrice = view.findViewById(R.id.lineChartTotalPrice);
        XAxis xAxis2 = lineChartTotalPrice.getXAxis();
        xAxis2.setValueFormatter(new ValueFormatter() {
            @SuppressLint("SimpleDateFormat")
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM");

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value;
                return mFormat.format(new Date(millis));
            }
        });


        linearReport = view.findViewById(R.id.linearReport);
        linearReport.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void addEvent() {
        edtDay1.setOnClickListener(v -> showDialogDatePicker(edtDay1));

        edtDay2.setOnClickListener(v -> showDialogDatePicker(edtDay2));

        btnGetReport.setOnClickListener(v -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            String dateString1 = edtDay1.getText().toString();
            String dateString2 = edtDay2.getText().toString();


            if (edtDay1.getText().toString().equals("--/--/----")
                    || edtDay1.getText().toString().equals("--/--/----")) {
                Toast.makeText(getContext(), "Thiếu thông tin ngày tháng", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    linearReport.setVisibility(View.VISIBLE);

                    date1 = sdf.parse(dateString1);
                    date2 = sdf.parse(dateString2);

                    reference = FirebaseDatabase.getInstance().getReference("Requests");

                    Query query = reference.orderByChild("restaurant").equalTo(Common.ID);

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            requests.clear();

                            int count = 0;
                            int count2 = 0;
                            int count3 = 0;
                            double totalPrice = 0;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Request request = snapshot.getValue(Request.class);
                                assert request != null;
                                long time = request.getTimeStamp();
                                assert date1 != null;
                                if (date1.getTime() <= time && time <= (date2.getTime() + 86400000)) {
                                    count++;
                                    if (request.getStatus() == 2) {
                                        count2++;
                                        totalPrice += request.getTotal();
                                        requests.add(request);
                                    } else if (request.getStatus() == -1) {
                                        count3++;
                                    }
                                }
                            }

                            txtSum.setText(count + " đơn");
                            txtSum2.setText(count2 + " đơn");
                            txtSum3.setText(count3 + " đơn");
                            txtTotalPrice.setText(totalPrice + " VND");

                            Log.d("Day1:::::::::", date1.getTime() + "");
                            Log.d("Day2:::::::::", date2.getTime() + "");
                            setLineChartData(requests, date1, date2);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


        });

        txtDetail.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ReportDetailActivity.class);
            intent.putExtra("startDate", date1.getTime());
            intent.putExtra("endDate", date2.getTime());
            startActivity(intent);
        });
    }

    private void setLineChartData(ArrayList<Request> requests, Date date1, Date date2) {
        for (long i = date1.getTime(); i < date2.getTime() + 86400000; i += 86400000) {
            int countOrder = 0;
            int priceTotal = 0;
            for (Request request : requests) {
                long time = request.getTimeStamp();
                if (i <= time && time <= (i + 86400000)) {
                    countOrder++;
                    priceTotal += request.getTotal();
                }
            }
            orderDataList.add(new Entry(i, countOrder));
            priceDataList.add(new Entry(i, priceTotal));
        }

        Log.d("Day1:::::::::", priceDataList + "");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet highLineDataSet = new LineDataSet(orderDataList, getString(R.string.total_order));
        highLineDataSet.setDrawCircles(true);
        highLineDataSet.setCircleRadius(4);
        highLineDataSet.setDrawValues(false);
        highLineDataSet.setLineWidth(3);
        highLineDataSet.setColor(Color.GREEN);
        highLineDataSet.setCircleColor(Color.GREEN);
        dataSets.add(highLineDataSet);
        LineData lineData = new LineData(dataSets);
        lineChartOrder.setData(lineData);
        lineChartOrder.invalidate();

        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
        LineDataSet highLineDataSet2 = new LineDataSet(priceDataList, getString(R.string.total_price));
        highLineDataSet2.setDrawCircles(true);
        highLineDataSet2.setCircleRadius(4);
        highLineDataSet2.setDrawValues(false);
        highLineDataSet2.setLineWidth(3);
        highLineDataSet2.setColor(Color.RED);
        highLineDataSet2.setCircleColor(Color.RED);
        dataSets2.add(highLineDataSet2);
        LineData lineData2 = new LineData(dataSets2);
        lineChartTotalPrice.setData(lineData2);
        lineChartTotalPrice.invalidate();
    }

    @SuppressLint("SetTextI18n")
    private void showDialogDatePicker(EditText editText) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                (view, year, monthOfYear, dayOfMonth) -> editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}

