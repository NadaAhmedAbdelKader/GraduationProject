package com.switchak.switchak;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Observable;
import java.util.StringTokenizer;

/**
 * Created by Osama on 07/03/2018.
 * Singleton for retrieving data from firebase,
 * avoiding multiple instances to avoid multiple listeners overhead
 */

class FirebaseUtils extends Observable {
    public static final int READING_ADDED = 0;
    public static final int PERIOD_CHANGED = 1;


    private static final FirebaseUtils ourInstance = new FirebaseUtils();
    private float totalLatestReading;
    private float totalReading;
    private final List<Room> rooms;
    private int roomsCount;
    private final DatabaseReference myRef;
    private ChildEventListener roomsListener;
    private ChildEventListener readingsEventListener;
    private int monthSelection;
    private int daySelection;
    private long beginningTime;
    private long endTime;


    private FirebaseUtils() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        myRef = database.getReference();


        rooms = new ArrayList<>();

        roomsCount = -1;
        monthSelection = -1;
        daySelection = -1;
        setMonthSelection(0);
        setDaySelection(0);


        //Initial listener to get the number of rooms (used to create objects of rooms first before populating these objects with data)
        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rooms")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("child name", dataSnapshot.getKey());
                        roomsCount = (int) dataSnapshot.getChildrenCount();
                        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rooms").addChildEventListener(roomsListener);
                        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rooms").keepSynced(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        roomsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey() != null) {
                    Room room = new Room(dataSnapshot.getKey());
                    if (dataSnapshot.hasChild("room_name"))
                        room.setRoomName(dataSnapshot.child("room_name").getValue(String.class));
                    if (dataSnapshot.hasChild("power"))
                        room.setPower(dataSnapshot.child("power").getValue(Integer.class) > 0);
                    rooms.add(room);

                    //When all rooms are added start getting readings data
                    if (rooms.size() == roomsCount) {
                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseAuth.getInstance().getUid())
                                .child("readings").addChildEventListener(readingsEventListener);
                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseAuth.getInstance().getUid())
                                .child("readings").keepSynced(true);
                    }
                    House.getInstance().getPieEntries().add(new PieEntry(0));

                    // 07/03/2018 notify rooms adapters that a room is added and implement the update
                    setChanged();
                    notifyObservers();

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int index = -1;

                if (dataSnapshot.getKey() != null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(dataSnapshot.getKey(), "_", false);
                    stringTokenizer.nextToken();
                    index = Integer.parseInt(stringTokenizer.nextToken());
                    index--;
                }
                if (index > -1) {
                    Room room = rooms.get(index);
                    room.setRoomId(dataSnapshot.getKey());
                    if (dataSnapshot.hasChild("room_name"))
                        room.setRoomName(dataSnapshot.child("room_name").getValue(String.class));
                    if (dataSnapshot.hasChild("power"))
                        room.setPower(dataSnapshot.child("power").getValue(Integer.class) > 0);
                    // TODO: 07/03/2018 notify rooms adapters that a room is changed and implement the update
                    //notifyItemChanged(index);
                    Log.e("room item changed", String.valueOf(room.isPower()) + dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = -1;

                if (dataSnapshot.getKey() != null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(dataSnapshot.getKey(), "_", false);
                    stringTokenizer.nextToken();
                    index = Integer.parseInt(stringTokenizer.nextToken());
                    index--;
                }

                if (index > -1) {
                    rooms.remove(index);
                    // TODO: 07/03/2018 notify rooms adapters that a room is removed and implement the update
                    //notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //set calender to the beginning of current month
        final Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        totalReading = 0;

        readingsEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Object value = dataSnapshot.getValue();
                String values = value != null ? value.toString() : null;

                String time = dataSnapshot.getKey();


                StringTokenizer stringTokenizer = new StringTokenizer(values, ",", false);

                totalLatestReading = 0;

                boolean readingBelongsToThisMonth = Long.parseLong(dataSnapshot.getKey()) > calendar.getTime().getTime();

                for (int i = 0; i < rooms.size(); i++) {
                    if (i == 0) totalLatestReading = 0;
                    if (stringTokenizer.hasMoreTokens()) {
                        float reading = Float.parseFloat(stringTokenizer.nextToken());

                        //add the room reading to the list of readings of that room
                        rooms.get(i).getReadings().add(reading);
//                        rooms.get(i).getEntries().add(new Entry(reading, Float.parseFloat(time)));

                        //increase the sum this month readings by that reading
                        if (readingBelongsToThisMonth) {
                            rooms.get(i).addReading(reading);
                        }

                        totalLatestReading += reading;
                    }
                }

                if (readingBelongsToThisMonth) {
                    totalReading += (totalLatestReading / 3600);
                    House.getInstance().setThisMonthReading(House.getInstance().getThisMonthReading() + totalLatestReading);
                }


                for (int i = 0; i < rooms.size(); i++) {
                    try {
                        Timestamp timestamp = new Timestamp(Long.parseLong(time));
                        rooms.get(i).getTimestampList().add(timestamp);
                    } catch (NumberFormatException e) {
                        //do nothing
                    }
                }

                House.getInstance().getDataSet()
                        .addEntry(new Entry(Float.parseFloat(dataSnapshot.getKey()), totalLatestReading));

                // 07/03/2018 notifying observers
                setChanged();
                notifyObservers(READING_ADDED);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    static FirebaseUtils getInstance() {
        return ourInstance;
    }

    float getTotalLatestReading() {
        return totalLatestReading;
    }

    float getTotalReading() {
        return totalReading;
    }

    List<Room> getRooms() {
        return rooms;
    }

    public void setMonthSelection(int monthSelection) {
        if (monthSelection != this.monthSelection) {
            this.monthSelection = monthSelection;

            //set calender to beginning of selected month
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH) - monthSelection);

            //set beginningTime to the corresponding selection
            beginningTime = calendar.getTime().getTime();

            //set calender to end to selected month
            calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH) - monthSelection + 1);

            //set endTime to the corresponding selection
            endTime = calendar.getTime().getTime();

            daySelection = 0;
            setChanged();
            notifyObservers(PERIOD_CHANGED);
        }
    }

    public int getMonthSelection() {
        return monthSelection;
    }

    public int getDaySelection() {
        return daySelection;
    }

    public void setDaySelection(int daySelection) {
        if (daySelection != this.daySelection) {
            this.daySelection = daySelection;


            if (daySelection == 0) {
                //set calender to beginning of selected month
                Calendar calendar = new GregorianCalendar();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH) - monthSelection);

                //set beginningTime to the corresponding selection
                beginningTime = calendar.getTime().getTime();

                //set calender to end to selected month
                calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH) - monthSelection + 1);

                //set endTime to the corresponding selection
                endTime = calendar.getTime().getTime();
            }


            if (daySelection != 0) {
                //set calender to beginning of selected month
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(new Date(beginningTime));

                if (monthSelection == 0) {
                    calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - daySelection + 1);
                    //set beginningTime to the corresponding selection
                    beginningTime = calendar.getTime().getTime();

                    calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - daySelection + 2);
                    //set endTime to the corresponding selection
                    endTime = calendar.getTime().getTime();

                } else {
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - daySelection + 1);
                    //set beginningTime to the corresponding selection
                    beginningTime = calendar.getTime().getTime();

                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - daySelection + 2);
                    //set endTime to the corresponding selection
                    endTime = calendar.getTime().getTime();
                }
            }
            setChanged();
            notifyObservers(PERIOD_CHANGED);
        }
    }

    public void update() {
        setChanged();
        notifyObservers();
    }

    public long getBeginningTime() {
        return beginningTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
