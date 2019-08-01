package com.app.WeOut;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


import java.util.ArrayList;

import utils.AnimatorPath;
import utils.EventHomeFeedRecyclerViewAdapter;
import utils.PathEvaluator;
import utils.PathPoint;
import utils.Event_withID;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * MainActivityHomeFragment extends {@link Fragment} subclass. {@link MainActivity} contains this
 * fragment and implements the {@link MainActivityHomeFragment.OnFragmentInteractionListener}
 * interface to handle interaction events and the {@link MainActivityHomeFragment.OnListFragmentInteractionListener}
 * interface to handle interaction with Recycler View in the fragment.
 */
public class MainActivityHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView eventInvitesRecyclerView;
    private TextView emptyRecyclerViewText;

    private  ArrayList<Event_withID> eventList = new ArrayList<>();

    private FloatingActionButton addEventFAB;

    private FrameLayout createEventContainer;
    private RelativeLayout createEventContentContainer;

    private TextView homeHeader;

    private final static float SCALE_FACTOR = 20f;
    private final static int ANIMATION_DURATION = 300;
    private final static int MINIMUN_X_DISTANCE = 200;
    private boolean mRevealFlag;
    private float mFabSize;
    private float fabOriginX;
    private float fabOriginY;

    private OnListFragmentInteractionListener listListener;

    private OnFragmentInteractionListener mListener;
    private EventHomeFeedRecyclerViewAdapter myAdapter;

    public MainActivityHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.mainactivity_fragment_tab1, container, false);

        // Associate all XML components w/ their IDs
        this.eventInvitesRecyclerView = view.findViewById(R.id.eventInvitesHomeFeed);
        this.addEventFAB = view.findViewById(R.id.addEventButton);
        this.emptyRecyclerViewText = view.findViewById(R.id.emptyRecyclerViewEventHomeFeed);

        this.createEventContainer = getActivity().findViewById(R.id.createEventScreen);
        this.createEventContentContainer = getActivity()
                .findViewById(R.id.relativeLayoutEventCreateContainer);

        this.mFabSize = getResources().getDimensionPixelSize(R.dimen.fab_size);

        // Set the empty text to visible at the start
        this.emptyRecyclerViewText.setVisibility(View.VISIBLE);

        //set on click listener for floating action button
        this.addEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabPressed();
            }
        });

        // Set the layout manager for the Recycler View
        this.eventInvitesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Create the adapter
        this.myAdapter = new EventHomeFeedRecyclerViewAdapter(
                this.eventList, this.listListener, this.emptyRecyclerViewText);

        // Set the adapter
        this.eventInvitesRecyclerView.setAdapter(this.myAdapter);

        //Set the font for the header
        this.homeHeader = view.findViewById(R.id.home_TextView);
        this.homeHeader.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.lobster));

        //in the onCreateView the exact position of a view is not determined until much later on. Adding
        //a global layout listener so that once the view is constructed on the screen, we can get the exact
        //coordinates of the floating action button
        view.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int[] originalFABPostion = new int[2];
                        addEventFAB.getLocationOnScreen(originalFABPostion);
                        fabOriginX = originalFABPostion[0];
                        fabOriginY = originalFABPostion[1] + addEventFAB.getHeight() / 2;
                    }
                });

        return view;
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
        listListener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Helper method to complete the process of clicking on the add event floating action button and
     * opening the event input screen
     */
    private void fabPressed() {
        final float startX = this.addEventFAB.getX();
        //when we animate the floating action button, we want the icon in the FAB to disappear
        this.addEventFAB.setImageResource(0);

        //create an AnimatorPath for the floating action button to follow in its animation
        AnimatorPath path = new AnimatorPath();
        path.moveTo(0, 0);
        //the floating action button will curve to following end point with following control points
        path.curveTo(-300, -200, -300, -300, -400, -600);

        //instantiate an ObjectAnimator with AnimatorPath path and attach to floating action button
        final ObjectAnimator anim = ObjectAnimator.ofObject(this, "fabLoc",
                new PathEvaluator(), path.getPoints().toArray());

        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(ANIMATION_DURATION);
        anim.start();
        //remove the tab layout view so that the event input screen can be seen in full screen
        TabLayout tabLayout = getActivity().findViewById(R.id.mainToolbar);
        tabLayout.setVisibility(View.GONE);

        //add an update listener to listen to the ObjectAnimator
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //if the floating action button has moved more than our minimum required distance
                if (Math.abs(startX - addEventFAB.getX()) > MINIMUN_X_DISTANCE) {
                    //if the event input screen hasn't been revealed yet, make the floating action button
                    //grow to cover the screen and then replace with eventCreationContainer
                    if (!mRevealFlag) {
                        //make the floating action button very large
                        addEventFAB.animate()
                                .scaleXBy(SCALE_FACTOR)
                                .scaleYBy(SCALE_FACTOR)
                                .setListener(mEndRevealListener)
                                .setDuration(ANIMATION_DURATION);

                        mRevealFlag = true;

                    }
                }
            }
        });
    }

    /**
     * AnimatorListenerAdapter to listen for when the floating action button is done growing
     */
    private AnimatorListenerAdapter mEndRevealListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);

            //begin transaction process to MainActivityAddEventFragment
            MainActivityAddEventFragment fragment = new MainActivityAddEventFragment();
            //pass a reference of the floating action button to the next fragment
            fragment.setAddEventFAB(addEventFAB, fabOriginX, fabOriginY);
            //replace the fragment container with the new fragment MainActivityAddEventFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.createEventScreen, fragment).commit();

            //animate the container so that it reveals itself almost immediately
            createEventContainer.animate().scaleX(1).scaleY(1).setDuration(1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            //once the animation is done, set the container to visible and hide the floating action
                            //button
                            createEventContainer.setVisibility(View.VISIBLE);
                            addEventFAB.hide();
                        }
                    }).start();

            mRevealFlag = false;

        }
    };

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
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other fragments
     * contained in that activity.
     * <p>
     * See the Android Training lesson <a href= "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface OnListFragmentInteractionListener {

        // TODO: Update argument type and name
        void onListFragmentInteraction(Event_withID event, View v);
    }


    /**
     * We need this setter to translate between the information the animator produces (a new
     * "PathPoint" describing the current animated location) and the information that the button
     * requires (an xy location). The setter will be called by the ObjectAnimator given the 'fabLoc'
     * property string.
     */
    public void setFabLoc(PathPoint newLoc) {
        addEventFAB.setTranslationX(newLoc.mX);

        if (mRevealFlag) {
            addEventFAB.setTranslationY(newLoc.mY - (mFabSize / 2));
        } else {
            addEventFAB.setTranslationY(newLoc.mY);
        }
    }

}
