package com.anand.android.passwordwallet;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity  {

    private DrawerLayout drawerLayout;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Intent intent = getIntent();
        final String userName = intent.getStringExtra("user");
        userEmail = intent.getStringExtra("email");
        String dpLink = intent.getStringExtra("dp");
        Uri displayPicture = Uri.parse(dpLink);

        View navView = navigationView.getHeaderView(0);
        TextView name = navView.findViewById(R.id.username);
        TextView email = navView.findViewById(R.id.email);
        ImageView dp = navView.findViewById(R.id.user_dp);
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
        if (savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new EntriesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                    case R.id.nav_sync:
                        Toast.makeText(getApplicationContext(), "Sync with Cloud", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_logout:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
                        dialog.setMessage("You will be redirected to Login Page.... ");
                        dialog.setTitle("Caution !");
                        dialog.setIcon(R.drawable.ic_logout);
                        dialog.setPositiveButton("EXIT",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
                                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        progressDialog.setTitle("");
                                        progressDialog.setMessage("Logging Out...");
                                        progressDialog.setIndeterminate(true);
                                        progressDialog.setIcon(android.R.drawable.ic_menu_upload);
                                        progressDialog.setCanceledOnTouchOutside(false);
                                        progressDialog.show();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(DashboardActivity.this, UserLoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }, 1000);
                                    }
                                });
                        dialog.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
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
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            final ProgressDialog progressDialog = new ProgressDialog(DashboardActivity.this);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setTitle("");
                            progressDialog.setMessage("Logging Out...");
                            progressDialog.setIndeterminate(true);
                            progressDialog.setIcon(android.R.drawable.ic_menu_upload);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(DashboardActivity.this, UserLoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1000);
                        }
                    });
            dialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }
}
