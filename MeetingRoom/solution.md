

Entities

1. User
2. Room
3. Meeting


Services

1. MeetingService

- Book (Slot, Attendees, Organiser, Room)
- Auto Assign Room (Slot, AttendeeCount)
- CancelMeeting (meetingId)

2. MeetingRoomService

- IsValidSlotForRoom(Slot, Room)
- GetValidSlotAcrossRooms(Slot)

3. UserService

- register

4. UserCalenderService

- IsValidSlotForAttendees


5. Algorithm:

- IsOverlappingSlot (List<Slot>, Slot)

Behaviour

1. Auto Assign
2. Book a meeting
3. Cancel by orgaiser
4. Query by
    a. User bookings
    b. By room
    c. Get available rooms by slot
5. Attendee conflict




- IsValidSlotForRoom
- GetValidSlotAcrossRooms
- IsValidSlotForAttendees


