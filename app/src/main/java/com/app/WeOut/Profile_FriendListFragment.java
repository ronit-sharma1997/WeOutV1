package com.app.WeOut;

import android.content.Context;
import android.os.Bundle;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


import java.util.ArrayList;



import javax.annotation.Nullable;

import utils.MyFriendRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class Profile_FriendListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<String> friendList;
    private RecyclerView myFriendsRecyclerView;
    private TextView emptyRecyclerView;
    private MyFriendRecyclerViewAdapter myFriendRecyclerViewAdapter;
    private String TAG;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Profile_FriendListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Profile_FriendListFragment newInstance(int columnCount) {
        Profile_FriendListFragment fragment = new Profile_FriendListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        this.TAG = "Profile_FriendListFragment";
        this.friendList = new ArrayList<>();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        this.myFriendsRecyclerView = view.findViewById(R.id.recyclerViewMyFriends);
        this.emptyRecyclerView = view.findViewById(R.id.emptyRecyclerViewMyFriendsList);
        this.myFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setUpListenerAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myFriendsRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        myFriendsRecyclerView.addItemDecoration(dividerItemDecoration);

//        }
        return view;
    }

    private void setUpListenerAdapter() {
        String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String shortenUserName = userName.substring(0, userName.indexOf("@weout.com"));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference df = db.collection("users").document(shortenUserName).collection("friends").document("current");
//        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if(documentSnapshot.exists() && documentSnapshot != null) {
//
//                        friendList = new ArrayList<>(documentSnapshot.getData().keySet());
//                        emptyRecyclerView.setVisibility(friendList.size() == 0 ? View.VISIBLE : View.GONE);
//                    }
//
//
//                }
//                else {
//                    emptyRecyclerView.setVisibility(View.VISIBLE);
//                    Log.d(TAG, "Error with getting current friends");
//                }
//                myFriendRecyclerViewAdapter = new MyFriendRecyclerViewAdapter(friendList, mListener);
//                myFriendsRecyclerView.setAdapter(myFriendRecyclerViewAdapter);
//
//            }
//        });


        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    //we have some sort of error
                    Log.d(TAG, e.getMessage());
                    return;
                }
                if(documentSnapshot.exists() && documentSnapshot != null) {
                    friendList = new ArrayList<>(documentSnapshot.getData().keySet());
                    Log.d(TAG, "FriendList Size: " + friendList.size());
                    emptyRecyclerView.setVisibility(friendList.size() == 0 ? View.VISIBLE : View.GONE);
                    myFriendRecyclerViewAdapter = new MyFriendRecyclerViewAdapter(friendList, mListener);
                    myFriendsRecyclerView.setAdapter(myFriendRecyclerViewAdapter);
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
