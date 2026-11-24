package taskmgmt;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Driver only: no business logic here. Covers the 6 required demo scenarios. */
public class Main {

    public static void main(String[] args) throws Exception {
        DemoClock clock = new DemoClock(Instant.parse("2026-11-01T09:00:00Z"));
        UserRegistry users = new UserRegistry();
        TaskService svc = new TaskService(users, clock);
        svc.addListener(new NotificationListener(new ConsoleNotifier()));

        users.register("u1", "Asha");
        users.register("u2", "Ravi");

        // ------------------------------------------------------------------
        section("1. Happy path + invalid transitions (the sample scenario)");
        attempt("create T1 (HIGH, due 2026-11-05)", () -> svc.createTask("u1",
                new NewTask("Fix login bug", "Users cannot log in", Priority.HIGH,
                        LocalDate.of(2026, 11, 5), Set.of("bug"), null)));
        attempt("T1 -> IN_PROGRESS (no assignee)", () -> svc.changeStatus("T1", Status.IN_PROGRESS, "u1"));
        attempt("assign T1 to u2", () -> svc.assign("T1", "u2", "u1"));
        attempt("T1 -> IN_PROGRESS", () -> svc.changeStatus("T1", Status.IN_PROGRESS, "u2"));
        attempt("T1 -> DONE (skips review)", () -> svc.changeStatus("T1", Status.DONE, "u2"));
        attempt("add subtask 'Write tests' to T1", () -> svc.addSubtask("T1", "u1", "Write tests"));
        attempt("T1 -> IN_REVIEW", () -> svc.changeStatus("T1", Status.IN_REVIEW, "u2"));
        attempt("T1 -> DONE (subtask open)", () -> svc.changeStatus("T1", Status.DONE, "u2"));
        attempt("unknown task / unknown user", () -> svc.changeStatus("T99", Status.DONE, "u2"));
        attempt("", () -> svc.assign("T1", "ghost", "u1"));
        attempt("past due date on create", () -> svc.createTask("u1",
                new NewTask("Old", "", Priority.LOW, LocalDate.of(2026, 10, 1), null, null)));
        System.out.println("  list(assignee=u2, status=IN_REVIEW, sort=DUE_DATE ASC):");
        print(svc.listTasks(List.of(Filters.assignee("u2"), Filters.status(Status.IN_REVIEW)),
                SortBy.DUE_DATE, Order.ASC));

        // ------------------------------------------------------------------
        section("2. Subtask rule: finish the subtask, then the parent can be DONE");
        attempt("subtask of a subtask", () -> svc.addSubtask("T1.1", "u1", "nested"));
        attempt("assign T1.1 to u2", () -> svc.assign("T1.1", "u2", "u1"));
        attempt("T1.1 -> IN_PROGRESS", () -> svc.changeStatus("T1.1", Status.IN_PROGRESS, "u2"));
        attempt("T1.1 -> IN_REVIEW", () -> svc.changeStatus("T1.1", Status.IN_REVIEW, "u2"));
        attempt("T1.1 -> DONE", () -> svc.changeStatus("T1.1", Status.DONE, "u2"));
        attempt("T1 -> DONE", () -> svc.changeStatus("T1", Status.DONE, "u2"));
        attempt("add subtask to DONE parent", () -> svc.addSubtask("T1", "u1", "late"));

        // ------------------------------------------------------------------
        section("3. Filtering, sorting, overdue, update, delete");
        svc.createTask("u1", new NewTask("Refactor auth", "", Priority.MEDIUM,
                LocalDate.of(2026, 11, 10), Set.of("backend"), "u2"));               // T2 (assigned at creation)
        svc.createTask("u1", new NewTask("Update docs", "", Priority.LOW,
                LocalDate.of(2026, 11, 3), Set.of("docs"), "u2"));                   // T3
        svc.createTask("u2", new NewTask("Prod incident", "", Priority.CRITICAL,
                LocalDate.of(2026, 11, 4), Set.of("bug", "backend"), "u1"));         // T4
        svc.createTask("u2", new NewTask("No due date", "", Priority.HIGH, null, null, null)); // T5

        System.out.println("  assignee=u2, sort PRIORITY DESC:");
        print(svc.listTasks(List.of(Filters.assignee("u2")), SortBy.PRIORITY, Order.DESC));
        System.out.println("  label=backend, sort DUE_DATE ASC:");
        print(svc.listTasks(List.of(Filters.label("backend")), SortBy.DUE_DATE, Order.ASC));
        System.out.println("  status=TODO + priority=HIGH:");
        print(svc.listTasks(List.of(Filters.status(Status.TODO), Filters.priority(Priority.HIGH)),
                SortBy.CREATED_AT, Order.ASC));
        System.out.println("  all, sort CREATED_AT DESC:");
        print(svc.listTasks(List.of(), SortBy.CREATED_AT, Order.DESC));

        clock.advanceDays(5);   // today is now 2026-11-06
        System.out.println("  overdue on 2026-11-06 (T1 is DONE so it is excluded):");
        print(svc.listOverdue());

        attempt("update T2 title + priority", () -> svc.update("T2", "u1",
                new TaskUpdate("Refactor auth module", null, Priority.HIGH, null, null)));
        svc.addSubtask("T4", "u2", "Write postmortem");
        attempt("delete T4 (cascades to T4.1)", () -> svc.delete("T4", "u2"));
        attempt("get T4.1 after cascade", () -> svc.getTask("T4.1"));

        // ------------------------------------------------------------------
        section("4. History");
        System.out.println("  T1:");
        svc.getHistory("T1").forEach(e -> System.out.println("    " + e));
        System.out.println("  T2:");
        svc.getHistory("T2").forEach(e -> System.out.println("    " + e));

        // ------------------------------------------------------------------
        section("5. Concurrent updates");
        runConcurrentDemo(svc);
    }

    /** Two races on one task: status moves and reassignments, from many threads at once. */
    private static void runConcurrentDemo(TaskService svc) throws Exception {
        Task race = svc.createTask("u1", new NewTask("Race target", "", Priority.MEDIUM, null, null, "u1"));
        String id = race.getId();

        // Race A: 8 threads all try TODO -> IN_PROGRESS. Exactly one may win.
        AtomicInteger won = new AtomicInteger(), rejected = new AtomicInteger();
        runTogether(8, i -> {
            try {
                svc.changeStatus(id, Status.IN_PROGRESS, i % 2 == 0 ? "u1" : "u2");
                won.incrementAndGet();
            } catch (TaskException e) {
                rejected.incrementAndGet();
            }
        });
        System.out.println("  Race A (8 threads, TODO -> IN_PROGRESS): won=" + won + " rejected=" + rejected
                + "  final=" + svc.getTask(id).getStatus());

        // Race B: 40 threads reassign between u1 and u2. History must form an unbroken old->new chain.
        AtomicInteger assigned = new AtomicInteger();
        runTogether(40, i -> {
            try {
                svc.assign(id, i % 2 == 0 ? "u1" : "u2", "u1");
                assigned.incrementAndGet();
            } catch (TaskException ignored) { /* "already assigned" is a legal rejection */ }
        });
        List<TaskEvent> assignEvents = svc.getHistory(id).stream().filter(e -> e.field() == Field.ASSIGNEE).toList();
        boolean chainOk = true;
        for (int i = 1; i < assignEvents.size(); i++)
            chainOk &= Objects.equals(assignEvents.get(i).oldValue(), assignEvents.get(i - 1).newValue());
        System.out.println("  Race B (40 reassignments): applied=" + assigned + " assign-events=" + assignEvents.size()
                + "  history chain consistent=" + chainOk);
        long statusEvents = svc.getHistory(id).stream().filter(e -> e.field() == Field.STATUS).count();
        System.out.println("  status events recorded=" + statusEvents + " (expected 1)");
    }

    private static void runTogether(int threads, java.util.function.IntConsumer body) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        for (int i = 0; i < threads; i++) {
            final int n = i;
            pool.submit(() -> {
                try { start.await(); } catch (InterruptedException e) { return; }
                body.accept(n);
            });
        }
        start.countDown();
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }

    // ---------------------------------------------------------------- tiny output helpers

    private static void section(String title) { System.out.println("\n=== " + title + " ==="); }

    private static void attempt(String what, Runnable action) {
        try {
            action.run();
            if (!what.isEmpty()) System.out.println("  OK    " + what);
        } catch (TaskException e) {
            System.out.println("  ERROR " + (what.isEmpty() ? "" : what + " -> ") + e.getMessage());
        }
    }

    private static void print(List<Task> tasks) {
        if (tasks.isEmpty()) System.out.println("    (none)");
        tasks.forEach(t -> System.out.println("    " + t));
    }

    /** Deterministic clock for the demo. Ticks 1s per read so created-at timestamps differ. */
    static class DemoClock extends Clock {
        private Instant now;
        DemoClock(Instant start) { this.now = start; }
        synchronized void advanceDays(long days) { now = now.plus(Duration.ofDays(days)); }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public synchronized Instant instant() { now = now.plusSeconds(1); return now; }
    }
}
