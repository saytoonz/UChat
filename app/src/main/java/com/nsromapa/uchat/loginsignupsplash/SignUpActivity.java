package com.nsromapa.uchat.loginsignupsplash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nsromapa.uchat.MainActivity;
import com.nsromapa.uchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nsromapa.uchat.customizations.CustomIntent;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    Button signupBtn;
    TextView loginLink;
    ConstraintLayout signupLayout;
    EditText userId, password1, password2, fullname;

    ProgressDialog progressDialog;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() != null) {

                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    CustomIntent.customType(SignUpActivity.this, "up-to-bottom");
                    finish();
                }
            }
        };


        signupBtn = findViewById(R.id.signupBtn);
        loginLink = findViewById(R.id.loginLink);
        userId = findViewById(R.id.signupUserId);
        password1 = findViewById(R.id.signupPass);
        password2 = findViewById(R.id.signupRPass);
        fullname = findViewById(R.id.signupName);
        signupLayout = findViewById(R.id.signupLayout);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signupBtn.setVisibility(View.GONE);

                final String index = userId.getText().toString();
                final String mail = index+"@uner.nsromapa";
                final String fname = fullname.getText().toString();
                final String password = password1.getText().toString();
                final String passwordR = password2.getText().toString();

                if (!fname.equals("") && !mail.equals("") && !password.equals("") && !passwordR.equals("")) {
                    if (password.equals(passwordR)) {

                        ShowProgressDialog(SignUpActivity.this,"Please wait...");

                        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    signupBtn.setVisibility(View.VISIBLE);
                                    //LoginActivity.ShowSnacckbar(signupLayout, task.getResult().toString());

                                    String errorText = Objects.requireNonNull(task.getException()).toString();

                                   if (errorText.contains("FirebaseApiNotAvailableException")) {
                                        errorText = "Please check your internet connection...";
                                    } else if (errorText.contains("PasswordException")) {
                                        errorText = "Password must be at least 6 characters...";

                                    } else if (errorText.contains("UserCollisionException")) {
                                        errorText = "User already exist...";

                                    } else {
                                        errorText = "Unrecognised error...";
                                    }

                                   errorText = errorText.replace("com.google.firebase.auth.FirebaseAuthInvalidUserException:", "");
                                   errorText = errorText.replace("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException:", "");
                                   errorText = errorText.replace("com.google.firebase.auth.FirebaseAuthTooManyRequestsException:", "");

                                   errorText = errorText.replace("Firebase", "");
                                   errorText = errorText.replace("firebase", "");
                                   errorText = errorText.replace("com", "");
                                   errorText = errorText.replace("google", "");
                                   errorText = errorText.replace(".", "");
                                   errorText = errorText.replace(":", "");

                                    ShowErrorAlertDialog(SignUpActivity.this, errorText);

                                    progressDialog.dismiss();



                                } else {
                                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference()
                                            .child("users").child(userId);

                                    HashMap<String,Object> userInfo = new HashMap<>();
                                    userInfo.put("name",fname);
                                    userInfo.put("email",mail);
                                    userInfo.put("index",index);
                                    userInfo.put("uid",userId);
                                    userInfo.put("profileImageUrl","default");
                                    userInfo.put("welcomed","not yet");

                                    currentUserDb.updateChildren(userInfo);

                                }
                            }
                        });

                    } else {
                        signupBtn.setVisibility(View.VISIBLE);
                        LoginActivity.ShowSnacckbar(signupLayout, "Passwords do not much...");
                    }

                } else {
                    signupBtn.setVisibility(View.VISIBLE);
                    LoginActivity.ShowSnacckbar(signupLayout, "Please provide all informations");
                }

            }
        });


        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                CustomIntent.customType(SignUpActivity.this,"left-to-right");
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }



    public void ShowErrorAlertDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                progressDialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void ShowProgressDialog(Context ctx, String message) {
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }
}
