package utils;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.app.WeOut.Profile_FriendListFragment.OnListFragmentInteractionListener;
import com.app.WeOut.R;
import com.app.WeOut.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyFriendRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRecyclerViewAdapter.ViewHolder> implements SectionIndexer {

    private List<Friend> friendsList;
    private final OnListFragmentInteractionListener mListener;
    private ArrayList<Integer> mSectionPositions;

    public MyFriendRecyclerViewAdapter(List<Friend> items, OnListFragmentInteractionListener listener) {
        friendsList = items;
        mListener = listener;
//        Collections.sort(friendsList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_creation_friend_checkbox_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get friend object from arraylist
        Friend friend = friendsList.get(position);

        // set corresponding info
        holder.friendUsername.setText(friend.getUserName());
        holder.friendFullName.setText(friend.getFullName());
        holder.friendLogo.setText(friend.getLogo());

        // hide the checkbox
        holder.friendCheckBox.setVisibility(View.GONE);

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

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        for (int i = 0, size = friendsList.size(); i < size; i++) {
            String section = String.valueOf(friendsList.get(i).firstName.charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int i) {
        return mSectionPositions.get(i);
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
//        public final ImageView imgId;

        public final TextView friendUsername;
        public final TextView friendFullName;
        public final TextView friendLogo;
        public final CheckBox friendCheckBox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
//            imgId = view.findViewById(R.id.myFriendLogo);

            friendUsername = view.findViewById(R.id.friendCheckboxItem_username);
            friendFullName = view.findViewById(R.id.friendCheckboxItem_fullName);
            friendLogo = view.findViewById(R.id.friendCheckboxItem_Logo);
            friendCheckBox = view.findViewById(R.id.friendCheckboxItem_checkBox);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + friendUsername.getText() + "'";
        }
    }
}
