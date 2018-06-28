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
    private String totalReading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cost, container, false);
        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_cost_rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        mAdapter = new RoomsAdapter("cost");
        mRoomsList.setAdapter(mAdapter);
        mRoomsList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        Fragment changePeriodFragment = new ChangePeriodFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_change_period, changePeriodFragment);
        transaction.commit();

        pieChart = rootView.findViewById(R.id.pie_chart);
        pieEntries = House.getInstance().getPieEntries();

        dataSet = new PieDataSet(pieEntries, "Wh");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(15f);
        dataSet.setSliceSpace(5);
//        dataSet.setValueFormatter(new IValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//                return String.format(Locale.US, "%.2g", getCostFromUsage(value));
//            }
//        });


        pieChart.setUsePercentValues(false);


        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.setEntryLabelColor(Color.WHITE);

        pieData = new PieData(dataSet);

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

    public float getCostFromUsage(float usage) {
        float value = House.getInstance().getThisMonthReading() / 1000;
        float cost = 0;
        usage = usage / 1000;

        if (value >= 0 && value <= 50)
            cost = usage * 0.13f;
        else if (value >= 51 && value <= 100)
            cost = usage * 0.22f;
        else if (value > 100 && value <= 200)
            cost = usage * 0.22f;
        else if (value > 200 && value <= 350)
            cost = usage * 0.45f;
        else if (value > 350 && value <= 650)
            cost = usage * 0.55f;
        else if (value > 650 && value <= 1000)
            cost = usage * 0.95f;
        else if (value > 1000)
            cost = usage * 1.35f;
        return cost;
    }

    @Override
    public void update(Observable o, Object arg) {


        if (House.getInstance().getPieEntries().size() > 0)
            pieChart.setData(pieData);

        pieData.notifyDataChanged();
        pieChart.notifyDataSetChanged();

        if (arg != null && (int) arg == FirebaseUtils.READING_ADDED) {
            float timeOfLastReading = House.getInstance().getEntries().get(House.getInstance().getEntries().size() - 1).getX();
            if (timeOfLastReading >= beginningTime
                    && timeOfLastReading < endTime) {
                for (int i = 0; i < House.getInstance().getRooms().size(); i++) {


                    if (House.getInstance().getRooms().get(i).getReadings().size() > 0) {
                        float newReading = pieEntries.get(i).getValue() + House.getInstance().getRooms().get(i)
                                .getReadings().get(House.getInstance().getRooms().get(i).getReadings().size() - 1);

                        pieEntries.set(i, new PieEntry(newReading));
                        FirebaseUtils.getInstance().getRooms().get(i).setSelectedPeriodReading(newReading);
                    }
                }
                pieData.notifyDataChanged();
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
            FirebaseUtils.getInstance().update();
        }


        //If user changed period
        if (arg != null && (int) arg == FirebaseUtils.PERIOD_CHANGED) {
            beginningTime = FirebaseUtils.getInstance().getBeginningTime();
            endTime = FirebaseUtils.getInstance().getEndTime();
            for (int i = 0; i < House.getInstance().getRooms().size(); i++) {
                float roomIReading = 0;
                for (int j = 0; j < House.getInstance().getRooms().get(i).getReadings().size(); j++) {
                    float timeOfJReading = House.getInstance().getEntries().get(j).getX();
                    if (timeOfJReading >= beginningTime && timeOfJReading < endTime)
                        roomIReading = roomIReading + House.getInstance().getRooms().get(i).getReadings().get(j);
                }
                pieEntries.set(i, new PieEntry(getCostFromUsage(roomIReading)));
                pieEntries.set(i, new PieEntry(roomIReading));
                FirebaseUtils.getInstance().getRooms().get(i).setSelectedPeriodReading(roomIReading);
            }
            pieData.notifyDataChanged();
            pieChart.notifyDataSetChanged();
            pieChart.animateXY(1000, 1000);
            FirebaseUtils.getInstance().update();
        }
        mAdapter.notifyDataSetChanged();
    }


}

