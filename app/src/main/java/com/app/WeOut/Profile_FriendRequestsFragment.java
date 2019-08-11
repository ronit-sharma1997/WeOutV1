package com.app.WeOut;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.WeOut.dummy.DummyContent.DummyItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utils.AcceptRejectButtonListener;
import utils.CustomSnackBar;
import utils.Friend;
import utils.MyFriendRequestRecyclerViewAdapter;

/**
 * A fragment representing a list of Friend Requests a user would receive.
 * <p/>
 * {@link MainActivity} contains this fragment and implements the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class Profile_FriendRequestsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<Friend> pendingFriendRequests;
    private RecyclerView myFriendRequestsRecyclerView;
    private TextView emptyRecyclerViewFriendRequests;
    private MyFriendRequestRecyclerViewAdapter myFriendRequestRecyclerViewAdapter;
    private AcceptRejectButtonListener acceptRejectButtonListener;
    private String TAG;
    private FirebaseFirestore db;

    // Snackbar Variable
    private CustomSnackBar snackBar;

    // User Information from database
    private String email;
    private String userName;
    private static String currUserFullName;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Profile_FriendRequestsFragment(String currUserFullName) {
        TAG = "Profile_FriendRequestsFragment";

        this.currUserFullName = currUserFullName;
        Log.d(TAG, "User Full Name: " + this.currUserFullName);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Profile_FriendRequestsFragment newInstance(int columnCount) {
        Profile_FriendRequestsFragment fragment = new Profile_FriendRequestsFragment(currUserFullName);
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        this.pendingFriendRequests = new ArrayList<>();

        this.db = FirebaseFirestore.getInstance();
        this.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        this.userName = email.substring(0, email.indexOf("@weout.com"));

        // Initialize snackbar for user feedback
        snackBar = new CustomSnackBar();

        // Set up Accept Reject Listener functions
        this.acceptRejectButtonListener = new AcceptRejectButtonListener() {
            @Override
            public void onAccept(int position) {

                //create a batch write. All the following writes must succeed or else none of the writes will happen
                WriteBatch stepsToAcceptFriendRequest = db.batch();
                final String friendRequestUsernameToAccept = pendingFriendRequests.get(position).getUserName();
                Log.d(TAG, "User clicked Accepted Button: " + friendRequestUsernameToAccept);

                // Get the friend to add's full name
                String fullNameOfFriendToAccept = pendingFriendRequests.get(position).getFullName();

                //user accepts the friend request, so we can remove it from the document reference that holds friend requests
                Map<String, Object> receivedFriendRequest = new HashMap<>();
                receivedFriendRequest.put(friendRequestUsernameToAccept, FieldValue.delete());

                //create document reference for received friend requests for the current user
                DocumentReference dfReceivedFriendRequest = db
                        .collection("users").document(userName)
                        .collection("friends").document("received");

                stepsToAcceptFriendRequest.update(dfReceivedFriendRequest, new HashMap<>(receivedFriendRequest));

                //create document reference for current friend's list for the current user
                DocumentReference dfCurrentFriends = db
                        .collection("users").document(userName)
                        .collection("friends").document("current");

                //update the current user's current friend list by placing the friend's username and fullname in the list
                receivedFriendRequest.put(friendRequestUsernameToAccept, fullNameOfFriendToAccept);
                //we want to merge the current friend's list with the new friend(SetOptions.merge())
                stepsToAcceptFriendRequest.set(dfCurrentFriends,new HashMap<>(receivedFriendRequest), SetOptions.merge());
                //we'll reuse the hashmap from above instead of allocating a new HashMap by just deleting what's in it already
                receivedFriendRequest.remove(friendRequestUsernameToAccept);

                //create document reference for current friend's list for the user that send the friend request
                DocumentReference dfAddedFriendCurrentFriends = db
                        .collection("users").document(friendRequestUsernameToAccept)
                        .collection("friends").document("current");

                //we will put the current user's username and full name as a friend in the user that sent the friend request
                receivedFriendRequest.put(userName, currUserFullName);
                stepsToAcceptFriendRequest.set(dfAddedFriendCurrentFriends,new HashMap<>(receivedFriendRequest), SetOptions.merge());

                //commit the updates we have set up and add a listener to make sure all the batch writes happen successfully
                stepsToAcceptFriendRequest.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Database successfully updated with friend connection " + userName + " : " + friendRequestUsernameToAccept);
                            snackBar.display(getView(), getActivity().getApplicationContext(), "Accepted " + friendRequestUsernameToAccept);
                        } else {
                            Log.d(TAG, "Database failed to update with friend connection " + userName + " : " + friendRequestUsernameToAccept);
                            snackBar.display(getView(), getActivity().getApplicationContext(), "Error accepting " + friendRequestUsernameToAccept);
                        }
                    }
                });

            }

            @Override
            public void onReject(int position) {
                Log.d(TAG, "User Clicked Rejected Button: " + pendingFriendRequests.get(position));
                final String usernameOfFriendToReject = pendingFriendRequests.get(position).getUserName();

                //current user rejected the friend request, so we need to delete it from the current user's pending friend request list
                Map<String, Object> receivedFriendRequest = new HashMap<>();
                receivedFriendRequest.put(usernameOfFriendToReject, FieldValue.delete());

                //create document reference for current user's received friend requests
                DocumentReference dfReceivedFriendRequest = db.collection("users").document(userName)
                        .collection("friends").document("received");

                //simply update the friend request and add a listener to see if the task was successful
                dfReceivedFriendRequest.update(receivedFriendRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Database successfully removed friend request " + usernameOfFriendToReject);
                            snackBar.display(getView(), getActivity().getApplicationContext(), "Rejected " + usernameOfFriendToReject);
                        } else {
                            Log.d(TAG, "Database failed to remove friend request " + usernameOfFriendToReject);
                            snackBar.display(getView(), getActivity().getApplicationContext(), "Error: Failed to reject " + usernameOfFriendToReject);
                        }
                    }
                });
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request_list, container, false);

        //Associate XML Components by ID and assign to variables
        this.myFriendRequestsRecyclerView = view.findViewById(R.id.recyclerViewMyFriendRequests);
        this.emptyRecyclerViewFriendRequests = view.findViewById(R.id.emptyRecyclerViewMyFriendRequests);
        this.myFriendRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.setUpListenerAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this.myFriendRequestsRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        this.myFriendRequestsRecyclerView.addItemDecoration(dividerItemDecoration);
        return view;
    }

    private void setUpListenerAdapter() {
        final DocumentReference df = this.db
                .collection("users").document(userName)
                .collection("friends").document("received");

        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot mapUsernameToFullName, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    //we have some sort of error
                    Log.d(TAG, e.getMessage());
                    return;
                }
                if(mapUsernameToFullName.exists() && mapUsernameToFullName != null) {

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@
                    // New way to display friend requests

                    // Store all the user's friend requests in a set.
                    Set<String> setOfFriendUsernames = mapUsernameToFullName.getData().keySet();

                    // If the user does not have any friend requests
                    if (setOfFriendUsernames.size() == 0) {
                        pendingFriendRequests.clear();
                    }

                    // Else, if the user does have friend requests
                    else {

                        // For every friend that is stored locally,
                        for (int i = 0; i < pendingFriendRequests.size(); i++) {

                            // Check if this friend exists in the set.
                            //      If this friend does, then remove this item from the set.
                            if (setOfFriendUsernames.contains(pendingFriendRequests.get(i).getUserName())) {
                                setOfFriendUsernames.remove(pendingFriendRequests.get(i).getUserName());
                            }

                        }

                        // Now you should have a set of friends that the local friends list does not have
                        for (String usernameToAdd : setOfFriendUsernames) {

                            // Add each one of these friends from the set to the local array list
                            pendingFriendRequests.add(
                                    new Friend(usernameToAdd, mapUsernameToFullName.get(usernameToAdd).toString()));
                        }
                    }
                    // @@@@@@@@@@@@@@@@@@@@@@@@@@
                    // End of new way to display friend requests

                    emptyRecyclerViewFriendRequests.setVisibility(pendingFriendRequests.size() == 0 ? View.VISIBLE : View.GONE);

                    // TODO: Move below out of this function...
                    myFriendRequestRecyclerViewAdapter = new MyFriendRequestRecyclerViewAdapter(pendingFriendRequests, mListener, acceptRejectButtonListener);
                    myFriendRequestsRecyclerView.setAdapter(myFriendRequestRecyclerViewAdapter);
                }

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }

}
