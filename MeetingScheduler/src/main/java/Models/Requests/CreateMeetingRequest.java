package Models.Requests;

import Models.DateRange;

import java.util.List;

public record CreateMeetingRequest(String title,
                                   String description,
                                   List<String> attendeeIds,
                                   String organiserId,
                                   String roomId,
                                   DateRange timing) {
}
