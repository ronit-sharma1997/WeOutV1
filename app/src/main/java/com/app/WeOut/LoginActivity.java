package com.app.WeOut;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import utils.CustomSnackBar;

/**
 * Class to handle the Login Screen of the App. Here the user can login with a username and
 * password.
 */
public class LoginActivity extends AppCompatActivity {

    // XML Variables
    private TextView registerTextView, loginHeader;
    private EditText inputUsername, inputPassword;
    private ProgressBar progressBar;
    private Button btnLogin;

    private CustomSnackBar snackBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // Initialize snackbar
        snackBar = new CustomSnackBar();

        auth.signOut();

        //if we are already logged in, we can go straight to the Main Activity
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        //obtain Views from the screen and assign to variables
        inputUsername = findViewById(R.id.username_input);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        registerTextView = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        loginHeader = findViewById(R.id.loginHeader);
        loginHeader.setTypeface(ResourcesCompat.getFont(this, R.font.lobster));

        //if user needs to sign up, go to Register activity and end current activity
        this.registerTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
//                finish();
            }
        });


        //attempt to login with input from EditText Views
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                String username = inputUsername.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    snackBar.display(v, getApplicationContext(),"Enter username!", R.color.black);
                    return;
                }
                else if (username.contains(" ")) {
                    snackBar.display(v, getApplicationContext(),"Username cannot contain spaces!", R.color.black);
                    return;
                }
                else if (TextUtils.isEmpty(password)) {
                    snackBar.display(v, getApplicationContext(),"Enter password!", R.color.black);
                    return;
                }

                // Append @weout.com to username
                String email = username + "@weout.com";

                // Show progress of login authentication at bottom of screen
                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string
                                                .minimum_password));
                                    } else {
                                        snackBar.display(v, getApplicationContext(),getString(R.string.auth_failed), R.color.black);
                                    }
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                }
//                                progressBar.setVisibility(View.GONE);
                                finish();
                            }
                        });
            }
        });
    }
}
