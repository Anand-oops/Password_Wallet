package com.anand.android.passwordwallet;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class EntryInput extends AppCompatActivity {
    private static final String TAG = "EntryInput";
    EntryHelper entryHelper = new EntryHelper(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry_menu, menu);
        return true;
    }

    public void saveEntry(MenuItem item) {
        final EditText name = findViewById(R.id.name);
        final EditText userId = findViewById(R.id.userid);
        final EditText password = findViewById(R.id.password);
        Log.i(TAG, "saveEntry: name" + name.getText().toString());
        if (entryHelper.insert(name.getText().toString().trim(), userId.getText().toString().trim(), password.getText().toString().trim())) {
            Snackbar.make(getWindow().getDecorView(), "Data Added...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
    }
}
