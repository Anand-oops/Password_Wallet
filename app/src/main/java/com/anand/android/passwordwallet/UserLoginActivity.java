package com.anand.android.passwordwallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class UserLoginActivity extends AppCompatActivity {

    Toast backToast;
    DbHelper db = new DbHelper(this);
    GoogleSignInClient mGoogleSignInClient;
    private long backPressedTime;
    String userEmail;
    EditText pEntry;
    Uri displayPicture;
    String userName;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        Button changeUser = findViewById(R.id.change_user);
        checkBox = findViewById(R.id.checkBox);
        TextView nameTV = findViewById(R.id.name);
        pEntry = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        TextView emailTV = findViewById(R.id.email);
        ImageView photoIV = findViewById(R.id.photo);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(UserLoginActivity.this);
        if (acct != null) {
            userName = acct.getDisplayName();
            userEmail = Objects.requireNonNull(acct.getEmail()).trim();
            displayPicture = acct.getPhotoUrl();

            nameTV.setText("Name: " + userName);
            emailTV.setText("Email: " + userEmail);
            Glide.with(this).load(displayPicture).into(photoIV);
            if (db.checkEmail(userEmail)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UserLoginActivity.this);
                dialog.setMessage("No Password-Wallet  found associated with this email-id ");
                dialog.setTitle("Login Failed !!");
                dialog.setIcon(android.R.drawable.ic_dialog_info);
                dialog.setPositiveButton("Register",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        });
                AlertDialog alertDialog = dialog.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

            }
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    pEntry.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    pEntry.setTransformationMethod(new HideReturnsTransformationMethod());
                }
                pEntry.setSelection(pEntry.getText().length());
            }
        });

        changeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Change();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String password = pEntry.getText().toString().trim();
                if (password.length() == 0)
                    Toast.makeText(getApplicationContext(), "Field is empty", Toast.LENGTH_SHORT).show();
                else {
                    if (db.checkEmail(userEmail)) {
                        boolean insert = db.insert(userEmail, password);
                        if (insert) {
                            Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                            MasterLogin();
                        }
                    } else {
                        if (db.checked(userEmail, password)) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            MasterLogin();
                        } else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(UserLoginActivity.this);
                            dialog.setMessage("Wrong Password");
                            dialog.setTitle("Error");
                            dialog.setIcon(android.R.drawable.ic_dialog_alert);
                            dialog.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            pEntry.setText("");
                                        }
                                    });
                            AlertDialog alertDialog = dialog.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        }
                    }
                }
            }
        });
    }

    public void MasterLogin() {
        Intent intent = new Intent(UserLoginActivity.this, DashboardActivity.class);
        intent.putExtra("user",userName);
        intent.putExtra("email",userEmail);
        intent.putExtra("dp",displayPicture.toString());
        startActivity(intent);
        finish();
    }

    private void Change() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserLoginActivity.this, "Select your Account", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserLoginActivity.this, GoogleSignInActivity.class));
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            System.exit(0);
            super.onBackPressed();
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press again to Exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
