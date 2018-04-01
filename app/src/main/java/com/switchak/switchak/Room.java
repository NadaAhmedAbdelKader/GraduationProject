package com.switchak.switchak;

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
    private float totalReadings = 0f;
    //Variables
    private String roomId;
    private String roomName;
    private boolean power;
    private List<Float> readings = new ArrayList<>();
    private List<Timestamp> timestampList = new ArrayList<>();

    //Constructor that takes room name
    Room(String roomId) {
        this.roomId = roomId;
        totalReadings = 0;
    }


    void addReadings(Float Reading) {
        totalReadings = totalReadings + Reading;
    }

    //Getters and setters
    float getTotalReadings() {
        return totalReadings;
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

    boolean isPower() {
        return power;
    }

    void setPower(boolean power) {
        this.power = power;
    }
}