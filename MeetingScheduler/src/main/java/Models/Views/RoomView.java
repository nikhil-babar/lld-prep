package Models.Views;

import Models.DateRange;

public record RoomView(String name, String id, DateRange timing, int capacity) {
}
