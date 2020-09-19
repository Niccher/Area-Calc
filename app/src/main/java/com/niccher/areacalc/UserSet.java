package com.niccher.areacalc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import com.niccher.areacalc.mod.Mod_UserConfig;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class UserSet extends AppCompatActivity {

    EditText emlR,pwdR;
    TextView sback;
    ImageView imgsel,imgshow;
    ProgressDialog pds;
    ProgressBar prog;

    Uri uri_image;
    Uri uri_thums;
    Bitmap bit_thum;

    FirebaseAuth mAuth;
    FirebaseUser userf;
    DatabaseReference dref;
    FirebaseDatabase fdbas;
    StorageReference stoRef,stoThumb;

    StorageTask mUploadTask,mUploadThub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_set);

        mAuth = FirebaseAuth.getInstance();
        userf=mAuth.getCurrentUser();
        fdbas= FirebaseDatabase.getInstance();

        stoRef = FirebaseStorage.getInstance().getReference("Area_Calc");
        stoThumb = FirebaseStorage.getInstance().getReference("Area_Calc_Thumb");

        dref = FirebaseDatabase.getInstance().getReference("Area_Calc");

        sback = (TextView) findViewById(R.id.user_new_procid);
        imgsel = findViewById(R.id.com_imgsel);
        imgshow = findViewById(R.id.com_image);

        emlR= (EditText) findViewById(R.id.user_set_usr);
        pwdR= (EditText) findViewById(R.id.user_set_phone);

        pds=new ProgressDialog(this);

        imgsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setBorderLineColor(Color.RED)
                    .setBorderCornerColor(Color.BLUE)
                    .setGuidelinesColor(Color.GREEN)
                    .setBorderLineThickness(2)
                    .start(UserSet.this);
            }
        });

        sback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(UserSet.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    Verrif();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri_image = result.getUri();
                uri_thums = result.getUri();
                Picasso.get().load(uri_image).into(imgshow);
                Verrif();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                String er=error.getMessage().toString();
                Toast.makeText(this, "onActivityResult error\n"+er, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void Verrif() {
        final String usern = emlR.getText().toString().trim();
        final String phone = pwdR.getText().toString().trim();

        if (usern.isEmpty()) {
            emlR.setError("Username is required");
            emlR.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            pwdR.setError("Phone is required");
            pwdR.requestFocus();
            return;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            emlR.setError("Please enter a valid Phone Number");
            emlR.requestFocus();
            return;
        }

        if (phone.length() < 8) {
            pwdR.setError("Minimum lenghth of a Phone should be 10");
            pwdR.requestFocus();
            return;
        }

        if (uri_image != null) {

            File thumb_file = new File(uri_image.getPath());

            try {
                bit_thum = new Compressor(this)
                        .setMaxWidth(120)
                        .setMaxHeight(120)
                        .setQuality(60)
                        .compressToBitmap(thumb_file);
            } catch (IOException e) {
                Toast.makeText(this, "Bitmap errors", Toast.LENGTH_SHORT).show();
            }

            pds.setMessage("Please Wait");
            pds.show();

            ByteArrayOutputStream baos =new ByteArrayOutputStream();
            bit_thum.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] dat=baos.toByteArray();

            StorageReference fileReference = stoRef.child(userf.getUid()+"." + "jpeg");

            StorageReference thumbReference = stoThumb.child(userf.getUid()+ "." + "jpeg");

            try {
                mUploadThub = thumbReference.putBytes(dat).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> thumbres = taskSnapshot.getStorage().getDownloadUrl();
                        thumbres.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri urithum) {

                                Mod_UserConfig upload = new Mod_UserConfig(userf.getUid(),userf.getEmail(),usern,phone,urithum.toString(),urithum.toString(),urithum.toString(),urithum.toString());
                                dref.child(userf.getUid()).setValue(upload);

                                finish();
                                startActivity(new Intent(UserSet.this,Casa.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            }
                        });

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pds.dismiss();
                                Toast.makeText(UserSet.this, "Thumb addOnFailureListener\n"+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }catch ( Exception uplod){
                pds.dismiss();
                Toast.makeText(this, "Thumbnail Upload failure\n"+uplod.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            pds.dismiss();
            Toast.makeText(this, "No associative Profile image is selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(UserSet.this,UserNew.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
