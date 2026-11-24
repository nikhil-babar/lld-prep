package Services.Room;

import Models.Room;
import Models.Slot;

public interface SlotService {
    public boolean isValidSlot(Slot slot, Room room);

    public void reserveSlot(Slot slot, Room room);
}
