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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    Button sendCodeBtn;
    EditText email;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        sendCodeBtn =  findViewById(R.id.button_forget_password_sendCode);
        email = findViewById(R.id.forgetpasswordPage_email);

        auth = FirebaseAuth.getInstance();

        //back icon logic
        ImageView backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go back to previous activity
                finish();
            }
        });

        //send code btn listener
        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                if (TextUtils.isEmpty(txt_email)){
                    Toast.makeText(ForgetPasswordActivity.this,"Email is Empty!", Toast.LENGTH_SHORT).show();
                }
                else{
                    ResetPassword(txt_email);
                }

            }
        });


    }

    private void ResetPassword(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //send code successful dialog
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ForgetPasswordActivity.this);
                        dialog.setMessage("Reset Password link has been sent to your registered email!");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // redirect user to login page
                                Intent intent = new Intent(ForgetPasswordActivity.this, StartActivity.class);
                                startActivity(intent);
                                //finish the current activity
                                finish();
                            }
                        });
                        dialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgetPasswordActivity.this, "Send Code failed, Please try again later!", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}