package com.switchak.switchak;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("history ", "view created");

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_history_rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        final RoomsAdapter mAdapter = new RoomsAdapter("history");
        mRoomsList.setAdapter(mAdapter);
        final LineChart chart = rootView.findViewById(R.id.chart);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String userId = currentUser != null ? currentUser.getUid() : null;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users").child(userId);

        Button btn =rootView.findViewById(R.id.histoTest);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Entry> entries = new ArrayList<>();

//        entries.add(new Entry(1, (float) 12.5));
//        entries.add(new Entry(6, (float) 17.5));
//        entries.add(new Entry(16, (float) 10));
//        entries.add(new Entry(20, (float) 15));

                int roomnum = mAdapter.getRooms().size();
                double total=0.0;
                Timestamp time;

                for(int j=0 ; j<mAdapter.getRooms().get(0).getReadings().size() ;j++) {

                    for (int i = 0; i < roomnum; i++) {
                        total = total + (double) mAdapter.getRooms().get(i).getTotalReadings();
                    }
                    time= mAdapter.getRooms().get(j).getTimestampList().get(j);
                    entries.add(new Entry(Float.parseFloat(String.valueOf(time)),(float)total));
                }

                List<Timestamp> Time = new ArrayList<>();

//                Calendar cal = Calendar.getInstance();
//                cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
//                cal.clear(Calendar.MINUTE);
//                cal.clear(Calendar.SECOND);
//                cal.clear(Calendar.MILLISECOND);
//
//                // get start of the month
//                cal.set(Calendar.DAY_OF_MONTH, 1);
//                System.out.println("Start of the month:       " + cal.getTime());
//                System.out.println("... in milliseconds:      " + cal.getTimeInMillis());
//
//                // get start of the next month
//                cal.add(Calendar.MONTH, 1);
//                System.out.println("Start of the next month:  " + cal.getTime());
//                System.out.println("... in milliseconds:      " + cal.getTimeInMillis());
//
//                Calendar now = Calendar.getInstance();
//                Date date = new Date();
//                now.setTime(date);
//
//                cal.compareTo(now);

                XAxis xAxis = chart.getXAxis();

                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                xAxis.setAvoidFirstLastClipping(true);

                xAxis.setDrawAxisLine(true);
                xAxis.setEnabled(true);
                xAxis.setTextSize(10f);
                xAxis.setTextColor(Color.WHITE);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(true);
                xAxis.setTextColor(Color.rgb(255, 100, 100));
                xAxis.setCenterAxisLabels(true);
                xAxis.setGranularity(1f); // one hour

                xAxis.setAxisMinimum(0);
                xAxis.setAxisMaximum(31);


                xAxis.setValueFormatter(new IAxisValueFormatter() {

                    private SimpleDateFormat mFormat = new SimpleDateFormat("dd  ");


                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {

                        long millis = TimeUnit.DAYS.toMillis((long) value);
                        return mFormat.format(new Date(millis));
                    }
                });


                YAxis yAxis = chart.getAxisLeft();
                yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                // leftAxis.setTextColor(ColorTemplate.getHoloBlue());

                yAxis.setDrawGridLines(true);
                yAxis.setTextColor(Color.rgb(255, 192, 56));
                YAxis rightAxis = chart.getAxisRight();
                yAxis.setDrawZeroLine(true);
                rightAxis.setEnabled(false);

                yAxis.setAxisMinimum(0);
                chart.getAxisLeft().setStartAtZero(true);


                LineDataSet dataSet = new LineDataSet(entries, "Label");
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                LineData lineData = new LineData(dataSet);
                chart.setData(lineData);
                chart.invalidate();

                // enable scaling and dragging
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);


                LineDataSet dataset = new LineDataSet(entries, "");
                dataset.isDrawCirclesEnabled();
                dataset.setCircleRadius(0f);
                dataset.setDrawFilled(true);
                dataset.setFillColor(Color.rgb(0, 0, 0));
                dataset.setLineWidth(0.2f);
                dataset.setValueTextSize(0f);


            }
        });



        // Inflate the layout for this fragment
        return rootView;
    }

}
