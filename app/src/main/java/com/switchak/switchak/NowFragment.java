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
import java.util.FormatFlagsConversionMismatchException;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;


public class NowFragment extends Fragment implements Observer {


    private RoomsAdapter mAdapter;
    private TextView latestReadingTextView;
    private TextView totalReadingTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_now, container, false);


        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_now_rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        mAdapter = new RoomsAdapter("now");
        mRoomsList.setAdapter(mAdapter);


        //prototype
        latestReadingTextView = rootView.findViewById(R.id.tv_latest_reading);
        totalReadingTextView = rootView.findViewById(R.id.tv_total_reading);



        FirebaseUtils.getInstance().addObserver(this);
        update(null, null);

        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void update(Observable observable, Object o) {
        mAdapter.notifyDataSetChanged();
        latestReadingTextView.setText("" + FirebaseUtils.getInstance().getTotalLatestReading());
        totalReadingTextView.setText(""+FirebaseUtils.getInstance().getTotalReading());
    }
}
