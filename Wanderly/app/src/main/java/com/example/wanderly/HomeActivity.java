package com.example.wanderly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String currentUserId;
    private TextView home_greeting;
    private ImageView menuMyProfileBtn;
    private String userLastName;
    private String userFirstname;
    private ImageView viewmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        home_greeting = findViewById(R.id.home_greeting);
        menuMyProfileBtn = findViewById(R.id.menu_profile);
        viewmap = findViewById(R.id.imageView8);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User Information");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(currentUserId)){
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

        //menu navigation
        menuMyProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
                intent.putExtra("userLastName", userLastName);
                intent.putExtra("userFirstName", userFirstname);

                startActivity(intent);
            }
        });

        viewmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MapsActivity2.class);
                startActivity(intent);
            }
        });



    }
}