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
import android.widget.ScrollView;
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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MyProfileActivity extends AppCompatActivity {

    private ImageView menuMyProfileBtn;
    private ImageView menuHomeBtn;
    private ImageView menuMapBtn;
    private ImageView menuTripBtn;

    private TextView myProfileName;
    private ImageView myProfileSetting;
    private TextView editProfileBtn;
    private String userLastName;
    private String userFirstName;
    private ImageView myProfilePic;
    private TextView myProfileMessage;
    private ImageView shareProfileBtn;

    private ImageView addImageBtn;
    private GridLayout gridLayout;
    private ScrollView savedPageLayout;
    private TextView savedPageBtn, postPageBtn;
    private ImageView saveBtnUnderline, postBtnUnderline;

    private TextView postNum, tripNum;

    Uri image;
    AlertDialog progressDialog;
    private FirebaseAuth auth;
    private String currentUserId;

    private ArrayList<String> posts_list = new ArrayList<>();

    List<String> savedList;

    private String savedName, savedDesc;
    private float savedRating;
    RatingBar savedRating1, savedRating2, savedRating3;
    private TextView savedName1, savedDesc1, savedName2, savedDesc2, savedName3, savedDesc3;
    ConstraintLayout saved1, saved2, saved3;

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
                Toast.makeText(MyProfileActivity.this, "Please Select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_profile);

/*
        // Retrieve the data from Intent
        String userLastName = getIntent().getStringExtra("userLastName");
        String userFirstName = getIntent().getStringExtra("userFirstName");
*/
        menuMyProfileBtn = findViewById(R.id.menu_profile);
        menuHomeBtn = findViewById(R.id.menu_homebutton);
        menuTripBtn = findViewById(R.id.menu_tripbutton);
        menuMapBtn = findViewById(R.id.menu_map);

        myProfileName = findViewById(R.id.my_profile_name);
        myProfileSetting = findViewById(R.id.my_profile_setting);
        editProfileBtn = findViewById(R.id.my_profile_editProfile);

        //myProfileName.setText(userFirstName + " " + userLastName);
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        FirebaseApp.initializeApp(MyProfileActivity.this);
        //imageView = findViewById(R.id.my_profile_post);
        addImageBtn = findViewById(R.id.my_profile_addimagebtn);
        gridLayout = findViewById(R.id.my_profile_gridLayout);
        savedPageLayout = findViewById(R.id.my_profile_scrollview_savedpage);
        myProfilePic = findViewById(R.id.my_profile_pic);
        myProfileMessage = findViewById(R.id.my_profile_message);
        shareProfileBtn = findViewById(R.id.my_profile_shareProfile);
        savedPageBtn = findViewById(R.id.my_profile_saved_text);
        postPageBtn = findViewById(R.id.my_profile_post_text);
        saveBtnUnderline = findViewById(R.id.my_profile_saved_btn_underline);
        postBtnUnderline = findViewById(R.id.my_profile_post_btn_underline);
        postNum = findViewById(R.id.my_profile_postNum);
        tripNum = findViewById(R.id.my_profile_tripNum);

        saved1 = findViewById(R.id.saved1);
        savedName1 = findViewById(R.id.saved_name1);
        savedRating1 = findViewById(R.id.saved_rating1);
        savedDesc1 = findViewById(R.id.saved_desc1);
        saved2 = findViewById(R.id.saved2);
        savedName2 = findViewById(R.id.saved_name2);
        savedRating2 = findViewById(R.id.saved_rating2);
        savedDesc2 = findViewById(R.id.saved_desc2);
        saved3 = findViewById(R.id.saved3);
        savedName3 = findViewById(R.id.saved_name3);
        savedRating3 = findViewById(R.id.saved_rating3);
        savedDesc3 = findViewById(R.id.saved_desc3);

        // display number of trips
        findTrips();

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
                //uploadImage(image);
            }
        });


        //menu navigation
        menuMyProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, MyProfileActivity.class);
                //intent.putExtra("userLastName", userLastName);
                //intent.putExtra("userFirstName", userFirstname);
                startActivity(intent);
            }
        });
        menuHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        menuMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        menuTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, MyTripsActivity.class);
                startActivity(intent);
            }
        });

        //navigate to setting page
        myProfileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        // navigate to edit profile page
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        // navigate to saved section
        savedPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedPageLayout.setVisibility(View.VISIBLE);
                gridLayout.setVisibility(View.GONE);
                saveBtnUnderline.setVisibility(View.VISIBLE);
                postBtnUnderline.setVisibility(View.GONE);

                // get user saved list
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
                                fetchSaved();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
            }
        });

        // navigate to post section
        postPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedPageLayout.setVisibility(View.GONE);
                gridLayout.setVisibility(View.VISIBLE);
                saveBtnUnderline.setVisibility(View.GONE);
                postBtnUnderline.setVisibility(View.VISIBLE);
            }
        });


        // access db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User Information");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts_list.clear();

                // keep the first element
                for (int i = gridLayout.getChildCount() - 1; i > 0; i--) {
                    View child = gridLayout.getChildAt(i);
                    gridLayout.removeView(child);
                }
                //gridLayout.removeAllViews();
                //search data in db
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(currentUserId)){
                        userLastName = snapshot.child("lastname").getValue(String.class);
                        userFirstName = snapshot.child("firstname").getValue(String.class);
                        //display user name
                        myProfileName.setText(userFirstName + " " + userLastName);
                        String profilePicUrl = snapshot.child("profile_pic").getValue(String.class);
                        if(profilePicUrl != null){
                            // display profile pic
                            Glide.with(MyProfileActivity.this).load(profilePicUrl).circleCrop().into(myProfilePic);
                        }

                        //display bio
                        String message = snapshot.child("userBio").getValue(String.class);
                        myProfileMessage.setText(message);

                        // add profile posts in Posts
                        for(DataSnapshot data : snapshot.child("Profile Posts").getChildren()){
                            posts_list.add(Objects.requireNonNull(data.getValue()).toString());
                        }
                        // update the post number
                        postNum.setText(posts_list.size() + " posts");
                        if (posts_list.size() <= 2) {
                            postNum.setText(posts_list.size() + " post");
                        } else {
                            postNum.setText(posts_list.size() + " posts");
                        }
                    }
                }

                for (String url : posts_list){
                    // display all images under posts (gridlayout).
                    ImageView imageView = new ImageView(MyProfileActivity.this);

                    int widthInDp = 118;
                    int heightInDp = 118;
                    // Convert dp to pixels programmatically
                    int widthInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics());
                    int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
                    // Create LayoutParams
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = widthInPx;
                    params.height = heightInPx;
                    // Set margins (5dp converted to pixels)
                    int marginInDp = 5;
                    int marginInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginInDp, getResources().getDisplayMetrics());
                    params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);

                    imageView.setLayoutParams(params);
                    //load img to ImageView
                    Glide.with(MyProfileActivity.this).load(url).into(imageView);

                    // add imageview to the grid layout
                    gridLayout.addView(imageView);

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        shareProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "www.wanderlyappfuturelink.com");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this amazing travel planning app Wanderly!");
                startActivity(Intent.createChooser(shareIntent, "Share Link"));
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
                        Toast.makeText(MyProfileActivity.this, "Upload image successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyProfileActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyProfileActivity.this, "Upload image failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveUrlToDB(String imageUrl) {
        // get user id
        String userId = auth.getCurrentUser().getUid();
        // store image url in db with random key.
        FirebaseDatabase.getInstance().getReference().child("User Information").child(userId).child("Profile Posts").push().setValue(imageUrl);

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

    // get saved details
    public void fetchSaved() {
        DatabaseReference reference_saved = FirebaseDatabase.getInstance().getReference("Stops");
        reference_saved.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (count >= 3) break; // only want first three

                    savedName = snapshot.child("name").getValue(String.class);
                    savedDesc = snapshot.child("description").getValue(String.class);
                    savedRating = snapshot.child("rating").getValue(Float.class);

                    Log.d("1", "           "+savedList.toString());
                    Log.d("2", "           "+savedName);
                    for (String saved : savedList) {
                        if (saved.equals(savedName)) {
                            Log.d("3", "                 CONTAINS");
                            if (count == 0) {
                                saved1.setVisibility(View.VISIBLE);
                                savedName1.setText(savedName);
                                savedDesc1.setText(savedDesc);
                                savedRating1.setRating(savedRating);

                            } else if (count == 1) {
                                saved2.setVisibility(View.VISIBLE);
                                savedName2.setText(savedName);
                                savedDesc2.setText(savedDesc);
                                savedRating2.setRating(savedRating);

                            } else if (count == 2) {
                                saved3.setVisibility(View.VISIBLE);
                                savedName3.setText(savedName);
                                savedDesc3.setText(savedDesc);
                                savedRating3.setRating(savedRating);

                            }
                            count++;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findTrips() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User Information");
        DatabaseReference scheduleReference = FirebaseDatabase.getInstance().getReference("Trips");

        userReference.child(currentUserId).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    String userEmail = userSnapshot.getValue(String.class);

                    // Perform the trip retrieval in parallel to reduce delay
                    new Thread(() -> {
                        scheduleReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int count = 0;
                                for (DataSnapshot tripSnapshot : snapshot.getChildren()) {
                                    String tid = tripSnapshot.getKey();
                                    DataSnapshot membersSnapshot = tripSnapshot.child("Members");
                                    for (DataSnapshot memberSnapshot : membersSnapshot.getChildren()) {
                                        String tripEmail = memberSnapshot.child("email").getValue(String.class);
                                        if (userEmail != null && userEmail.equals(tripEmail)) {
                                            count++;
                                        }
                                    }
                                    // update the trips number
                                    if (count <= 2) {
                                        tripNum.setText(count + " trip");
                                    } else {
                                        tripNum.setText(count + " trips");
                                    }

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

}