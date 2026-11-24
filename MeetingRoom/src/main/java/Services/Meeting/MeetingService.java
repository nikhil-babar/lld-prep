package Services.Meeting;

import Models.Meeting;
import Models.Room;
import Models.Slot;
import Models.User;
import Services.Room.SlotService;
import Services.User.UserService;

import java.util.*;

public class MeetingService {
    private final Map<String, Meeting> meetings;
    private final UserService userService;
    private final SlotService slotService;

    public MeetingService(UserService userService, SlotService slotService) {
        this.userService = userService;
        this.slotService = slotService;
        this.meetings = new HashMap<>();
    }

    public BookingResult book(User organiser, List<User> attendees, Slot slot, Room room){
        List<User> users = new ArrayList<>(attendees);
        users.add(organiser);
        userService.requireUsers(users);

        if(!slotService.isValidSlot(slot, room)){
            return BookingResult.fromStatus(BookingStatus.ROOM_NOT_AVAILABLE);
        }

        if(!userService.isOverlappingSlot(users, slot)){
            return BookingResult.fromStatus(BookingStatus.USER_HAS_OVERLAPPING_SLOT);
        }

        slotService.reserveSlot(slot, room);
        userService.reserveSlot(users, slot);

        Meeting meeting = new Meeting(UUID.randomUUID().toString(), organiser.id(), attendees.stream().map(User::id).toList(), slot);
        meetings.put(meeting.id(), meeting);

        return BookingResult.fromResult(meeting);
    }

}
