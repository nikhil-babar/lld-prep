package Provider;

import Models.DateRange;
import Models.Room;
import Models.Views.RoomView;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface AutoAssignRoomStrategy {
    Optional<Room> autoAssign(List<Room> rooms, DateRange timing, int attendeeCount);
}
