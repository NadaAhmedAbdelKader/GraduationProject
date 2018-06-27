package com.switchak.switchak;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

/**
 * Created by Osama on 08/02/2018.
 * Recycler view adapter that populates the rooms list with views,
 * number of views equals number of rooms
 */


//Adapter that gets the data from firebase and populate the recycler view
public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {


    private final List<Room> rooms = FirebaseUtils.getInstance().getRooms();
    private final String fragment;
    private int lastSelectedPosition = -1;

    RoomsAdapter(String fragment) {
        this.fragment = fragment;
    }


    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        //keeping data fresh - Jannat

        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference().child("rooms");
        roomsRef.keepSynced(true);

        int layoutIdForListItem;
        if (fragment.equals("history"))
            layoutIdForListItem = R.layout.view_history_room;
        else if (fragment.equals("now"))
            layoutIdForListItem = R.layout.view_now_room;
        else if (fragment.equals("cost"))
            layoutIdForListItem = R.layout.view_cost_room;
        else
            layoutIdForListItem = R.layout.view_history_room;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RoomViewHolder(view, fragment);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.bind(rooms.get(position));


    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }


    class RoomViewHolder extends RecyclerView.ViewHolder {
        final TextView roomName;
        final TextView roomReading;
        final Switch roomPower;
        public RadioButton selectionState;

        RoomViewHolder(final View itemView, String fragment) {
            super(itemView);

            roomName = itemView.findViewById(R.id.tv_room_name);
            roomReading = itemView.findViewById(R.id.tv_room_reading);
            roomPower = itemView.findViewById(R.id.switch_room_power);
            selectionState = itemView.findViewById(R.id.rb_room_select);
        }

        void bind(final Room room) {
            if (room.getRoomName() != null)
                roomName.setText(room.getRoomName());
            else
                roomName.setText(room.getRoomId());

            if (fragment.equals("history")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lastSelectedPosition == getAdapterPosition())
                            lastSelectedPosition = -1;
                        else
                            lastSelectedPosition = getAdapterPosition();
                        notifyDataSetChanged();
                    }
                });

                selectionState.setChecked(lastSelectedPosition == getAdapterPosition());
            }


            if (fragment.equals("cost"))
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        builder.setTitle("Room name");

                        LinearLayout.LayoutParams lp =
                                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                        final EditText editText = new EditText(itemView.getContext());
                        editText.setText(roomName.getText());

                        editText.setLayoutParams(lp);

                        builder.setView(editText);


                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseDatabase.getInstance().getReference()
                                        .child(FirebaseAuth.getInstance().getUid()).child("rooms")
                                        .child(room.getRoomId())
                                        .child("room_name")
                                        .setValue(editText.toString());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                        builder.show();
                        return true;
                    }
                });

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
//                roomReading.setText(String.format(Locale.US, "%.2f", room.getReadings().get(room.getReadings().size() - 1)));

                if (fragment.equals("history"))
                    roomReading.setText(String.format(Locale.US, "%.2g", room.getSelectedPeriodReading()));
                else if (fragment.equals("cost"))
//                    roomReading.setText(String.format(Locale.US, "%.2g", getCostFromUsage(room.getSelectedPeriodReading())));
                    roomReading.setText(String.format(Locale.US, "%.2g", room.getSelectedPeriodReading()));
                else if (fragment.equals("now"))
                    roomReading.setText(String.format(Locale.US, "%.2g", room.getReadings().get(room.getReadings().size() - 1)));

            } catch (Exception e) {
                roomReading.setText("");
            }
        }


        public float getCostFromUsage(float value) {
            value = House.getInstance().getThisMonthReading() / 1000;
            float cost = 0;

            if (value >= 0 && value <= 50)
                cost = value * 0.13f;
            else if (value >= 51 && value <= 100)
                cost = value * 0.22f;
            else if (value > 100 && value <= 200)
                cost = value * 0.22f;
            else if (value > 200 && value <= 350)
                cost = value * 0.45f;
            else if (value > 350 && value <= 650)
                cost = value * 0.55f;
            else if (value > 650 && value <= 1000)
                cost = value * 0.95f;
            else if (value > 1000)
                cost = value * 1.35f;
            return cost;
        }
    }
}