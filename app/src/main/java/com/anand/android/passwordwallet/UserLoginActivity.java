package com.anand.android.passwordwallet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE)).requestEmail().build();

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
                dialog.setMessage("No Password-Wallet found associated with this email-id... ");
                loginButton.setText("REGISTER");
                dialog.setTitle("Wait!!");
                dialog.setIcon(android.R.drawable.ic_dialog_info);
                dialog.setPositiveButton("Register",
                        (dialog1, which) -> {
                        });
                AlertDialog alertDialog = dialog.create();
                alertDialog.setCancelable(false);
                alertDialog.show();

            }
        }

        checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!isChecked) {
                pEntry.setTransformationMethod(new PasswordTransformationMethod());
            } else {
                pEntry.setTransformationMethod(new HideReturnsTransformationMethod());
            }
            pEntry.setSelection(pEntry.getText().length());
        });

        changeUser.setOnClickListener(view -> Change());

        loginButton.setOnClickListener(view -> {
            final String password = pEntry.getText().toString().trim();
            if (password.isEmpty() || password.length() < 6) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UserLoginActivity.this);
                dialog.setMessage("6 minimum characters required in password...");
                dialog.setTitle("Credentials Error");
                dialog.setIcon(R.drawable.ic_block);
                dialog.setPositiveButton("RETRY",
                        (dialog13, which) -> pEntry.setText(""));
                AlertDialog alertDialog = dialog.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            } else {
                if (db.checkEmail(userEmail)) {
                    boolean insert = db.insert(userEmail, password);
                    if (insert) {
                        MasterLogin();
                    }
                } else {
                    if (db.checked(userEmail, password)) {
                        MasterLogin();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(UserLoginActivity.this);
                        dialog.setMessage("Wrong Password");
                        dialog.setTitle("Error");
                        dialog.setIcon(android.R.drawable.ic_dialog_alert);
                        dialog.setPositiveButton("OK",
                                (dialog12, which) -> pEntry.setText(""));
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }
                }
            }
        });
    }

    public void MasterLogin() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Entering Wallet !");
        dialog.setMessage("Logging you in...");
        dialog.setIndeterminate(true);
        dialog.setIcon(R.drawable.ic_login);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
                Intent intent = new Intent(UserLoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        }, 800);
    }

    private void Change() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    Toast.makeText(UserLoginActivity.this, "Select your Account", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserLoginActivity.this, GoogleSignInActivity.class));
                    finish();
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