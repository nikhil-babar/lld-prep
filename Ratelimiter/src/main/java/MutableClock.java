import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class MutableClock extends Clock {

    private Instant instant;
    private final ZoneId zone;

    public MutableClock(Instant instant){
        this.instant = instant;
        this.zone = ZoneId.of("UTC");
    }

    public MutableClock(Instant instant, ZoneId zone){
        this.zone = zone;
        this.instant = instant;
    }

    public void advance(long millis){
        this.instant = instant.plusMillis(millis);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new MutableClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
