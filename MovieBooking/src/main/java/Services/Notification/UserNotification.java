package Services.Notification;

import Enums.UserNotificationType;
import Models.User;

public interface UserNotification {
    void notify(User targetUser, UserNotificationType notificationType, String id);
}
