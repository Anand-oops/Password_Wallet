package com.anand.android.passwordwallet;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditEntry extends AppCompatActivity {

    private static final String TAG = "EditEntry";
    EditText mName, mUser, mPassword, mNote;
    int id;
    EntryHelper entryHelper = new EntryHelper(this);
    EntryClass entry;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);


        id = getIntent().getIntExtra("id", 0);
        Log.i(TAG, "onCreate: ID" + id);
        mName = findViewById(R.id.name);
        mUser = findViewById(R.id.userid);
        mPassword = findViewById(R.id.password);
        mNote = findViewById(R.id.note);
        if (id == 0) {
            finish();
        } else {
            entry = entryHelper.getRow(id);
            mName.setText(entry.getName());
            mUser.setText(entry.getUser());
            mPassword.setText(entry.getPass());
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
        switch (item.getItemId()) {
            case R.id.edit_entry:
                if (entryHelper.updateRow(id, mName.getText().toString(), mUser.getText().toString(),
                        mPassword.getText().toString(), mNote.getText().toString())) {
                    finish();
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
                                entryHelper.deleteRow(entry);
                                finish();
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
