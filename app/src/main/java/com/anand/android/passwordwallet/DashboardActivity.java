package com.anand.android.passwordwallet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;

import java.util.Timer;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "Dashboard";
    private DrawerLayout drawerLayout;
    DriveServiceHelper driveServiceHelper = new DriveServiceHelper(this);
    private String userEmail;
    public static final String MY_PREFERENCES = "MyPrefs";
    SharedPreferences sharedPreferences;
    String databaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        NavigationView navigationView = findViewById(R.id.nav_view);
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(DashboardActivity.this);
        assert acct != null;
        final String userName = acct.getDisplayName();
        userEmail = acct.getEmail();
        Uri displayPicture = acct.getPhotoUrl();

        View navView = navigationView.getHeaderView(0);
        TextView name = navView.findViewById(R.id.username);
        TextView email = navView.findViewById(R.id.email);
        ImageView dp = navView.findViewById(R.id.user_dp);
        if (displayPicture != null)
            Glide.with(this).load(displayPicture).into(dp);
        name.setText(userName);
        email.setText(userEmail);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new EntriesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new EntriesFragment()).commit();
                    break;
                case R.id.nav_changepass:
                    Fragment fragment = new ChangePassFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("email", userEmail);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
                    break;
                case R.id.nav_upload:
                    fileUpload();
                    break;
                case R.id.nav_download:
                    fileDownload();
                    break;
                case R.id.nav_logout:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
                    dialog.setMessage("You will be redirected to Login Page.... ");
                    dialog.setTitle("Caution !");
                    dialog.setIcon(R.drawable.ic_logout);
                    dialog.setPositiveButton("EXIT",
                            (dialog1, which) -> {
                                final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setTitle("Logging Out...");
                                progressDialog.setIndeterminate(true);
                                progressDialog.setIcon(R.drawable.ic_logout);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Intent intent1 = new Intent(DashboardActivity.this, UserLoginActivity.class);
                                        startActivity(intent1);
                                        finish();
                                    }
                                }, 500);
                            });
                    dialog.setNegativeButton("Cancel",
                            (dialogInterface, i) -> {
                            });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    private void fileDownload() {
        ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        progressDialog.setTitle("Downloading from Google Drive");
        progressDialog.setIcon(R.drawable.ic_download);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        driveServiceHelper.downloadFile(databaseId).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new EntriesFragment()).commit();
            AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
            dialog.setMessage("File download successful...");
            dialog.setPositiveButton("OK",
                    (dialog1, which) -> {
                    });
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            e.printStackTrace();
            AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
            dialog.setMessage("File not found...");
            dialog.setPositiveButton("OK",
                    (dialog1, which) -> {

                    });
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        });

    }

    public void fileUpload() {

        deletePrevious();

        ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
        progressDialog.setTitle("Uploading to Google Drive");
        progressDialog.setIcon(R.drawable.ic_upload);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        driveServiceHelper.createFileSql().addOnSuccessListener(s -> {
            progressDialog.dismiss();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("id", driveServiceHelper.getId()).apply();
            databaseId = driveServiceHelper.getId();
            AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
            dialog.setMessage("File upload successful...");
            dialog.setPositiveButton("OK",
                    (dialog1, which) -> {

                    });
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }).addOnFailureListener((Exception e) -> {
            progressDialog.dismiss();
            AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
            dialog.setMessage("File upload failure...");
            dialog.setPositiveButton("OK",
                    (dialog1, which) -> {

                    });
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        });
        onResume();
    }

    private void deletePrevious() {
        driveServiceHelper.deletePreviousFile(databaseId)
                .addOnFailureListener(e -> Log.i(TAG, "deletePrevious: deleted database id " + databaseId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
            dialog.setMessage("You will be redirected to Login Page.... ");
            dialog.setTitle("Caution !");
            dialog.setIcon(R.drawable.ic_logout);
            dialog.setPositiveButton("EXIT",
                    (dialog1, which) -> {
                        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setTitle("Logging Out...");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setIcon(R.drawable.ic_logout);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Intent intent = new Intent(DashboardActivity.this, UserLoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 500);
                    });
            dialog.setNegativeButton("Cancel",
                    (dialogInterface, i) -> {
                    });
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    public void deleteDatabase(MenuItem item) {
        EntryHelper entryHelper = new EntryHelper(DashboardActivity.this);
        AlertDialog.Builder dialog1 = new AlertDialog.Builder(DashboardActivity.this);
        dialog1.setMessage("All the entries will be deleted... ");
        dialog1.setTitle("Caution !");
        dialog1.setIcon(R.drawable.ic_delete);
        dialog1.setPositiveButton("Delete",
                (dialog2, which) -> {
                    final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setTitle("Deleting Database");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setIcon(R.drawable.ic_delete);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            entryHelper.deleteDatabase();
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new EntriesFragment()).commit();
                        }
                    }, 500);
                });
        dialog1.setNegativeButton("Cancel",
                (dialogInterface, i) -> {
                });
        AlertDialog alertdialog = dialog1.create();
        alertdialog.setCancelable(false);
        alertdialog.show();
        entryHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseId = sharedPreferences.getString("id", "fakeId");
        Log.i(TAG, "onCreate: databaseId " + databaseId);
    }

    public void deleteDriveData(MenuItem item) {
        AlertDialog.Builder dialog1 = new AlertDialog.Builder(DashboardActivity.this);
        dialog1.setMessage("Drive Database will be deleted.\nDo you want to continue? ");
        dialog1.setTitle("Caution !");
        dialog1.setIcon(R.drawable.ic_delete);
        dialog1.setPositiveButton("Delete",
                (dialog2, which) -> {
                    final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setTitle("Deleting Database");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setIcon(R.drawable.ic_delete);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            driveServiceHelper.deletePreviousFile(databaseId)
                                    .addOnSuccessListener(aVoid -> {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
                                        dialog.setIcon(android.R.drawable.stat_notify_error);
                                        dialog.setMessage("Drive Database deleted...");
                                        dialog.setPositiveButton("OK",
                                                (dialog11, which1) -> {
                                                });
                                        AlertDialog alertDialog = dialog.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                    })
                                    .addOnFailureListener(e -> {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
                                        dialog.setIcon(android.R.drawable.stat_notify_error);
                                        dialog.setMessage("No file found...");
                                        dialog.setPositiveButton("OK",
                                                (dialog1, which) -> {
                                                });
                                        AlertDialog alertDialog = dialog.create();
                                        alertDialog.setCancelable(false);
                                        alertDialog.show();
                                    });
                        }
                    }, 500);
                });
        dialog1.setNegativeButton("Cancel",
                (dialogInterface, i) -> {
                });
        AlertDialog alertdialog = dialog1.create();
        alertdialog.setCancelable(false);
        alertdialog.show();
    }
}
