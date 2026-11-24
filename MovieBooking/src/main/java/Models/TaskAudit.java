package Models;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class TaskAudit extends Identifiable {
    private final LocalDateTime updatedAt;
    private final List<TaskAuditItem> auditItems;
    private final User updatedBy;
}
