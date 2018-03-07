package com.switchak.switchak;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.StringTokenizer;


public class NowFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_now, container, false);


        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_now_rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        final RoomsAdapter mAdapter = new RoomsAdapter("now");
        mRoomsList.setAdapter(mAdapter);


        //prototype
        final double[] totalReading = {0f};
        final TextView latestReadingTextView = rootView.findViewById(R.id.tv_latest_reading);
        final TextView totalReadingTextView = rootView.findViewById(R.id.tv_total_reading);


        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid())
                .child("readings").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Object value = dataSnapshot.getValue();
                String values = value.toString();

                StringTokenizer stringTokenizer = new StringTokenizer(values, ",", false);

                float totalLatestReading = 0;

                for (int i = 0; i < mAdapter.getRooms().size(); i++) {
                    if (i == 0) {
                        totalLatestReading = 0;
                    }
                    if (stringTokenizer.hasMoreTokens()) {
                        float reading = Float.parseFloat(stringTokenizer.nextToken().toString());
                        reading = (float) Math.floor(reading * 100) / 100;
                        mAdapter.getRooms().get(i).getReadings().add(reading);
                        mAdapter.getRooms().get(i).addReadings(reading);
                        totalLatestReading = totalLatestReading + reading;
                        totalLatestReading = (float) Math.floor(totalLatestReading * 100) / 100;
                    }
                }

                latestReadingTextView.setText("" + totalLatestReading);
                totalReading[0] += totalLatestReading;
                totalReadingTextView.setText("" + totalReading[0]);

                for (int i = 0; i < mAdapter.getRooms().size(); i++) {
                    String time = dataSnapshot.getKey();
                    try {
                        Timestamp timestamp = new Timestamp(Long.parseLong(time));
                        mAdapter.getRooms().get(i).getTimestampList().add(timestamp);
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


        // Inflate the layout for this fragment
        return rootView;
    }


}
