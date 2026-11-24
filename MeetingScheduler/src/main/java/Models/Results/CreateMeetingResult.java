package Models.Results;

import Models.Views.MeetingView;

public record CreateMeetingResult(CreateMeetingStatus status, MeetingView meetingView) {
}
