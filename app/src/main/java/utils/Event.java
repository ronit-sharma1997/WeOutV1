package utils;

import java.util.HashMap;

public class Event {

    // Event attributes
    private String title;
    private String location;
    private String eventDate;
    private String eventTime;
    private String dateCreated;
    private String description;
    private String organizer;

    // Hashmaps to handle attendees / invitees
    private HashMap <String, String> attendingMap;
    private HashMap <String, String> invitedMap;

    public Event(String title, String location, String eventDate, String eventTime, String dateCreated, String description, String organizer, HashMap<String, String> attendingMap, HashMap<String, String> invitedMap) {
        this.title = title;
        this.location = location;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.dateCreated = dateCreated;
        this.description = description;
        this.organizer = organizer;
        this.attendingMap = attendingMap;
        this.invitedMap = invitedMap;
    }

    public Event(String title, String location, String eventDate, String eventTime, String dateCreated, String description, String organizer) {
        this.title = title;
        this.location = location;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.dateCreated = dateCreated;
        this.description = description;
        this.organizer = organizer;

        this.attendingMap = null;
        this.invitedMap = null;
    }

    public Event () {

    }

    // TODO: Is this function necessary?
//    public HashMap <String, String> getEventHashMap() {
//        HashMap <String, String> hashMap = new HashMap<>();
//
//        hashMap.put("title", this.title);
//        hashMap.put("location", this.location);
//        hashMap.put("date", this.date);
//        hashMap.put("description", this.description);
//        hashMap.put("organizer", this.organizer);
//
//        return hashMap;
//    }


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

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public HashMap<String, String> getAttendingMap() {
        return attendingMap;
    }

    public void setAttendingMap(HashMap<String, String> attendingMap) {
        this.attendingMap = attendingMap;
    }

    public HashMap<String, String> getInvitedMap() {
        return invitedMap;
    }

    public void setInvitedMap(HashMap<String, String> invitedMap) {
        this.invitedMap = invitedMap;
    }

}
