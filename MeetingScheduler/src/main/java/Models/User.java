package Models;

import Models.Views.UserView;

public class User extends Identifiable{
    private final String name;
    private final String email;
    private final Calender calender;

    public Calender getCalender() {
        return calender;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public User(String name, String email, Calender calender){
        this.email = email;
        this.name = name;
        this.calender = calender;
    }

    public UserView toView(){
        return new UserView(getId(), name, email);
    }
}
