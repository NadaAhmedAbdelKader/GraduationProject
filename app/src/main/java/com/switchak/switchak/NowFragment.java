package com.switchak.switchak;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;


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


        // TODO: 07/03/2018 refer to this, add observer
        FirebaseUtils.getInstance().addObserver(this);
        update(null, null);

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: 07/03/2018 refer to this, don't forget to delete the observer
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseUtils.getInstance().deleteObserver(this);
    }

    // TODO: 07/03/2018 refer to this for updating
    //Osama 07/03/2018
    //Implementation for the Observer interface to update the fragment views
    @Override
    public void update(Observable observable, Object o) {
        mAdapter.notifyDataSetChanged();
        latestReadingTextView.setText("" + FirebaseUtils.getInstance().getTotalLatestReading());
        totalReadingTextView.setText("" + FirebaseUtils.getInstance().getTotalReading());
    }
}
