package com.switchak.switchak;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muham on 08/02/2018.
 */

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {


    private int numberOfRooms;
    private List<Room> rooms = new ArrayList<>();


    private static final String TAG = "RoomsAdapter";

    public RoomsAdapter() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Log.d(TAG, "UID is: " + FirebaseAuth.getInstance().getCurrentUser().getUid());



        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                numberOfRooms = (int) dataSnapshot.getChildrenCount();
//                while (dataSnapshot.getChildren().iterator().hasNext()) {
//                    rooms.add(new Room(dataSnapshot.getChildren().iterator().next().getKey()));
//                    notifyItemChanged(rooms.size() - 1);
//                }
                rooms.add(new Room(dataSnapshot.getKey()));

                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + numberOfRooms);
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


    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
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


        TextView roomName;
        TextView roomReading;

        public RoomViewHolder(View itemView) {
            super(itemView);

            roomName = (TextView) itemView.findViewById(R.id.tv_room_name);
            roomReading = (TextView) itemView.findViewById(R.id.tv_room_reading);
        }

        public void bind(Room room) {
            roomName.setText(room.getRoomName());
            roomReading.setText(String.valueOf(room.getReadings().get(room.getReadings().size() - 1)));
        }
    }

}
