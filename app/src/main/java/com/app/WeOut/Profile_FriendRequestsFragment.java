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
import android.widget.Toast;

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

import utils.AcceptRejectButtonListener;
import utils.CustomSnackBar;
import utils.MyFriendRequestRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class Profile_FriendRequestsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<String> pendingFriendRequests;
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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Profile_FriendRequestsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Profile_FriendRequestsFragment newInstance(int columnCount) {
        Profile_FriendRequestsFragment fragment = new Profile_FriendRequestsFragment();
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
        this.TAG = "Profile_FriendRequestsFragment";
        this.db = FirebaseFirestore.getInstance();
        this.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        this.userName = email.substring(0, email.indexOf("@weout.com"));

        // Initialize snackbar for user feedback
        snackBar = new CustomSnackBar();

        // Set up Accept Reject Listener functions
        this.acceptRejectButtonListener = new AcceptRejectButtonListener() {
            @Override
            public void onAccept(int position) {

                WriteBatch stepsToAcceptFriendRequest = db.batch();
                final String acceptedFriendRequest = pendingFriendRequests.get(position);
                Log.d(TAG, "User clicked Accepted Button: " + acceptedFriendRequest);
                Map<String, Object> receivedFriendRequest = new HashMap<>();
                receivedFriendRequest.put(acceptedFriendRequest, FieldValue.delete());
                DocumentReference dfReceivedFriendRequest = db.collection("users").document(userName).collection("friends").document("received");
                stepsToAcceptFriendRequest.update(dfReceivedFriendRequest, new HashMap<>(receivedFriendRequest));
                DocumentReference dfCurrentFriends = db.collection("users").document(userName).collection("friends").document("current");
                receivedFriendRequest.put(acceptedFriendRequest, true);
                stepsToAcceptFriendRequest.set(dfCurrentFriends,new HashMap<>(receivedFriendRequest), SetOptions.merge());
                DocumentReference dfAddedFriendCurrentFriends = db.collection("users").document(acceptedFriendRequest).collection("friends").document("current");
                receivedFriendRequest.put(userName, true);
                receivedFriendRequest.remove(acceptedFriendRequest);
                stepsToAcceptFriendRequest.set(dfAddedFriendCurrentFriends,new HashMap<>(receivedFriendRequest), SetOptions.merge());
                stepsToAcceptFriendRequest.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Database successfully updated with friend connection " + userName + " : " + acceptedFriendRequest);
                            snackBar.display(getView(), getActivity().getApplicationContext(), "Accepted " + acceptedFriendRequest);
                        } else {
                            Log.d(TAG, "Database failed to update with friend connection " + userName + " : " + acceptedFriendRequest);
                            snackBar.display(getView(), getActivity().getApplicationContext(), "Error accepting " + acceptedFriendRequest);
                        }
                    }
                });

            }

            @Override
            public void onReject(int position) {
                Log.d(TAG, "User Clicked Rejected Button: " + pendingFriendRequests.get(position));
                final String rejectedFriendRequest = pendingFriendRequests.get(position);
                Map<String, Object> receivedFriendRequest = new HashMap<>();
                receivedFriendRequest.put(rejectedFriendRequest, FieldValue.delete());
                DocumentReference dfReceivedFriendRequest = db.collection("users").document(userName).collection("friends").document("received");
                dfReceivedFriendRequest.update(receivedFriendRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Database successfully removed friend request " + rejectedFriendRequest);
                            snackBar.display(getView(), getActivity().getApplicationContext(), "Rejected " + rejectedFriendRequest);
                        } else {
                            Log.d(TAG, "Database failed to remove friend request " + rejectedFriendRequest);
                            snackBar.display(getView(), getActivity().getApplicationContext(), "Error: Failed to reject " + rejectedFriendRequest);
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
        DocumentReference df = this.db.collection("users").document(userName).collection("friends").document("received");
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists() && documentSnapshot != null) {
                        pendingFriendRequests = new ArrayList<>(documentSnapshot.getData().keySet());
                        emptyRecyclerViewFriendRequests.setVisibility(pendingFriendRequests.size() == 0 ? View.VISIBLE : View.GONE);
                    }


                }
                else {
                    emptyRecyclerViewFriendRequests.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Error with getting friend requests");
                }
                myFriendRequestRecyclerViewAdapter = new MyFriendRequestRecyclerViewAdapter(pendingFriendRequests, mListener, acceptRejectButtonListener);
                myFriendRequestsRecyclerView.setAdapter(myFriendRequestRecyclerViewAdapter);

            }
        });


        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    //we have some sort of error
                    Log.d(TAG, e.getMessage());
                    return;
                }
                if(documentSnapshot.exists() && documentSnapshot != null) {
                    pendingFriendRequests = new ArrayList<>(documentSnapshot.getData().keySet());
                    emptyRecyclerViewFriendRequests.setVisibility(pendingFriendRequests.size() == 0 ? View.VISIBLE : View.GONE);
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
