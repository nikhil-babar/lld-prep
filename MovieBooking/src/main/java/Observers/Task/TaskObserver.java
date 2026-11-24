package Observers.Task;

import Models.Task;
import Models.TaskMetadata;
import Models.User;

import java.util.Optional;

public interface TaskObserver {
    void listen(Task task, Task beforeUpdate, User updatedBy);
}
