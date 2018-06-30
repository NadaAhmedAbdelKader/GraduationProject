package com.switchak.switchak;

import com.github.mikephil.charting.data.Entry;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Osama on 06/02/2018.
 * Represents a room the gets retrieved from the backend,
 * and gets populated with data that corresponds to that room
 */

class Room {


    //Total Readings for each room
    private float thisMonthReading;
    private float totalReading;
    private float selectedPeriodReading;
    //Variables
    private String roomId;
    private String roomName;
    private boolean power;
    private final List<Float> readings = new ArrayList<>();
    private final List<Timestamp> timestampList = new ArrayList<>();
    private final List<Entry> entries;

    //Constructor that takes room name
    Room(String roomId) {
        this.roomId = roomId;
        thisMonthReading = 0;
        totalReading = 0;
        selectedPeriodReading = 0;
        entries = new ArrayList<>();
    }


    void addReading(Float Reading) {
        thisMonthReading = thisMonthReading + Reading;
    }

    //Getters and setters
    float getThisMonthReading() {
        return thisMonthReading;
    }


    float getTotalReading() {
        return totalReading;
    }

    void setTotalReading(float totalReading) {
        this.totalReading = totalReading;
    }

    String getRoomId() {
        return roomId;
    }

    void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    String getRoomName() {
        return roomName;
    }

    void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    List<Timestamp> getTimestampList() {
        return timestampList;
    }


    List<Float> getReadings() {
        return readings;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    boolean isPower() {
        return power;
    }

    void setPower(boolean power) {
        this.power = power;
    }

    public float getSelectedPeriodReading() {
        return selectedPeriodReading;
    }

    public void setSelectedPeriodReading(float selectedPeriodReading) {
        this.selectedPeriodReading = selectedPeriodReading;
    }
}