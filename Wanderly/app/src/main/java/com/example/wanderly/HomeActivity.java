package com.example.wanderly;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String currentUserId;
    private TextView home_greeting, foodPageBtn, attractionPageBtn,
            recName1, recDesc1, recName2, recDesc2, recName3, recDesc3;
    private ImageView menuMyProfileBtn, menuMapBtn, menuHomeBtn, menuTripBtn, notificationBtn,
            recFoodBorder, recAttractionBorder,
            recSaved1, recSaved2, recSaved3;
    private String userLastName, userFirstname,
            foodName, foodDesc, attrName, attrDesc;
    private float foodRating, attrRating;
    RatingBar recRating1, recRating2, recRating3;
    List<String> savedList;

    ViewPager mSlideViewPager;
    LinearLayout mDotLayout;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        home_greeting = findViewById(R.id.home_greeting);

        // bottom navbar
        menuHomeBtn = findViewById(R.id.menu_homebutton);
        menuTripBtn = findViewById(R.id.menu_tripbutton);
        menuMyProfileBtn = findViewById(R.id.menu_profile);
        menuMapBtn = findViewById(R.id.menu_map);

        // notification
        notificationBtn = findViewById(R.id.notification_btn);

        // Your Schedule
        loadSchedule();
        mSlideViewPager = (ViewPager) findViewById(R.id.slide_viewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        // set up dots
        setUpIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        // recommendation section
        foodPageBtn = findViewById(R.id.rec_food);
        attractionPageBtn = findViewById(R.id.rec_attraction);
        recFoodBorder = findViewById(R.id.rec_food_border);
        recAttractionBorder = findViewById(R.id.rec_attraction_border);

        recName1 = findViewById(R.id.rec_name1);
        recRating1 = findViewById(R.id.rec_rating1);
        recDesc1 = findViewById(R.id.rec_desc1);
        recSaved1 = findViewById(R.id.rec_saved1);
        recName2 = findViewById(R.id.rec_name2);
        recRating2 = findViewById(R.id.rec_rating2);
        recDesc2 = findViewById(R.id.rec_desc2);
        recSaved2 = findViewById(R.id.rec_saved2);
        recName3 = findViewById(R.id.rec_name3);
        recRating3 = findViewById(R.id.rec_rating3);
        recDesc3 = findViewById(R.id.rec_desc3);
        recSaved3 = findViewById(R.id.rec_saved3);

        // get user
        DatabaseReference reference_username = FirebaseDatabase.getInstance().getReference("User Information");
        reference_username.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.getKey().equals(currentUserId)){
                        userLastName = snapshot.child("lastname").getValue(String.class);
                        userFirstname = snapshot.child("firstname").getValue(String.class);
                        home_greeting.setText("Bon Voyage, " + userFirstname + "!");
                        break; // Exit the loop once the user is found
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // recommendation: navigate to food section
        foodPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recFoodBorder.setVisibility(View.VISIBLE);
                recAttractionBorder.setVisibility(View.INVISIBLE);
                fetchRecommendations();
            }
        });

        // recommendation: navigate to attraction section
        attractionPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recAttractionBorder.setVisibility(View.VISIBLE);
                recFoodBorder.setVisibility(View.INVISIBLE);
                fetchRecommendations();
            }
        });



        // navigate to notifications page
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        // get user saved list and load recommendation section
        DatabaseReference reference_saved = FirebaseDatabase.getInstance().getReference();
        reference_saved.child("User Information").child(currentUserId).child("saved")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        savedList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // store all of user's saved places to 'saved'
                            String savedValue = snapshot.child("stop_name").getValue(String.class);
                            if (savedValue != null) {
                                savedList.add(savedValue);
                            }
                        }
                        fetchRecommendations();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        // when click on stops in recommendation
//        newLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HomeActivity.this, StopActivity.class);
//                intent.putExtra("placeName", placeName);
//                startActivity(intent);
//            }
//        });

        // menu navigation
        menuMyProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
                //intent.putExtra("userLastName", userLastName);
                //intent.putExtra("userFirstName", userFirstname);
                startActivity(intent);
            }
        });
        menuHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        menuTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MyTripsActivity.class);
                startActivity(intent);
            }
        });

        menuMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                //intent.putExtra("userLastName", userLastName);
                //intent.putExtra("userFirstName", userFirstname);
                startActivity(intent);
            }
        });
    }

    // notification icon
    protected void onResume() {
        super.onResume();
        updateNotificationIcon();
    }

    private void updateNotificationIcon() {
        SharedPreferences preferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        boolean hasNotification = preferences.getBoolean("hasNotification", false);

        if (hasNotification) {
            notificationBtn.setImageResource(R.drawable.notification_red);
        } else {
            notificationBtn.setImageResource(R.drawable.notification);
        }
    }

    // dots
    private void setUpIndicator(int position) {
        dots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(".");
            dots[i].setTextSize(45);
            dots[i].setTypeface(null, Typeface.BOLD);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.dot_inactive));
            mDotLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(ContextCompat.getColor(this, R.color.dot_active));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void loadSchedule() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User Information");
        DatabaseReference scheduleReference = FirebaseDatabase.getInstance().getReference("Trips");

        userReference.child(currentUserId).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    String userEmail = userSnapshot.getValue(String.class);

                    // Perform the trip retrieval in parallel to reduce delay
                    new Thread(() -> {
                        scheduleReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot tripSnapshot : snapshot.getChildren()) {
                                    String tid = tripSnapshot.getKey();

                                    new Thread(() -> {
                                        DataSnapshot membersSnapshot = tripSnapshot.child("Members");
                                        for (DataSnapshot memberSnapshot : membersSnapshot.getChildren()) {
                                            String tripEmail = memberSnapshot.child("email").getValue(String.class);
                                            if (userEmail != null && userEmail.equals(tripEmail)) {
                                                getSchedule(tid, scheduleReference);
                                                break; // stop searching once found
                                            }
                                        }
                                    }).start();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                onFirebaseLoadFailed(error.getMessage());
                            }
                        });
                    }).start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get schedules
    public void getSchedule(String tid1, DatabaseReference scheduleReference) {
        scheduleReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Integer> days = new ArrayList<>();
                List<String> dates = new ArrayList<>();
                List<HomeSchedule> stops = new ArrayList<>();

                // get today, tomorrow and the day after
                LocalDate today = LocalDate.now();
                String todayDate = today.format(DateTimeFormatter.ofPattern("dd/MM"));
                String tomorrowDate = today.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM"));
                String dayAfterDate = today.plusDays(2).format(DateTimeFormatter.ofPattern("dd/MM"));

                stops.clear();

                for (DataSnapshot tripSnapshot : snapshot.getChildren()) {
                    String tid2 = tripSnapshot.getKey();
                    if (Objects.equals(tid1, tid2)) {
                        for (DataSnapshot activitiesSnapshot : tripSnapshot.getChildren()) {
                            if (activitiesSnapshot.getKey().equals("activities")) {
                                for (DataSnapshot activitySnapshot : activitiesSnapshot.getChildren()) {
                                    for (DataSnapshot daySnapshot : activitySnapshot.getChildren()) {
                                        String did = daySnapshot.getKey();
                                    }

                                    // get day of the trip
                                    String dayValue = activitySnapshot.getKey();
                                    String[] parts = dayValue.split("Day");
                                    int dayNumber = Integer.parseInt(parts[1]);

                                    days.add(dayNumber);

                                    // format date
//                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//                                    LocalDate departureDate = LocalDate.parse(departureDateStr, formatter);
//                                    LocalDate returnDate = LocalDate.parse(returnDateStr, formatter);
//                                    for (LocalDate date = departureDate; !date.isAfter(returnDate); date = date.plusDays(1)) {
//                                        // Add the formatted date to the list
//                                        dates.add(date.format(formatter));
//                                    }
//                                    dates.add()

//                                    if ("Day1".equals(dayKey)) {
//                                        days.add("Today");
//                                        dates.add(todayDate);
//                                    } else if ("Day2".equals(dayKey)) {
//                                        days.add("Tomorrow");
//                                        dates.add(tomorrowDate);
//                                    } else if ("Day3".equals(dayKey)) {
//                                        days.add("Day After");
//                                        dates.add(dayAfterDate);
//                                    }
//
//                                    String placeName = activitySnapshot.child("placeName").getValue(String.class);
//                                    String timeFrom = activitySnapshot.child("timeFrom").getValue(String.class);
//                                    String timeTo = activitySnapshot.child("timeTo").getValue(String.class);
//                                    String type = activitySnapshot.child("type").getValue(String.class);
//                                    stops.add(new HomeSchedule(placeName, timeFrom, timeTo));
                                }

//                                for (DataSnapshot scheduleSnapshot : tripSnapshot.child("activities").getChildren()) {
//                                    scheduleList.add(scheduleSnapshot.getValue(HomeSchedule.class));
//                                    Log.d("2", "                    "+scheduleList.toString());
//                                }
//                                DaySchedule schedule = new DaySchedule(days, dates, stops);
                                onFirebaseLoadSuccess((List<HomeSchedule>) stops);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // load trip schedule viewpager if success
    public void onFirebaseLoadSuccess(List<HomeSchedule> scheduleList) {
        viewPagerAdapter = new ViewPagerAdapter(this, scheduleList);
        mSlideViewPager.setAdapter(viewPagerAdapter);
    }

    // return error message when trip schedule viewpager failed
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    private int getItem(int i) {
        return mSlideViewPager.getCurrentItem() + i;
    }

    // get recommendation details
    public void fetchRecommendations() {

        DatabaseReference reference_rec = FirebaseDatabase.getInstance().getReference("Stops");
        reference_rec.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int fcount = 0, acount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (fcount >= 3 && acount >= 3) break; // only want first three

                    // get food recommendation places
                    if (recFoodBorder.getVisibility() == View.VISIBLE) {
                        if (Objects.equals(snapshot.child("type").getValue(String.class), "food")) {
                            foodName =  snapshot.child("name").getValue(String.class);
                            foodDesc =  snapshot.child("description").getValue(String.class);
                            foodRating = snapshot.child("rating").getValue(Float.class);
                            if (fcount == 0) {
                                recName1.setText(foodName);
                                recDesc1.setText(foodDesc);

                                recRating1.setVisibility(View.VISIBLE);
                                recRating1.setRating(foodRating);

                            } else if (fcount == 1) {
                                recName2.setText(foodName);
                                recDesc2.setText(foodDesc);

                                recRating2.setVisibility(View.VISIBLE);
                                recRating2.setRating(foodRating);

                            } else if (fcount == 2){
                                recName3.setText(foodName);
                                recDesc3.setText(foodDesc);

                                recRating3.setVisibility(View.VISIBLE);
                                recRating3.setRating(foodRating);
                            }

                            // reset visibility of saved button
                            recSaved1.setVisibility(View.INVISIBLE);
                            recSaved2.setVisibility(View.INVISIBLE);
                            recSaved3.setVisibility(View.INVISIBLE);

                            // check if user saved the place
                            for (String string : savedList) {
                                if (recName1.getText().toString().equals(string)) {
                                    recSaved1.setVisibility(View.VISIBLE);
                                } else if (recName2.getText().toString().equals(string)) {
                                    recSaved2.setVisibility(View.VISIBLE);
                                } else if (recName3.getText().toString().equals(string)) {
                                    recSaved3.setVisibility(View.VISIBLE);
                                }
                            }

                            fcount++;
                        }
                    }

                    // get attraction recommendation places
                    if (recAttractionBorder.getVisibility() == View.VISIBLE) {
                        if (Objects.equals(snapshot.child("type").getValue(String.class), "attraction")) {
                            attrName =  snapshot.child("name").getValue(String.class);
                            attrDesc =  snapshot.child("description").getValue(String.class);
                            attrRating = snapshot.child("rating").getValue(Float.class);
                            if (acount == 0) {
                                recName1.setText(attrName);
                                recDesc1.setText(attrDesc);

                                recRating1.setVisibility(View.VISIBLE);
                                recRating1.setRating(attrRating);

                            } else if (acount == 1) {
                                recName2.setText(attrName);
                                recDesc2.setText(attrDesc);

                                recRating2.setVisibility(View.VISIBLE);
                                recRating2.setRating(attrRating);

                            } else if (acount == 2){
                                recName3.setText(attrName);
                                recDesc3.setText(attrDesc);

                                recRating3.setVisibility(View.VISIBLE);
                                recRating3.setRating(attrRating);

                            }

                            // reset visibility of saved button
                            recSaved1.setVisibility(View.INVISIBLE);
                            recSaved2.setVisibility(View.INVISIBLE);
                            recSaved3.setVisibility(View.INVISIBLE);

                            // check if user saved the place
                            for (String string : savedList) {
                                if (string != null) {
                                    if (recName1.getText().toString().equals(string)) {
                                        recSaved1.setVisibility(View.VISIBLE);
                                    } else if (recName2.getText().toString().equals(string)) {
                                        recSaved2.setVisibility(View.VISIBLE);
                                    } else if (recName3.getText().toString().equals(string)) {
                                        recSaved3.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            acount++;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}