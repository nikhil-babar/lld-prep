package taskmgmt;

public class ConsoleNotifier implements Notifier {
    @Override
    public void send(User to, String message) {
        System.out.println("    [Notify " + to.id() + "] " + message);
    }
}
