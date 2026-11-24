package Services.TaskInventory;

import Models.Task;
import Models.TaskMetadata;
import Models.TaskView;
import Models.User;
import Observers.Task.TaskObserver;
import Store.TaskStore;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
public class TaskInventoryServiceImpl implements TaskInventoryService {
    private final TaskStore store;
    private final Map<String, Semaphore> idToLock = new ConcurrentHashMap<>();
    private List<TaskObserver> observers = new ArrayList<>();

    @Override
    public TaskView createTask(TaskMetadata data, User created) {
        Task task = new Task(data, created);
        this.store.put(task);
        notify(task, null, created);
        return task.toView();
    }

    @Override
    public void updateTask(String taskId, TaskMetadata data, User updatedBy) {
        Runnable runnable = () -> {
            Optional<Task> task = store.get(taskId);
            if(task.isEmpty()) return;

            Task beforeUpdate = task.get().toBuilder().build();
            task.get().updateData(data);
            notify(task.get(), beforeUpdate, updatedBy);
        };
        lockAndExecute(taskId, runnable);
    }

    @Override
    public void changeStatus(String taskId, User updatedBy) {
        Runnable runnable = () -> {
            Optional<Task> task = store.get(taskId);
            if(task.isEmpty()) return;

            Task beforeUpdate = task.get().toBuilder().build();
            task.get().changeStatus();
            notify(task.get(), beforeUpdate, updatedBy);
        };
        lockAndExecute(taskId, runnable);
    }

    public void lockAndExecute(String taskId, Runnable task){
        if(!idToLock.containsKey(taskId)){
            synchronized (idToLock){
                if(!idToLock.containsKey(taskId)){
                    idToLock.put(taskId, new Semaphore(1, true));
                }
            }
        }
        Semaphore lock = idToLock.get(taskId);
        synchronized (lock) {
            task.run();
        }
    }

    @Override
    public void attach(TaskObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(TaskObserver observer) {
        observers = observers.stream().filter(o -> o != observer).toList();
    }

    private void notify(Task currentTask, Task previousTask, User updatedBy){
        observers.forEach(o -> o.listen(currentTask, previousTask, updatedBy));
    }
}
