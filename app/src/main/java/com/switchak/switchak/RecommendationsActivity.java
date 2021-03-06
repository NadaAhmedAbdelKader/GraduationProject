package com.switchak.switchak;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class RecommendationsActivity extends AppCompatActivity {
    private static SeekBar seek_bar;
    private static TextView text_view;
    int count;
    LinearLayout priorityList;
    final List<Room> rooms = FirebaseUtils.getInstance().getRooms();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        seekBarr();
        count = 0;
        addSpinner();
        addButton();


    }

    @SuppressLint("SetTextI18n")
    public void seekBarr() {
        seek_bar = findViewById(R.id.seekBar);
        text_view = findViewById(R.id.textView);
        text_view.setText(getString(R.string.Budget) + seek_bar.getProgress());

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            int progress_value = progress;


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text_view.setText(getString(R.string.Budget) + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                text_view.setText(getString(R.string.Budget) + progress_value);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(RecommendationsActivity.this, "Seek bar progress is : " + seek_bar.getProgress(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void addSpinner() {

        List<String> roomsNames = new ArrayList<>();
        final boolean[] spinnerCreated = {false};
        roomsNames.add("Select room");

        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomName() == null) {
                roomsNames.add(rooms.get(i).getRoomId());
            } else
                roomsNames.add(rooms.get(i).getRoomName());

        }

        priorityList = findViewById(R.id.priority_list);
        Spinner firstPriority = new Spinner(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        firstPriority.setLayoutParams(lp);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomsNames);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorityList.addView(firstPriority);
        firstPriority.setAdapter(listAdapter);


        firstPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

// 3shan mayfdalsh yzwed f 3dad el spinners w el 3dad da y3dii 3dad el 2wad aslun

                if (position != 0 && count < rooms.size() - 1 && spinnerCreated[0] == false) {
                    addSpinner();
                    count++;
                    spinnerCreated[0] = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void addButton() {
        LinearLayout buttonLayout = findViewById(R.id.button_layout);
        Button recbutton = new Button(this);
        buttonLayout.addView(recbutton);
        recbutton.setText("Recommend");

        recbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                double theta1 = 1;
                double theta2 = 0.01;
                double theta3 = 0.1;
                double theta0 = 50;
                LinearLayout recommendedText = findViewById(R.id.layout_recommended_text);
                recommendedText.removeAllViews();


                for (int i = 0; i < priorityList.getChildCount(); i++) {

                    Spinner spinner = (Spinner) priorityList.getChildAt(i);
                    int roomIndex = spinner.getSelectedItemPosition() - 1;

                    if (spinner.getSelectedItemPosition() > 0) {

                        float selectedRoomReading = rooms.get(roomIndex).getTotalReading() / rooms.get(i).getReadings().size();

                        double totalHours = theta0 + priorityList.getChildCount() - i * theta1 + selectedRoomReading * theta2 + seek_bar.getProgress() * theta3;


                        TextView rtext = new TextView(recommendedText.getContext());
                        rtext.setText("Optimal Use for " + spinner.getSelectedItem() + " is "
                                + new DecimalFormat("#.##").format(totalHours)
                                + " hrs/month");
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(16, 16, 16, 16);
                        rtext.setLayoutParams(lp);

                        recommendedText.addView(rtext);
                    }


                }


            }


        });
    }


}




