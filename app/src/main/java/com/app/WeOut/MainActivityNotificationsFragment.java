package com.app.WeOut;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.WeOut.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

import utils.Event;
import utils.EventInviteButtonListener;
import utils.MyEventInvitesRecyclerViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainActivityNotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainActivityNotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainActivityNotificationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private OnListFragmentInteractionListener listListener;
    private EventInviteButtonListener acceptRejectListener;
    private List<Event> eventInvites;
    private MyEventInvitesRecyclerViewAdapter myAdapter;

    public MainActivityNotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainActivityNotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainActivityNotificationsFragment newInstance(String param1, String param2) {
        MainActivityNotificationsFragment fragment = new MainActivityNotificationsFragment();
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

        this.eventInvites = new ArrayList<>();
        this.addDummyEventInvites();
        this.acceptRejectListener = new EventInviteButtonListener() {
            @Override
            public void onAccept(int position) {
                System.out.println("Event Invite " + eventInvites.get(position).getTitle() + " was accepted!");
            }

            @Override
            public void onReject(int position) {
                System.out.println("Event Invite " + eventInvites.get(position).getTitle() + " was rejected!");
                eventInvites.remove(position);
                myAdapter.notifyItemRemoved(position);
                myAdapter.notifyItemRangeChanged(position, eventInvites.size());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_activity_notifications, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.eventInviteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.myAdapter = new MyEventInvitesRecyclerViewAdapter(this.eventInvites, this.listListener, this.acceptRejectListener);
        recyclerView.setAdapter(this.myAdapter);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyContent.DummyItem item);
    }

    private void addDummyEventInvites(){
        this.eventInvites.add(new Event("Watch Spider-Man", "7/18/2019 10:00 P.M.", "sbillah1969", "", ""));
        this.eventInvites.add(new Event("Complete App Dev Project", "7/19/2019 10:00 P.M.", "sbillah1969", "", ""));
        this.eventInvites.add(new Event("Watch Venom", "7/20/2019 10:00 P.M.", "sbillah1969", "", ""));
    }
}
