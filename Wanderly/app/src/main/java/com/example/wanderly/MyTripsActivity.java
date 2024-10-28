package com.example.wanderly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
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
    private LinearLayout pastTripContainer;
    private DatabaseReference databaseReference;
    String profilePicUrl = new String();
    private String tripImageUrl;



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
        pastTripContainer = findViewById(R.id.pasttriplinearLayout);
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
        // find trips that contains the current user email address
        databaseReference.child("Trips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot membersSnapshot = tripSnapshot.child("Members");

                    String numberOfPeople = String.valueOf(membersSnapshot.getChildrenCount());
                    Log.d("numPeople", numberOfPeople);

                    for (DataSnapshot member : membersSnapshot.getChildren()) {
                        String memberEmail = member.child("email").getValue(String.class);
                        if (currentUserEmail.equals(memberEmail)) {
                            addTripView(tripSnapshot, numberOfPeople);
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


    private void addTripView(DataSnapshot tripSnapshot, String numberOfPeople) {
        View tripView = LayoutInflater.from(this).inflate(R.layout.trip_overview_template, null, false);

        tripView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pass the trip ID to schedule activity
                Intent intent = new Intent(MyTripsActivity.this, TripScheduleActivity.class);
                String tripId = tripSnapshot.getKey(); // Get the unique trip ID
                intent.putExtra("TRIP_ID", tripId);
                startActivity(intent);
            }
        });


        TextView tripStartDate = tripView.findViewById(R.id.trip_start_date);
        TextView tripEndDate = tripView.findViewById(R.id.trip_end_date);
        TextView tripFrom = tripView.findViewById(R.id.tripFrom_text);
        TextView inHowmanyDays = tripView.findViewById(R.id.inhowmanydays);
        TextView memberNum = tripView.findViewById(R.id.trippeople_number);
        ImageView userImage = tripView.findViewById(R.id.user_profile_image_tem);
        ImageView tripImage = tripView.findViewById(R.id.trip_image);
        ImageView displayTripBackground = tripView.findViewById(R.id.display_trip_background);


        String departureDate = tripSnapshot.child("departureDate").getValue(String.class);
        String returnDate = tripSnapshot.child("returnDate").getValue(String.class);
        String tripFromText = tripSnapshot.child("departure").getValue(String.class);

        String formattedDepartureDate = DateConverter.convertDate(departureDate);
        String formattedReturnDate = DateConverter.convertDate(returnDate);
        String formattedTripFromText = formatCityCode(tripFromText);

        long daysUntil = DateUtils.daysUntil(departureDate);

        tripStartDate.setText(formattedDepartureDate);
        tripEndDate.setText(formattedReturnDate);
        tripFrom.setText(formattedTripFromText);
        memberNum.setText("+" + numberOfPeople);

        if (daysUntil < 0) {
            inHowmanyDays.setText("Past trip");
            displayTripBackground.setImageResource(R.drawable.tripbackground_past);
            ViewGroup.LayoutParams layoutParams = displayTripBackground.getLayoutParams();
            layoutParams.height = convertDpToPx(150); // Adjust the height
            displayTripBackground.setLayoutParams(layoutParams);
            pastTripContainer.addView(tripView);  // Add to past trip container
        }
        else if (daysUntil == 0){
            inHowmanyDays.setText("today");
            upcomingTripsContainer.addView(tripView);
        }
        else {
            inHowmanyDays.setText("in " + daysUntil + " days");
            upcomingTripsContainer.addView(tripView);  // Add to upcoming trip container
        }

        // Load profile and trip images
        fetchUserProfilePicByEmail(currentUserEmail, userImage);
        String tripImageUrl = formatCityImgCode(tripFromText);
        //String tripImageUrl = "https://firebasestorage.googleapis.com/v0/b/wanderly-ce7ee.appspot.com/o/Uploads%2FMEL_header_2-1.webp?alt=media&token=a6dd3547-74cb-4637-b3d3-6f566503224c";
        Glide.with(this).load(tripImageUrl).into(tripImage);
    }

    private String formatCityImgCode(String city) {
        switch(city) {
            case "Melbourne": return "https://firebasestorage.googleapis.com/v0/b/wanderly-ce7ee.appspot.com/o/Uploads%2FMEL_header_2-1.webp?alt=media&token=a6dd3547-74cb-4637-b3d3-6f566503224c";
            case "Sydney": return "https://firebasestorage.googleapis.com/v0/b/wanderly-ce7ee.appspot.com/o/Uploads%2FOur_favourite_beaches_close_to_Melbourne_1200x600_crop_center.webp?alt=media&token=391f5eea-4c77-4725-838d-ad26e00fb346";
            case "Canberra": return "https://firebasestorage.googleapis.com/v0/b/wanderly-ce7ee.appspot.com/o/Uploads%2Faustralia-melbourne-flinders-street-station-2022-2100x1100__ScaleMaxWidthWzkzMF0.jpg?alt=media&token=1d68524d-61ee-451d-a218-9799bfce3eed";
            case "Perth": return "https://firebasestorage.googleapis.com/v0/b/wanderly-ce7ee.appspot.com/o/Uploads%2Fb2ap3_amp_8-Optimized-shutterstock_274387127.jpg?alt=media&token=f6034a0e-ce2f-40a2-b907-131812db3438";
            case "Gold Coast": return "https://firebasestorage.googleapis.com/v0/b/wanderly-ce7ee.appspot.com/o/Uploads%2Fcaption.jpg?alt=media&token=27fa91f3-3c01-4aee-9d6d-0e18a34e00ea";
            case "Brisbane": return "https://firebasestorage.googleapis.com/v0/b/wanderly-ce7ee.appspot.com/o/Uploads%2Fimage.adapt.800.HIGH.webp?alt=media&token=e22123ce-7983-42da-ae7f-ada5f9bfc301";
            default: return ""; // Default case if city is not matched
        }
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

    private int convertDpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }


    private void fetchUserProfilePicByEmail(String email, ImageView userImage) {
        databaseReference.child("User Information").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        // Safely obtain the profile picture URL, check if it's null first
                        Object profilePicObj = child.child("profile_pic").getValue();
                        profilePicUrl = profilePicObj != null ? profilePicObj.toString() : null;

                        if (profilePicObj != null) {
                            profilePicUrl = profilePicObj.toString();
                            // Load the image directly here after ensuring URL is fetched
                            Glide.with(MyTripsActivity.this).load(profilePicUrl).circleCrop().into(userImage);
                        }
                    }
                }
                if (profilePicUrl == null) {
                    // Fallback if no URL is found
                    Glide.with(MyTripsActivity.this).load(R.drawable.vector).circleCrop().into(userImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching user data", databaseError.toException());
            }
        });
    }



}