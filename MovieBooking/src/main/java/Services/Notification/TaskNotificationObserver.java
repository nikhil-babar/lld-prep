package Services.Notification;

import Enums.UserNotificationType;
import Models.Task;
import Models.User;
import Observers.Task.TaskObserver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TaskNotificationObserver implements TaskObserver {

    private final UserNotification userNotification;

    @Override
    public void listen(Task task, Task beforeUpdate, User updatedBy) {
        if(beforeUpdate == null){
            userNotification.notify(task.getCreator(), UserNotificationType.CREATED, task.getId());
            return;
        }
        if(beforeUpdate.getAssignee() != task.getAssignee()){
            userNotification.notify(task.getCreator(), UserNotificationType.ASSIGNED, task.getId());
            userNotification.notify(beforeUpdate.getAssignee(), UserNotificationType.UNASSIGNED, task.getId());
            userNotification.notify(task.getAssignee(), UserNotificationType.ASSIGNED, task.getId());
            return;
        }
        if(
                !task.getTitle().equals(beforeUpdate.getTitle())
                || !task.getDueDate().equals(beforeUpdate.getDueDate())
        ){
            userNotification.notify(task.getCreator(), UserNotificationType.CHANGED_METADATA, task.getId());
            userNotification.notify(task.getAssignee(), UserNotificationType.CHANGED_METADATA, task.getId());
        }
    }
}
