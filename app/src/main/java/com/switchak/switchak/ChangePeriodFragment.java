package com.switchak.switchak;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePeriodFragment extends Fragment implements Observer {

    Spinner monthsList;
    Spinner daysList;


    public ChangePeriodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_period, container, false);
        monthsList = rootView.findViewById(R.id.months_list);
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsList.setAdapter(monthsAdapter);

        daysList = rootView.findViewById(R.id.days_list);
        final ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysList.setAdapter(daysAdapter);

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        monthsList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Calendar now = new GregorianCalendar();
                if (i == 0) {
                    daysAdapter.clear();
                    daysAdapter.add("All");
                    for (int day = now.get(Calendar.DAY_OF_MONTH); day > 0; day--) {
                        daysAdapter.add(String.valueOf(day));
                    }
                } else {
                    now.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH) - i);
                    daysAdapter.clear();
                    daysAdapter.add("All");
                    for (int day = now.getActualMaximum(Calendar.DAY_OF_MONTH); day > 0; day--) {
                        daysAdapter.add(String.valueOf(day));
                    }
                }
                daysList.setSelection(0);
                FirebaseUtils.getInstance().setMonthSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        daysList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                FirebaseUtils.getInstance().setDaySelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        monthsAdapter.add(new SimpleDateFormat("MMM", Locale.US).format(calendar.getTime()));
        for (int month = 1; month < 3; month++) {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            monthsAdapter.add(new SimpleDateFormat("MMM", Locale.US).format(calendar.getTime()));
        }

        FirebaseUtils.getInstance().addObserver(this);
        update(null, null);

        return rootView;
    }

    @Override
    public void update(Observable observable, Object o) {
        int monthSelection = FirebaseUtils.getInstance().getMonthSelection();
        if (monthsList.getSelectedItemPosition() != monthSelection)
            monthsList.setSelection(monthSelection);
        int daySelection = FirebaseUtils.getInstance().getDaySelection();
        if (daysList.getSelectedItemPosition() != daySelection)
            daysList.setSelection(daySelection);
    }
}
