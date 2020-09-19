package com.niccher.areacalc.frags;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.niccher.areacalc.R;
import com.niccher.areacalc.UserLogin;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Profile extends Fragment {

    ImageView userpic,coverimg,fab,testa;
    TextView uname,uphone,uemail;

    FirebaseAuth mAuth;
    FirebaseUser userf;
    DatabaseReference dref1,mDatabaseRef;
    StorageTask mUploadTask;
    StorageReference mStorageRef;

    ProgressDialog pds;
    String PermStor[];
    public static String Varr;
    private static final int StorageCode=20,ImgPickGalleyCode=40,ImgPickCameraCode=60;
    Uri uri_image,uri_cam;
    Bitmap camimg;

    public Frag_Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View solv= inflater.inflate(R.layout.frag_profile, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Profile");

        mAuth= FirebaseAuth.getInstance();

        userf=mAuth.getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference("Area_Calc");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Area_Calc").child(userf.getUid());
        dref1=FirebaseDatabase.getInstance().getReference("Area_Calc").child(userf.getUid());
        dref1.keepSynced(true);

        userpic=solv.findViewById(R.id.com_image);
        uname=solv.findViewById(R.id.com_name);
        uphone=solv.findViewById(R.id.com_phone);
        uemail=solv.findViewById(R.id.com_email);
        coverimg=solv.findViewById(R.id.com_usercover);
        testa=solv.findViewById(R.id.com_test);

        pds=new ProgressDialog(getContext());

        fab=solv.findViewById(R.id.com_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfile();
            }
        });

        PermStor = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        PopulateMe();

        return solv;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser fuse=mAuth.getCurrentUser();
        if (fuse!=null){}else {
            startActivity(new Intent(getContext(), UserLogin.class));
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser fuse=mAuth.getCurrentUser();
        if (fuse!=null){}else {
            startActivity(new Intent(getContext(), UserLogin.class));
            getActivity().finish();
        }
    }

    private void showEditProfile() {
        String options[]={"Edit Profile Picture","Edit Phone","Edit Name"};
        AlertDialog.Builder aka=new AlertDialog.Builder(getContext());

        aka.setTitle("Select any Action");

        aka.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    showEditImg();

                } else if (i==1){
                    //pds.setMessage("Updating Phone");
                    showPhone();

                } else if (i==2){
                    //pds.setMessage("Updating Name");
                    showName();
                }
            }
        });

        aka.create().show();
    }

    private void showEditImg() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setBorderLineColor(Color.RED)
                .setBorderCornerColor(Color.BLUE)
                .setGuidelinesColor(Color.GREEN)
                .setBorderLineThickness(2)
                .start(getContext(),this);
    }

    private void PopulateMe() {
        dref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gEmail,gUsername,gPhone;//, gimgProfile, gimgCover;
                //gUid=dataSnapshot.child("Uid").getValue().toString();
                gEmail=dataSnapshot.child("gEmail").getValue().toString();
                gUsername=dataSnapshot.child("gUsername").getValue().toString();
                gPhone=dataSnapshot.child("gPhone").getValue().toString();
                final String gimgProfile=dataSnapshot.child("gProfile").getValue().toString();
                final String gimgCover=dataSnapshot.child("gCover").getValue().toString();

                uname.setText(gUsername);
                uphone.setText(gPhone);
                uemail.setText(gEmail);

                Picasso.get().load(gimgProfile).resize(200,200).networkPolicy(NetworkPolicy.OFFLINE).into(userpic, new Callback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(gimgProfile).into(userpic);
                    }
                });

                Picasso.get().load(gimgProfile).networkPolicy(NetworkPolicy.OFFLINE).into(coverimg, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(gimgCover).into(coverimg);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean chckPermStor(){

        boolean outcom= ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);
        return outcom;
    }

    private void reqPermStor(){
        ActivityCompat.requestPermissions(getActivity(),PermStor,StorageCode);
    }

    private void showPhone() {

        AlertDialog.Builder aka2=new AlertDialog.Builder(getActivity());

        aka2.setTitle("Update Phone");

        LinearLayout linlay=new LinearLayout(getActivity());
        linlay.setOrientation(LinearLayout.VERTICAL);
        linlay.setPadding(10,10,10,10);

        final EditText edi=new EditText(getActivity());
        edi.setHint("Enter new Phone");
        linlay.addView(edi);

        aka2.setView(linlay);

        aka2.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String new1=edi.getText().toString().trim();
                if (!TextUtils.isEmpty(new1)){
                    pds.show();
                    HashMap<String , Object> hasm2=new HashMap<>();
                    hasm2.put("gPhone",new1);

                    dref1.updateChildren(hasm2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pds.dismiss();
                            PopulateMe();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pds.dismiss();
                            Toast.makeText(getActivity(), "Failed to Update\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(getActivity(), "Blank Space is not Allowed please", Toast.LENGTH_SHORT).show();
                }
            }
        });
        aka2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        aka2.create().show();
    }

    private void showName() {

        AlertDialog.Builder aka2=new AlertDialog.Builder(getActivity());

        aka2.setTitle("Update Username");

        LinearLayout linlay=new LinearLayout(getActivity());
        linlay.setOrientation(LinearLayout.VERTICAL);
        linlay.setPadding(10,10,10,10);

        final EditText edi=new EditText(getActivity());
        edi.setHint("Enter new Username");
        linlay.addView(edi);

        aka2.setView(linlay);

        aka2.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String new1=edi.getText().toString().trim();
                if (!TextUtils.isEmpty(new1)){
                    pds.show();
                    HashMap<String , Object> hasm2=new HashMap<>();
                    hasm2.put("gUsername",new1);

                    dref1.updateChildren(hasm2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pds.dismiss();
                            PopulateMe();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pds.dismiss();
                            Toast.makeText(getActivity(), "Failed to Update\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(getActivity(), "Blank Space is not Allowed please", Toast.LENGTH_SHORT).show();
                }
            }
        });
        aka2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        aka2.create().show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void PublishaProfile(){
        if (uri_image != null) {
            pds.setTitle("Updating Profile");
            pds.setMessage("Uploading the new profile");
            pds.show();
            StorageReference stoRef = FirebaseStorage.getInstance().getReference("Area_Calc_Thumb");
            final DatabaseReference dref2 = FirebaseDatabase.getInstance().getReference("Area_Calc").child(userf.getUid());

            StorageReference fileReference = stoRef.child(System.currentTimeMillis()+ "." + getFileExtension(uri_image));

            mUploadTask = fileReference.putFile(uri_image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String , Object> hasm2=new HashMap<>();
                                    hasm2.put("gProfile",uri.toString());

                                    dref2.updateChildren(hasm2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pds.dismiss();
                                            PopulateMe();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pds.dismiss();
                                            Toast.makeText(getActivity(), "Failed to Update\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    testa.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            //mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No associative Image is selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri_image = result.getUri();
                testa.setVisibility(View.VISIBLE);
                Picasso.get().load(uri_image).into(testa);
                PublishaProfile();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                String er=error.getMessage().toString();
                Toast.makeText(getActivity(), ""+er, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

