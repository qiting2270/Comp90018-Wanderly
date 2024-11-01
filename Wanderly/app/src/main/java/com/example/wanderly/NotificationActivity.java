package com.example.wanderly;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private String currentUserId;
    private TextView notificationTextView;
    private TextView notifyTitle, notifyNone;
    private ImageView notifyFrame;
    private DatabaseReference tripsRef, userRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        EdgeToEdge.enable(this);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        // Back button
        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set title
        notifyTitle = findViewById(R.id.notify_title);
        notifyNone = findViewById(R.id.notify_none);

        // Set content
        notificationTextView = findViewById(R.id.notify_content);
        notifyFrame = findViewById(R.id.notification_content);
        tripsRef = FirebaseDatabase.getInstance().getReference("Trips");
        userRef = FirebaseDatabase.getInstance().getReference("User Information");

        userRef.child(currentUserId).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    String userEmail = userSnapshot.getValue(String.class);

                    // Perform the trip retrieval in parallel to reduce delay
                    new Thread(() -> {
                        tripsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot tripSnapshot : snapshot.getChildren()) {
                                    String tid = tripSnapshot.getKey();

                                    new Thread(() -> {
                                        DataSnapshot membersSnapshot = tripSnapshot.child("Members");
                                        for (DataSnapshot memberSnapshot : membersSnapshot.getChildren()) {
                                            String tripEmail = memberSnapshot.child("email").getValue(String.class);
                                            if (userEmail != null && userEmail.equals(tripEmail)) {
                                                displayNotification(tid);
                                                break; // stop searching once found
                                            } else {
                                                displayNotification("null");
                                            }
                                        }
                                    }).start();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

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

    private void displayNotification(String tid1) {
        tripsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences preferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                if (tid1.equals("null")) {
                    editor.putBoolean("hasNotification", false); // No notification
                    editor.apply();
                    return;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date nearestDate = null;
                String nearestDestination = null;

                // Iterate all trips to find the most recent one
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    String departureDateStr = tripSnapshot.child("departureDate").getValue(String.class);
                    String destination = tripSnapshot.child("destination").getValue(String.class);

                    String tid2 = tripSnapshot.getKey();
                    if (Objects.equals(tid1, tid2)) {
                        if (departureDateStr != null && destination != null) {
                            try {
                                Date departureDate = dateFormat.parse(departureDateStr);

                                if (nearestDate == null || (departureDate != null && departureDate.before(nearestDate))) {
                                    nearestDate = departureDate;
                                    nearestDestination = destination;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                if (nearestDate != null) {
                    // Get the next day of the current date
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Date tomorrowDate = calendar.getTime();

                    // Formatted as a ‘yyyy-MM-dd’ string
                    String formattedNearestDate = dateFormat.format(nearestDate);
                    String formattedTomorrowDate = dateFormat.format(tomorrowDate);

                    notifyNone.setVisibility(View.INVISIBLE);
                    notifyFrame.setVisibility(View.VISIBLE);
                    notifyTitle.setText("New");

                    // Compare dates
                    if (formattedNearestDate.equals(formattedTomorrowDate)) {
                        String message = "Get ready for tomorrow! Tomorrow you have a trip to " + nearestDestination + ".";
                        notificationTextView.setText(message);
                        editor.putBoolean("hasNotification", true);
                    }
                    else {
                        notificationTextView.setText("Upcoming trip: " + formattedNearestDate);
                        editor.putBoolean("hasNotification", false);
                    }
                } else {
                    notificationTextView.setText("No upcoming trips found.");
                    editor.putBoolean("hasNotification", false);
                }
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                notificationTextView.setText("Failed to load trip information.");
                SharedPreferences preferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("hasNotification", false);
                editor.apply();
            }
        });
    }

}
