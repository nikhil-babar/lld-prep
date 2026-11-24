package Models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calender {

    private final Map<LocalDate, List<DateRange>> slots;

    public Calender() {
        this.slots = new HashMap<>();
    }

    public boolean isOverlapping(DateRange timing){
        var date = timing.startTime().toLocalDate();
        return slots.containsKey(date) && slots.get(date).stream().anyMatch(range -> range.isOverlapping(timing));
    }

    public void reserve(DateRange timing){
        if(isOverlapping(timing)){
            throw new IllegalArgumentException("Overlapping slot detected");
        }
        var date = timing.startTime().toLocalDate();
        slots.computeIfAbsent(date, (date1) -> new ArrayList<DateRange>()).add(timing);
    }
}
