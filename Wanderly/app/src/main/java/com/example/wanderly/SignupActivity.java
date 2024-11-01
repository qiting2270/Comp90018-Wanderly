package com.example.wanderly;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button register;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        //back icon logic
        ImageView backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go back to previous activity
                finish();
            }
        });

        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirm_password);
        register = findViewById(R.id.button_create_new_account);
        firstName = findViewById(R.id.signup_firstname);
        lastName = findViewById(R.id.signup_lastname);

        auth = FirebaseAuth.getInstance();
        // when create new account btn is pressed
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_confirmPassword = confirmPassword.getText().toString();
                String txt_firstName = firstName.getText().toString();
                String txt_lastName = lastName.getText().toString();

                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(SignupActivity.this,"Email or Password is Empty!", Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.length() < 6){
                    Toast.makeText(SignupActivity.this, "Password must be at least 6 digits!", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(txt_firstName) || TextUtils.isEmpty(txt_lastName)){
                    Toast.makeText(SignupActivity.this,"First name or Last name is Empty!", Toast.LENGTH_SHORT).show();
                }
                else if(!txt_password.equals(txt_confirmPassword)){
                    Toast.makeText(SignupActivity.this, "Password not identical, Please try again!", Toast.LENGTH_SHORT).show();
                }
                else{
                    registerUser(txt_email, txt_password, txt_firstName, txt_lastName);
                }


            }
        });

    }

    private void registerUser(String email, String password, String firstName, String lastName) {
        //sign up user into database
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // get user id
                    String userId = auth.getCurrentUser().getUid();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("firstname", firstName);
                    map.put("lastname", lastName);
                    map.put("email", email);
                    map.put("password", password);
                    // store user info in db, make userId the key
                    FirebaseDatabase.getInstance().getReference("User Information").child(userId).updateChildren(map);

                    //sign up successful dialog
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SignupActivity.this);
                    dialog.setMessage("Create new account Successful!");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // direct user to login page
                            Intent intent = new Intent(SignupActivity.this, StartActivity.class);
                            startActivity(intent);
                            //finish the current activity
                            finish();
                        }
                    });
                    dialog.show();
                }
                else{
                    //sign up unsuccessful dialog
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SignupActivity.this);
                    dialog.setMessage("Create new account UnSuccessful, Please Try again Later!");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.show();
                }
            }
        });
    }
}