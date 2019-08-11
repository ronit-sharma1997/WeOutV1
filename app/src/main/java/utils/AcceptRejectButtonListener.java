package utils;

/**
 * Interface to represent the accepting and rejecting methods for an Event.
 */
public interface AcceptRejectButtonListener {
        void onAccept(int position);
        void onReject(int position);
}
