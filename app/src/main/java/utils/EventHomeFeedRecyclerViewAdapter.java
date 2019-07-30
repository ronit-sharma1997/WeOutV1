package utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.app.WeOut.R;
import java.util.List;
import com.app.WeOut.MainActivityHomeFragment.OnListFragmentInteractionListener;
import com.google.firebase.auth.FirebaseAuth;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EventHomeFeedRecyclerViewAdapter extends RecyclerView.Adapter<EventHomeFeedRecyclerViewAdapter.ViewHolder> {
    private List<Event> eventsList;
    private final OnListFragmentInteractionListener mListener;

    public EventHomeFeedRecyclerViewAdapter(List<Event> items, OnListFragmentInteractionListener listener) {
        this.eventsList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_event_invent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // fill the holder with information for the corresponding event
        holder.eventInvite = this.eventsList.get(position);
        holder.eventTitle.setText(this.eventsList.get(position).getTitle());
        holder.eventLocation.setText(this.eventsList.get(position).getLocation());
        holder.eventDate.setText(
                this.eventsList.get(position).getEventDate() + " " +
                this.eventsList.get(position).getEventTime()
        );

        // Get current user's information from firebase
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String userName = email.substring(0, email.indexOf("@weout.com"));

        // Only show the organizer badge if this event was created by the current user
        holder.organizer.setVisibility(View.GONE);
        if(userName.equals(this.eventsList.get(position).getOrganizer())) {
            holder.organizer.setVisibility(View.VISIBLE);
        }

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
        return this.eventsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView eventTitle;
        public final TextView eventDate;
        public final TextView eventLocation;
        public final FrameLayout organizer;
        public Event eventInvite;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            eventTitle = view.findViewById(R.id.eventInviteHomeFeedTitle);
            eventDate = view.findViewById(R.id.eventInviteHomeFeedDateTime);
            eventLocation = view.findViewById(R.id.eventInviteHomeFeedLocation);
            organizer = view.findViewById(R.id.organizerBadge);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + eventTitle.getText() + "'";
        }
    }
}
