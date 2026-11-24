package taskmgmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Append-only history per task; events arrive in order, so it is chronological by construction. */
public class ActivityLog implements TaskListener {
    private final Map<String, List<TaskEvent>> byTask = new HashMap<>();

    @Override
    public synchronized void onEvent(TaskEvent event) {
        byTask.computeIfAbsent(event.task().getId(), k -> new ArrayList<>()).add(event);
    }

    public synchronized List<TaskEvent> getHistory(String taskId) {
        return List.copyOf(byTask.getOrDefault(taskId, List.of()));
    }
}
