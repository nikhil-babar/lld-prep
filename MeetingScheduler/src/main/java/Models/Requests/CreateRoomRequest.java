package Models.Requests;

import Models.DateRange;

public record CreateRoomRequest (String name, int capacity, DateRange workingHours){
}
