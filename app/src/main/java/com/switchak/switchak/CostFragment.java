package com.switchak.switchak;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class CostFragment extends Fragment implements Observer {
    private PieChart pieChart;

    private RoomsAdapter mAdapter;
    private List<PieEntry> pieEntries;
    private PieDataSet dataSet;
    private PieData pieData;
    private long beginningTime = FirebaseUtils.getInstance().getBeginningTime();
    private long endTime = FirebaseUtils.getInstance().getEndTime();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cost, container, false);
        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_cost_rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        mAdapter = new RoomsAdapter("history");
        mRoomsList.setAdapter(mAdapter);
        mRoomsList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Fragment changePeriodFragment = new ChangePeriodFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_change_period, changePeriodFragment);
        transaction.commit();

        pieChart = rootView.findViewById(R.id.pie_chart);
        pieEntries = House.getInstance().getPieEntries();
        dataSet = new PieDataSet(pieEntries, "Usage percentage");
        pieData = new PieData(dataSet);

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        pieChart.setUsePercentValues(true);


        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.setEntryLabelColor(Color.WHITE);
        dataSet.setValueTextSize(15f);
        dataSet.setSliceSpace(5);

        pieChart.setData(pieData);

        FirebaseUtils.getInstance().addObserver(this);
        update(null, null);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        pieChart.animateXY(1000, 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseUtils.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {

        mAdapter.notifyDataSetChanged();

        if (arg != null && (int) arg == FirebaseUtils.READING_ADDED) {
            if (House.getInstance().getEntries().get(House.getInstance().getEntries().size() - 1).getX() >= beginningTime
                    && House.getInstance().getEntries().get(House.getInstance().getEntries().size() - 1).getX() < endTime) {
                for (int i = 0; i < House.getInstance().getRooms().size(); i++) {
                    pieEntries.set(i, new PieEntry(pieEntries.get(i).getValue() + House.getInstance().getRooms().get(i)
                            .getReadings().get(House.getInstance().getEntries().size() - 1)));
                }
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
        }


        //If user changed period
        if (arg != null && (int) arg == FirebaseUtils.PERIOD_CHANGED) {
            beginningTime = FirebaseUtils.getInstance().getBeginningTime();
            endTime = FirebaseUtils.getInstance().getEndTime();
            for (int i = 0; i < House.getInstance().getRooms().size(); i++) {
                float roomIReading = 0;
                for (int j = 0; j < House.getInstance().getEntries().size(); j++) {
                    float timeOfJReading = House.getInstance().getEntries().get(j).getX();
                    if (timeOfJReading >= beginningTime && timeOfJReading < endTime)
                        roomIReading = roomIReading + House.getInstance().getRooms().get(i).getReadings().get(j);
                }
                pieEntries.set(i, new PieEntry(roomIReading));
            }
            pieChart.notifyDataSetChanged();
            pieChart.animateXY(1000, 1000);
        }
    }
}

