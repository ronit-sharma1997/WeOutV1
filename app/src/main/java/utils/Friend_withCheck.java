package utils;

/**
 * Class that extends {@link Friend} and is used on the invite friends screen of the
 * {@link com.app.WeOut.MainActivityAddEventInviteFriendsFragment}. This class can easily tell us
 * if a friend has been checked when a user selects or deselects off of the friend's list.
 */
public class Friend_withCheck extends Friend {

    private boolean checked;

    public Friend_withCheck(String userName, String firstName, String lastName) {
        super(userName, firstName, lastName);
        this.checked = false;
    }

    public Friend_withCheck(String username, String fullName) {
        super(username, fullName);
        this.checked = false;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
