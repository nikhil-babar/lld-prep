package Models;

import Models.Views.MeetingView;

import java.util.List;

public class Meeting extends Identifiable {
    private final String title;
    private final String description;
    private final DateRange timing;
    private final List<User> attendees;
    private final User organiser;
    private final Room room;

    public DateRange getTiming() {
        return timing;
    }

    public List<User> getAttendees() {
        return attendees;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public User getOrganiser() {
        return organiser;
    }

    public Room getRoom() {
        return room;
    }

    public Meeting(String title, String description, DateRange timing, List<User> attendees, User organiser, Room room) {
        this.title = title;
        this.description = description;
        this.timing = timing;
        this.attendees = List.copyOf(attendees);
        this.organiser = organiser;
        this.room = room;
    }

    public MeetingView toView(){
        return new MeetingView(title, description, attendees.stream().map(User::toView).toList(), organiser.toView(), timing);
    }
}
