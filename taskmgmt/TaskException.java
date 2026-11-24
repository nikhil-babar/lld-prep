package taskmgmt;

/** Single error type: every invalid input/state produces one with a clear message. */
public class TaskException extends RuntimeException {
    public TaskException(String message) { super(message); }
}
