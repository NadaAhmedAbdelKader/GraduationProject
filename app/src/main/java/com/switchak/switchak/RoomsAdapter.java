package com.switchak.switchak;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muham on 08/02/2018.
 */


//Adapter that gets the data from firebase and populate the recycler view
public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {


    private final List<Room> rooms = new ArrayList<>();
    private int numberOfRooms;


    RoomsAdapter() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();



        ChildEventListener roomsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Room room = new Room(dataSnapshot.getKey());
                if (dataSnapshot.hasChild("room_name"))
                    room.setRoomName(dataSnapshot.child("room_name").getValue(String.class));
                if (dataSnapshot.hasChild("power"))
                    room.setPower(dataSnapshot.child("power").getValue(Boolean.class));
                rooms.add(room);
                notifyItemInserted(rooms.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int index = -1;
                for (int i = 0; i < rooms.size(); i++) {
                    if (rooms.get(i).getRoomId().equals(dataSnapshot.getKey()))
                        index = i;
                }

                if (index > -1) {
                    Room room = rooms.get(index);
                    room.setRoomId(dataSnapshot.getKey());
                    if (dataSnapshot.hasChild("room_name"))
                        room.setRoomName(dataSnapshot.child("room_name").getValue(String.class));
                    if (dataSnapshot.hasChild("power"))
                        room.setPower(dataSnapshot.child("power").getValue(Boolean.class));
                    notifyItemChanged(index);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = -1;
                for (int i = 0; i < rooms.size(); i++) {
                    if (rooms.get(i).getRoomId().equals(dataSnapshot.getKey()))
                        index = i;
                }
                if (index > -1) {
                    rooms.remove(index);
                    notifyItemRemoved(index);
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


        ChildEventListener readingsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String sensor = dataSnapshot.child("sensor").getValue(String.class);
                for (int i = 0; i < rooms.size(); i++) {
                    if (rooms.get(i).getRoomId().equals(sensor)) {
                        rooms.get(i).getReadings().add((dataSnapshot.child("reading").getValue(Float.class)));
                        rooms.get(i).getTimes().add((dataSnapshot.child("time_stamp").getValue(Long.class)));
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
        };

        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("readings").addChildEventListener(readingsListener);
        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("readings").keepSynced(true);
    }


    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.view_room;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        holder.bind(rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }


    class RoomViewHolder extends RecyclerView.ViewHolder {
        final TextView roomName;
        final TextView roomReading;

        RoomViewHolder(View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.tv_room_name);
            roomReading = itemView.findViewById(R.id.tv_room_reading);
        }

        void bind(Room room) {
            if (room.getRoomName() != null)
                roomName.setText(room.getRoomName());
            else
                roomName.setText(room.getRoomId());

            try {
                roomReading.setText(String.valueOf(room.getReadings().get(room.getReadings().size() - 1)));
            } catch (Exception e) {
                roomReading.setText("N/A");
            }
        }
    }
}