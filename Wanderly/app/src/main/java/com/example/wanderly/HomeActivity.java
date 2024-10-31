package com.example.wanderly;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;




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

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String currentUserId;
    private String currentUserEmail;
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
                Intent intent = new Intent(HomeActivity.this, MapsActivity2.class);
                //intent.putExtra("userLastName", userLastName);
                //intent.putExtra("userFirstName", userFirstname);
                startActivity(intent);
            }
        });

        loadUserTrips();






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