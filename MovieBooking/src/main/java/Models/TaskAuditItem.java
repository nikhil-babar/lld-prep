package Models;

import lombok.Builder;

@Builder
public class TaskAuditItem {
    private final String changedField;
    private final Object previousValue;
    private final Object currentValue;
}
