package com.example.wanderly;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.UUID;

public class StopActivity extends AppCompatActivity {
    private String tripID = new String();
    private String stopID = new String();
    private String placeName = new String();
    private String day = new String();

    private TextView stopPlaceName;
    private ImageView stop_save_btn;
    //private TextView DayText;

    private TextView stopTextDescription;
    private String textDescription;

    private DatabaseReference databaseReference;
    RatingBar stopRating;
    private float rating;

    private GridLayout gridLayout;
    Uri image;
    AlertDialog progressDialog;
    private FirebaseAuth auth;

    ImageView stop_uploadImageBtn;

    /*
    private String timeFrom = new String();
    private String timeTo = new String();*/
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null){
                //uploadImageBtn.setEnabled(true);
                image = result.getData().getData();
                //temporary display in imageVIew
                //Glide.with(getApplicationContext()).load(image).into(imageView);
                showProgressDialog();
                uploadImage(image);
            }
            else{
                Toast.makeText(StopActivity.this, "Please Select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stop);

        tripID = getIntent().getStringExtra("tripID");
        placeName = getIntent().getStringExtra("placeName");
        stopID = getIntent().getStringExtra("stopID");
        day = getIntent().getStringExtra("day");
        Log.d("intent_info", day);
        Log.d("intent_info", tripID);
        Log.d("intent_info", stopID);

        stopPlaceName = findViewById(R.id.stop_place_name);
        stop_save_btn = findViewById(R.id.stop_save_Btn);
        stopTextDescription = findViewById(R.id.stop_text_description);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        stopRating = findViewById(R.id.stop_ratingbar);
        gridLayout = findViewById(R.id.stop_grid_layout);

        stop_uploadImageBtn = findViewById(R.id.stop_upload_image_btn);


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


        stop_uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });






    }

    private void uploadImage(Uri image) {
        StorageReference fileRef = FirebaseStorage.getInstance().getReference("Uploads").child("images/" + UUID.randomUUID().toString());
        fileRef.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // retrieve the image URL
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        Log.d("urllink", imageUrl);
                        // save url to real time database
                        saveUrlToDB(imageUrl);
                        dismissProgressDialog();
                        Toast.makeText(StopActivity.this, "Upload image successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StopActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StopActivity.this, "Upload image failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUrlToDB(String imageUrl) {
        // store image url in db with random key.
        FirebaseDatabase.getInstance().getReference().child("Trips").child(tripID).child("activities").child(day).child(stopID).child("stop_images").push().setValue(imageUrl);
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progress_dialog, null));
        builder.setCancelable(false);  // Disables outside touch dismissal

        progressDialog = builder.create();
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }



}