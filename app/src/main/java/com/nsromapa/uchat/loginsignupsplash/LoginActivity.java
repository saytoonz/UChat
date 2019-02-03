package com.nsromapa.uchat.loginsignupsplash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.auth.FirebaseUser;
import com.nsromapa.uchat.customizations.CustomIntent;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    TextView signupLink;
    private EditText loginPass, loginUserId;
    ConstraintLayout loginLayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAutListener;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        firebaseAutListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    CustomIntent.customType(LoginActivity.this, "rotateout-to-rotatein");
                    finish();
                }
            }
        };

        loginBtn =  findViewById(R.id.loginBtn);
        signupLink =  findViewById(R.id.signupLink);
        loginUserId =  findViewById(R.id.loginUserId);
        loginPass =  findViewById(R.id.loginPass);
        loginLayout =  findViewById(R.id.loginLayout);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setVisibility(View.GONE);

                final String index = loginUserId.getText().toString();
                final String mail = index+"@uner.nsromapa";
                final String password = loginPass.getText().toString();

                if (!mail.equals("") && !password.equals("")) {

                    ShowProgressDialog(LoginActivity.this, "Please wait....");


                    mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            progressDialog.dismiss();
                            loginBtn.setVisibility(View.VISIBLE);

                            if (!task.isSuccessful()) {

                                String errorText = Objects.requireNonNull(task.getException()).toString();

                                if (errorText.contains("FirebaseAuthInvalidUserException") || errorText.contains("FirebaseAuthInvalidCredentialsException")
                                        || errorText.contains("FirebaseAuthTooManyRequestsException")) {
                                    errorText = errorText;
                                } else if (errorText.contains("FirebaseApiNotAvailableException")) {
                                    errorText = "Please check your internet connection...";
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

                                ShowErrorAlertDialog(LoginActivity.this, errorText);

                                progressDialog.dismiss();

                            }
                        }
                    });
                } else {
                    loginBtn.setVisibility(View.VISIBLE);
                    ShowSnacckbar(loginLayout, "Please Enter Email and Password....");
                }


            }
        });


        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                CustomIntent.customType(LoginActivity.this,"right-to-left");
                finish();
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAutListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(firebaseAutListener);
    }






    public static void ShowSnacckbar(View layout, String string) {
        Snackbar.make(layout, string, Snackbar.LENGTH_LONG).show();
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
