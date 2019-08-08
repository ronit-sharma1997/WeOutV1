package com.app.WeOut;

import android.widget.TextView;
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

import androidx.core.content.res.ResourcesCompat;
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

import io.opencensus.internal.StringUtils;
import utils.CustomSnackBar;
import utils.User;

/**
 * Class to handle the Register Screen of the App. Here the user can register with given first name,
 * last name, email address, username(less than or equal to 15 characters), and appropriate
 * password
 */
public class RegisterActivity extends AppCompatActivity {

  // Variables in XML
  private EditText inputUsername, inputPassword, inputRetypePassword, inputFirstName,
      inputLastName;
  private TextView signupHeader;
  private Button btnSignUp;
  private ProgressBar progressBar;

  // Snackbar for displaying feedback
  CustomSnackBar snackBar;

  // Firebase variable
  private FirebaseAuth auth;

  private String friendCollectionPath;

  private String TAG = "RegisterActivity_TAG";

  // TODO: Decide if we will remove these bluebutton
  // private Button btnSignIn, btnResetPassword;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    System.out.println("Starting register activity");

    // Associate all views by id
    btnSignUp = findViewById(R.id.sign_up_button);
    inputUsername = findViewById(R.id.username_input);
    inputPassword = findViewById(R.id.password);
    inputRetypePassword = findViewById(R.id.retype_password);
    inputFirstName = findViewById(R.id.firstName);
    inputLastName = findViewById(R.id.lastName);
    progressBar = findViewById(R.id.progressBar);
    this.signupHeader = findViewById(R.id.signUpHeader);
    this.signupHeader.setTypeface(ResourcesCompat.getFont(this, R.font.lobster));

    // Initialize necessary variables
    snackBar = new CustomSnackBar();

    // Get Firebase auth instance
    auth = FirebaseAuth.getInstance();

    btnSignUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View v) {

        final String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String retype_password = inputRetypePassword.getText().toString().trim();
        final String firstName = inputFirstName.getText().toString().trim();
        final String lastName = inputLastName.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
          snackBar
              .display(v, getApplicationContext(), "Please enter First Name!", R.color.lightBlue);
          return;
        } else if (TextUtils.isEmpty(lastName)) {
          snackBar
              .display(v, getApplicationContext(), "Please enter Last Name!", R.color.lightBlue);
          return;
        } else if (TextUtils.isEmpty(username)) {
          snackBar.display(v, getApplicationContext(), "Please enter username!", R.color.lightBlue);
          return;
        } else if (username.contains(" ")) {
          snackBar.display(v, getApplicationContext(), "Username cannot contain spaces!", R.color.lightBlue);
          return;
        } else if (TextUtils.isEmpty(password)) {
          snackBar.display(v, getApplicationContext(), "Please enter password!", R.color.lightBlue);
          return;
        } else if (firstName.length() > 25) {
          snackBar.display(v, getApplicationContext(),
              "First Name is too long, maximum 25 characters!", R.color.lightBlue);
          return;
        } else if (lastName.length() > 25) {
          snackBar.display(v, getApplicationContext(),
              "Last Name is too long, maximum 25 characters!", R.color.lightBlue);
          return;
        } else if (username.length() > 15) {
          snackBar.display(v, getApplicationContext(),
              "Username is too long, maximum 15 characters!", R.color.lightBlue);
          return;
        } else if (password.length() < 6) {
          snackBar.display(v, getApplicationContext(),
              "Password too short, enter minimum 6 characters!", R.color.lightBlue);
          return;
        } else if (!password.equals(retype_password)) {
          snackBar
              .display(v, getApplicationContext(), "Passwords do not match!", R.color.lightBlue);
          return;
        }

        // Show progress at bottom of screen
        progressBar.setVisibility(View.VISIBLE);

        //create user
        auth.createUserWithEmailAndPassword(username + "@weout.com", password)
            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                  snackBar.display(v, getApplicationContext(),
                      "Error: Account creation failed:" + task.getException(), R.color.lightBlue);
                }

                // If task is successful,
                else {
                  // Get database variable instance from firebase
                  FirebaseFirestore db = FirebaseFirestore.getInstance();

                  // Create a batch to upload a batch of data at once
                  WriteBatch batch = db.batch();

                  // Create map for initial friend to store for the user
                  Map<String, Object> demoFriendData = new HashMap<>();
                  // Store one username in the map
                  demoFriendData.put("sf0", "Sai Bill");

                  // Commented out because the user does not need friends to start.
                  // Get the new user's current friend document
//                                    DocumentReference currentFriends = db.collection("users").document(username).collection("friends").document("current");
                  // Write the new friend data to the current friends document
//                                    batch.set(currentFriends, demoFriendData);

                  // Get the new user's friend requests document
                  DocumentReference friendRequests = db.collection("users").document(username)
                      .collection("friends").document("received");
                  // Write the demo friend to users friend requests doc
                  batch.set(friendRequests, demoFriendData);

                  // Create an Events accepted and invited document
                  HashMap<String, Object> fakeEventMap = new HashMap<>();
                  fakeEventMap.put("FakeEvent", true);

                  // Get the new user's event requests document
                  DocumentReference eventInvitedRef = db.collection("users").document(username)
                      .collection("events").document("accepted");
                  // Write the demo friend to users friend requests doc
                  batch.set(eventInvitedRef, fakeEventMap);

                  // Get the new user's event accepted document
                  DocumentReference eventAcceptedRef = db.collection("users").document(username)
                      .collection("events").document("invited");
                  // Write the demo friend to users friend requests doc
                  batch.set(eventAcceptedRef, fakeEventMap);

                  // Create a document with the user's username
                  DocumentReference currentUser = db.collection("users").document(username);

                  // Create a new User object and write that data
                  batch.set(currentUser,
                      new User(
                          firstName.substring(0, 1).toUpperCase() + firstName.substring(1),
                          lastName.substring(0, 1).toUpperCase() + lastName.substring(1),
                          username,
                          "fake@gmail.com",
                          String.valueOf(new Timestamp(new Date()).getSeconds())
                      )
                  );

                  // Commit all of the data in the batch
                  batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      Log.d(TAG, "Database successfully written to with user info.");
                      startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                      snackBar.display(v, getApplicationContext(), "Account created successfully.",
                          R.color.lightBlue);
                      progressBar.setVisibility(View.INVISIBLE);
                      finish();

                    }
                  }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Log.d(TAG, "Database unsuccessfully written to with user info.");
                      snackBar.display(v, getApplicationContext(),
                          "Error: Failure writing to database.", R.color.lightBlue);
                      progressBar.setVisibility(View.INVISIBLE);
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
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
        "[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
        "A-Z]{2,7}$";

    Pattern pat = Pattern.compile(emailRegex);
    if (emailAddress == null) {
      return false;
    }
    return pat.matcher(emailAddress).matches();
  }

  @Override
  protected void onResume() {
    super.onResume();
    progressBar.setVisibility(View.INVISIBLE);
  }
}
