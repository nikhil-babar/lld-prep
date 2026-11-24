package taskmgmt;

/** Delivery channel. New channel (email, Slack) = new implementation, nothing else changes. */
public interface Notifier {
    void send(User to, String message);
}
