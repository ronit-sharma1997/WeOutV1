package utils;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.WeOut.MyFriendRequestsFragment.OnListFragmentInteractionListener;
import com.app.WeOut.R;
import com.app.WeOut.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFriendRequestRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRequestRecyclerViewAdapter.ViewHolder> {

    private List<Friend> pendingFriendsList;
    private final OnListFragmentInteractionListener mListener;

    public MyFriendRequestRecyclerViewAdapter(List<Friend> items, OnListFragmentInteractionListener listener) {
        pendingFriendsList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friendrequest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.pendingFriend = pendingFriendsList.get(position);
        holder.personLogo.setText(String.valueOf(Character.toUpperCase(pendingFriendsList.get(position).userName.charAt(0))));
        holder.userName.setText(pendingFriendsList.get(position).userName);

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView personLogo;
        public final TextView userName;
        public Friend pendingFriend;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            personLogo = view.findViewById(R.id.pendingFriendLogo);
            userName = view.findViewById(R.id.pendingFriendNameRequest);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + userName.getText() + "'";
        }
    }
}
