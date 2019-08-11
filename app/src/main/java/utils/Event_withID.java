package utils;

/**
 * Class that represents an Event with a unique ID. This class encompasses an {@link Event} object
 * and adds additional attributes.
 */
public class Event_withID {

    private Event event;
    private String eventID;

    public Event_withID(Event event, String eventID) {
        this.event = event;
        this.eventID = eventID;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}
