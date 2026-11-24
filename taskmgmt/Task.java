package taskmgmt;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Plain data holder. Getters are public, mutators are package-private: only TaskService changes state. */
public class Task {
    private final String id;
    private final String parentId;          // null for top-level tasks
    private final User creator;
    private final Instant createdAt;
    private final List<String> subtaskIds = new ArrayList<>();
    private int subtaskSeq = 0;             // counter, so ids stay unique even after deletes

    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private Set<String> labels;
    private User assignee;
    private Status status = Status.TODO;
    private Instant updatedAt;

    Task(String id, String parentId, User creator, String title, String description,
         Priority priority, LocalDate dueDate, Set<String> labels, Instant now) {
        this.id = id;
        this.parentId = parentId;
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.labels = new LinkedHashSet<>(labels);
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getId() { return id; }
    public String getParentId() { return parentId; }
    public boolean isSubtask() { return parentId != null; }
    public User getCreator() { return creator; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public Set<String> getLabels() { return Collections.unmodifiableSet(labels); }
    public User getAssignee() { return assignee; }
    public Status getStatus() { return status; }
    public List<String> getSubtaskIds() { return Collections.unmodifiableList(subtaskIds); }

    void setTitle(String v) { title = v; }
    void setDescription(String v) { description = v; }
    void setPriority(Priority v) { priority = v; }
    void setDueDate(LocalDate v) { dueDate = v; }
    void setLabels(Set<String> v) { labels = new LinkedHashSet<>(v); }
    void setAssignee(User v) { assignee = v; }
    void setStatus(Status v) { status = v; }
    void setUpdatedAt(Instant v) { updatedAt = v; }
    String nextSubtaskId() { return id + "." + (++subtaskSeq); }
    void addSubtask(String subtaskId) { subtaskIds.add(subtaskId); }
    void removeSubtask(String subtaskId) { subtaskIds.remove(subtaskId); }

    @Override
    public String toString() {
        return id + " [" + status + ", " + priority + ", due=" + dueDate
                + ", assignee=" + (assignee == null ? "-" : assignee.id()) + "] " + title;
    }
}
