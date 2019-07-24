package utils;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.app.WeOut.MyFriendRequestsFragment.OnListFragmentInteractionListener;
import com.app.WeOut.R;
import com.app.WeOut.dummy.DummyContent.DummyItem;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFriendRequestRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRequestRecyclerViewAdapter.ViewHolder> {

    private List<Friend> pendingFriendsList;
    private final OnListFragmentInteractionListener mListener;
    private AcceptRejectButtonListener acceptRejectButtonListener;

    public MyFriendRequestRecyclerViewAdapter(List<Friend> items, OnListFragmentInteractionListener listener, AcceptRejectButtonListener acceptRejectButtonListener) {
        this.pendingFriendsList = items;
        this.mListener = listener;
        this.acceptRejectButtonListener = acceptRejectButtonListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_friendrequest, parent, false);
        return new ViewHolder(view, this.acceptRejectButtonListener);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView personLogo;
        public final TextView userName;
        public Friend pendingFriend;
        public final Button acceptButton;
        public final Button rejectButton;

        public WeakReference<AcceptRejectButtonListener> listener;

        public ViewHolder(View view, AcceptRejectButtonListener acceptRejectButtonListener) {
            super(view);

            this.mView = view;

            // Associate all items with views by id
            this.personLogo = view.findViewById(R.id.pendingFriendLogo);
            this.userName = view.findViewById(R.id.pendingFriendNameRequest);
            this.acceptButton = view.findViewById(R.id.acceptButton);
            this.rejectButton = view.findViewById(R.id.rejectButton);

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
