package Models.Views;

import Models.DateRange;
import java.util.List;

public record MeetingView (String title, String description, List<UserView> attendees, UserView organiser, DateRange timing){
}
