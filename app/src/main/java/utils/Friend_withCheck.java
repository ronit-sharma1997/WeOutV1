package utils;

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
