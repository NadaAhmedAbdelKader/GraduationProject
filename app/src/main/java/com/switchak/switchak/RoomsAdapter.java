package com.switchak.switchak;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by Osama on 08/02/2018.
 * Recycler view adapter that populates the rooms list with views,
 * number of views equals number of rooms
 */


//Adapter that gets the data from firebase and populate the recycler view
public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {


    private List<Room> rooms = FirebaseUtils.getInstance().getRooms();
    private String fragment;
    RoomsAdapter(String fragment) {
        this.fragment = fragment;
    }


    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        //keeping data fresh - Jannat

        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference().child("rooms");
        roomsRef.keepSynced(true);

        int layoutIdForListItem;
        if (fragment.equals("now"))
            layoutIdForListItem = R.layout.view_now_room;
        else
            layoutIdForListItem = R.layout.view_history_room;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RoomViewHolder(view, fragment);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        holder.bind(rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public List<Room> getRooms() {
        return rooms;
    }


    class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomName;
        TextView roomReading;
        Switch roomPower;

        RoomViewHolder(View itemView, String fragment) {
            super(itemView);

            roomName = itemView.findViewById(R.id.tv_room_name);
            roomReading = itemView.findViewById(R.id.tv_room_reading);
            roomPower = itemView.findViewById(R.id.switch_room_power);
        }

        void bind(final Room room) {
            if (room.getRoomName() != null)
                roomName.setText(room.getRoomName());
            else
                roomName.setText(room.getRoomId());

            if (fragment.equals("now")) {
                roomPower.setChecked(room.isPower());
                roomPower.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid())
                                .child("rooms").child(room.getRoomId()).child("power").setValue(roomPower.isChecked() ? 1 : 0);
                    }
                });
            }

            try {
                roomReading.setText(String.valueOf(room.getReadings().get(room.getReadings().size() - 1)));
            } catch (Exception e) {
                roomReading.setText("");
            }
        }
    }
}