package com.example.wanderly;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class TripScheduleActivity extends AppCompatActivity {
    private String tripId = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_schedule);


        tripId = getIntent().getStringExtra("TRIP_ID");
        Log.d("trip_id", tripId);

        //back icon logic
        ImageView backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go back to previous activity
                finish();
            }
        });

    }
}