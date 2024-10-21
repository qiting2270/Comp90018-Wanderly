package com.example.wanderly;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class BookmarkActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private String currentUserId;
    private ImageView menuMyProfileBtn;
    private ImageView menuHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookmark);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        menuHomeBtn = findViewById(R.id.menu_homebutton);
        menuMyProfileBtn = findViewById(R.id.menu_profile);

        //menu navigation
        menuHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookmarkActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        menuMyProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookmarkActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });
    }



}
