package com.switchak.switchak;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;


public class HistoryFragment extends Fragment implements Observer {

    private RoomsAdapter mAdapter;
    private LineChart chart;
    private LineDataSet lineDataSet;
    LineData lineData;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("history ", "view created");

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_history_rooms);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        mAdapter = new RoomsAdapter("history");
        mRoomsList.setAdapter(mAdapter);
        mRoomsList.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));

        chart = rootView.findViewById(R.id.chart);

        Fragment changePeriodFragment = new ChangePeriodFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_change_period, changePeriodFragment);
        transaction.commit();


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar c = new GregorianCalendar();
                c.setTime(new Date((long) value));

                String hour = String.valueOf(c.get(Calendar.HOUR));
                int amPm = c.get(Calendar.AM_PM);
                String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                if (chart.getHighestVisibleX() - chart.getLowestVisibleX() < 86400000 * 4) //milliseconds in a day = 86400000
                    return day + " " + hour + (amPm > 0 ? "PM" : "AM");
                else return day;
            }
        });


        YAxis yAxis = chart.getAxisLeft();

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        yAxis.setAxisMinimum(0);


        lineDataSet = House.getInstance().getDataSet();
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setLineWidth(3);
        lineDataSet.setDrawFilled(true);

        lineData = new LineData(lineDataSet);

        // enable scaling and dragging
        chart.setDragXEnabled(true);
        chart.setScaleXEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setVisibleXRangeMinimum(2147483647); //max float number
        xAxis.setLabelCount(4);


        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawFilled(true);


        //Registering observer
        FirebaseUtils.getInstance().addObserver(this);
        update(null, null);


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        chart.animateY(1000);
    }

    public void onDestroyView() {
        super.onDestroyView();

        //Delete observer
        FirebaseUtils.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {

        if (House.getInstance().getEntries().size() > 0)
            chart.setData(lineData);

        lineData.notifyDataChanged();
        chart.notifyDataSetChanged();
        if (FirebaseUtils.getInstance().getBeginningTime() != chart.getXAxis().getAxisMinimum()
                || FirebaseUtils.getInstance().getEndTime() != chart.getXAxis().getAxisMaximum()) {
            chart.getXAxis().setAxisMinimum(FirebaseUtils.getInstance().getBeginningTime());
            chart.getXAxis().setAxisMaximum(FirebaseUtils.getInstance().getEndTime());
            chart.animateY(1000);
        } else chart.invalidate();
        mAdapter.notifyDataSetChanged();

    }
}
