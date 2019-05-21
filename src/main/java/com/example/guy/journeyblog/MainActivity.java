package com.example.guy.journeyblog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar mainTool;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton addPostBtn;
    private String current_user_id;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private ToDoFragment toDoFragment;
    private ProgressBar delet_progress;
    private boolean exit = true;
    private AlertDialog alertDialog;
    private static final int LOCATION_REQUEST = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainTool = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainTool);
        getSupportActionBar().setTitle(R.string.myjoureny);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        addPostBtn = findViewById(R.id.add_post_btn);
        delet_progress = findViewById(R.id.deleteProgress);
        delet_progress.setVisibility(View.INVISIBLE);
        alertDialog = new SpotsDialog(this);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
            }
        });
        //Fragments
        toDoFragment = new ToDoFragment();
        homeFragment = new HomeFragment();


        bottomNavigationView = findViewById(R.id.mainBottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home_text:
                        addPostBtn.setVisibility(View.VISIBLE);
                        replaceFragments(homeFragment);
                        exit = true;

                        return true;
                    case R.id.buttom_map_text:
                        addPostBtn.setVisibility(View.INVISIBLE);
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                showPermissionDialog();
                            }else{
                                Intent loginIntent = new Intent(MainActivity.this, MapsActivity.class);
                                startActivity(loginIntent);
                            }
                        return true;
                    case R.id.buttom_todo_text:
                        exit = false;

                        addPostBtn.setVisibility(View.INVISIBLE);
                        replaceFragments(toDoFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
        replaceFragments(homeFragment);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        } else {
            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("User").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            //     finish();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, getString(R.string.error) + error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout_btn: {
                logout();
                return false;
            }
            case R.id.action_settings_btn: {
                Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingsIntent);
                return true;
            }
            default:
                return true;
        }

    }

    private void logout() {
        homeFragment.onDestroy();
        mAuth.signOut();
        sendToLogin();
    }

    private void replaceFragments(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bottom_home_text);
    }

    @Override
    public void onBackPressed() {
        if (exit)
            super.onBackPressed();
        else {
            replaceFragments(homeFragment);
            bottomNavigationView.setSelectedItemId(R.id.bottom_home_text);
            exit = true;
        }
    }

    private void showPermissionDialog() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST);
        }
    }
}
