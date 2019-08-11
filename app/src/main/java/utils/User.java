package utils;


/**
 * Class to represent the properties of An Existing User such as username, first name, and last name.
 */
public class User {
    private String firstName;
    private String lastName;
    private String userName;
    private String emailAddress;
    private String joinedDate;

    public User(String firstName, String lastName, String userName, String emailAddress, String joinedDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.joinedDate = joinedDate;
    }

    public User() {

    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

}


