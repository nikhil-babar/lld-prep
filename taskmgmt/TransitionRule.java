package taskmgmt;

/** Extra guard on a status change beyond the allowed-transitions table. Throw TaskException to reject. */
@FunctionalInterface
public interface TransitionRule {
    void check(Task task, Status target);
}
