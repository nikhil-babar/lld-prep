package Models;

import java.time.LocalDateTime;

public record DateRange(LocalDateTime startTime, LocalDateTime endTime) {
    public boolean isOverlapping(DateRange range){
        var date1 = startTime.toLocalDate();
        var date2 = range.startTime.toLocalDate();
        if(!date1.equals(date2)) return false;
        return (range.startTime.isBefore(startTime) && range.endTime.isAfter(startTime)) || (range.startTime.isAfter(startTime) && endTime.isAfter(range.startTime));
    }
}