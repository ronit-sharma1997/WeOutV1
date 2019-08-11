package com.app.WeOut;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import utils.FriendActivityPagerAdapter;
import utils.User;


/**
 * A simple {@link Fragment} for the My Profile Tab of the app.
 * {@link MainActivity} contains this fragment and implements the
 * {@link MainActivityMyProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainActivityMyProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView userLogo, userName, fullName, profileHeader;

    private Button logOutButton;

    public MainActivityMyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainActivityMyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainActivityMyProfileFragment newInstance(String param1, String param2) {
        MainActivityMyProfileFragment fragment = new MainActivityMyProfileFragment();
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
        final View view = inflater.inflate(R.layout.mainactivity_fragment_tab3,
            container, false);

        // Associate members of this class with the layout views
        this.userLogo = view.findViewById(R.id.userLogo);
        this.userName = view.findViewById(R.id.userName);
        this.fullName = view.findViewById(R.id.fullName);

        this.logOutButton = view.findViewById(R.id.logoutButton);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(intent);
            }
        });

        // Display Friend Tabs within the user's profile
        final TabLayout tabLayout = view.findViewById(R.id.friendToolbar);
        tabLayout.addTab(tabLayout.newTab().setText("My Friends"));
        tabLayout.addTab(tabLayout.newTab().setText("Add Friends"));
        tabLayout.addTab(tabLayout.newTab().setText("Added Me"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Get info pertaining to current user
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String username = email.substring(0, email.indexOf("@weout.com"));

        // Get instance of Database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference the current user by their username within the "users" collection
        DocumentReference dr = db.collection("users").document(username);

        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists() && documentSnapshot != null) {
                    User currentUser = documentSnapshot.toObject(User.class);

                    String userFullName = currentUser.getFirstName() + " " + currentUser.getLastName();
                    String userInitials =
                            String.valueOf(Character.toUpperCase(currentUser.getFirstName().charAt(0))) +
                                    String.valueOf(Character.toUpperCase(currentUser.getLastName().charAt(0)));

                    // Set user full name
                    fullName.setText(userFullName);

                    // Set User logo to the First and Last letter of persons name
                    userLogo.setText(userInitials);

                    // Set up the view pager (with the user full name as input)
                    setUpViewPager(view, tabLayout, userFullName);
                }
            }
        });

        // Set username in profile to specific username
        this.userName.setText(username);

        //set font for Title Section
        this.profileHeader = view.findViewById(R.id.welcomeTitle);
        this.profileHeader.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.lobster));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setUpViewPager(View view, TabLayout tabLayout, String currUserFullName) {
        final ViewPager viewPager = view.findViewById(R.id.myProfilePager);
        final FriendActivityPagerAdapter myAdapter = new FriendActivityPagerAdapter(
                getChildFragmentManager(), tabLayout.getTabCount(), currUserFullName);
        viewPager.setAdapter(myAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
