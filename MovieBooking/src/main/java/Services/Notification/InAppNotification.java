package Services.Notification;

import Enums.UserNotificationType;
import Models.User;

public class InAppNotification implements UserNotification {
    @Override
    public void notify(User targetUser, UserNotificationType notificationType, String taskId) {
        System.out.println(targetUser.getName() + " received notification of type: " + notificationType + " for task: " + taskId);
    }
}
