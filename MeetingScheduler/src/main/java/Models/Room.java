package Models;

import Models.Views.RoomView;

public class Room extends Identifiable {
    private final String name;
    private final Calender calender;
    private final int capacity;
    private final DateRange workingHours;

    public String getName() {
        return name;
    }

    public Calender getCalender() {
        return calender;
    }

    public int getCapacity() {
        return capacity;
    }

    public DateRange getWorkingHours() {
        return workingHours;
    }

    public Room(String name, int capacity, DateRange workingHours) {
        this.name = name;
        this.capacity = capacity;
        this.workingHours = workingHours;
        this.calender = new Calender();
    }

    public RoomView toView(){
        return new RoomView(name, getId(), workingHours, capacity);
    }
}
