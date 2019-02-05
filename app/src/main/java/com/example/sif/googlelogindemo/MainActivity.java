package com.example.sif.googlelogindemo;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnGoogleSignIn;
    private int SIGN_IN_REQUEST_CODE = 2;

    private TextView tSignInResultShow, tvEmail, tvName;
    private Button btSignOut;
    private ImageView ivUserProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGoogleSignIn = findViewById(R.id.bt_google_sign_in);
        tSignInResultShow = findViewById(R.id.tv_result_show);
        tvEmail = findViewById(R.id.tv_email);
        tvName = findViewById(R.id.tv_name);
        btSignOut = findViewById(R.id.bt_sign_out);
        ivUserProfilePic = findViewById(R.id.iv_user_profile_pic);


        // Configure sign-in to request the user's ID, email address, and basic  profile.
        // ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by googleSignInOptions.
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btnGoogleSignIn.setOnClickListener(this);
        btSignOut.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        /** If GoogleSignIn.getLastSignedInAccount returns a GoogleSignInAccount object (rather than null), the user has already signed in to your app with Google.
         * Update your UI accordingly—that is, hide the sign-in button, launch your main activity, or whatever is appropriate for your app.*/
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...); */
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_google_sign_in:
                signInFlowStarts();
                break;
            case R.id.bt_sign_out:
                signOutGoogleAccount();
                break;
        }
    }

    private void updateUI(GoogleSignInAccount signInAccount) {
        if (signInAccount != null) {

            getUserProfileInformation(signInAccount);
            btnGoogleSignIn.setVisibility(View.GONE);

            tSignInResultShow.setVisibility(View.VISIBLE);
            tSignInResultShow.setText("Signed In Successfully");
            btSignOut.setVisibility(View.VISIBLE);
            Toast.makeText(this, "User successfully signed In", Toast.LENGTH_SHORT).show();
        } else {
            tSignInResultShow.setVisibility(View.GONE);
            btnGoogleSignIn.setVisibility(View.VISIBLE);
            btSignOut.setVisibility(View.GONE);
        }
    }

    private void signInFlowStarts() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            e.printStackTrace();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GoogleCode", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }

    }

    private void signOutGoogleAccount() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "User Logged-out", Toast.LENGTH_SHORT).show();
                btnGoogleSignIn.setVisibility(View.VISIBLE);
                btSignOut.setVisibility(View.GONE);
                tSignInResultShow.setVisibility(View.GONE);
                tvEmail.setVisibility(View.GONE);
                tvName.setVisibility(View.GONE);
                ivUserProfilePic.setImageResource(0);
            }
        });
    }

    private void getUserProfileInformation(GoogleSignInAccount googleSignInAccount) {
        if (googleSignInAccount != null) {

            ivUserProfilePic.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);

            tvEmail.setText(googleSignInAccount.getEmail());
            tvName.setText(googleSignInAccount.getDisplayName());
            Uri photoUri = googleSignInAccount.getPhotoUrl();

            Glide.with(MainActivity.this).load(photoUri).into(ivUserProfilePic);
        } else {
            tvEmail.setText("");
            tvName.setText("");
            ivUserProfilePic.setImageResource(0);
            tvEmail.setVisibility(View.GONE);
            tvName.setVisibility(View.GONE);
        }
    }


}
