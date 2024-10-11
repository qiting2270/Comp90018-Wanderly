package com.example.wanderly;

import static android.content.Intent.ACTION_GET_CONTENT;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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

import java.util.HashMap;
import java.util.UUID;

public class MyProfileActivity extends AppCompatActivity {

    private ImageView menuHomeBtn;
    private TextView myProfileName;


    private TextView addImageBtn;

    Uri image;
    ImageView imageView;

    private FirebaseAuth auth;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null){
                //uploadImageBtn.setEnabled(true);
                image = result.getData().getData();
                //temporary display in imageVIew
                Glide.with(getApplicationContext()).load(image).into(imageView);
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


        // Retrieve the data from Intent
        String userLastName = getIntent().getStringExtra("userLastName");
        String userFirstName = getIntent().getStringExtra("userFirstName");

        menuHomeBtn = findViewById(R.id.menu_homebutton);
        myProfileName = findViewById(R.id.my_profile_name);
        myProfileName.setText(userFirstName + " " + userLastName);
        auth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(MyProfileActivity.this);
        imageView = findViewById(R.id.my_profile_post);
        addImageBtn = findViewById(R.id.my_profile_addimagebtn);


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
                        String imageUrl = uri.toString();  // Here you get the download URL as a string
                        Log.d("urllink", imageUrl);
                        Toast.makeText(MyProfileActivity.this, "Upload image successful", Toast.LENGTH_SHORT).show();
                        // save url to real time database
                        //saveUrlToDB(imageUrl);

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
/*
    private void saveUrlToDB(String imageUrl) {
        // get user id
        String userId = auth.getCurrentUser().getUid();

    }*/


}