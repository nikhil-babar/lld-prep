package taskmgmt;

/** Rules: assignee hears about assignment and status changes; creator hears when it reaches DONE. */
public class NotificationListener implements TaskListener {
    private final Notifier notifier;

    public NotificationListener(Notifier notifier) { this.notifier = notifier; }

    @Override
    public void onEvent(TaskEvent e) {
        Task task = e.task();
        if (e.field() == Field.ASSIGNEE) {
            notifier.send(task.getAssignee(), task.getId() + " assigned to you");
        } else if (e.field() == Field.STATUS) {
            if (task.getAssignee() != null)
                notifier.send(task.getAssignee(), task.getId() + ": " + e.oldValue() + " -> " + e.newValue());
            if (e.newValue() == Status.DONE)
                notifier.send(task.getCreator(), task.getId() + " is DONE");
        }
    }
}
