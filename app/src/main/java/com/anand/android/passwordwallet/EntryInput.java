package com.anand.android.passwordwallet;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class EntryInput extends AppCompatActivity {
    EntryHelper entryHelper = new EntryHelper(this);
    CryptoHelper cryptoHelper = new CryptoHelper();
    LinearLayout rootLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        rootLayout = findViewById(R.id.root_view);
        if (savedInstanceState == null) {
            rootLayout.setVisibility(View.INVISIBLE);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }
    }

    private void circularRevealActivity() {

        int cx = rootLayout.getRight() - getDips();
        int cy = rootLayout.getBottom() - getDips();

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(800);

        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    private int getDips() {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 68, resources.getDisplayMetrics());
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
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        finish();
                    }
                }, 500);
            }
        }
    }

    @Override
    public void onBackPressed() {
        int cx = rootLayout.getRight() - getDips();
        int cy = rootLayout.getBottom() - getDips();
        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, finalRadius, 0);

        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                rootLayout.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        circularReveal.setDuration(800);
        circularReveal.start();
    }
}
