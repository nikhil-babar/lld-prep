package taskmgmt;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * All business logic lives here.
 *
 * Concurrency: every public method is synchronized on the service. Validation, mutation and event
 * emission therefore happen atomically, so two threads can never both pass the same transition check.
 * One lock (rather than one per task) is deliberate: the subtask rule couples a parent to its children,
 * and per-task locks would need lock ordering. Upgrade path is described in DESIGN.md.
 */
public class TaskService {
    private final UserRegistry users;
    private final Clock clock;                                   // injected so "overdue" is testable
    private final Map<String, Task> tasks = new LinkedHashMap<>(); // O(1) lookup by id
    private final ActivityLog activityLog = new ActivityLog();
    private final List<TaskListener> listeners = new ArrayList<>();
    private final List<TransitionRule> rules = new ArrayList<>();
    private int taskSeq = 0;

    public TaskService(UserRegistry users, Clock clock) {
        this.users = users;
        this.clock = clock;
        listeners.add(activityLog);

        rules.add((task, target) -> {
            if (task.getStatus() == Status.TODO && task.getAssignee() == null)
                throw new TaskException(task.getId() + " has no assignee");
        });
        rules.add((task, target) -> {
            if (target != Status.DONE) return;
            List<String> open = task.getSubtaskIds().stream()
                    .filter(id -> tasks.get(id).getStatus() != Status.DONE)
                    .toList();
            if (!open.isEmpty())
                throw new TaskException(task.getId() + " has incomplete subtasks (" + String.join(", ", open) + ")");
        });
    }

    public synchronized void addListener(TaskListener listener) { listeners.add(listener); }

    // ---------------------------------------------------------------- create / subtasks / delete

    public synchronized Task createTask(String byId, NewTask req) {
        User by = users.get(byId);
        requireTitle(req.title());
        if (req.dueDate() != null && req.dueDate().isBefore(LocalDate.now(clock)))
            throw new TaskException("Due date " + req.dueDate() + " is in the past");
        User assignee = req.assigneeId() == null ? null : users.get(req.assigneeId()); // validate before creating

        Task task = new Task("T" + (++taskSeq), null, by, req.title(),
                req.description() == null ? "" : req.description(),
                req.priority() == null ? Priority.MEDIUM : req.priority(),
                req.dueDate(), req.labels() == null ? Set.of() : req.labels(), clock.instant());
        tasks.put(task.getId(), task);
        emit(task, Field.CREATED, null, task.getTitle(), by);
        if (assignee != null) doAssign(task, assignee, by);
        return task;
    }

    public synchronized Task addSubtask(String parentId, String byId, String title) {
        Task parent = get(parentId);
        User by = users.get(byId);
        requireTitle(title);
        if (parent.isSubtask()) throw new TaskException(parentId + " is a subtask; subtasks cannot have subtasks");
        if (parent.getStatus() == Status.DONE) throw new TaskException("Cannot add a subtask to DONE task " + parentId);

        Task sub = new Task(parent.nextSubtaskId(), parent.getId(), by, title, "",
                parent.getPriority(), parent.getDueDate(), Set.of(), clock.instant());
        tasks.put(sub.getId(), sub);
        parent.addSubtask(sub.getId());
        emit(sub, Field.CREATED, null, title, by);
        return sub;
    }

    public synchronized void delete(String taskId, String byId) {
        Task task = get(taskId);
        users.get(byId);
        for (String subId : task.getSubtaskIds()) tasks.remove(subId);   // cascade
        if (task.isSubtask()) get(task.getParentId()).removeSubtask(taskId);
        tasks.remove(taskId);
    }

    // ---------------------------------------------------------------- update / assign / status

    public synchronized void update(String taskId, String byId, TaskUpdate u) {
        Task t = get(taskId);
        User by = users.get(byId);
        if (u.title() != null) requireTitle(u.title());               // validate everything before mutating

        if (u.title() != null && !u.title().equals(t.getTitle())) {
            String old = t.getTitle(); t.setTitle(u.title()); emit(t, Field.TITLE, old, u.title(), by);
        }
        if (u.description() != null && !u.description().equals(t.getDescription())) {
            String old = t.getDescription(); t.setDescription(u.description()); emit(t, Field.DESCRIPTION, old, u.description(), by);
        }
        if (u.priority() != null && u.priority() != t.getPriority()) {
            Priority old = t.getPriority(); t.setPriority(u.priority()); emit(t, Field.PRIORITY, old, u.priority(), by);
        }
        if (u.dueDate() != null && !u.dueDate().equals(t.getDueDate())) {
            LocalDate old = t.getDueDate(); t.setDueDate(u.dueDate()); emit(t, Field.DUE_DATE, old, u.dueDate(), by);
        }
        if (u.labels() != null && !u.labels().equals(t.getLabels())) {
            Set<String> old = Set.copyOf(t.getLabels()); t.setLabels(u.labels()); emit(t, Field.LABELS, old, u.labels(), by);
        }
    }

    public synchronized void assign(String taskId, String assigneeId, String byId) {
        Task task = get(taskId);
        User assignee = users.get(assigneeId);
        User by = users.get(byId);
        doAssign(task, assignee, by);
    }

    public synchronized void changeStatus(String taskId, Status target, String byId) {
        Task task = get(taskId);
        User by = users.get(byId);
        if (target == null) throw new TaskException("Target status is required");
        if (!task.getStatus().canMoveTo(target))
            throw new TaskException("Invalid transition " + task.getStatus() + " -> " + target);
        for (TransitionRule rule : rules) rule.check(task, target);

        Status old = task.getStatus();
        task.setStatus(target);
        emit(task, Field.STATUS, old, target, by);
    }

    // ---------------------------------------------------------------- queries

    public synchronized Task getTask(String taskId) { return get(taskId); }

    /** Filters are ANDed. A scan is fine here; see DESIGN.md for how to index it. */
    public synchronized List<Task> listTasks(List<Predicate<Task>> filters, SortBy sortBy, Order order) {
        Predicate<Task> all = filters.stream().reduce(t -> true, Predicate::and);
        return tasks.values().stream().filter(all).sorted(sortBy.comparator(order)).toList();
    }

    /** Past due date and not DONE. */
    public synchronized List<Task> listOverdue() {
        LocalDate today = LocalDate.now(clock);
        Predicate<Task> overdue = t -> t.getStatus() != Status.DONE
                && t.getDueDate() != null && t.getDueDate().isBefore(today);
        return listTasks(List.of(overdue), SortBy.DUE_DATE, Order.ASC);
    }

    public synchronized List<TaskEvent> getHistory(String taskId) {
        get(taskId);                                                   // unknown task -> error
        return activityLog.getHistory(taskId);
    }

    // ---------------------------------------------------------------- helpers

    private Task get(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) throw new TaskException("Task not found: " + taskId);
        return task;
    }

    private void doAssign(Task task, User assignee, User by) {
        if (assignee.equals(task.getAssignee()))
            throw new TaskException(task.getId() + " is already assigned to " + assignee.id());
        String old = task.getAssignee() == null ? null : task.getAssignee().id();
        task.setAssignee(assignee);
        emit(task, Field.ASSIGNEE, old, assignee.id(), by);
    }

    private static void requireTitle(String title) {
        if (title == null || title.isBlank()) throw new TaskException("Title is required");
    }

    /** One failing listener must not break the others or the already-applied change. */
    private void emit(Task task, Field field, Object oldValue, Object newValue, User by) {
        Instant at = clock.instant();
        task.setUpdatedAt(at);
        TaskEvent event = new TaskEvent(task, field, oldValue, newValue, by, at);
        for (TaskListener listener : listeners) {
            try {
                listener.onEvent(event);
            } catch (RuntimeException ex) {
                System.err.println("Listener " + listener.getClass().getSimpleName() + " failed: " + ex);
            }
        }
    }
}
