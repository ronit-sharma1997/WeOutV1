package utils;

import android.os.Parcel;

public class Event {
    private String title;
    private String location;
    private String date;
    private String description;
    private String organizer;


    public Event(String title, String date, String organizer, String location, String description) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.organizer = organizer;
        this.description = description;
    }

    protected Event(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);
        this.title = data[0];
        this.location = data[1];
        this.date = data[2];
        this.organizer = data[3];
        this.description = data[4];
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
