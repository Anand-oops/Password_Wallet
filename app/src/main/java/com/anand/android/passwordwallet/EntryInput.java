package com.anand.android.passwordwallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EntryInput extends AppCompatActivity {
    private static final String TAG = "EntryInput";
    EntryHelper entryHelper = new EntryHelper(this);
    CryptoHelper cryptoHelper = new CryptoHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.entry_menu, menu);
        return true;
    }

    public void saveEntry(MenuItem item) throws Exception {
        EditText name = findViewById(R.id.name);
        EditText userId = findViewById(R.id.userid);
        EditText password = findViewById(R.id.password);
        EditText note = findViewById(R.id.note);

        if (name.getText().toString().trim().isEmpty() || userId.getText().toString().trim().isEmpty()
                || password.getText().toString().trim().isEmpty()) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(EntryInput.this);
            dialog.setMessage("Invalid Entry.... ");
            dialog.setTitle("Error !");
            dialog.setIcon(R.drawable.ic_block);
            dialog.setPositiveButton("Enter Again",
                    (dialog1, which) -> {
                    });
            dialog.setNegativeButton("Cancel Feed",
                    (dialogInterface, i) -> finish());
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            View view = this.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert view != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            if (entryHelper.insert(name.getText().toString().trim(), userId.getText().toString().trim(),
                    cryptoHelper.encrypt(password.getText().toString().trim()), note.getText().toString().trim())) {

                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle("Encrypting...");
                dialog.setMessage("Saving your data");
                dialog.setIndeterminate(true);
                dialog.setIcon(android.R.drawable.ic_menu_save);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        finish();
                    }
                }, 1000);
            }
        }
    }
}
