package com.switchak.switchak;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class House {


    private static final House ourInstance = new House();
    private List<BarEntry> entries;
    private Float firstTimeStamp;
    private BarDataSet dataSet;

    public static House getInstance() {
        return ourInstance;
    }

    private House() {
        entries = new ArrayList<>();
        dataSet = new BarDataSet(getEntries(), "kWatts");
    }

    public void setFirstTimeStamp(Float timeStamp) {
        firstTimeStamp = timeStamp;
    }

    public List<BarEntry> getEntries() {
        return entries;
    }

    public BarDataSet getDataSet() {
        return dataSet;
    }

    public Float getFirstTimeStamp() {
        return firstTimeStamp;
    }
}
