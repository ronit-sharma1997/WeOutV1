package utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.WeOut.MainActivityAddEventInviteFriendsFragment;
import com.app.WeOut.R;
import com.app.WeOut.MainActivityAddEventInviteFriendsFragment.OnListFragmentInteractionListener;

import java.util.List;

public class InviteFriendRecyclerViewAdapter extends RecyclerView.Adapter<InviteFriendRecyclerViewAdapter.ViewHolder> {

    private List<String> friendsList;
    private final OnListFragmentInteractionListener mListener;

    public InviteFriendRecyclerViewAdapter(List<String> items, OnListFragmentInteractionListener listener) {
        friendsList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.friendUsername.setText(friendsList.get(position));
//        Icon friendLogo = Icon.createWithResource(this,R.drawable.friendaccount);
//        holder.imgId.setImageResource(R.drawable.friendaccount);

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
        return friendsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imgId;
        public final TextView friendUsername;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imgId = view.findViewById(R.id.myFriendLogo);
            friendUsername = view.findViewById(R.id.myFriendsRecycleViewAdapter);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + friendUsername.getText() + "'";
        }
    }
}

