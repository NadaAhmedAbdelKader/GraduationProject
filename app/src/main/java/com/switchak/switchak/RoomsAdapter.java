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
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Osama on 08/02/2018.
 * Recycler view adapter that populates the rooms list with views,
 * number of views equals number of rooms
 */


//Adapter that gets the data from firebase and populate the recycler view
public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {


    private final List<Room> rooms = FirebaseUtils.getInstance().getRooms();
    private final String fragment;

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
        final TextView roomCost;
        final CircleImageView circleImageView;

        RoomViewHolder(final View itemView, String fragment) {
            super(itemView);

            roomName = itemView.findViewById(R.id.tv_room_name);
            roomReading = itemView.findViewById(R.id.tv_room_reading);
            roomCost = itemView.findViewById(R.id.tv_room_cost);
            roomPower = itemView.findViewById(R.id.switch_room_power);
            circleImageView = itemView.findViewById(R.id.circleImageView);
        }

        void bind(final Room room) {
            if (room.getRoomName() != null)
                roomName.setText(room.getRoomName());
            else
                roomName.setText(room.getRoomId());


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
                                    .setValue(editText.getText().toString());
                            notifyDataSetChanged();
                            FirebaseUtils.getInstance().update();
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

            circleImageView.setCircleBackgroundColor(ColorTemplate.MATERIAL_COLORS[getAdapterPosition()]);

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
                String selectedPeriodReading;
                if (room.getSelectedPeriodReading() / (3600 * 1000) < 1)
                    selectedPeriodReading = new DecimalFormat("#.##").format(room.getSelectedPeriodReading() / 3600) + " Wh";
                else
                    selectedPeriodReading = new DecimalFormat("#.##").format(room.getSelectedPeriodReading() / (3600 * 1000)) + " kWh";

                if (fragment.equals("history"))
                    roomReading.setText(selectedPeriodReading);
                else if (fragment.equals("cost")) {
                    roomReading.setText(selectedPeriodReading);
                    float cost = getCostFromUsage(room.getSelectedPeriodReading());
                    if (cost < 1)
                        roomCost.setText(new DecimalFormat("#").format(getCostFromUsage(room.getSelectedPeriodReading()) * 100) + " PT");
                    else
                        roomCost.setText(new DecimalFormat("#.##").format(getCostFromUsage(room.getSelectedPeriodReading())) + " LE");
                } else if (fragment.equals("now"))
                    roomReading.setText(new DecimalFormat("#.##").format(room.getReadings().get(room.getReadings().size() - 1)) + " W");

            } catch (Exception e) {
                roomReading.setText("");
            }
        }


        public float getCostFromUsage(float usage) {
            float value = House.getInstance().getThisMonthReading() / (3600 * 1000);
            float cost = 0;
            usage = usage / (1000 * 3600);

            if (value >= 0 && value <= 50)
                cost = usage * 0.13f;
            else if (value >= 51 && value <= 100)
                cost = usage * 0.22f;
            else if (value > 100 && value <= 200)
                cost = usage * 0.22f;
            else if (value > 200 && value <= 350)
                cost = usage * 0.45f;
            else if (value > 350 && value <= 650)
                cost = usage * 0.55f;
            else if (value > 650 && value <= 1000)
                cost = usage * 0.95f;
            else if (value > 1000)
                cost = usage * 1.35f;
            return cost;
        }
    }
}