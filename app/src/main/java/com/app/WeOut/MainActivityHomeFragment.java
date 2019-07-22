package com.app.WeOut;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.WeOut.dummy.DummyContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;

import utils.Event;
import utils.EventHomeFeedRecyclerViewAdapter;


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

    private RecyclerView eventInvites;

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
        eventList.add(new Event("Spider Man", "7.4.19, 8PM", "ronswanson", "AMC Theater", "Watching Spiderman with the homies"));
        eventList.add(new Event("Pho and I", "7.5.19, 1PM", "ronswanson1", "AMC Theater", "Watching Spiderman with the homies"));
        eventList.add(new Event("Billiards", "7.23.19, 4PM", "ronswanson", "AMC Theater", "Watching Spiderman with the homies"));
        eventList.add(new Event("Avengers", "7.25.19, 8PM", "ronswanson2", "AMC Theater", "Watching Spiderman with the homies"));
        eventList.add(new Event("Amelias", "7.27.19, 1PM", "ronswanson", "AMC Theater", "Watching Spiderman with the homies"));
        eventList.add(new Event("Swimming", "7.27.19, 3PM", "ronswanson4", "AMC Theater", "Watching Spiderman with the homies"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mainactivity_fragment_tab1, container, false);
        eventInvites = view.findViewById(R.id.eventInvitesHomeFeed);
        this.addEventFAB = view.findViewById(R.id.addEventButton);
        this.addEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), MainActivityAddEvent.class);
                startActivityForResult(myIntent, 1);

            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.eventInvitesHomeFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layoutanimationelementsfalldown);
//        recyclerView.setLayoutAnimation(animation);
        this.myAdapter = new EventHomeFeedRecyclerViewAdapter(this.eventList, this.listListener);
        recyclerView.setAdapter(this.myAdapter);


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
                eventList.add(new Gson().fromJson(data.getStringExtra("newEvent"), Event.class));
                myAdapter.notifyItemInserted(eventList.size()-1);
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
