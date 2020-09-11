package com.anand.android.passwordwallet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

public class GoogleSignInActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 0;
    SignInButton signInButton;
    RelativeLayout choiceLayout;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int SIGN_IN = 1;
    public static final String MY_PREFERENCES = "MyPrefs";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(view -> {
            Intent intent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(intent, SIGN_IN);
        });
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(GoogleSignInActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            signInButton.setVisibility(View.GONE);
            choiceLayout.setVisibility(View.VISIBLE);
            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
                switch (radioGroup1.findViewById(i).getId()) {
                    case R.id.create_new_radio:
                        startActivity(new Intent(GoogleSignInActivity.this, UserLoginActivity.class));
                        finish();
                        break;
                    case R.id.restore_radio:
                        restoreFromDrive();
                        break;
                }
            });

        } catch (ApiException e) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(GoogleSignInActivity.this);
            dialog.setMessage("Account Required");
            dialog.setTitle("Select an account to proceed...");
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.setPositiveButton("OK",
                    (dialog12, which) -> {
                    });
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    private void restoreFromDrive() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Restoring from Drive");
        progressDialog.setIcon(R.drawable.ic_sync);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        DriveServiceHelper driveServiceHelper = new DriveServiceHelper(this);
        driveServiceHelper.restoreFromDrive()
                .addOnSuccessListener(aVoid -> {
                    String driveRestoreId = driveServiceHelper.getDriveRestoreId();
                    driveServiceHelper.downloadFile(driveRestoreId).addOnSuccessListener((Void bVoid) -> {
                        progressDialog.dismiss();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(GoogleSignInActivity.this);
                        dialog.setMessage("File restore successful...");
                        dialog.setPositiveButton("OK",
                                (dialog1, which) -> {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("id", driveRestoreId).apply();
                                    startActivity(new Intent(GoogleSignInActivity.this, UserLoginActivity.class));
                                });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(GoogleSignInActivity.this);
                        dialog.setMessage("Restore point not found...");
                        dialog.setPositiveButton("OK",
                                (dialog1, which) -> {
                                });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    });
                });
    }


    @Override
    protected void onStart() {
        requestPermission();
        choiceLayout = findViewById(R.id.layout_choice);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            startActivity(new Intent(GoogleSignInActivity.this, UserLoginActivity.class));
            finish();
        }
        super.onStart();
    }
}