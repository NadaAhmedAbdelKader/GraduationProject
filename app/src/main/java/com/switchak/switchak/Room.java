package com.switchak.switchak;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muham on 06/02/2018.
 */

public class Room {
    public Room(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    String roomName;

    public List<Timestamp> getTimestampList() {
        return timestampList;
    }

    public void setTimestampList(List<Timestamp> timestampList) {
        this.timestampList = timestampList;
    }

    List<Timestamp> timestampList = new ArrayList<>();

    public List<Float> getReadings() {
        return readings;
    }

    public void setReadings(List<Float> readings) {
        this.readings = readings;
    }

    List<Float> readings = new ArrayList<>();

}
