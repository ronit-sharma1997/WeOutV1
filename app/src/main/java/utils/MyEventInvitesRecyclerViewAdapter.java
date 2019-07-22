package utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.WeOut.MainActivityNotificationsFragment.OnListFragmentInteractionListener;
import com.app.WeOut.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class MyEventInvitesRecyclerViewAdapter extends RecyclerView.Adapter<MyEventInvitesRecyclerViewAdapter.ViewHolder> {

    private List<Event> eventInvites;
    private OnListFragmentInteractionListener myListener;
    private EventInviteButtonListener buttonListener;

    public MyEventInvitesRecyclerViewAdapter(List<Event> items, OnListFragmentInteractionListener listener, EventInviteButtonListener buttonListener) {
        this.eventInvites = items;
        this.myListener = listener;
        this.buttonListener = buttonListener;
    }

    public void removeAt(int position) {
        this.eventInvites.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, this.eventInvites.size());
    }

    @NonNull
    @Override
    public MyEventInvitesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_event, viewGroup, false);
        return new ViewHolder(view, this.buttonListener);
//                new ViewHolder.ButtonListener() {
//            @Override
//            public void onAccept(int position) {
//                System.out.println("Accept Button clicked on index " +String.valueOf(position));
//            }
//
//            @Override
//            public void onReject(int position) {
//                System.out.println("Reject Button clicked on index " +String.valueOf(position));
//            }
//        });
    }

    @Override
    public void onBindViewHolder(@NonNull MyEventInvitesRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        viewHolder.event = this.eventInvites.get(i);
        viewHolder.eventTitle.setText(viewHolder.event.getTitle());
        viewHolder.eventDate.setText(viewHolder.event.getDate());
        viewHolder.eventOrganizer.setText("Organizer: " + viewHolder.event.getOrganizer());
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
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
        return this.eventInvites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public WeakReference<EventInviteButtonListener> listener;
        public final View mView;
        public final TextView eventTitle;
        public final TextView eventDate;
        public final TextView eventOrganizer;
        public final Button accept;
        public final Button reject;
        public Event event;
        public ViewHolder(@NonNull View itemView, EventInviteButtonListener listener) {
            super(itemView);
            this.mView = itemView;
            this.eventTitle = this.mView.findViewById(R.id.eventInviteTitle);
            this.eventDate = this.mView.findViewById(R.id.eventInviteDateTime);
            this.eventOrganizer = this.mView.findViewById(R.id.eventInviteInvitor);
            this.accept = this.mView.findViewById(R.id.eventInviteAcceptButton);
            this.reject = this.mView.findViewById(R.id.eventInviteRejectButton);
            this.listener = new WeakReference<>(listener);

            this.accept.setOnClickListener(this);
            this.reject.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.eventInviteAcceptButton:
                    this.listener.get().onAccept(getLayoutPosition());
                    break;
                case R.id.eventInviteRejectButton:
                    this.listener.get().onReject(getLayoutPosition());
                    break;
                default:
                    break;
            }
        }


    }
}
