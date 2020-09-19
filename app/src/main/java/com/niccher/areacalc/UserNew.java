package com.niccher.areacalc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class UserNew extends AppCompatActivity {

    EditText emlR,pwdR;
    ProgressBar rotR;
    ImageView sback;
    TextView go;
    private FirebaseAuth mAuth;

    Boolean term=false;

    ProgressDialog pds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_new);

        go = (TextView) findViewById(R.id.user_new_procid);

        mAuth = FirebaseAuth.getInstance();


        emlR= (EditText) findViewById(R.id.user_new_eml);
        pwdR= (EditText) findViewById(R.id.user_new_pwd);

        pds=new ProgressDialog(this);

        sback = (ImageView)findViewById(R.id.sback);
        sback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(UserNew.this,UserLogin.class);
                startActivity(it);
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                RegisterUser();
            }
        });
    }

    private void RegisterUser() {
        String email = emlR.getText().toString().trim();
        String password = pwdR.getText().toString().trim();

        if (email.isEmpty()) {
            emlR.setError("Email is required");
            emlR.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emlR.setError("Please enter a valid email");
            emlR.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            pwdR.setError("Password is required");
            pwdR.requestFocus();
            return;
        }

        if (password.length() < 6) {
            pwdR.setError("Minimum lenght of password should be 6");
            pwdR.requestFocus();
            return;
        }

        pds.setTitle("Creating Account");
        pds.setMessage("Please wait");
        pds.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pds.dismiss();
                if (task.isSuccessful()) {
                    finish();
                    startActivity(new Intent(UserNew.this, UserSet.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Email has been registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(UserNew.this,UserLogin.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
