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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import utils.User;

/**
 * Class to handle the Register Screen of the App. Here the user can register with given first name,
 * last name, email address, username(less than or equal to 15 characters), and appropriate password
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText inputUsername, inputPassword, inputRetypePassword, inputFirstName,
            inputLastName, inputEmailAddress;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private String friendCollectionPath;

    private String TAG = "RegisterActivity_TAG";

    // TODO: Decide if we will remove these buttons
    // private Button btnSignIn, btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignUp = findViewById(R.id.sign_up_button);

        inputUsername = findViewById(R.id.username_input);
        inputPassword = findViewById(R.id.password);
        inputRetypePassword = findViewById(R.id.retype_password);
        inputFirstName = findViewById(R.id.firstName);
        inputLastName = findViewById(R.id.lastName);
        inputEmailAddress = findViewById(R.id.emailInput);
        progressBar = findViewById(R.id.progressBar);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = inputUsername.getText().toString().trim();
                final String emailAddress = inputEmailAddress.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String retype_password = inputRetypePassword.getText().toString().trim();
                final String firstName = inputFirstName.getText().toString().trim();
                final String lastName = inputLastName.getText().toString().trim();

                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(getApplicationContext(), "Please enter First Name!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(getApplicationContext(), "Please enter Last Name!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Please enter username!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter password!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(emailAddress) || !ValidEmailAddress(emailAddress)) {
                    Toast.makeText(getApplicationContext(), "Please enter valid email address!",
                            Toast.LENGTH_SHORT).show();
                }
                else if (username.length() > 15) {
                    Toast.makeText(getApplicationContext(), "Username is too long, " +
                            "maximum 15 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter " +
                            "minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!password.equals(retype_password)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match!",
                            Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(RegisterActivity.this,
                                            "Error: Account creation failed:" +
                                                    task.getException(), Toast.LENGTH_SHORT).show();
                                }

                                // If task is successful,
                                else {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    WriteBatch createFriendsCollection = db.batch();
//                                    Map<String, Object> userInfoHashMap = new HashMap<>();
//                                    userInfoHashMap.put("firstName", firstName);
//                                    userInfoHashMap.put("lastName", lastName);
//                                    userInfoHashMap.put("joinedDate", new Timestamp(new Date()));
//                                    userInfoHashMap.put("emailAddress", emailAddress);
                                    Map<String, Object> demoFriend = new HashMap<>();
                                    demoFriend.put("demoFriend", true);
                                    DocumentReference currentFriends = db.collection("users").document(username).collection("friends").document("current");
                                    createFriendsCollection.set(currentFriends, demoFriend);
                                    DocumentReference friendRequests = db.collection("users").document(username).collection("friends").document("received");
                                    createFriendsCollection.set(friendRequests, demoFriend);
                                    DocumentReference currentUser =  db.collection("users").document(username);
                                    createFriendsCollection.set(currentUser, new User(firstName, lastName, username, emailAddress, new Timestamp(new Date()).toString()));
                                    createFriendsCollection.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "Database successfully written to with user info.");
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            Toast.makeText(RegisterActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
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

    //checks if a potential email address is valid using regex
    private boolean ValidEmailAddress(String emailAddress) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (emailAddress == null)
            return false;
        return pat.matcher(emailAddress).matches();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
