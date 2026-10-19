package xox.labvorty.vortylib.utilities;

public class TimeBuilder {
    private final int time;

    public TimeBuilder() {
        this(0);
    }

    private TimeBuilder(int ticks) {
        this.time = ticks;
    }

    public TimeBuilder ticks(int ticks) {
        return new TimeBuilder(this.time + ticks);
    }

    public TimeBuilder seconds(int seconds) {
        return this.ticks(seconds * 20);
    }

    public TimeBuilder minutes(int minutes) {
        return this.seconds(minutes * 60);
    }

    public TimeBuilder hours(int hours) {
        return this.minutes(hours * 60);
    }

    public int build() {
        return this.time;
    }
}