package com.example.guy.journeyblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegActivity extends AppCompatActivity {
    private EditText reg_EmailText;
    private EditText reg_PassText;
    private EditText reg_ConfirmPassText;
    private Button reg_Btn;
    private Button reg_loginBtn;
    private FirebaseAuth mAuth;
    private ProgressBar reg_Progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        reg_EmailText = findViewById(R.id.reg_email);
        reg_PassText = findViewById(R.id.reg_password);
        reg_ConfirmPassText= findViewById(R.id.reg_confirm_password);
        reg_Btn = findViewById(R.id.reg_bth);
        reg_loginBtn = findViewById(R.id.reg_login_bth);
        reg_Progress = findViewById(R.id.reg_progress);
        mAuth=FirebaseAuth.getInstance();
        reg_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reg_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = reg_EmailText.getText().toString();
                String password = reg_PassText.getText().toString();
                String confirmed_pass = reg_ConfirmPassText.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmed_pass))
                    if(password.equals(confirmed_pass))
                    {
                        reg_Progress.setVisibility(View.VISIBLE);
                            mAuth.createUserWithEmailAndPassword(email,password) .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Intent setUpIntent = new Intent(RegActivity.this,SetupActivity.class);
                                            Toast.makeText(RegActivity.this,getString(R.string.done),Toast.LENGTH_LONG).show();

                                            startActivity(setUpIntent);
                                    finish();
                                }
                                        else
                                        {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(RegActivity.this,getString(R.string.error)+ error,Toast.LENGTH_LONG).show();
                                        }



                                    reg_Progress.setVisibility(View.INVISIBLE);
                                }
                            });
                    }
                    else{
                        Toast.makeText(RegActivity.this,getString(R.string.passnotmatch),Toast.LENGTH_LONG).show();
                    }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null)
            sendToMain();
        }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
