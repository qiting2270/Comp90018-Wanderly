package com.example.wanderly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView changeProfilePic;
    Uri image;
    private String imageUrl;
    private TextView Done;
    private EditText editFirstname;
    private EditText editLastname;
    private EditText editBio;

    private FirebaseAuth auth;
    private String currentUserId;
    AlertDialog progressDialog;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null){
                //uploadImageBtn.setEnabled(true);
                image = result.getData().getData();
                //temporary display in imageVIew
                Glide.with(getApplicationContext()).load(image).into(changeProfilePic);
                showProgressDialog();
                uploadImage(image);
            }
            else{
                Toast.makeText(EditProfileActivity.this, "Please Select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        changeProfilePic = findViewById(R.id.setting_change_profilePicture_Btn);
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        Done = findViewById(R.id.edit_profile_done);
        editFirstname = findViewById(R.id.edit_profile_firstName);
        editLastname = findViewById(R.id.edit_profile_lastName);
        editBio = findViewById(R.id.edit_profile_Bio);

        //back icon logic
        ImageView backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go back to previous activity
                finish();
            }
        });


        changeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = editFirstname.getText().toString();
                String lastName = editLastname.getText().toString();
                String bio = editBio.getText().toString();
                // save url to real time database
                saveInfoToDB(imageUrl, firstName, lastName, bio);

                Toast.makeText(EditProfileActivity.this, "Update Profile info successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditProfileActivity.this, MyProfileActivity.class);
                startActivity(intent);

            }
        });

        //access db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User Information");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.getKey().equals(currentUserId)){
                        String userLastName = snapshot.child("lastname").getValue(String.class);
                        String userFirstName = snapshot.child("firstname").getValue(String.class);
                        String userBio = snapshot.child("userBio").getValue(String.class);
                        String profile_pic = snapshot.child("profile_pic").getValue(String.class);
                        editFirstname.setText(userFirstName);
                        editLastname.setText(userLastName);
                        editBio.setText(userBio);
                        if (profile_pic != null){
                            Glide.with(getApplicationContext()).load(profile_pic).into(changeProfilePic);
                        }

                    }
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
                        imageUrl = uri.toString();
                        dismissProgressDialog();
                        Log.d("Url image", imageUrl);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Upload image failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveInfoToDB(String imageUrl, String firstName, String lastName, String userBio) {
        // get user id
        String userId = auth.getCurrentUser().getUid();

        HashMap<String, Object> map = new HashMap<>();
        map.put("firstname", firstName);
        map.put("lastname", lastName);
        map.put("userBio", userBio);
        // Only add imageUrl to the map if it is not null or empty
        if (imageUrl != null && !imageUrl.isEmpty()) {
            map.put("profile_pic", imageUrl);
        }

        // store user info in db, make userId the key
        FirebaseDatabase.getInstance().getReference("User Information").child(userId).updateChildren(map);
        //FirebaseDatabase.getInstance().getReference().child("User Information").child(userId).child("profile_pic").setValue(imageUrl);

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