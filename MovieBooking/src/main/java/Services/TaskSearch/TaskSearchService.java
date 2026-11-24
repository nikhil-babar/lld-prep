package Services.TaskSearch;

import Models.Task;
import Models.TaskView;

import java.util.List;
import java.util.Optional;

public interface TaskSearchService {
    List<TaskView> searchByFilter(TaskFilter filter);

    Optional<TaskView> getById(String id) throws Exception;
}
