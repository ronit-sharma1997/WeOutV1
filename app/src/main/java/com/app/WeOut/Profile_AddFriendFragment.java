package com.app.WeOut;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import utils.CustomSnackBar;
import utils.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profile_AddFriendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profile_AddFriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile_AddFriendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button addFriend;
    private ProgressBar progressBar;
    private EditText addFriendByUsername;
    private String TAG;
    private CustomSnackBar snackBar;

    private OnFragmentInteractionListener mListener;

    // User info
    private static String currUserFullName;

    public Profile_AddFriendFragment(String currUserFullName) {
        this.currUserFullName = currUserFullName;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile_AddFriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile_AddFriendFragment newInstance(String param1, String param2) {
        Profile_AddFriendFragment fragment = new Profile_AddFriendFragment(currUserFullName);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        this.TAG = "Profile_AddFriendFragment";

        snackBar = new CustomSnackBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        this.addFriend = view.findViewById(R.id.addFriendButton);
        this.progressBar = view.findViewById(R.id.progressBarAddFriend);
        this.addFriendByUsername = view.findViewById(R.id.addUsernameFriend);

        // Get info for logged in user
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final String username = email.substring(0, email.indexOf("@weout.com"));

        this.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Initially set the progress bar to visible
                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(addFriendByUsername.getText()) || addFriendByUsername.getText().toString().equals(username)) {
                    snackBar.display(view, getActivity(), "Please enter a valid username!");
                }
                else if (addFriendByUsername.getText().toString().contains(" ")) {
                    snackBar.display(view, getActivity(), "Username cannot contain spaces!");
                }
                else {

                    final String usernameOfFriendToAdd = addFriendByUsername.getText().toString().trim();
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();

                    /*
                      @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                        Steps to add a friend:
                            1. Check if friendToAdd's username exists.
                            2. Check if the friendToAdd is already friends with the current user,
                                OR if the friendToAdd already has a friend request from the current user.
                            3. Check if the current user is already friends with the friendToAdd,
                                OR if the current user already has a friend request from the friendToAdd.
                            4. Add the friend.
                      @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    */

                    /*
                      @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                        Step 1. Check if friendToAdd's username exists.
                      @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    */

                    db.collection("users").document(usernameOfFriendToAdd).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    // If the username exists,
                                    if(documentSnapshot.exists() && documentSnapshot != null) {

                                        // Get the full name of the friend to use later (when updating the database)
                                        final String friendToAddFullName = documentSnapshot.toObject(User.class).getFullName();

                                        /*
                                          @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                                            Step 2. Check if the friendToAdd is already friends with the current user,
                                                    OR if the friendToAdd already has a friend request from the current user.
                                          @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                                        */

                                        Query query_friendToAdd = db
                                                .collection("users").document(usernameOfFriendToAdd)
                                                .collection("friends").whereEqualTo(username, currUserFullName);

                                        query_friendToAdd.get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshot_friendToAdd) {

                                                        // If the currentUser is somewhere in the friendToAdd's friends document already,
                                                        if (!queryDocumentSnapshot_friendToAdd.isEmpty()) {
                                                            snackBar.display(view, getActivity(), "You're already friends with this person, or you've already sent a friend request to them!");
                                                        }

                                                        // The friendToAdd does not have anything to do with the currentUser (yet)
                                                        else {

                                                            /*
                                                              @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                                                                Step 3. Check if the current user is already friends with the friendToAdd,
                                                                        OR if the current user already has a friend request from the friendToAdd.
                                                              @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                                                            */

                                                            Query query_currentUser = db
                                                                    .collection("users").document(username)
                                                                    .collection("friends").whereEqualTo(usernameOfFriendToAdd, friendToAddFullName);

                                                            query_currentUser.get()
                                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshot_currentUser) {

                                                                            // If the friendToAdd is somewhere in the currentUser's friends document already,
                                                                            if (!queryDocumentSnapshot_currentUser.isEmpty()) {
                                                                                snackBar.display(view, getActivity(), "You're already friends with this person, or you have a friend request from them already!");
                                                                            }

                                                                            // The friendToAdd does not have anything to do with the currentUser (yet)
                                                                            else {

                                                                                /*
                                                                                  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                                                                                    Step 4. Add the friend.
                                                                                            Specifically, add the currentUser to the friendToAdd/friends/received document
                                                                                  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                                                                                */

                                                                                // Create a hashmap for data to be sent
                                                                                Map<String, Object> friendToAddHashMap = new HashMap<>();

                                                                                // Add the current user name to the map
                                                                                friendToAddHashMap.put(username, currUserFullName);

                                                                                // Add the friend request to the friendToAdd's received document
                                                                                db.collection("users").document(usernameOfFriendToAdd)
                                                                                        .collection("friends").document("received")
                                                                                        .set(friendToAddHashMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        snackBar.display(view, getActivity(), "Friend request successfully sent");
                                                                                        // Clear the text box if successful
                                                                                        addFriendByUsername.getText().clear();
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        snackBar.display(view, getActivity(), "Friend request failed to send");
                                                                                    }
                                                                                });


                                                                            }

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            snackBar.display(view, getActivity(), "Error connecting to database.");
                                                                            Log.d(TAG, e.getMessage());
                                                                        }
                                                                    });

                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        snackBar.display(view, getActivity(), "Error connecting to database.");
                                                        Log.d(TAG, e.getMessage());
                                                    }
                                                });

                                    }
                                    // Else, the username does not exist,
                                    else {
                                        snackBar.display(view, getActivity(), "Username doesn't exist.");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    snackBar.display(view, getActivity(), "Error connecting to database.");
                                    Log.d(TAG, e.getMessage());
                                }
                            });


                    /*

                    // Check if friendToAdd is already friends with current user
                    // or if the friendToAdd already has a friend request from current user
                    Query query = db
                            .collection("users").document(usernameOfFriendToAdd)
                            .collection("friends").whereEqualTo(username, currUserFullName);

                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                final QuerySnapshot queryDocumentSnapshot = task.getResult();

                                // If query is not empty, then two scenarios:
                                    // 1. friendToAdd is already friends with current user
                                    // 2. friendToAdd already has a friend request from the current user
                                if(!queryDocumentSnapshot.isEmpty()) {
                                    snackBar.display(view, getActivity(), "You're already friends with this person, or you've already sent a friend request to them!");
                                }
                                else {
                                    // Check to see if the current user is:
                                        // Friends with the friendToAdd already
                                        // OR already has a friend request from the friendToAdd
                                    Query query1 = db
                                            .collection("users").document(username)
                                            .collection("friends").whereEqualTo(usernameOfFriendToAdd, true);

                                    query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot queryDocumentSnapshot1 = task.getResult();

                                                // If the query snapshot is not empty, then
                                                    // Current user is already friends with friendToAdd
                                                    // Current user already has a friend request from friendToAdd
                                                if(!queryDocumentSnapshot1.isEmpty()) {
                                                    snackBar.display(view, getActivity(), "You're already friends with this person, or you have a friend request from them already!");
                                                }

                                                // Finally, send friend request, as all tests have passed
                                                else {
                                                    // First, make sure that the username exists
                                                    db
                                                            .collection("users").document(usernameOfFriendToAdd)
                                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot snapshot = task.getResult();
                                                                // if username exists
                                                                if (snapshot.exists()) {

                                                                    // Get the full name of the friend
                                                                    String friendToAddFullName = snapshot.toObject(User.class).getFullName();

                                                                    // Create a hashmap for data to be sent
                                                                    Map<String, Object> pendingFriendRequest = new HashMap<>();
                                                                    // Add the current user name to the map
                                                                    pendingFriendRequest.put(username, currUserFullName);
                                                                    
                                                                    // Add the friend request to the friendToAdd's received document
                                                                    db.collection("users").document(usernameOfFriendToAdd)
                                                                            .collection("friends").document("received")
                                                                            .set(pendingFriendRequest, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            snackBar.display(view, getActivity(), "Friend request successfully sent");
                                                                            // Clear the text box if successful
                                                                            addFriendByUsername.getText().clear();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            snackBar.display(view, getActivity(), "Friend request failed to send");
                                                                        }
                                                                    });
                                                                }
                                                                else {
                                                                    snackBar.display(view, getActivity(), "Username doesn't exist");
                                                                }
                                                            }
                                                        }
                                                    }); // end of Sending Friend Request

                                                }
                                            }
                                        }
                                    }); // End of Query1 OnCompleteListener

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }

                    });

                    */

                }

                // Set the progress bar to gone after running all on-click functionality
                progressBar.setVisibility(View.GONE);
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
