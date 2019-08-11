package com.app.WeOut;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import java.util.Set;


import javax.annotation.Nullable;

import fastscroll.app.fastscrollalphabetindex.AlphabetIndexFastScrollRecyclerView;
import utils.Friend;
import utils.MyFriendRecyclerViewAdapter;

/**
 * Fragment used to represent a User's Current Friend's list. {@link MainActivity} contains this
 * fragment and implements the {@link OnListFragmentInteractionListener} interface.
 */
public class Profile_FriendListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private OnListFragmentInteractionListener mListener;
    private ArrayList<Friend> friendList;

    //custom alphabet index fast scroll recycler view
    private AlphabetIndexFastScrollRecyclerView myFriendsRecyclerView;
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

        //set the color of the index bar to light blue
        myFriendsRecyclerView.setIndexBarColor(R.color.lightBlue);
        //set the transparent value so that it's fully visible
        myFriendsRecyclerView.setIndexBarTransparentValue(0);

        myFriendsRecyclerView.setIndexBarVisibility(false);

        return view;
    }

    private void setUpListenerAdapter() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String username = email.substring(0, email.indexOf("@weout.com"));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference df = db
                .collection("users").document(username)
                .collection("friends").document("current");

        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot mapUsernameToFullName,
                @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    //we have some sort of error
                    Log.d(TAG, e.getMessage());
                    return;
                }
                if(mapUsernameToFullName.exists() && mapUsernameToFullName != null) {
                    // @@@@@@@@@@@@@@@@@@@@@@@@@@

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@
                    // New Way to display friends

                    // Store all the user's friends in a set.
                    Set<String> setOfFriendUsernames = mapUsernameToFullName.getData().keySet();

                    // If the user does not have any friends,
                    if (setOfFriendUsernames.size() == 0) {
                        friendList.clear();
                    }

                    // Else, if the user does have friends
                    else {

                        // For every friend that is stored locally,
                        for (int i = 0; i < friendList.size(); i++) {

                            // Check if this friend exists in the set.
                            //      If this friend does, then remove this item from the set.
                            if (setOfFriendUsernames.contains(friendList.get(i).getUserName())) {
                                setOfFriendUsernames.remove(friendList.get(i).getUserName());
                            }

                        }

                        // Now you should have a set of friends that the local friends list does not have
                        for (String usernameToAdd : setOfFriendUsernames) {

                            // Add each one of these friends from the set to the local array list
                            friendList.add(
                                    new Friend(usernameToAdd, mapUsernameToFullName.get(usernameToAdd).toString()));
                        }

                    }

                    // @@@@@@@@@@@@@@@@@@@@@@@@@@
                    // End of new way to display friends

                    // For debugging
                    Log.d(TAG, "FriendList Size: " + friendList.size());
                    Log.d(TAG, "Friends: " + mapUsernameToFullName.getData().keySet());

                    // Make the empty text visible based on friends list size
                    emptyRecyclerView.setVisibility(friendList.size() == 0 ? View.VISIBLE : View.GONE);
                    myFriendsRecyclerView.setIndexBarTransparentValue(friendList.size() != 0 ? 1 : 0);

                    myFriendsRecyclerView.setIndexBarVisibility(friendList.size() == 0 ? false : true);

                    //Make the alphabetic index bar visible based on friends list size

                    // Set up the adapter
                    myFriendRecyclerViewAdapter = new MyFriendRecyclerViewAdapter(friendList, mListener);
                    myFriendRecyclerViewAdapter.notifyDataSetChanged();
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
