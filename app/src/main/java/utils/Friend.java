package utils;

//TODO: Write documentation
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
}
