package com.anand.android.passwordwallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class DashboardActivity extends AppCompatActivity  {

    private static final String TAG ="DashboardActivity" ;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Intent intent= getIntent();
        String userName=intent.getStringExtra("user");
        String userEmail=intent.getStringExtra("email");
        String dpLink=intent.getStringExtra("dp");
        Uri displayPicture=Uri.parse(dpLink);

        View navView= navigationView.getHeaderView(0);
        TextView name=navView.findViewById(R.id.username);
        TextView email=navView.findViewById(R.id.email);
        ImageView dp=navView.findViewById(R.id.user_dp);
        Glide.with(this).load(displayPicture).into(dp);
        name.setText(userName);
        email.setText(userEmail);

        Toolbar toolbar=findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
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
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new EntriesFragment()).commit();
                        break;
                    case R.id.nav_changepass:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,new ChangePassFragment()).commit();
                        break;
                    case R.id.nav_logout:
                        Toast.makeText(DashboardActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                        //finishActivity(0);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}
