package Store;

import Models.Task;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


public class TaskStore {
    public final Map<String, Task> idToTask = new ConcurrentHashMap<>();

    public Stream<Task> stream() {
        return idToTask.values().stream();
    }

    public Optional<Task> get(String id){
        if(!idToTask.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(idToTask.get(id));
    }

    public void put(Task task){
        idToTask.put(task.getId(), task);
    }
}
