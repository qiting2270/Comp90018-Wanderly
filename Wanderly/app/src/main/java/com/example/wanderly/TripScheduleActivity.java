package com.example.wanderly;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripScheduleActivity extends AppCompatActivity {
    private String tripId = new String();
    private DatabaseReference databaseReference;
    TextView topText;
    TextView tripFromDay, tripFromMonth;
    TextView tripToDay, tripToMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_schedule);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        topText = findViewById(R.id.trip_schedule_text);
        tripFromDay = findViewById(R.id.trip_schedule_from_day);
        tripFromMonth = findViewById(R.id.trip_schedule_from_month);
        tripToDay = findViewById(R.id.trip_schedule_to_day);
        tripToMonth = findViewById(R.id.trip_schedule_to_month);

        // read trip ID from previous intent
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

        // access db
        databaseReference.child("Trips").child(tripId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String departureDate = snapshot.child("departureDate").getValue(String.class);
                String returnDate = snapshot.child("returnDate").getValue(String.class);
                String departureCity = snapshot.child("departure").getValue(String.class);

                // set top text
                String formattedCity = formatCityCode(departureCity);
                topText.setText(formattedCity + " - " + "MEL");

                // format the date
                String[] formattedDepartureDate = DateFormatter.getDayAndMonth(departureDate);
                String departureDay = formattedDepartureDate[0];
                String departureMonth = formattedDepartureDate[1];
                String[] formattedReturnDate = DateFormatter.getDayAndMonth(returnDate);
                String returnDay = formattedReturnDate[0];
                String returnMonth = formattedReturnDate[1];
                tripFromDay.setText(departureDay);
                tripFromMonth.setText(departureMonth);
                tripToDay.setText(returnDay);
                tripToMonth.setText(returnMonth);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private String formatCityCode(String city) {
        switch(city) {
            case "Melbourne": return "MEL";
            case "Sydney": return "SYD";
            case "Canberra": return "CBR";
            case "Perth": return "PER";
            case "Gold Coast": return "OOL";
            case "Brisbane": return "BNE";
            default: return ""; // Default case if city is not matched
        }
    }

}