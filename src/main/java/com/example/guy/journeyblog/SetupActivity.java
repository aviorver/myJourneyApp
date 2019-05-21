package com.example.guy.journeyblog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private CircleImageView setupImage;
    private Uri mainImageUri =null;
    private ProgressBar setupProgress;
    private EditText setupName;
    private Button setupBtn;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private String user_id;
    private boolean isChanged = false;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar_setup = findViewById(R.id.setup_toolbar);
        toolbar_setup.setTitle("Account Setup");

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        setupProgress = findViewById(R.id.setup_progress);
        user_id = mAuth.getCurrentUser().getUid();
        setupBtn = findViewById(R.id.setup_button);
        setupName = findViewById(R.id.setup_name);
        setupImage = findViewById(R.id.setup_image);
        setupBtn.setEnabled(false);
        setupProgress.setVisibility(View.VISIBLE);
        if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        firebaseFirestore.collection("User").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mainImageUri = Uri.parse(image);
                        setupName.setText(name);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.default_image);
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(requestOptions).load(image).into(setupImage);
                    }
                }
                else
                {
                    String error = task.getException().getMessage() ;
                    Toast.makeText(SetupActivity.this, getString(R.string.firebaseerror)+error, Toast.LENGTH_LONG).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);

            }
        });
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }
                }
                else{
                    BringImagePicker();
                }
            }
        });

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_name = setupName.getText().toString();
                if (!TextUtils.isEmpty(user_name) && mainImageUri != null) {
                    setupProgress.setVisibility(View.VISIBLE);
                    if(isChanged) {

                        user_id = mAuth.getCurrentUser().getUid();
                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        image_path.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(task, user_name);

                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                                    setupProgress.setVisibility(View.INVISIBLE);
                                }

                            }
                        });
                    }else {
                        storeFirestore(null,user_name);
                    }
                }else
                {
                    Toast.makeText(SetupActivity.this, getString(R.string.enterpicandname), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void storeFirestore(Task<UploadTask.TaskSnapshot> task, String user_name) {
        Uri download_uri;
        if(task !=null) {
            download_uri = task.getResult().getDownloadUrl();
        }
        else
        {
            download_uri=mainImageUri;
        }
        Map<String,String> userMap = new HashMap<>();
        userMap.put("name",user_name);
        userMap.put("image",download_uri.toString());

        firebaseFirestore.collection("User").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
                else {
                    String error = task.getException().getMessage() ;
                    Toast.makeText(SetupActivity.this, getString(R.string.error)+error, Toast.LENGTH_LONG).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }

    private void BringImagePicker() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(SetupActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                setupImage.setImageURI(mainImageUri);
                isChanged=true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }
}