package com.app.WeOut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputUsername, inputPassword, inputRetypePassword, inputFirstName, inputLastName, emailAddress;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private String TAG = "RegisterActivity_TAG";

    // TODO: Decide if we will remove these buttons
    // private Button btnSignIn, btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

//        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);

        inputUsername = (EditText) findViewById(R.id.username_input);
        inputPassword = (EditText) findViewById(R.id.password);
        inputRetypePassword = (EditText) findViewById(R.id.retype_password);

        inputFirstName = findViewById(R.id.firstName);
        inputLastName = findViewById(R.id.lastName);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emailAddress = findViewById(R.id.emailInput);
//        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

//        btnResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
//            }
//        });

//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//                finish();
//            }
//        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = inputUsername.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String retype_password = inputRetypePassword.getText().toString().trim();
                final String firstName = inputFirstName.getText().toString().trim();
                final String lastName = inputLastName.getText().toString().trim();

                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(getApplicationContext(), "Enter First Name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(getApplicationContext(), "Enter Last Name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (username.length() > 15) {
                    Toast.makeText(getApplicationContext(), "Username is too long, maximum 15 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!password.equals(retype_password)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show progress at bottom of screen
                progressBar.setVisibility(View.VISIBLE);

                //create user
                auth.createUserWithEmailAndPassword(username+"@weout.com", password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // TODO: Remove before final product
                                    Toast.makeText(RegisterActivity.this, "Error: Account creation failed:" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }

                                // If task is successful,
                                else {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String, Object> userInfoHashMap = new HashMap<>();
                                    userInfoHashMap.put("firstName", firstName);
                                    userInfoHashMap.put("lastName", lastName);
                                    userInfoHashMap.put("joinedDate", new Timestamp(new Date()));
                                    userInfoHashMap.put("emailAddress", emailAddress.getText().toString());
                                    db.collection("users").document(username).set(userInfoHashMap)

                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Database successfully written to with user info.");
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            Toast.makeText(RegisterActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Database unsuccessfully written to with user info.");
                                            Toast.makeText(RegisterActivity.this, "Error: Failure writing to database.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
