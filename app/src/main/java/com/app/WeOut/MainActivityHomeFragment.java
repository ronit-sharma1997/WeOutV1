package com.app.WeOut;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.WeOut.dummy.DummyContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import utils.Event;
import utils.EventHomeFeedRecyclerViewAdapter;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainActivityHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainActivityHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainActivityHomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView eventInvitesRecyclerView;
    private TextView emptyRecyclerViewText;

    private  ArrayList<Event> eventList = new ArrayList<>();

    private FloatingActionButton addEventFAB;

    private OnListFragmentInteractionListener listListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EventHomeFeedRecyclerViewAdapter myAdapter;

    public MainActivityHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainActivityHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainActivityHomeFragment newInstance(String param1, String param2) {
        MainActivityHomeFragment fragment = new MainActivityHomeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mainactivity_fragment_tab1, container, false);

        // Associate all XML components w/ their IDs
        this.eventInvitesRecyclerView = view.findViewById(R.id.eventInvitesHomeFeed);
        this.addEventFAB = view.findViewById(R.id.addEventButton);
        this.emptyRecyclerViewText = view.findViewById(R.id.emptyRecyclerViewEventHomeFeed);

        // Set the empty text to visible at the start
        this.emptyRecyclerViewText.setVisibility(View.GONE);

        // Open the Add Event activity when the FAB is clicked
        this.addEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), MainActivityAddEvent.class);
                startActivityForResult(myIntent, 1);
            }
        });

        // Set the layout manager for the Recycler View
        this.eventInvitesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layoutanimationelementsfalldown);
//        recyclerView.setLayoutAnimation(animation);

//        this.eventList.add(new Event(
//                "Title", "Location", "Date", "Time",
//                "WhenCreated", "Description", "Organizer" ));

        // Create the adapter
        this.myAdapter = new EventHomeFeedRecyclerViewAdapter(
                this.eventList, this.listListener, this.emptyRecyclerViewText);

//
//        this.myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                checkEmpty();
//            }
//
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                checkEmpty();
//            }
//
//            @Override
//            public void onItemRangeRemoved(int positionStart, int itemCount) {
//                super.onItemRangeRemoved(positionStart, itemCount);
//                checkEmpty();
//            }
//            void checkEmpty() {
//                emptyRecyclerViewText.setVisibility(myAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
//            }
//        });
//

        // Set the adapter
        this.eventInvitesRecyclerView.setAdapter(this.myAdapter);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
//                eventList.add(new Gson().fromJson(data.getStringExtra("newEventJson"), Event.class));
                Log.d(TAG, "Size of event List: " + eventList.size());
//                myAdapter.notifyItemInserted(eventList.size()-1);
                myAdapter.notifyDataSetChanged();
            }
        }
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

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyContent.DummyItem item);
    }
}
