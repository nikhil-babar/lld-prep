package Enums;

import java.util.Optional;

public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    IN_REVIEW,
    IN_TEST,
    TEST_COMPLETE,
    DONE;

    public static Optional<TaskStatus> getNextState(TaskStatus current){
        if(current == TaskStatus.DONE) return Optional.empty();
        boolean hasCurrentArrived = false;
        for(TaskStatus status : TaskStatus.values()){
            if(hasCurrentArrived){
                return Optional.of(status);
            }
            if(status == current){
                hasCurrentArrived = true;
            }
        }
        return Optional.empty();
    }

    public static TaskStatus getDefaultStatus() {
        return TaskStatus.TODO;
    }
}
