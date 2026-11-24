package Services.User;

import Models.Slot;
import Models.User;
import Utility.SlotUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private final Map<String, User> users;
    private final Map<String, List<Slot>> slots;

    @Override
    public void requireUsers(List<User> users) {

    }

    @Override
    public boolean isOverlappingSlot(List<User> users, Slot slot) {
        return users.stream().
    }

    @Override
    public void reserveSlot(List<User> users, Slot slot) {

    }
}

