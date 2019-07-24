package com.app.WeOut;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import utils.FriendActivityPagerAdapter;
import utils.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainActivityMyProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainActivityMyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
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

    private TextView userLogo, userName, fullName;


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
        View view = inflater.inflate(R.layout.mainactivity_fragment_tab3, container, false);
        this.userLogo = view.findViewById(R.id.userLogo);
        this.userName = view.findViewById(R.id.userName);
        this.fullName = view.findViewById(R.id.fullName);

        String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String shortenUserName = userName.substring(0, userName.indexOf("@weout.com"));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dr = db.collection("users").document(shortenUserName);
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists() && documentSnapshot != null) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    fullName.setText(currentUser.getFirstName().toString() + " " + currentUser.getLastName().toString());
                }
            }
        });

        this.userName.setText(shortenUserName);
        this.userLogo.setText(String.valueOf(Character.toUpperCase(this.userName.getText().charAt(0))));
        TabLayout tabLayout = view.findViewById(R.id.friendToolbar);
        tabLayout.addTab(tabLayout.newTab().setText("My Friends"));
        tabLayout.addTab(tabLayout.newTab().setText("Add Friends"));
        tabLayout.addTab(tabLayout.newTab().setText("Added Me"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = view.findViewById(R.id.myProfilePager);
        final FriendActivityPagerAdapter myAdapter = new FriendActivityPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
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
