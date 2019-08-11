package utils;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.WeOut.Profile_FriendRequestsFragment.OnListFragmentInteractionListener;
import com.app.WeOut.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Class that extends the {@link RecyclerView.Adapter} for configuration with the user's friend requests to display on the
 * {@link com.app.WeOut.Profile_FriendRequestsFragment} screen and makes a call to the specified {@link OnListFragmentInteractionListener}
 * and {@link AcceptRejectButtonListener}.
 */
public class MyFriendRequestRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRequestRecyclerViewAdapter.ViewHolder> {

    private List<Friend> pendingFriendsList;
    private final OnListFragmentInteractionListener mListener;
    private AcceptRejectButtonListener acceptRejectButtonListener;
    private String TAG;

    public MyFriendRequestRecyclerViewAdapter(List<Friend> items, OnListFragmentInteractionListener listener, AcceptRejectButtonListener acceptRejectButtonListener) {
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Friend friend = pendingFriendsList.get(position);

        holder.userName.setText(friend.getUserName());
        holder.fullName.setText(friend.getFullName());
        holder.personLogo.setText(friend.getLogo());

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
            Log.d(TAG, "Friend request item clicked");

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