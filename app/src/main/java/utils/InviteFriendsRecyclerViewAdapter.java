package utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.WeOut.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class that extends the {@link RecyclerView.Adapter} for configuration with the user's current friend list to display on the
 * {@link com.app.WeOut.MainActivityAddEventInviteFriendsFragment} screen.
 */
public class InviteFriendsRecyclerViewAdapter extends RecyclerView.Adapter <InviteFriendsRecyclerViewAdapter.ViewHolder> {

    private ArrayList <Friend_withCheck> inviteFriendsList;
    private String TAG = "EventCreation: ";

    public InviteFriendsRecyclerViewAdapter(ArrayList<Friend_withCheck> inviteFriendsList) {
        this.inviteFriendsList = inviteFriendsList;

        populateFriendsList();
    }

    private void populateFriendsList() {
        Log.d(TAG, "Populating friends list...");

        // Get current username
        String currentUsername = Utilities.getCurrentUsername();

        // Get the current user's friends
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference df = db
                .collection("users").document(currentUsername)
                .collection("friends").document("current");

        // Retrieve the data, store it if data retrieval successful
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // If there are friends, then store them.
                if(documentSnapshot.exists() && documentSnapshot != null) {

                    Log.d(TAG, "Get current friends successful.");

                    for (Map.Entry <String, Object> entry : documentSnapshot.getData().entrySet()) {
                        inviteFriendsList.add(
                                new Friend_withCheck(entry.getKey(), entry.getValue().toString()));
                    }

                    notifyDataSetChanged();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error retrieving friends
                Log.d(TAG, "Error retrieving friends: " + e.getMessage());
            }
        });

        // Add each one of these friends to your list
    }

    @Override
    public int getItemCount() {
        return inviteFriendsList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_creation_friend_checkbox_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        // Get a friend list object to work with
        Friend_withCheck friend = inviteFriendsList.get(position);

        // Set View Holder Values according to invite friends list Friend object
        holder.fullName.setText(friend.getFullName());
        holder.userName.setText(friend.getUserName());
        holder.personLogo.setText(friend.getLogo());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Set the state of the friend check to the boolean
                Friend_withCheck friend_withCheck = inviteFriendsList.get(position);
                friend_withCheck.setChecked(isChecked);

                if (friend_withCheck.isChecked()) {
                    holder.mView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.veryLightGray));
                }
                else {
                    holder.mView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.white));
                }

                Log.d(TAG, inviteFriendsList.get(position).getUserName() + " checked: " + inviteFriendsList.get(position).isChecked());
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Friend_withCheck friend_withCheck = inviteFriendsList.get(position);
                Log.d(TAG, friend_withCheck.getUserName() + " clicked");

                // Toggle checked state of button, and change color to "highlight" checked item
                if (friend_withCheck.isChecked()) {
                    holder.checkBox.setChecked(false);
                    holder.mView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.white));
                }
                else {
                    holder.checkBox.setChecked(true);
                    holder.mView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.veryLightGray));
                }

            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Declare view variables
        public final View mView;

        // TextViews for User Initials (logo), User Name, and Full Name
        public final TextView personLogo;
        public final TextView userName;
        public final TextView fullName;

        // Checkbox
        public final CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);

            this.mView = view;

            // Associate all items with views by id
            this.personLogo = view.findViewById(R.id.friendCheckboxItem_Logo);
            this.userName = view.findViewById(R.id.friendCheckboxItem_username);
            this.fullName = view.findViewById(R.id.friendCheckboxItem_fullName);

            this.checkBox = view.findViewById(R.id.friendCheckboxItem_checkBox);
        }

    }
}
