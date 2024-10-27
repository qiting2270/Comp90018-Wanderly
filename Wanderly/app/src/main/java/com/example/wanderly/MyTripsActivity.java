package com.example.wanderly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.LayoutInflater;


import org.w3c.dom.Text;

import java.util.Objects;


public class MyTripsActivity extends AppCompatActivity {

    private ImageView menuMyProfileBtn;
    private ImageView menuHomeBtn;
    private ImageView menuTripBtn;
    private TextView addTripBtn;
    private ImageView menuMapBtn;
    private FirebaseAuth auth;
    private String currentUserEmail;

    private LinearLayout upcomingTripsContainer;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_trips);

        menuMyProfileBtn = findViewById(R.id.menu_profile);
        menuHomeBtn = findViewById(R.id.menu_homebutton);
        menuTripBtn = findViewById(R.id.menu_tripbutton);
        addTripBtn = findViewById(R.id.my_trips_add_icon);
        menuMapBtn = findViewById(R.id.menu_map);

        upcomingTripsContainer = findViewById(R.id.upcomingtriplinearLayout);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();
        currentUserEmail = Objects.requireNonNull(auth.getCurrentUser()).getEmail();



        //menu navigation
        menuMyProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyTripsActivity.this, MyProfileActivity.class);
                //intent.putExtra("userLastName", userLastName);
                //intent.putExtra("userFirstName", userFirstname);
                startActivity(intent);
            }
        });
        menuHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyTripsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        menuTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyTripsActivity.this, MyTripsActivity.class);
                startActivity(intent);
            }
        });
        menuMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyTripsActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        // navigate to Add trip page.
        addTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyTripsActivity.this, AddTripActivity.class);
                startActivity(intent);
            }
        });


        loadUserTrips();


    }



    private void loadUserTrips() {
        databaseReference.child("Trips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot membersSnapshot = tripSnapshot.child("Members");

                    for (DataSnapshot member : membersSnapshot.getChildren()) {
                        String memberEmail = member.child("email").getValue(String.class);
                        if (currentUserEmail.equals(memberEmail)) {
                            addTripView(tripSnapshot);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TripOverview", "Failed to load trip data.", databaseError.toException());
            }
        });
    }


    private void addTripView(DataSnapshot tripSnapshot) {
        View tripView = LayoutInflater.from(this).inflate(R.layout.trip_overview_template, upcomingTripsContainer, false);

        TextView tripStartDate = tripView.findViewById(R.id.trip_start_date);
        TextView tripEndDate = tripView.findViewById(R.id.trip_end_date);
        TextView tripFrom = tripView.findViewById(R.id.tripFrom_text);


        String departureDate = tripSnapshot.child("departureDate").getValue(String.class);
        String returnDate = tripSnapshot.child("returnDate").getValue(String.class);

        String tripFromText = tripSnapshot.child("departure").getValue(String.class);

        String formattedDepartureDate = DateConverter.convertDate(departureDate);
        String formattedReturnDate = DateConverter.convertDate(returnDate);
        String formattedTripFromText = new String();


        if (Objects.equals(tripFromText, "Melbourne")){
             formattedTripFromText = "MEL";
        }
        else if (Objects.equals(tripFromText, "Sydney")){
            formattedTripFromText = "SYD";
        }
        else if (Objects.equals(tripFromText, "Canberra")){
            formattedTripFromText = "CBR";
        }
        else if (Objects.equals(tripFromText, "Perth")){
            formattedTripFromText = "PER";
        }
        else if (Objects.equals(tripFromText, "Gold Coast")){
            formattedTripFromText = "OOL";
        }
        else if (Objects.equals(tripFromText, "Brisbane")){
            formattedTripFromText = "BNE";
        }



        tripStartDate.setText(formattedDepartureDate);
        tripEndDate.setText(formattedReturnDate);
        tripFrom.setText(formattedTripFromText);



        upcomingTripsContainer.addView(tripView);
    }


}