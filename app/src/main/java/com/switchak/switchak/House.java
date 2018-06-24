package com.switchak.switchak;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class House {


    private final List<PieEntry> pieEntries;

    private static final House ourInstance = new House();
    private final List<Entry> entries;
    private final LineDataSet dataSet;

    public static House getInstance() {
        return ourInstance;
    }

    private House() {
        entries = new ArrayList<>();
        pieEntries = new ArrayList<>();
        dataSet = new LineDataSet(entries, "kWatts");
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public LineDataSet getDataSet() {
        return dataSet;
    }

    public List<Room> getRooms() {
        return FirebaseUtils.getInstance().getRooms();
    }

    public List<PieEntry> getPieEntries() {
        return pieEntries;
    }

}
