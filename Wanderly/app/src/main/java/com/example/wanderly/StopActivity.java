package com.example.wanderly;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StopActivity extends AppCompatActivity {
    private String dayNum = new String();
    private String placeName = new String();
    private String timeFrom = new String();
    private String timeTo = new String();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stop);


        // read trip ID from previous intent
        dayNum = getIntent().getStringExtra("day");
        Log.d("dayNum", dayNum);
        placeName = getIntent().getStringExtra("placeName");
        Log.d("dayNum", placeName);



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