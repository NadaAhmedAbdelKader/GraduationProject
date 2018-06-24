package com.switchak.switchak;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


public class RecommendationsActivity extends AppCompatActivity {
    private static SeekBar seek_bar;
    private static TextView text_view;
    int count;

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
                Toast.makeText(RecommendationsActivity.this, "Seek bar progress is : " + progress_value,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void addSpinner() {

        final List<Room> rooms = FirebaseUtils.getInstance().getRooms();
        List<String> roomsNames = new ArrayList<>();
        final boolean[] spinnerCreated = {false};
        roomsNames.add("Select room");

        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomName() == null) {
                roomsNames.add(rooms.get(i).getRoomId());
            } else
                roomsNames.add(rooms.get(i).getRoomName());

        }

        LinearLayout priorityList = findViewById(R.id.priority_list);
        Spinner firstPriority = new Spinner(this);
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
        recbutton.setText("Recommendation");

        recbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }


        });
    }
}

