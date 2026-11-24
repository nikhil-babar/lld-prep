package taskmgmt;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum Status {
    TODO, IN_PROGRESS, IN_REVIEW, DONE;

    // Workflow is DATA: a new status or transition = one line here.
    private static final Map<Status, Set<Status>> ALLOWED = new EnumMap<>(Status.class);
    static {
        ALLOWED.put(TODO, EnumSet.of(IN_PROGRESS));
        ALLOWED.put(IN_PROGRESS, EnumSet.of(IN_REVIEW));
        ALLOWED.put(IN_REVIEW, EnumSet.of(IN_PROGRESS, DONE));
        ALLOWED.put(DONE, EnumSet.noneOf(Status.class));
    }

    public boolean canMoveTo(Status next) {
        return ALLOWED.getOrDefault(this, Set.of()).contains(next);
    }
}
