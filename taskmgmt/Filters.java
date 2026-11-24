package taskmgmt;

import java.util.function.Predicate;

/** New filter = one new static method. Filters are combined with AND by the service. */
public final class Filters {
    private Filters() {}

    public static Predicate<Task> assignee(String userId) {
        return t -> t.getAssignee() != null && t.getAssignee().id().equals(userId);
    }
    public static Predicate<Task> status(Status status) { return t -> t.getStatus() == status; }
    public static Predicate<Task> priority(Priority p) { return t -> t.getPriority() == p; }
    public static Predicate<Task> label(String label) { return t -> t.getLabels().contains(label); }
}
