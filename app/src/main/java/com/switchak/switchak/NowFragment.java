package com.switchak.switchak;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NowFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NowFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public double totalReading;


    private OnFragmentInteractionListener mListener;

    public NowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NowFragment newInstance(String param1, String param2) {
        NowFragment fragment = new NowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_now, container, false);


        totalReading = 0f;
        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_now_rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        RoomsAdapter mAdapter = new RoomsAdapter("now");
        mRoomsList.setAdapter(mAdapter);


        //prototype
        final TextView latestReadingTextView = (TextView) rootView.findViewById(R.id.tv_latest_reading);
        final TextView totalReadingTextView = (TextView) rootView.findViewById(R.id.tv_total_reading);
        final Switch powerToggleButton = (Switch) rootView.findViewById(R.id.tb_power);


        FirebaseDatabase.getInstance().getReference().child("logs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                double latestReading = dataSnapshot.getValue(Float.class);
                latestReading = latestReading / 3600;
                latestReading = Math.floor(latestReading * 100) / 100;
                if (latestReading == 0.39)
                    latestReading = 0.01;
                latestReadingTextView.setText(String.valueOf(latestReading));
                totalReading = totalReading + latestReading;
                totalReading = Math.floor(totalReading * 1000) / 1000;
                totalReadingTextView.setText(String.valueOf(totalReading));
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

        // Read from the database
        FirebaseDatabase.getInstance().getReference().child("power").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(Integer.class);
                powerToggleButton.setChecked(value > 0);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


        powerToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("power");
                if (b) {
                    myRef.setValue(1);
                } else {
                    myRef.setValue(0);
                }
            }

        });


        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
