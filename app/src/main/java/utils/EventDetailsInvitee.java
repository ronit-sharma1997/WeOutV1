package utils;

public class EventDetailsInvitee extends Friend {

  private boolean isAttending;

  public EventDetailsInvitee(String userName, String firstName, String lastName, Boolean isAttending) {
    super(userName, firstName, lastName);
    this.isAttending = isAttending;
  }

  public boolean isAttending() {
    return isAttending;
  }
}
