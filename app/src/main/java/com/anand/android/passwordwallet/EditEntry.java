package com.anand.android.passwordwallet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditEntry extends AppCompatActivity {

    private static final String TAG = "EditEntry";
    EditText mName, mUser, mPassword, mNote;
    int id;
    EntryClass entry;
    CryptoHelper cryptoHelper;
    private Context context = this;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);


        cryptoHelper = new CryptoHelper();
        id = getIntent().getIntExtra("id", 0);
        Log.i(TAG, "onCreate: ID" + id);
        mName = findViewById(R.id.name);
        mUser = findViewById(R.id.userid);
        mPassword = findViewById(R.id.password);
        mNote = findViewById(R.id.note);
        if (id == 0) {
            finish();
        } else {
            EntryHelper entryHelper = new EntryHelper(this);
            entry = entryHelper.getRow(id);
            mName.setText(entry.getName());
            mUser.setText(entry.getUser());
            try {
                mPassword.setText(cryptoHelper.decrypt(entry.getPass()));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error in decrypting", Toast.LENGTH_SHORT).show();
            }
            mNote.setText(entry.getNote());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.entry_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final EntryHelper entryHelper = new EntryHelper(this);
        switch (item.getItemId()) {
            case R.id.edit_entry:
                cryptoHelper = new CryptoHelper();
                try {
                    if (entryHelper.updateRow(id, mName.getText().toString().trim(), mUser.getText().toString().trim(),
                            cryptoHelper.encrypt(mPassword.getText().toString().trim()), mNote.getText().toString().trim())) {
                        final ProgressDialog dialog = new ProgressDialog(this);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setTitle("Saving");
                        dialog.setMessage("Updating your entry...");
                        dialog.setIndeterminate(true);
                        dialog.setIcon(android.R.drawable.ic_menu_upload);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete:
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditEntry.this);
                dialog.setMessage("This entry will be deleted....\nDo yo want to delete it? ");
                dialog.setTitle("Delete Confirmation !");
                dialog.setIcon(R.drawable.ic_delete);
                dialog.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                final ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setTitle("Deleting...");
                                progressDialog.setMessage("Deleting your entry");
                                progressDialog.setIndeterminate(true);
                                progressDialog.setIcon(android.R.drawable.ic_menu_delete);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        entryHelper.deleteRow(entry);
                                        progressDialog.dismiss();
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
        return super.onOptionsItemSelected(item);
    }
}
