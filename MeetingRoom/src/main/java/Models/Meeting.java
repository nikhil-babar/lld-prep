package Models;

import java.util.List;

public record Meeting (String id, String organiser, List<String> attendees, Slot timing) {}
