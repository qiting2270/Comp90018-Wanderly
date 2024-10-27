package com.example.wanderly;

import static android.content.ContentValues.TAG;

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
    private TextView home_greeting, foodPageBtn, attractionPageBtn,
            recName1, recDesc1, recName2, recDesc2, recName3, recDesc3;
    private ImageView menuMyProfileBtn, menuMapBtn, menuHomeBtn, menuTripBtn, notificationBtn,
            recFoodBorder, recAttractionBorder,
            recSaved1, recUnsaved1, recSaved2, recUnsaved2, recSaved3, recUnsaved3;
    private String userLastName, userFirstname,
            foodName, foodDesc, attrName, attrDesc;
    private float foodRating, attrRating;
    RatingBar recRating1, recRating2, recRating3,
            attrRating1, attrRating2, attrRating3;
    String[] saved;

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
        mSlideViewPager = (ViewPager) findViewById(R.id.slide_viewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        viewPagerAdapter = new ViewPagerAdapter(this);

        mSlideViewPager.setAdapter(viewPagerAdapter);

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
        recUnsaved1 = findViewById(R.id.rec_unsaved1);
        recName2 = findViewById(R.id.rec_name2);
        recRating2 = findViewById(R.id.rec_rating2);
        recDesc2 = findViewById(R.id.rec_desc2);
        recSaved2 = findViewById(R.id.rec_saved2);
        recUnsaved2 = findViewById(R.id.rec_unsaved2);
        recName3 = findViewById(R.id.rec_name3);
        recRating3 = findViewById(R.id.rec_rating3);
        recDesc3 = findViewById(R.id.rec_desc3);
        recSaved3 = findViewById(R.id.rec_saved3);
        recUnsaved3 = findViewById(R.id.rec_unsaved3);

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

        // get user saved
//        DatabaseReference reference_saved = FirebaseDatabase.getInstance().getReference();
//        reference_saved.child("User Information").child(currentUserId).child("saved")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                saved = new String[8];
//                int count = 0;
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    // store all of user's saved places to 'saved'
//                    if (count >= 8) break; // Stop if the array is full
//
//                    // Get the stop_name value for the current snapshot
//                    String savedValue = snapshot.child("stop_name").getValue(String.class);
//                    Log.d(TAG, "                    saving");
//                    if (savedValue != null) { // Ensure savedValue is not null
//                        saved[count] = savedValue; // Store it in the array
//                        count++; // Increment the counter
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });

        fetchRecommendations();
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

                            // check if user saved the place
//                            for (String string : saved) {
//                                if (string != null) {
//                                    if (recName1.getText().toString().equals(string)) {
//                                        recSaved1.setVisibility(View.VISIBLE);
//                                        recUnsaved1.setVisibility(View.INVISIBLE);
//                                    } else if (recName2.getText().toString().equals(string)) {
//                                        recSaved2.setVisibility(View.VISIBLE);
//                                        recUnsaved2.setVisibility(View.INVISIBLE);
//                                    } else if (recName3.getText().toString().equals(string)) {
//                                        recSaved3.setVisibility(View.VISIBLE);
//                                        recUnsaved3.setVisibility(View.INVISIBLE);
//                                    }
//                                }
//                            }

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

                            // check if user saved the place
//                            for (String string : saved) {
//                                if (string != null) {
//                                    if (recName1.getText().toString().equals(string)) {
//                                        recSaved1.setVisibility(View.VISIBLE);
//                                        recUnsaved1.setVisibility(View.INVISIBLE);
//                                    } else if (recName2.getText().toString().equals(string)) {
//                                        recSaved2.setVisibility(View.VISIBLE);
//                                        recUnsaved2.setVisibility(View.INVISIBLE);
//                                    } else if (recName3.getText().toString().equals(string)) {
//                                        recSaved3.setVisibility(View.VISIBLE);
//                                        recUnsaved3.setVisibility(View.INVISIBLE);
//                                    }
//                                }
//                            }

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