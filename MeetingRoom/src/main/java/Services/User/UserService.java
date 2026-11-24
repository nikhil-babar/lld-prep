package Services.User;

import Models.Slot;
import Models.User;

import java.util.List;

public interface UserService {
    public void requireUsers(List<User> users);

    public boolean isOverlappingSlot(List<User> users, Slot slot);

    public void reserveSlot(List<User> users, Slot slot);
}
