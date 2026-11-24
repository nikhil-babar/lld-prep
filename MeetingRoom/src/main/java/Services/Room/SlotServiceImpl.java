package Services.Room;

import Models.Room;
import Models.Slot;
import Utility.SlotUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SlotServiceImpl implements SlotService {

    public Map<String, List<Slot>> slots;

    @Override
    public boolean isValidSlot(Slot slot, Room room) {
        return !SlotUtility.isOverlappingSlot(slots.getOrDefault(room.id(), new ArrayList<>()), slot);
    }

    @Override
    public void reserveSlot(Slot slot, Room room) {
        slots.compute(room.id(), (String roomId, List<Slot> slots) -> {
            if(slots == null){
                return List.of(slot);
            }
            List<Slot> updatedSlots = new ArrayList<>(slots);
            updatedSlots.add(slot);
            return updatedSlots;
        });
    }
}
