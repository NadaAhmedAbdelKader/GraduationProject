package com.switchak.switchak;

import android.util.Log;

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
 * Created by Muham on 07/03/2018.
 */

public class FirebaseUtils extends Observable {
    private static final FirebaseUtils ourInstance = new FirebaseUtils();
    private float totalLatestReading;
    private float totalReading;
    private List<Room> rooms;


    public static FirebaseUtils getInstance() {
        return ourInstance;
    }

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
                    //TODO: notify rooms adapters that a room is added
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
                    //TODO: notify rooms adapters that a room is changed
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
                    //TODO: notify rooms adapters that a room is removed
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
                        String values = value.toString();

                        StringTokenizer stringTokenizer = new StringTokenizer(values, ",", false);

                        totalLatestReading = 0;

                        for (int i = 0; i < rooms.size(); i++) {
                            if (i == 0) totalLatestReading = 0;
                            if (stringTokenizer.hasMoreTokens()) {
                                float reading = Float.parseFloat(stringTokenizer.nextToken().toString());
                                reading = (float) Math.floor(reading * 100) / 100;
                                rooms.get(i).getReadings().add(reading);
                                rooms.get(i).addReadings(reading);
                                totalLatestReading += reading;
                                totalLatestReading = (float) Math.floor(totalLatestReading * 100) / 100;
                            }
                        }
                        totalReading += totalLatestReading;

                        //TODO: refer to these 2 lines for notifying
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



    public float getTotalLatestReading() {
        return totalLatestReading;
    }
    public float getTotalReading() {
        return totalReading;
    }
    public List<Room> getRooms() {
        return rooms;
    }


}
