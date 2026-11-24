import Models.DateRange;
import Models.Requests.CreateMeetingRequest;
import Models.Requests.CreateRoomRequest;
import Models.Results.CreateMeetingResult;
import Models.Room;
import Models.Views.RoomView;
import Models.Views.UserView;
import Provider.AutoAssignRoomStrategy;
import Services.MeetingService;
import Provider.ReentrantBookingLockProvider;
import Services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        AutoAssignRoomStrategy strategy = new AutoAssignRoomStrategy() {
            @Override
            public Optional<Room> autoAssign(List<Room> rooms, DateRange timing, int attendeeCount) {
                return rooms.stream()
                        .filter(room -> !room.getCalender().isOverlapping(timing) && room.getCapacity() >= attendeeCount)
                        .min((a, b) -> a.getCapacity() - b.getCapacity());
            }
        };

        UserService userService = new UserService();
        MeetingService meetingService = new MeetingService(userService, new ReentrantBookingLockProvider(), strategy);

        UserView nikhil = userService.register("Nikhil", "nikhilbabarjee@gmail.com");
        UserView rohit = userService.register("Rohit", "rohit@gmail.com");
        UserView karan = userService.register("Karan", "karan@gmail.com");
        UserView siddesh = userService.register("Siddesh", "karan@gmail.com");

        RoomView malhar = meetingService.registerRoom(new CreateRoomRequest("Malhar", 10, new DateRange(null, null)));
        RoomView jahan = meetingService.registerRoom(new CreateRoomRequest("Jahan", 10, new DateRange(null, null)));
        RoomView sargam = meetingService.registerRoom(new CreateRoomRequest("Sargam", 10, new DateRange(null, null)));

        Optional<RoomView> autoAssigned = meetingService.autoAssign(new DateRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1)), 2);
        if(autoAssigned.isEmpty()){
            System.out.println("No room selected");
            return;
        }
        RoomView room = autoAssigned.get();

        CreateMeetingResult result1 = meetingService.bookMeeting(new CreateMeetingRequest("Introduction of the team", "Introduction", List.of(rohit.id()), nikhil.id(), room.id(), new DateRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1))));
        CreateMeetingResult result2 = meetingService.bookMeeting(new CreateMeetingRequest("Introduction of the team", "Introduction", List.of(karan.id()), siddesh.id(), room.id(), new DateRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1))));

        System.out.println(result1.meetingView() + ": " + result1.status());
        System.out.println(result2.meetingView() + ": " + result2.status());
    }
}
