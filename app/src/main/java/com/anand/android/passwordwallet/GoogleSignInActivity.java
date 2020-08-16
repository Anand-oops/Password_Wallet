package com.anand.android.passwordwallet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleSignInActivity extends AppCompatActivity  {

    SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int SIGN_IN=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        signInButton=findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent,SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount account= completedTask.getResult(ApiException.class);
            startActivity(new Intent(GoogleSignInActivity.this, UserLoginActivity.class));
        } catch (ApiException e) {
            Toast.makeText(GoogleSignInActivity.this,"Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            startActivity(new Intent(GoogleSignInActivity.this,UserLoginActivity.class));
            finish();
        }
        super.onStart();
    }
}