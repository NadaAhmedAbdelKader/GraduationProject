package com.switchak.switchak;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView mRoomsList = rootView.findViewById(R.id.rv_history_rooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRoomsList.setLayoutManager(layoutManager);
        RoomsAdapter mAdapter = new RoomsAdapter("history");
        mRoomsList.setAdapter(mAdapter);
        LineChart chart = rootView.findViewById(R.id.chart);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users").child(userId);

        List<Entry> entries = new ArrayList<>();

        entries.add(new Entry(1, (float) 12.5));
        entries.add(new Entry(6, (float) 17.5));
        entries.add(new Entry(16, (float) 10));
        entries.add(new Entry(20, (float) 15));

        List< Timestamp > Time = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of the month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        System.out.println("Start of the month:       " + cal.getTime());
        System.out.println("... in milliseconds:      " + cal.getTimeInMillis());

        // get start of the next month
        cal.add(Calendar.MONTH, 1);
        System.out.println("Start of the next month:  " + cal.getTime());
        System.out.println("... in milliseconds:      " + cal.getTimeInMillis());

        Calendar now = Calendar.getInstance();
        Date date = new Date();
        now.setTime(date);

        cal.compareTo(now);

        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setAvoidFirstLastClipping(true);

        xAxis.setDrawAxisLine(true);
        xAxis.setEnabled(true);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 100, 100));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour

        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(31);


        xAxis.setValueFormatter(new IAxisValueFormatter() {

                                    private SimpleDateFormat mFormat = new SimpleDateFormat("dd  ");


                                    @Override
                                  public String getFormattedValue(float value, AxisBase axis) {

                                        long millis = TimeUnit.DAYS.toMillis((long) value);
                                        return mFormat.format(new Date(millis));
                                    }
        });


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
       // leftAxis.setTextColor(ColorTemplate.getHoloBlue());

        yAxis.setDrawGridLines(true);
        yAxis.setTextColor(Color.rgb(255, 192, 56));
        YAxis rightAxis = chart.getAxisRight();
        yAxis.setDrawZeroLine(true);
        rightAxis.setEnabled(false);

        yAxis.setAxisMinimum(0);
        chart.getAxisLeft().setStartAtZero(true);


        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);


        LineDataSet dataset = new LineDataSet(entries, "");
        dataset.isDrawCirclesEnabled();
        dataset.setCircleRadius(0f);
        dataset.setDrawFilled(true);
        dataset.setFillColor(Color.rgb(0, 0, 0));
        dataset.setLineWidth(0.2f);
        dataset.setValueTextSize(0f);




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

    private class AxisValueFormatter {
    }
}
