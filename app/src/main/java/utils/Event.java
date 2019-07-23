package utils;

import android.os.Parcel;

import java.util.HashMap;

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

    public HashMap <String, String> getEventHashMap() {
        HashMap <String, String> hashMap = new HashMap<>();

        hashMap.put("title", this.title);
        hashMap.put("location", this.location);
        hashMap.put("date", this.date);
        hashMap.put("description", this.description);
        hashMap.put("organizer", this.organizer);

        return hashMap;
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
