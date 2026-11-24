package Models;

import java.time.LocalDateTime;
import java.util.Objects;

public record Slot (LocalDateTime startTime, LocalDateTime endTime){
    public Slot {
        Objects.requireNonNull(startTime, "StartTime cannot be null");
        Objects.requireNonNull(endTime, "StartTime cannot be null");
        if(startTime.isAfter(endTime)){
            throw new IllegalArgumentException("StartTime can't be greater than Endtime");
        }
    }
}