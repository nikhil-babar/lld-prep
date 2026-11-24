package Services;

import Models.DateRange;
import Models.Meeting;
import Models.Requests.CreateMeetingRequest;
import Models.Requests.CreateRoomRequest;
import Models.Results.CreateMeetingResult;
import Models.Results.CreateMeetingStatus;
import Models.Room;
import Models.User;
import Models.Views.MeetingView;
import Models.Views.RoomView;
import Provider.AutoAssignRoomStrategy;
import Provider.BookingLockProvider;
import Provider.ReentrantBookingLockProvider;
import Utility.MapUtility;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

public class MeetingService {
    private final Map<String, Meeting> meetingMap;
    private final Map<String, Room> roomMap;
    private final UserService userService;
    private final BookingLockProvider roomLockProvider;
    private final AutoAssignRoomStrategy autoAssignRoomStrategy;

    public MeetingService(UserService userService, ReentrantBookingLockProvider roomLockProvider, AutoAssignRoomStrategy autoAssignRoomStrategy) {
        this.userService = userService;
        this.roomLockProvider = roomLockProvider;
        this.autoAssignRoomStrategy = autoAssignRoomStrategy;
        this.meetingMap = new HashMap<>();
        this.roomMap = new HashMap<>();
    }

    public List<MeetingView> listRoomMeetings(LocalDate date) {
        return filterMeetings(meeting -> meeting.getTiming().startTime().toLocalDate().equals(date));
    }

    public List<MeetingView> listUserMeetings(LocalDate date, String userId) {
        return filterMeetings(meeting -> meeting.getAttendees().stream().anyMatch(user -> user.getId().equals(userId)) || meeting.getOrganiser().getId().equals(userId));
    }

    public Optional<RoomView> autoAssign(DateRange timing, int attendeeCount){
        var result = autoAssignRoomStrategy.autoAssign(roomMap.values().stream().toList(), timing, attendeeCount);
        return result.map(Room::toView);
    }

    private List<MeetingView> filterMeetings(Predicate<Meeting> meetingPredicate){
        return meetingMap.values().stream().filter(meetingPredicate).map(Meeting::toView).toList();
    }

    public CreateMeetingResult bookMeeting(CreateMeetingRequest request) {
        User organiser = userService.getById(request.organiserId());
        List<User> attendees = userService.getByIds(request.attendeeIds());
        Room room = MapUtility.require(roomMap, request.roomId(), "room");

        List<User> users = new ArrayList<>(attendees);
        users.add(organiser);

        {
            var result = isEligibleForBooking(request.timing(), users, room);
            if(result != null){
                return result;
            }
        }

        return roomLockProvider.withLock(room.getId(), request.timing().startTime().toLocalDate(), () -> {
            var result = isEligibleForBooking(request.timing(), users, room);
            if(result != null){
                return result;
            }
            var meeting = new Meeting(
                    request.title(),
                    request.description(),
                    request.timing(),
                    attendees,
                    organiser,
                    room
            );
            users.forEach(user -> user.getCalender().reserve(meeting.getTiming()));
            room.getCalender().reserve(meeting.getTiming());
            meetingMap.put(meeting.getId(), meeting);
            return new CreateMeetingResult(CreateMeetingStatus.COMPLETED, meeting.toView());
        });
    }

    public RoomView registerRoom(CreateRoomRequest request){
        Room room = new Room(request.name(), request.capacity(), request.workingHours());
        roomMap.put(room.getId(), room);
        return room.toView();
    }

    private CreateMeetingResult isEligibleForBooking(DateRange range, List<User> users, Room room){
        for(User user : users){
            if(user.getCalender().isOverlapping(range)){
                return new CreateMeetingResult(CreateMeetingStatus.ATTENDEE_OVERLAPPING, null);
            }
        }
        if(room.getCalender().isOverlapping(range)){
            return new CreateMeetingResult(CreateMeetingStatus.SLOT_ALREADY_BOOKED, null);
        }
        return null;
    }
}
