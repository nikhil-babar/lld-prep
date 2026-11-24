package Services.TaskAudit;

import Models.TaskAudit;
import Observers.Task.TaskObserver;

import java.util.List;

public interface TaskAuditService extends TaskObserver {
    List<TaskAudit> getAudit(String taskId);
}
