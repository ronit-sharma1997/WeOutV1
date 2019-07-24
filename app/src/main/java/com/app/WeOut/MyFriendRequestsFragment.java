package com.app.WeOut;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.WeOut.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

import utils.AcceptRejectButtonListener;
import utils.Friend;
import utils.MyFriendRequestRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MyFriendRequestsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<Friend> pendingFriendRequests;
    private AcceptRejectButtonListener acceptRejectButtonListener;

    private String TAG = "MyFriendRequestsFragment_TAG";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyFriendRequestsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MyFriendRequestsFragment newInstance(int columnCount) {
        MyFriendRequestsFragment fragment = new MyFriendRequestsFragment();
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

        // Initialize pending friend requests list with dummy variables
        pendingFriendRequests = new ArrayList<>();
        this.addDummyFriendRequests();

        // Set up Accept Reject Listener functions
        this.acceptRejectButtonListener = new AcceptRejectButtonListener() {
            @Override
            public void onAccept(int position) {
                Log.d(TAG, "Accepted " + pendingFriendRequests.get(position).getUserName());
                Toast.makeText(getActivity().getApplicationContext(), "Accepted " + pendingFriendRequests.get(position).getUserName(), Toast.LENGTH_SHORT);
            }

            @Override
            public void onReject(int position) {
                Log.d(TAG, "Rejected " + pendingFriendRequests.get(position).getUserName());
                Toast.makeText(getActivity().getApplicationContext(), "Rejected " + pendingFriendRequests.get(position).getUserName(), Toast.LENGTH_SHORT);
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyFriendRequestRecyclerViewAdapter(this.pendingFriendRequests, this.mListener, this.acceptRejectButtonListener));
        }
        return view;
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

    private void addDummyFriendRequests() {
        this.pendingFriendRequests.add(new Friend("jsmith01", "", ""));
        this.pendingFriendRequests.add(new Friend("asmith01", "", ""));
        this.pendingFriendRequests.add(new Friend("bsmith01", "", ""));
        this.pendingFriendRequests.add(new Friend("csmith01", "", ""));
        this.pendingFriendRequests.add(new Friend("dsmith01", "", ""));
        this.pendingFriendRequests.add(new Friend("esmith01", "", ""));
        this.pendingFriendRequests.add(new Friend("fsmith01", "", ""));
        this.pendingFriendRequests.add(new Friend("jsmith010123123", "", ""));
    }
}
