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

public class SignupActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button register;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        auth = FirebaseAuth.getInstance();
        // register new user
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_confirmPassword = confirmPassword.getText().toString();
                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(SignupActivity.this,"Email or Password is Empty!", Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.length() < 6){
                    Toast.makeText(SignupActivity.this, "Password must be at least 6 digits!", Toast.LENGTH_SHORT).show();
                }
                else if(!txt_password.equals(txt_confirmPassword)){
                    Toast.makeText(SignupActivity.this, "Password not identical, Please try again!", Toast.LENGTH_SHORT).show();
                }
                else{
                    registerUser(txt_email, txt_password);
                }


            }
        });

    }

    private void registerUser(String email, String password) {
        //sign up user into database
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
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