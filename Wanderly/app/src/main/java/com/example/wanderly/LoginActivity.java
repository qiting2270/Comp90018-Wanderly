package com.example.wanderly;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ktx.Firebase;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //back icon logic
        ImageView backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go back to previous activity
                finish();
            }
        });

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.button_login);

        auth = FirebaseAuth.getInstance();
        //log in user
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                loginUser(txt_email, txt_password);
            }
        });

        // navigate to forgot password page
        TextView forgot_password = findViewById(R.id.forgot_password_btn);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPassword.class);
                startActivity(intent);
            }
        });


    }

    private void loginUser(String email, String password) {
        // sign in users
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // sign in successful dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                dialog.setMessage("Login successful, Welcome!");
                dialog.setCancelable(true);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // navigate to main page
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        // finish current activity, don't allow user to go back
                        finish();
                    }
                });
                dialog.show();

            }
        });
    }
}