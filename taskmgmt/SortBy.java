package taskmgmt;

import java.time.LocalDate;
import java.util.Comparator;

/** New sort criterion = one new constant. */
public enum SortBy {
    DUE_DATE(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.<LocalDate>naturalOrder()))),
    PRIORITY(Comparator.comparing(Task::getPriority)),
    CREATED_AT(Comparator.comparing(Task::getCreatedAt));

    private final Comparator<Task> comparator;

    SortBy(Comparator<Task> comparator) { this.comparator = comparator; }

    public Comparator<Task> comparator(Order order) {
        return order == Order.ASC ? comparator : comparator.reversed();
    }
}
