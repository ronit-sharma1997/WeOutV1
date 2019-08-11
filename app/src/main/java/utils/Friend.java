package utils;

import android.util.Log;

/**
 * Class that represents a Friend. This includes username, firstname, and lastname.
 */
public class Friend {

    String userName;

    String firstName;
    String lastName;
    String fullName;

    String logo;

    public Friend(String userName, String firstName, String lastName) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        setLogo();
        setFullName();
    }

    public Friend(String username, String fullName) {
        this.userName = username;
        this.fullName = fullName;

        // Set first and last names from the full name
        setFirstLastFromFullName();

        // Now you can set the logo (because first and last names have been defined)
        setLogo();

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogo() { return this.logo; }

    private void setLogo() {
        this.logo = firstName.substring(0,1) + lastName.substring(0,1);
    }

    private void setFullName() { fullName = firstName + " " + lastName; }

    public String getFullName() { return fullName; }

    private void setFirstLastFromFullName() {

        Log.d("Debugging: ", "Full name: " + this.fullName);

        // Find the index where the space occurs
        int indexOfSpace = this.fullName.indexOf(" ");

        // Set the first and last names
        this.firstName = this.fullName.substring(0, indexOfSpace);
        this.lastName = this.fullName.substring(indexOfSpace+1);

    }

}
