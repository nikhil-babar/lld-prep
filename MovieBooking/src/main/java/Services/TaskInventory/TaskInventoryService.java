package Services.TaskInventory;

import Models.Task;
import Models.TaskMetadata;
import Models.TaskView;
import Models.User;
import Observers.Task.TaskObserver;

public interface TaskInventoryService extends TaskSubject {
    TaskView createTask(TaskMetadata data, User createdBy);

    void updateTask(String taskId, TaskMetadata data, User updatedBy);

    void changeStatus(String taskId, User updatedBy);
}
