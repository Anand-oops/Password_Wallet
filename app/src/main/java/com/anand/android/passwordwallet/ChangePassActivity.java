package com.anand.android.passwordwallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePassActivity extends AppCompatActivity {
    private static final String TAG = "ChangePass";
    DbHelper dbHelper = new DbHelper(this);
    private String userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);

        userEmail = getIntent().getStringExtra("email");
        Log.i(TAG, "onCreate: email" + userEmail);

        Button changePass = findViewById(R.id.proceedToPassChange);
        final EditText CurrentPass = findViewById(R.id.currentPassword);
        final EditText NewPass = findViewById(R.id.newPassword);
        final EditText ConfirmPass = findViewById(R.id.confirmPassword);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dbHelper.checked(userEmail, CurrentPass.getText().toString().trim())) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePassActivity.this);
                    dialog.setMessage("Wrong Password...");
                    dialog.setTitle("Error ");
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    CurrentPass.setText("");
                                    NewPass.setText("");
                                    ConfirmPass.setText("");
                                }
                            });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                } else if (!NewPass.getText().toString().trim().equals(ConfirmPass.getText().toString().trim())) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePassActivity.this);
                    dialog.setMessage("Passwords do not match...");
                    dialog.setTitle("Confirmation Error");
                    dialog.setIcon(R.drawable.ic_block);
                    dialog.setPositiveButton("RETRY",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    CurrentPass.setText("");
                                    NewPass.setText("");
                                    ConfirmPass.setText("");
                                }
                            });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                } else {
                    if (NewPass.getText().toString().trim().isEmpty() || NewPass.getText().toString().trim().length() < 6) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePassActivity.this);
                        dialog.setMessage("6 minimum characters required in password...");
                        dialog.setTitle("Credentials Error");
                        dialog.setIcon(R.drawable.ic_block);
                        dialog.setPositiveButton("RETRY",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        CurrentPass.setText("");
                                        NewPass.setText("");
                                        ConfirmPass.setText("");
                                    }
                                });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    } else {
                        boolean update = dbHelper.update(userEmail, NewPass.getText().toString().trim());
                        if (update) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePassActivity.this);
                            dialog.setMessage("Login with your latest password... ");
                            dialog.setTitle("Redirecting to Login Page");
                            dialog.setIcon(R.drawable.ic_logout);
                            dialog.setPositiveButton("EXIT",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                                            startActivity(intent);
                                            finish();
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
}
