package com.anand.android.passwordwallet;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EntryInput extends AppCompatActivity {
    EntryHelper entryHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        final EditText name=(EditText) findViewById(R.id.name);
        final EditText userId=(EditText) findViewById(R.id.userid);
        final EditText password=(EditText) findViewById(R.id.password);

        Button saveEntry =findViewById(R.id.save_button);
        saveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entryHelper.insert(name.getText().toString().trim(),userId.getText().toString().trim(),password.getText().toString().trim());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry_menu, menu);
        return true;
    }
}
