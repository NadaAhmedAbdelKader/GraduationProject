package com.switchak.switchak;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;


public class HistoryFragment extends Fragment implements Observer {

    RoomsAdapter mAdapter;
    BarChart chart;
    BarData barData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("history ", "view created");

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_history_rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        mAdapter = new RoomsAdapter("history");
        mRoomsList.setAdapter(mAdapter);
        chart = rootView.findViewById(R.id.chart);


        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(calendar.getTime().getTime());
//        xAxis.setAvoidFirstLastClipping(true);
//        xAxis.setDrawAxisLine(true);
//        xAxis.setEnabled(true);
//        xAxis.setTextSize(10f);
//        xAxis.setTextColor(Color.WHITE);
//        xAxis.setDrawAxisLine(true);
//        xAxis.setDrawGridLines(true);
//        xAxis.setTextColor(Color.rgb(255, 100, 100));
//        xAxis.setCenterAxisLabels(true);
//        xAxis.setGranularity(1f); // one hour
//
//        xAxis.setAxisMinimum(0);
//        xAxis.setAxisMaximum(31);
//
//
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("dd  ");


            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis((long) value);
//                long millis = TimeUnit.DAYS.toMillis((long) value);
                return mFormat.format(c.get(Calendar.DAY_OF_MONTH));
            }
        });
//
//
        YAxis yAxis = chart.getAxisLeft();
//        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
//        yAxis.setTextColor(ColorTemplate.getHoloBlue());
//
//        yAxis.setDrawGridLines(true);
//        yAxis.setTextColor(Color.rgb(255, 192, 56));
        YAxis rightAxis = chart.getAxisRight();
//        yAxis.setDrawZeroLine(true);
        rightAxis.setEnabled(false);
//
        yAxis.setAxisMinimum(0);
//
//


//        dataSet.isDrawCirclesEnabled();
//        dataSet.setCircleRadius(0f);
//        dataSet.setDrawFilled(true);
//        dataSet.setFillColor(Color.rgb(0, 0, 0));
//        dataSet.setLineWidth(0.2f);
//        dataSet.setValueTextSize(0f);


        barData = new BarData(House.getInstance().getDataSet());
        barData.setBarWidth(1000000);


        // enable scaling and dragging
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);


        //Registering observer
        FirebaseUtils.getInstance().addObserver(this);
        update(null, null);


        // Inflate the layout for this fragment
        return rootView;
    }

    public void onDestroyView() {
        super.onDestroyView();

        //Delete observer
        FirebaseUtils.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {

        if (House.getInstance().getEntries().size() > 0)
            chart.setData(barData);
        barData.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();
        mAdapter.notifyDataSetChanged();

    }
}
