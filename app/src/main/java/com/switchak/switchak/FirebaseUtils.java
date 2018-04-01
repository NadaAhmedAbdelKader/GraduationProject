package com.switchak.switchak;

import android.util.Log;

import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.StringTokenizer;

/**
 * Created by Osama on 07/03/2018.
 * Singleton for retrieving data from firebase,
 * avoiding multiple instances to avoid multiple listeners overhead
 */

class FirebaseUtils extends Observable {
    public List<PieEntry> getEntries() {
        return entries;
    }

    private final  List<PieEntry> entries = new ArrayList<>();
    private static final FirebaseUtils ourInstance = new FirebaseUtils();
    private float totalLatestReading;
    private float totalReading;
    private List<Room> rooms;


    private FirebaseUtils() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        DatabaseReference myRef = database.getReference();


        rooms = new ArrayList<>();
        ChildEventListener roomsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey() != null) {
                    Room room = new Room(dataSnapshot.getKey());
                    if (dataSnapshot.hasChild("room_name"))
                        room.setRoomName(dataSnapshot.child("room_name").getValue(String.class));
                    if (dataSnapshot.hasChild("power"))
                        room.setPower(dataSnapshot.child("power").getValue(Integer.class) > 0);
                    rooms.add(room);
                    entries.add(new PieEntry(room.getTotalReadings()));
                    // TODO: 07/03/2018 notify rooms adapters that a room is added and implement the update
                    //notifyItemInserted(rooms.size() - 1);
                    Log.e("room item inserted", String.valueOf(room.isPower()) + dataSnapshot.getKey());
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

        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rooms").addChildEventListener(roomsListener);
        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rooms").keepSynced(true);


        ChildEventListener readingsListener = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid())
                .child("readings").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Object value = dataSnapshot.getValue();
                        String values = value != null ? value.toString() : null;

                        StringTokenizer stringTokenizer = new StringTokenizer(values, ",", false);

                        totalLatestReading = 0;

                        for (int i = 0; i < rooms.size(); i++) {
                            if (i == 0) totalLatestReading = 0;
                            if (stringTokenizer.hasMoreTokens()) {
                                float reading = Float.parseFloat(stringTokenizer.nextToken());
                                reading = (float) Math.floor(reading * 100) / 100;
                                rooms.get(i).getReadings().add(reading);
                                rooms.get(i).addReadings(reading);
                                totalLatestReading += reading;
                                totalLatestReading = (float) Math.floor(totalLatestReading * 100) / 100;
                                PieEntry entry = new PieEntry(totalLatestReading);
                                entries.set(i, entry);
                            }
                        }
                        totalReading += (totalLatestReading / 3600);

                        // TODO: 07/03/2018 refer to these 2 lines for notifying
                        setChanged();
                        notifyObservers();

                        for (int i = 0; i < rooms.size(); i++) {
                            String time = dataSnapshot.getKey();
                            try {
                                Timestamp timestamp = new Timestamp(Long.parseLong(time));
                                rooms.get(i).getTimestampList().add(timestamp);
                            } catch (NumberFormatException e) {
                                //do nothing
                            }
                        }
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
                });


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


}
