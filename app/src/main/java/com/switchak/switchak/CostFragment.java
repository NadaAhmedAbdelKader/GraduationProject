package com.switchak.switchak;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


public class CostFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cost, container, false);

        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_cost_rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        final RoomsAdapter mAdapter = new RoomsAdapter("history");
        mRoomsList.setAdapter(mAdapter);

        //jannat

        int roomnum= mAdapter.getRooms().size();

        PieChart pieChart = rootView.findViewById(R.id.pie_chart);
        pieChart.setUsePercentValues(true);

        List<PieEntry> entries = new ArrayList<>();

       /* entries.add(new PieEntry(1, (float) 12.5));
        entries.add(new PieEntry(2, (float) 17.5));
        entries.add(new PieEntry(3, (float) 10));
        entries.add(new PieEntry(4, (float) 20)); */

       for (int i=0 ; i<roomnum ; i++){
           entries.add(new PieEntry(new Float(mAdapter.getRooms().get(i).getTotalReadings())));
       }


        PieDataSet dataSet = new PieDataSet(entries, "Label");

        final int[] MY_COLORS = {Color.rgb(192,192,0), Color.rgb(255,0,0), Color.rgb(0,0,192),Color.rgb(0,200,0) };
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c: MY_COLORS) colors.add(c);

        dataSet.setColors(colors);
        dataSet.setValueTextSize(15f);


        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // refresh
        pieChart.setUsePercentValues(true);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(60f);
        pieChart.setTransparentCircleRadius(55f);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);




        pieChart.setEntryLabelColor(Color.WHITE);

        // Inflate the layout for this fragment
        return rootView;
    }

}
