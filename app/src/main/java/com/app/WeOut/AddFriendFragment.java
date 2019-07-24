package com.app.WeOut;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFriendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFriendFragment extends Fragment {
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
    private Snackbar messageBar;

    private OnFragmentInteractionListener mListener;

    public AddFriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriendFragment newInstance(String param1, String param2) {
        AddFriendFragment fragment = new AddFriendFragment();
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
        this.TAG = "AddFriendFragment";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        this.addFriend = view.findViewById(R.id.addFriendButton);
        this.progressBar = view.findViewById(R.id.progressBarAddFriend);
        this.addFriendByUsername = view.findViewById(R.id.addUsernameFriend);
        String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final String shortenUserName = userName.substring(0, userName.indexOf("@weout.com"));

        this.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(addFriendByUsername.getText()) || addFriendByUsername.getText().toString().equals(shortenUserName)) {
                    messageBar = Snackbar.make(view, "Please enter a valid username!", Snackbar.LENGTH_SHORT);
                    View messageBarView = messageBar.getView();
                    messageBarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.lightBlue));
                    messageBar.show();
                    //Toast.makeText(getContext(), "Please enter a valid username!", Toast.LENGTH_SHORT).show();
                } else {
                    final String friendToAdd = addFriendByUsername.getText().toString().trim();
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    //check if friend request was already sent before
                    Query query = db.collection("users").document(friendToAdd).collection("friends").whereEqualTo(shortenUserName, true);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                QuerySnapshot queryDocumentSnapshot = task.getResult();
                                if(!queryDocumentSnapshot.isEmpty()) {
                                    Toast.makeText(getContext(), "You've already sent a friend request to this person", Toast.LENGTH_SHORT).show();
                                } else {
                                    //if not send friend request
                                    db.collection("users").document(friendToAdd).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot snapshot = task.getResult();
                                                if (snapshot.exists()) {
                                                    Map<String, Object> pendingFriendRequest = new HashMap<>();
                                                    pendingFriendRequest.put(shortenUserName, true);
                                                    db.collection("users").document(friendToAdd)
                                                            .collection("friends").document("received")
                                                            .set(pendingFriendRequest, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext(), "Friend request successfully sent", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getContext(), "Friend request failed to send", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(getContext(), "Username doesn't exist", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                        }
                                    });
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }

                    });

                }
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
