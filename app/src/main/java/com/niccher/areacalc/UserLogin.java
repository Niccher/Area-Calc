package com.niccher.areacalc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.khirr.android.privacypolicy.PrivacyPolicyDialog;

public class UserLogin extends AppCompatActivity {

    TextView caac,forgpwd,lg,cret;
    EditText eml,pwd;
    ImageView sback;
    ProgressDialog pds;

    FirebaseAuth mAuth;

    View mview;
    Button terms;

    String term_accpt,term_liab,term_disclaimer,term_declare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //MultiDex.install(this);

        mAuth = FirebaseAuth.getInstance();

        lg = (TextView) findViewById(R.id.user_log_procid);
        cret = (TextView) findViewById(R.id.user_log_new);

        eml= (EditText) findViewById(R.id.user_log_eml);
        pwd= (EditText) findViewById(R.id.user_log_pwd);
        pds=new ProgressDialog(this);

        LoadData();

        lg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogmeIN();
            }
        });

        cret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLogin.this, UserNew.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        term_accpt = "By accessing this service we assume you accept these terms and conditions. Do not continue to use this app if you do not agree to take all of the terms and conditions stated here";
        term_liab = "We shall not be hold responsible for any content that appears on platform. No content should appear on this platform, if it may be interpreted as rebellious, obscene or criminal, or which infringes, otherwise violates, or advocates the infringement or other violation of, any third party rights.\n" +
                "    We do not ensure that the information on this platform is correct, we do not warrant its completeness or accuracy; nor do we promise to ensure that the platform remains available or that the material on the posted is kept up to date.";
        term_disclaimer = "To the maximum extent permitted by applicable law, we exclude all representations, warranties and conditions relating to our platform and the use of this website. Nothing in this disclaimer will:\n" +
                "\n" +
                "    limit or exclude our or your liability for personality demeanor;\n" +
                "    limit or exclude our or your liability for fraud or fraudulent misrepresentation;\n" +
                "    limit any of our or your liabilities in any way that is not permitted under applicable law; or\n" +
                "    exclude any of our or your liabilities that may not be excluded under applicable law.";
        term_declare = "As long as the information and services on the platform are provided free of charge, we will not be liable for any loss or damage of any nature";
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, Casa.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else if (mAuth.getCurrentUser() == null) {

            PrivacyPolicyDialog dialog = new PrivacyPolicyDialog(this,
                    "link1",
                    "link2");

            dialog.addPoliceLine(term_accpt);
            dialog.addPoliceLine(term_liab);
            dialog.addPoliceLine(term_disclaimer);
            dialog.addPoliceLine(term_declare);

            dialog.setTitleTextColor(Color.parseColor("#222222"));
            dialog.setAcceptButtonColor(ContextCompat.getColor(this, R.color.colorAccent));

            dialog.setOnClickListener(new PrivacyPolicyDialog.OnClickListener() {
                @Override
                public void onAccept(boolean isFirstTime) {
                }

                @Override
                public void onCancel() {
                    Log.e("Casa", "Policies not accepted");
                    finish();
                }
            });

            dialog.show();
        }
    }

    private void LogmeIN() {
        String email = eml.getText().toString().trim();
        String password = pwd.getText().toString().trim();

        if (email.isEmpty()) {
            eml.setError("Email is required");
            eml.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eml.setError("Please enter a valid email");
            eml.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            pwd.setError("Password is required");
            pwd.requestFocus();
            return;
        }

        if (password.length() < 6) {
            pwd.setError("Minimum length of password should be 6 Characters");
            pwd.requestFocus();
            return;
        }

        pds.setMessage("Please wait");
        pds.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pds.dismiss();
                if (task.isSuccessful()) {
                    finish();
                    Intent intent = new Intent(UserLogin.this, Casa.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void LoadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("Area_Calc_Prefs", MODE_PRIVATE);
        Boolean theme = sharedPreferences.getBoolean("themes", false);
        String intro = sharedPreferences.getString("intro_slide","has_not");

        Log.e("LoadData", "intro_slide is set as " + intro);

        if (Boolean.toString(theme)=="true"){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        if (Boolean.toString(theme)=="false"){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (intro =="has_not"){
            Intent gog=new Intent(this,ActivitySlider.class);
            gog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(gog);
            UserLogin.this.finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (intro =="has_seen") {

        }
    }

}