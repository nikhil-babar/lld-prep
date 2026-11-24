package Services;

import Models.Calender;
import Models.User;
import Models.Views.UserView;
import Utility.MapUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private final Map<String, User> userMap;

    public UserService() {
        this.userMap = new HashMap<>();
    }

    public List<User> getByIds(List<String> ids) {
        return ids.stream().map(id -> MapUtility.require(userMap, id, "user")).toList();
    }

    public User getById(String id){
        return MapUtility.require(userMap, id, "user");
    }

    public UserView register(String name, String email){
        User user = new User(name, email, new Calender());
        userMap.put(user.getId(), user);
        return user.toView();
    }
}
