package utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.app.WeOut.R;
import java.util.List;

/**
 * Class that extends the {@link RecyclerView.Adapter} for configuration with the invitees of an Event to display if a user is attending
 * when a user clicks on an {@link Event} object on the Home Feed to view more detail.
 */
public class EventHomeFeedClickedInviteesRecyclerViewAdapter extends RecyclerView.Adapter<EventHomeFeedClickedInviteesRecyclerViewAdapter.ViewHolder> {

    private List<EventDetailsInvitee> friendsList;

    public EventHomeFeedClickedInviteesRecyclerViewAdapter(List<EventDetailsInvitee> items) {
        friendsList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eventhomefeed_custom_adapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.friendUsername.setText(friendsList.get(position).getFullName());
        if (!friendsList.get(position).isAttending()) {
            holder.attendingUser.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView attendingUser;
        public final TextView friendUsername;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            attendingUser = view.findViewById(R.id.eventHomeFeedCheckCircle);
            friendUsername = view.findViewById(R.id.eventHomeFeedClickedUsername);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + friendUsername.getText() + "'";
        }
    }
}

