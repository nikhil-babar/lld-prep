package Services.TaskAudit;

import Models.Task;
import Models.TaskAudit;
import Models.TaskAuditItem;
import Models.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TaskAuditServiceImpl implements TaskAuditService {
    private final Map<String, List<TaskAudit>> taskAuditMap = new ConcurrentHashMap<>();

    @Override
    public void listen(Task task, Task beforeUpdate, User user) {
        List<TaskAuditItem> auditItems = new ArrayList<>();
        if(beforeUpdate == null || !task.getTaskStatus().equals(beforeUpdate.getTaskStatus())){
            var previousValue = beforeUpdate == null ? null : beforeUpdate.getTaskStatus();
            var item = TaskAuditItem.builder().changedField("status").previousValue(previousValue).currentValue(task.getTaskStatus()).build();
            auditItems.add(item);
        }
        TaskAudit auditEntry = TaskAudit.builder().auditItems(auditItems).updatedBy(user).updatedAt(LocalDateTime.now()).build();
        taskAuditMap.compute(task.getId(), (taskId, taskAudits) -> {
            List<TaskAudit> updatedAudits = new ArrayList<>();
            if(taskAudits != null){
                updatedAudits.addAll(taskAudits);
            }
            updatedAudits.add(auditEntry);
            return updatedAudits;
        });
    }

    @Override
    public List<TaskAudit> getAudit(String taskId) {
        return taskAuditMap.getOrDefault(taskId, new ArrayList<>());
    }
}
