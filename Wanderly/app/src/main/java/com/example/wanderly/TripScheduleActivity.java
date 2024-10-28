package com.example.wanderly;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TripScheduleActivity extends AppCompatActivity {
    private String tripId = new String();
    private DatabaseReference databaseReference;
    TextView topText;
    TextView tripFromDay, tripFromMonth;
    TextView tripToDay, tripToMonth;

    LinearLayout memberLinearLayout;

    TextView day1DateText, day2DateText, day3DateText;

    ConstraintLayout TripDay1, TripDay2, TripDay3;

    private int trip_duration;
    String departureDate = new String(), returnDate = new String();

    LinearLayout stopsLinearLayoutDay1;
    LinearLayout stopsLinearLayoutDay2;
    LinearLayout stopsLinearLayoutDay3;

    ImageView deleteBtn;


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

        memberLinearLayout = findViewById(R.id.trip_schedule_member_linearlayout);

        day1DateText = findViewById(R.id.trip_day1_inside_date_text);
        day2DateText = findViewById(R.id.trip_day2_inside_date_text);
        day3DateText = findViewById(R.id.trip_day3_inside_date_text);

        TripDay1 = findViewById(R.id.trip_day_1);
        TripDay2 = findViewById(R.id.trip_day_2);
        TripDay3 = findViewById(R.id.trip_day_3);
        stopsLinearLayoutDay1 = findViewById(R.id.stops_linear_layout_day1);
        stopsLinearLayoutDay2 = findViewById(R.id.stops_linear_layout_day2);
        stopsLinearLayoutDay3 = findViewById(R.id.stops_linear_layout_day3);

        deleteBtn = findViewById(R.id.trip_schedule_delete_icon);


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
                departureDate = snapshot.child("departureDate").getValue(String.class);
                Log.d("12345", departureDate);
                returnDate = snapshot.child("returnDate").getValue(String.class);
                String departureCity = snapshot.child("departure").getValue(String.class);
                trip_duration = snapshot.child("tripDuration").getValue(Long.class).intValue();

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

                // search member through email, add member's profile image
                for (DataSnapshot memberSnapshot : snapshot.child("Members").getChildren()) {
                    String email = memberSnapshot.child("email").getValue(String.class);
                    if (email != null) {
                        addImageToLayout(email, memberLinearLayout);
                    }
                }

                updateInsideLayoutVisibilty();

                // load trip activity data
                if (trip_duration == 1){
                    // load day 1 activity data from db to the linear layout
                    loadActivities(tripId, stopsLinearLayoutDay1, "Day1");
                }
                else if (trip_duration == 2){
                    loadActivities(tripId, stopsLinearLayoutDay1, "Day1");
                    loadActivities(tripId, stopsLinearLayoutDay2, "Day2");
                }
                else if (trip_duration == 3){
                    loadActivities(tripId, stopsLinearLayoutDay1, "Day1");
                    loadActivities(tripId, stopsLinearLayoutDay2, "Day2");
                    loadActivities(tripId, stopsLinearLayoutDay3, "Day3");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //delete the trip from db
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("Trips").child(tripId).removeValue();

                deleteSuccessDialog();

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

    private void fetchUserProfilePicByEmail(String email, ImageView userImage) {
        databaseReference.child("User Information").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String profilePicUrl = new String();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        // Safely obtain the profile picture URL, check if it's null first
                        Object profilePicObj = child.child("profile_pic").getValue();
                        profilePicUrl = profilePicObj != null ? profilePicObj.toString() : null;

                        if (profilePicObj != null) {
                            profilePicUrl = profilePicObj.toString();
                            // Load the image directly here after ensuring URL is fetched
                            Glide.with(TripScheduleActivity.this).load(profilePicUrl).circleCrop().into(userImage);
                        }
                    }
                }
                if (profilePicUrl == null) {
                    // Fallback if no URL is found
                    Glide.with(TripScheduleActivity.this).load(R.drawable.vector).circleCrop().into(userImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching user data", databaseError.toException());
            }
        });
    }

    private void addImageToLayout(String email, LinearLayout layout) {
        ImageView userImage = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                convertDpToPx(49), // width in pixels
                convertDpToPx(49)  // height in pixels
        );
        layoutParams.setMargins(5, 0, 25, 0);
        userImage.setLayoutParams(layoutParams);

        fetchUserProfilePicByEmail(email, userImage);


        layout.addView(userImage);
    }

    private int convertDpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    private void updateInsideLayoutVisibilty(){
        // Get all dates including departure and subsequent ones based on the trip duration
        List<String> allDates = DateUtils.getSubsequentDates(departureDate, trip_duration);

        if (trip_duration == 1){
            TripDay1.setVisibility(View.VISIBLE);
            TripDay2.setVisibility(View.GONE);
            TripDay3.setVisibility(View.GONE);
            day1DateText.setText(allDates.get(0));


        }
        else if (trip_duration == 2){
            TripDay1.setVisibility(View.VISIBLE);
            TripDay2.setVisibility(View.VISIBLE);
            TripDay3.setVisibility(View.GONE);
            day1DateText.setText(allDates.get(0));
            day2DateText.setText(allDates.get(1));

        }
        else if (trip_duration == 3){
            TripDay1.setVisibility(View.VISIBLE);
            TripDay2.setVisibility(View.VISIBLE);
            TripDay3.setVisibility(View.VISIBLE);
            day1DateText.setText(allDates.get(0));
            day2DateText.setText(allDates.get(1));
            day3DateText.setText(allDates.get(2));
        }

    }

    // read activity from database
    private void loadActivities(String tripId, LinearLayout parentLayout, String day) {
        databaseReference.child("Trips").child(tripId).child("activities").child(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot activitySnapshot : dataSnapshot.getChildren()) {
                    String placeName = activitySnapshot.child("placeName").getValue(String.class);
                    String timeFrom = activitySnapshot.child("timeFrom").getValue(String.class);
                    String timeTo = activitySnapshot.child("timeTo").getValue(String.class);

                    addNewPlaceLayout(placeName, timeFrom, timeTo, parentLayout, day);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB_ERROR", "Failed to read activities", databaseError.toException());
            }
        });
    }

    // Function to add a new ConstraintLayout to the parent layout
    private void addNewPlaceLayout(String placeName, String timeFrom, String timeTo, LinearLayout parentLayout, String day) {
        // Create a new ConstraintLayout
        ConstraintLayout newLayout = new ConstraintLayout(this);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.bottomMargin = (int) (15 * getResources().getDisplayMetrics().density); // Convert dp to pixels
        newLayout.setLayoutParams(layoutParams);

        newLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripScheduleActivity.this, StopActivity.class);
                intent.putExtra("placeName", placeName);
                intent.putExtra("timeFrom", timeFrom);
                intent.putExtra("timeTo", timeTo);
                intent.putExtra("day", day);
                startActivity(intent);
            }
        });

        // Create an ImageView
        ImageView imageView = new ImageView(this);
        int imageViewId = View.generateViewId();
        imageView.setId(imageViewId);
        if (Objects.equals(placeName, "Thai Town") || Objects.equals(placeName, "Billy‘s Central")
                || Objects.equals(placeName, "Bornga") || Objects.equals(placeName, "Sweet Canteen")){
            imageView.setImageResource(R.drawable.dining_icon);
        }
        else{
            imageView.setImageResource(R.drawable.attraction_icon);
        }

        ConstraintLayout.LayoutParams imageLayoutParams = new ConstraintLayout.LayoutParams(
                (int) (30 * getResources().getDisplayMetrics().density), // Convert dp to pixels
                (int) (30 * getResources().getDisplayMetrics().density)  // Convert dp to pixels
        );
        imageView.setLayoutParams(imageLayoutParams);

        // Add ImageView to ConstraintLayout
        newLayout.addView(imageView);

        // Create a TextView for place name
        TextView placeTextView = new TextView(this);
        int placeTextViewId = View.generateViewId();
        placeTextView.setId(placeTextViewId);
        placeTextView.setText(placeName);
        placeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        placeTextView.setTextColor(Color.parseColor("#3B5667"));
        Typeface typeface = ResourcesCompat.getFont(this, R.font.kulim_park); // Use ResourceCompat for compatibility
        placeTextView.setTypeface(typeface);
        ConstraintLayout.LayoutParams textLayoutParams = new ConstraintLayout.LayoutParams(
                (int) (120 * getResources().getDisplayMetrics().density),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textLayoutParams.setMargins((int) (16 * getResources().getDisplayMetrics().density), 0, 0, 0); // Left margin
        textLayoutParams.startToEnd = imageViewId;
        textLayoutParams.topToTop = imageViewId;
        textLayoutParams.bottomToBottom = imageViewId;
        placeTextView.setLayoutParams(textLayoutParams);

        // Add TextView to ConstraintLayout
        newLayout.addView(placeTextView);

        // Create the second TextView for the time
        TextView timeTextView = new TextView(this);
        int timeTextViewId = View.generateViewId();
        timeTextView.setId(timeTextViewId);
        timeTextView.setText(timeFrom + " - " + timeTo);
        timeTextView.setTextColor(Color.parseColor("#3B5667"));
        timeTextView.setTextSize(12);  // This uses SP
        ConstraintLayout.LayoutParams timeLayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        timeTextView.setLayoutParams(timeLayoutParams);


        newLayout.addView(timeTextView);

        // Set constraints using ConstraintSet
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(newLayout);
        constraintSet.connect(imageViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(imageViewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(placeTextViewId, ConstraintSet.START, imageViewId, ConstraintSet.END, (int) (16 * getResources().getDisplayMetrics().density));
        constraintSet.connect(placeTextViewId, ConstraintSet.TOP, imageViewId, ConstraintSet.TOP);
        constraintSet.connect(placeTextViewId, ConstraintSet.BOTTOM, imageViewId, ConstraintSet.BOTTOM);
        constraintSet.connect(timeTextViewId, ConstraintSet.START, placeTextViewId, ConstraintSet.END);
        constraintSet.connect(timeTextViewId, ConstraintSet.TOP, placeTextViewId, ConstraintSet.TOP);
        constraintSet.connect(timeTextViewId, ConstraintSet.BOTTOM, placeTextViewId, ConstraintSet.BOTTOM);
        constraintSet.connect(timeTextViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);  // Add 0 margin
        constraintSet.applyTo(newLayout);


        // Add the new ConstraintLayout to the parent layout
        parentLayout.addView(newLayout);
    }

    private void deleteSuccessDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(TripScheduleActivity.this);
        dialog.setMessage("Delete Trip Successful!");
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(TripScheduleActivity.this, MyTripsActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }






}