package taskmgmt;

/** Observer. History and notifications are both just listeners. */
public interface TaskListener {
    void onEvent(TaskEvent event);
}
