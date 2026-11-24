package Services.TaskSearch;

import Models.Task;
import Models.TaskView;
import Store.TaskStore;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class TaskSearchServiceImpl implements TaskSearchService {

    private final TaskStore store;

    @Override
    public List<TaskView> searchByFilter(TaskFilter filter) {
        var currentDateTime = LocalDateTime.now();
        BiFunction<Task, TaskFilter, Boolean> isApplicable = (Task task, TaskFilter f) -> {
            return (f.getByAssignee() == null || f.getByAssignee().equals(task.getAssignee()))
                    && (f.getByReporter() == null || f.getByReporter().equals(task.getCreator()))
                    && (!f.isByAheadOfDueDate() || task.getDueDate().isBefore(currentDateTime))
                    && (f.getByLabel() == null || task.getLabels().contains(f.getByLabel()));
        };
        return store.stream().filter(task -> isApplicable.apply(task, filter)).map(Task::toView).toList();
    }

    @Override
    public Optional<TaskView> getById(String id) throws Exception {
        if(store.get(id).isEmpty()) throw new Exception("Task not found");
        return Optional.of(store.get(id).get().toView());
    }
}
