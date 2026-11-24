package Models;

import Enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@ToString
public class Task extends Identifiable {
    public final User creator;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ToString.Include
    private String title;

    private String description;
    private List<String> labels;
    private User assignee;
    private TaskStatus taskStatus;
    private LocalDateTime dueDate;

    private TaskMetadata data;

    @Builder(toBuilder = true)
    public Task(TaskMetadata data, User creator){
        updateData(data);
        this.data = data;
        this.creator = creator;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.taskStatus = TaskStatus.getDefaultStatus();
    }

    public void updateData(TaskMetadata data){
        this.assignee = data.getAssignee();
        this.title = data.getTitle();
        this.description = data.getDescription();
        this.labels = data.getLabels();
        this.updatedAt = LocalDateTime.now();
        this.dueDate = data.getDueDate();
    }

    public void changeStatus(){
        Optional<TaskStatus> nextStatus = TaskStatus.getNextState(taskStatus);
        if(nextStatus.isEmpty()){
            throw new IllegalArgumentException("Task is at its final state");
        }
        taskStatus = nextStatus.get();
    }

    public TaskView toView() {
        return new TaskView(this.getId(), title, description, labels, dueDate, assignee);
    }
}
