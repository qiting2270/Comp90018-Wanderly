package com.example.wanderly;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class StopActivity extends AppCompatActivity {
    private String activity_ID = new String();
    private String placeName = new String();
    //private String dayNum = new String();

    private TextView stopPlaceName;
    private ImageView stop_save_btn;
    //private TextView DayText;

    private TextView stopTextDescription;
    private String textDescription;

    private DatabaseReference databaseReference;
    RatingBar stopRating;
    private float rating;

    /*
    private String timeFrom = new String();
    private String timeTo = new String();*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stop);

        placeName = getIntent().getStringExtra("placeName");
        activity_ID = getIntent().getStringExtra("activity_ID");
        //dayNum = getIntent().getStringExtra("day");

        stopPlaceName = findViewById(R.id.stop_place_name);
        stop_save_btn = findViewById(R.id.stop_save_Btn);
        stopTextDescription = findViewById(R.id.stop_text_description);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        stopRating = findViewById(R.id.stop_ratingbar);


        //back icon logic
        ImageView backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go back to previous activity
                finish();
            }
        });

        stopPlaceName.setText(placeName);


        // display description of the place from database
        databaseReference.child("Stops").orderByChild("name").equalTo(placeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                        textDescription = snapShot.child("description").getValue(String.class);
                        rating = snapShot.child("rating").getValue(float.class);
                        stopRating.setRating(rating);

                        if (textDescription != null){
                            stopTextDescription.setText(textDescription);
                        }
                        else{
                            stopTextDescription.setText("Description not available.");
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