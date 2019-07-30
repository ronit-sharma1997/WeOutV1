package utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.WeOut.Profile_FriendRequestsFragment.OnListFragmentInteractionListener;
import com.app.WeOut.R;
import com.app.WeOut.dummy.DummyContent.DummyItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFriendRequestRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRequestRecyclerViewAdapter.ViewHolder> {

    private List<String> pendingFriendsList;
    private final OnListFragmentInteractionListener mListener;
    private AcceptRejectButtonListener acceptRejectButtonListener;
    private String TAG;

    public MyFriendRequestRecyclerViewAdapter(List<String> items, OnListFragmentInteractionListener listener, AcceptRejectButtonListener acceptRejectButtonListener) {
        this.pendingFriendsList = items;
        this.mListener = listener;
        this.acceptRejectButtonListener = acceptRejectButtonListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friendrequest, parent, false);
        this.TAG = "MyFriendRequestRecyclerViewAdapter_TAG";
        return new ViewHolder(view, this.acceptRejectButtonListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Extract user full name from the database
        final String usernameByPosition = pendingFriendsList.get(position);

        // Get instance of Database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference the current user by their username within the "users" collection
        DocumentReference dr = db.collection("users").document(usernameByPosition);

        // Test what the document id is
        Log.d(TAG, "Document ID " + dr.getId());

        // Get the data from the current document
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                // Log successful data retrieval
                Log.d(TAG, "Friend Request User Specific Info successfully retrieved.");

                // If successful and the snapshot contains data
                if(documentSnapshot.exists() && documentSnapshot != null) {
                    // Use this data to create a User object, and set holder TextViews accordingly
                    User userByPosition = documentSnapshot.toObject(User.class);

                    // Create Strings for the full name and initials of the user by position
                    String userFullName = userByPosition.getFirstName() + " " + userByPosition.getLastName();
                    String userInitials =
                                    String.valueOf(Character.toUpperCase(userByPosition.getFirstName().charAt(0))) +
                                    String.valueOf(Character.toUpperCase(userByPosition.getLastName().charAt(0)));

                    // Set Holder Text Views based on user by position
                    holder.fullName.setText(userFullName);
                    holder.personLogo.setText(userInitials);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Log fail data retrieval
                Log.d(TAG, "Friend Request User Specific Info failed to retrieve.");
            }
        });

        // Set the text of each textView in the viewHolder to initial values
//        holder.personLogo.setText(String.valueOf(Character.toUpperCase(pendingFriendsList.get(position).charAt(0))));
        holder.userName.setText(pendingFriendsList.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingFriendsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Declare view variables
        public final View mView;

        // TextViews for User Initials (logo), User Name, and Full Name
        public final TextView personLogo;
        public final TextView userName;
        public final TextView fullName;

        // Accept Reject Buttons
        public final Button acceptButton;
        public final Button rejectButton;

        public WeakReference<AcceptRejectButtonListener> listener;

        public ViewHolder(View view, AcceptRejectButtonListener acceptRejectButtonListener) {
            super(view);

            this.mView = view;

            // Associate all items with views by id
            this.personLogo = view.findViewById(R.id.pendingFriendLogo);
            this.userName = view.findViewById(R.id.pendingFriendNameRequest);
            this.fullName = view.findViewById(R.id.pendingFriendFullName);
            this.acceptButton = view.findViewById(R.id.acceptButton);
            this.rejectButton = view.findViewById(R.id.rejectButton);

            // Initialize WeakReference
            this.listener = new WeakReference<>(acceptRejectButtonListener);

            // on click for accept/reject
            this.acceptButton.setOnClickListener(this);
            this.rejectButton.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + userName.getText() + "'";
        }

        @Override
        public void onClick(View view) {

            // TODO: See if this is necessary
//            if (pendingFriendsList.get(getLayoutPosition()).equals("demoFriend")){
//                Log.d(TAG, "demoFriend Button Clicked. No functionality.");
//                return;
//            }

            switch (view.getId()) {
                case R.id.acceptButton:
                    this.listener.get().onAccept(getLayoutPosition());
                    break;
                case R.id.rejectButton:
                    this.listener.get().onReject(getLayoutPosition());
                    break;
                default:
                    break;
            }
        }
    }
}