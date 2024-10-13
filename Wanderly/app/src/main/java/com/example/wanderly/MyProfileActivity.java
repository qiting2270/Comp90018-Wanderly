package com.example.wanderly;

import static android.content.Intent.ACTION_GET_CONTENT;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.Firebase;
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



import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MyProfileActivity extends AppCompatActivity {

    private ImageView menuHomeBtn;
    private TextView myProfileName;
    private ImageView myProfileSetting;
    private TextView editProfileBtn;
    private String userLastName;
    private String userFirstName;

    private TextView addImageBtn;
    private GridLayout gridLayout;

    Uri image;
    //ImageView imageView;

    private FirebaseAuth auth;
    private String currentUserId;

    private ArrayList<String> posts_list = new ArrayList<>();

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null){
                //uploadImageBtn.setEnabled(true);
                image = result.getData().getData();
                //temporary display in imageVIew
                //Glide.with(getApplicationContext()).load(image).into(imageView);
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
        menuHomeBtn = findViewById(R.id.menu_homebutton);
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
        menuHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, HomeActivity.class);
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


        // access db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User Information");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts_list.clear();
                gridLayout.removeAllViews();
                //search data in db
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(currentUserId)){
                        userLastName = snapshot.child("lastname").getValue(String.class);
                        userFirstName = snapshot.child("firstname").getValue(String.class);
                        myProfileName.setText(userFirstName + " " + userLastName);
                        for(DataSnapshot data : snapshot.child("Profile Posts").getChildren()){
                            posts_list.add(Objects.requireNonNull(data.getValue()).toString());
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


}