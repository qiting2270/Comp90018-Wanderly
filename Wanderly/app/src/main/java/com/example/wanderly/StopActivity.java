package com.example.wanderly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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

import com.bumptech.glide.Glide;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class StopActivity extends AppCompatActivity {
    private String tripID = new String();
    private String ActivityID = new String();
    private String placeName = new String();
    private String day = new String();

    private TextView stopPlaceName;
    private ImageView stop_save_btn_unsaved;
    private ImageView stop_save_btn_saved;
    //private TextView DayText;

    private TextView stopTextDescription;
    private String textDescription;

    private DatabaseReference databaseReference;
    RatingBar stopRating;
    private float rating;

    private GridLayout gridLayout;
    Uri image;
    AlertDialog progressDialog;

    ImageView stop_uploadImageBtn;
    private ArrayList<String> posts_list = new ArrayList<>();

    private FirebaseAuth auth;
    private String currentUserId = new String();

    private String stopID = new String();

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
        ActivityID = getIntent().getStringExtra("ActivityID");
        day = getIntent().getStringExtra("day");
        Log.d("intent_info", day);
        Log.d("intent_info", tripID);
        Log.d("intent_info", ActivityID);

        stopPlaceName = findViewById(R.id.stop_place_name);
        stop_save_btn_unsaved = findViewById(R.id.stop_save_Btn);
        stop_save_btn_saved = findViewById(R.id.stop_save_Btn_saved);
        stopTextDescription = findViewById(R.id.stop_text_description);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        stopRating = findViewById(R.id.stop_ratingbar);
        gridLayout = findViewById(R.id.stop_grid_layout);

        stop_uploadImageBtn = findViewById(R.id.stop_upload_image_btn);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid().toString();
        Log.d("CurrentUserId", currentUserId);


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
                        stopID = snapShot.getKey();

                        for (DataSnapshot userdata : snapShot.child("saved_user").getChildren()){
                            // check if userid in database match the current user id
                            String userIDinDB = userdata.child("userID").getValue(String.class);
                            if (Objects.equals(userIDinDB, currentUserId) && userIDinDB != null){
                                stop_save_btn_saved.setVisibility(View.VISIBLE);
                                stop_save_btn_unsaved.setVisibility(View.GONE);
                                break;
                            }
                            else {
                                stop_save_btn_saved.setVisibility(View.GONE);
                                stop_save_btn_unsaved.setVisibility(View.VISIBLE);
                            }
                        }

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


        // fetch images from database and display it in the gridlayout.
        databaseReference.child("Trips").child(tripID).child("activities").child(day).child(ActivityID).child("stop_images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts_list.clear();

                // keep the first element
                for (int i = gridLayout.getChildCount() - 1; i > 0; i--) {
                    View child = gridLayout.getChildAt(i);
                    gridLayout.removeView(child);
                }

                for(DataSnapshot data : snapshot.getChildren()){
                    // add each image url in the list.
                    posts_list.add(Objects.requireNonNull(data.getValue()).toString());
                }
                addImagesInLayout();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //save user_id in db under Stops:
        stop_save_btn_unsaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> savedUserMap = new HashMap<>();
                savedUserMap.put("userID", currentUserId);
                savedUserMap.put("tripID", tripID);
                savedUserMap.put("ActivityID", ActivityID);
                savedUserMap.put("placeName", placeName);
                savedUserMap.put("day", day);

                databaseReference.child("Stops").child(stopID).child("saved_user").push().setValue(savedUserMap);

                stop_save_btn_unsaved.setVisibility(View.GONE);
                stop_save_btn_saved.setVisibility(View.VISIBLE);
            }
        });

        //unsave stop:
        stop_save_btn_saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUserFromStopsDB();
                stop_save_btn_unsaved.setVisibility(View.VISIBLE);
                stop_save_btn_saved.setVisibility(View.GONE);
            }
        });








    }

    private void deleteUserFromStopsDB() {
        databaseReference.child("Stops").child(stopID).child("saved_user")
                .orderByChild("userID").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            // Loop through the result to delete each matching child
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase Error", "Error: " + databaseError.getMessage());
                    }
                });

    }

    private void addImagesInLayout() {
        if (!isFinishing() && !isDestroyed()) {  // Ensure activity is still running
            for (String url : posts_list) {
                ImageView imageView = new ImageView(StopActivity.this);

                int heightInDp = 118;
                int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = heightInPx;
                int marginInDp = 5;
                int marginInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginInDp, getResources().getDisplayMetrics());
                params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);

                // Specify the row and column index, and column weight
                int columnIndex = gridLayout.getChildCount() % 3; // Assuming 3 columns, modify if your column count is different
                GridLayout.Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
                GridLayout.Spec colSpec = GridLayout.spec(columnIndex, 1.0f); // Use a weight of 1 for even distribution

                params.rowSpec = rowSpec;
                params.columnSpec = colSpec;

                imageView.setLayoutParams(params);
                // Set scale type
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(StopActivity.this).load(url).into(imageView);
                gridLayout.addView(imageView);
            }
        } else {
            Log.d("GlideError", "Activity is either finishing or destroyed, not loading image.");
        }
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
        FirebaseDatabase.getInstance().getReference().child("Trips").child(tripID).child("activities").child(day).child(ActivityID).child("stop_images").push().setValue(imageUrl);
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