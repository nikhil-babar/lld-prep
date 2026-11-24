package taskmgmt;

import java.time.Instant;

/** One change to one task. Values are plain (assignee is stored as user id) so history is a stable record. */
public record TaskEvent(Task task, Field field, Object oldValue, Object newValue, User by, Instant at) {
    @Override
    public String toString() {
        if (field == Field.CREATED) return "created by " + by.id();
        return field.name().toLowerCase() + ": " + oldValue + " -> " + newValue + " by " + by.id();
    }
}
