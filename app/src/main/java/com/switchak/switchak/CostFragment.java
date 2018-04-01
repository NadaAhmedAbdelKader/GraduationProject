package com.switchak.switchak;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class CostFragment extends Fragment implements Observer {
    PieChart pieChart;

    RoomsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_cost, container, false);
        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_cost_rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        mAdapter = new RoomsAdapter("history");
        mRoomsList.setAdapter(mAdapter);
        Button button = rootView.findViewById(R.id.jannat);

        //Jannat

        // int roomnum = FirebaseUtils.getInstance().getRooms().size();

        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          int roomnum = mAdapter.getRooms().size();



                                          pieChart = rootView.findViewById(R.id.pie_chart);
                                          pieChart.setUsePercentValues(false);


                                         List<PieEntry> entries= FirebaseUtils.getInstance().getEntries();
//
//                                          for (int i = 0; i < roomnum; i++) {
//                                              entries.add(new PieEntry(mAdapter.getRooms().get(i).getTotalReadings()));
//                                          }


                                          PieDataSet dataSet = new PieDataSet(entries, "Label");

                                          final int[] MY_COLORS = {Color.rgb(192, 192, 0), Color.rgb(255, 0, 0), Color.rgb(0, 0, 192), Color.rgb(0, 200, 0)};
                                          ArrayList<Integer> colors = new ArrayList<>();

                                          for (int c : MY_COLORS) colors.add(c);

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


                                      }
                                  });

        // Inflate the layout for this fragment
        return rootView;

    }

    @Override
    public void update(Observable o, Object arg) {
        mAdapter.notifyDataSetChanged();
        pieChart.notifyDataSetChanged();

        // entries.add(new PieEntry(mAdapter.getRooms().get(mAdapter.getRooms().size()-1).getTotalReadings()));



    }
}

