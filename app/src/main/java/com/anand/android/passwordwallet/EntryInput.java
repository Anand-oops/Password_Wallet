package com.anand.android.passwordwallet;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
        EditText name = findViewById(R.id.name);
        EditText userId = findViewById(R.id.userid);
        EditText password = findViewById(R.id.password);
        EditText note = findViewById(R.id.note);
        Log.i(TAG, "saveEntry: name" + name.getText().toString());

        if (name.getText().toString().trim().isEmpty() || userId.getText().toString().trim().isEmpty()
                || password.getText().toString().trim().isEmpty()) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(EntryInput.this);
            dialog.setMessage("Invalid Entry.... ");
            dialog.setTitle("Error !");
            dialog.setIcon(R.drawable.ic_block);
            dialog.setPositiveButton("Enter Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                        }
                    });
            dialog.setNegativeButton("Cancel Feed",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            if (entryHelper.insert(name.getText().toString().trim(), userId.getText().toString().trim(),
                    password.getText().toString().trim(), note.getText().toString().trim())) {
                Snackbar.make(getWindow().getDecorView(), "Data Added...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }
}
