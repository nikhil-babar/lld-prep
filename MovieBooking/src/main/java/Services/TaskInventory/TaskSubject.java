package Services.TaskInventory;

import Models.Task;
import Observers.Task.TaskObserver;

import java.util.Optional;

public interface TaskSubject {
    void attach(TaskObserver observer);

    void detach(TaskObserver observer);
}
