package com.example.wanderly;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;




import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String currentUserId;
    private String currentUserEmail;
    private TextView home_greeting, foodPageBtn, attractionPageBtn;
    private ImageView menuMyProfileBtn, menuMapBtn, menuHomeBtn, menuTripBtn, notificationBtn,
            recFoodBorder, recAttractionBorder;
    private String userLastName, userFirstname;

    LinearLayout attractionLayout, foodLayout;


    ViewPager mSlideViewPager;
    LinearLayout mDotLayout;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    private DatabaseReference databaseReference;

    List<String> Day1List = new ArrayList<>();
    List<String> Day2List = new ArrayList<>();
    List<String> Day3List = new ArrayList<>();

    // Get today's and tomorrow's dates
    Calendar calToday = Calendar.getInstance();
    Calendar calTomorrow;
    Calendar calDayAfterTomorrow;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    String todayDate = sdf.format(calToday.getTime());
    String tomorrowDate;
    String dayAfterTomorrowDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        // set up date
        calTomorrow = (Calendar) calToday.clone();
        calTomorrow.add(Calendar.DATE, 1);

        calDayAfterTomorrow = (Calendar) calTomorrow.clone();
        calDayAfterTomorrow.add(Calendar.DATE, 1);
        tomorrowDate = sdf.format(calTomorrow.getTime());
        dayAfterTomorrowDate = sdf.format(calDayAfterTomorrow.getTime());


        Log.d("ABCDE", todayDate + tomorrowDate + dayAfterTomorrowDate);


        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        currentUserEmail = auth.getCurrentUser().getEmail();
        home_greeting = findViewById(R.id.home_greeting);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // bottom navbar
        menuHomeBtn = findViewById(R.id.menu_homebutton);
        menuTripBtn = findViewById(R.id.menu_tripbutton);
        menuMyProfileBtn = findViewById(R.id.menu_profile);
        menuMapBtn = findViewById(R.id.menu_map);

        // notification
        notificationBtn = findViewById(R.id.notification_btn);

        // Your Schedule
        //loadSchedule();
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
                foodLayout.setVisibility(View.VISIBLE);
                attractionLayout.setVisibility(View.GONE);

            }
        });

        // recommendation: navigate to attraction section
        attractionPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recAttractionBorder.setVisibility(View.VISIBLE);
                recFoodBorder.setVisibility(View.INVISIBLE);
                foodLayout.setVisibility(View.GONE);
                attractionLayout.setVisibility(View.VISIBLE);

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
                Intent intent = new Intent(HomeActivity.this, MapsActivity2.class);
                //intent.putExtra("userLastName", userLastName);
                //intent.putExtra("userFirstName", userFirstname);
                startActivity(intent);
            }
        });


        loadUserTrips();

        addFoodToRec();
        addAttractionToRec();

        // initialize
        foodLayout.setVisibility(View.VISIBLE);
        attractionLayout.setVisibility(View.GONE);







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

    private void loadUserTrips() {
        // find trips that contains the current user email address
        databaseReference.child("Trips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot membersSnapshot = tripSnapshot.child("Members");

                    for (DataSnapshot member : membersSnapshot.getChildren()) {
                        String memberEmail = member.child("email").getValue(String.class);
                        if (memberEmail != null && memberEmail.equals(currentUserEmail))  {

                            saveActivityToLists(tripSnapshot);
                           /*
                            for (String tripdetail : Day1List){
                                Log.d("ABCD", "day1"+tripdetail);
                            }
                            for (String tripdetail : Day2List){
                                Log.d("ABCD", "day2"+tripdetail);
                            }
                            for (String tripdetail : Day3List){
                                Log.d("ABCD", "day3"+tripdetail);
                            }*/

                            onFirebaseLoadSuccess(Day1List, Day2List, Day3List);


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

    private void saveActivityToLists(DataSnapshot tripSnapshot) {
        DataSnapshot activitiesSnapshot = tripSnapshot.child("activities");
        for (DataSnapshot daySnapshot : activitiesSnapshot.getChildren()) {
            for (DataSnapshot activitySnapshot : daySnapshot.getChildren()) {
                String date = activitySnapshot.child("Date").getValue(String.class);
                String placeName = activitySnapshot.child("placeName").getValue(String.class);
                String timeFrom = activitySnapshot.child("timeFrom").getValue(String.class);
                String timeTo = activitySnapshot.child("timeTo").getValue(String.class);

                String activityDetails = placeName + " from " + timeFrom + " - " + timeTo;

                //Log.d("ABCD", date + placeName + timeFrom + timeTo);
                if (date != null && date.equals(todayDate)) {
                    Day1List.add(activityDetails);
                }
                else if (date != null && date.equals(tomorrowDate)){
                    Day2List.add(activityDetails);
                }
                else if (date != null && date.equals(dayAfterTomorrowDate)){
                    Day3List.add(activityDetails);
                }
            }
        }
    }


    // load trip schedule viewpager if success
    public void onFirebaseLoadSuccess(List<String> day1List, List<String> day2List, List<String> day3List) {
        viewPagerAdapter = new ViewPagerAdapter(this, day1List, day2List, day3List);
        mSlideViewPager.setAdapter(viewPagerAdapter);
    }


    private void addFoodToRec() {
        foodLayout = findViewById(R.id.recommendFood_linearLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        DatabaseReference stopsRef = FirebaseDatabase.getInstance().getReference("Stops");

        stopsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int count = 0; // Counter to limit the number of stops added to 3

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot stopSnapshot : dataSnapshot.getChildren()) {
                    if (count >= 3) break; // Break if three stops have already been added
                    String type = stopSnapshot.child("type").getValue(String.class);
                    if (!"food".equals(type)) continue; // Skip if the stop is not of type 'food'

                    // Check if the current user has saved this stop
                    boolean isSavedByUser = false;
                    for (DataSnapshot savedUser : stopSnapshot.child("saved_user").getChildren()) {
                        String userID = savedUser.child("userID").getValue(String.class);
                        if (currentUserId.equals(userID)) {
                            isSavedByUser = true;
                            break; // Stop checking if the current user has already been found
                        }
                    }

                    // Inflate and add the layout
                    View customView = inflater.inflate(R.layout.home_recommendation_inside, foodLayout, false);
                    updateStopInfo(customView, stopSnapshot, isSavedByUser);

                    // Set the click listener to open stop activiy
                    customView.setOnClickListener(view -> {
                        Intent intent = new Intent(HomeActivity.this, StopActivity.class);
                        intent.putExtra("placeName", stopSnapshot.child("name").getValue(String.class));
                        startActivity(intent);
                    });


                    foodLayout.addView(customView);
                    count++; // Increment the counter when a stop is added
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("DBError", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void addAttractionToRec() {
        attractionLayout = findViewById(R.id.recommendAttraction_linearLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        DatabaseReference stopsRef = FirebaseDatabase.getInstance().getReference("Stops");

        stopsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int count = 0; // Counter to limit the number of stops added to 3

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot stopSnapshot : dataSnapshot.getChildren()) {
                    if (count >= 3) break; // Break if three stops have already been added
                    String type = stopSnapshot.child("type").getValue(String.class);
                    if (!"attraction".equals(type)) continue; // Skip if the stop is not of type 'attraction'

                    // Check if the current user has saved this stop
                    boolean isSavedByUser = false;
                    for (DataSnapshot savedUser : stopSnapshot.child("saved_user").getChildren()) {
                        String userID = savedUser.child("userID").getValue(String.class);
                        if (currentUserId.equals(userID)) {
                            isSavedByUser = true;
                            break; // Stop checking if the current user has already been found
                        }
                    }

                    // Inflate and add the layout
                    View customView = inflater.inflate(R.layout.home_recommendation_inside, attractionLayout, false);
                    updateStopInfo(customView, stopSnapshot, isSavedByUser);

                    // Set the click listener to open stop activiy
                    customView.setOnClickListener(view -> {
                        Intent intent = new Intent(HomeActivity.this, StopActivity.class);
                        intent.putExtra("placeName", stopSnapshot.child("name").getValue(String.class));
                        startActivity(intent);
                    });


                    attractionLayout.addView(customView);
                    count++; // Increment the counter when a stop is added
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("DBError", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void updateStopInfo(View view, DataSnapshot stopSnapshot, boolean isSavedByUser) {
        TextView nameText = view.findViewById(R.id.rec_name1);
        TextView descText = view.findViewById(R.id.rec_desc1);
        RatingBar ratingBar = view.findViewById(R.id.rec_rating1);
        ImageView saveIcon = view.findViewById(R.id.rec_saved1);

        nameText.setText(stopSnapshot.child("name").getValue(String.class));
        descText.setText(stopSnapshot.child("description").getValue(String.class));
        Float rating = stopSnapshot.child("rating").getValue(Float.class);
        if (rating != null) {
            ratingBar.setRating(rating);
        }

        // Set the visibility of the save icon based on whether the user has saved the stop
        saveIcon.setVisibility(isSavedByUser ? View.VISIBLE : View.GONE);
    }



}