package utils;

/**
 * Class that extends {@link Friend} class to provide detail on whether a friend is attending an
 * event, while also including details about the friend such as username, first name, and last name.
 */
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
