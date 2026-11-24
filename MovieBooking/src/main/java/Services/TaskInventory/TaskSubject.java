package Observers.Subject;

import Models.Task;
import Models.TaskData;

import java.util.Optional;

public interface TaskSubject {
    void notify(Task task, Optional<Task> previousState);
}
