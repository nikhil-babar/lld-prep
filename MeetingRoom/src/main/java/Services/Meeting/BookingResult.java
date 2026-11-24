package Services.Meeting;

import Models.Meeting;

public record BookingResult(BookingStatus status, Meeting meeting) {
    public static BookingResult fromStatus(BookingStatus status){
        return new BookingResult(status, null);
    }

    public static BookingResult fromResult(Meeting meeting){
        return new BookingResult(BookingStatus.COMPLETED, meeting);
    }

}
