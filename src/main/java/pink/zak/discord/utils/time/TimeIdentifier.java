package pink.zak.discord.utils.time;

public enum TimeIdentifier {

    MILLISECOND(1, 9223372036854775000L, "millisecond", "milliseconds", "ms"),
    SECOND(MILLISECOND.milliseconds * 1000, 9223372036854775L, "second", "seconds", "sec", "secs", "s"),
    MINUTE(SECOND.milliseconds * 60, 153722867000000L, "minute", "minutes", "min", "mins", "m"),
    HOUR(MINUTE.milliseconds * 60, 2562047780000L, "hour", "hours", "h", "hr", "hrs"),
    DAY(HOUR.milliseconds * 24, 106751991000L, "day", "days", "d", "ds"),
    WEEK(DAY.milliseconds * 7, 15250284400L, "week", "weeks", "w", "ws"),
    MONTH(Math.round(DAY.milliseconds * 30.42), 476571388, "month", "months", "mo", "mos"),
    YEAR(MONTH.milliseconds * 12, 1305675, "year", "years", "yr", "yrs", "y");

    private final long milliseconds;
    private final long maxAmountOf;
    private final String[] identifiers;

    TimeIdentifier(long milliseconds, long maxAmountOf, String... identifiers) {
        this.milliseconds = milliseconds;
        this.maxAmountOf = maxAmountOf;
        this.identifiers = identifiers;
    }

    public static TimeIdentifier match(String input) {
        for (TimeIdentifier identifier : TimeIdentifier.values()) {
            for (String possibility : identifier.getIdentifiers()) {
                if (possibility.equalsIgnoreCase(input)) {
                    return identifier;
                }
            }
        }
        return null;
    }

    public String[] getIdentifiers() {
        return this.identifiers;
    }

    public long getMilliseconds(long amountOfUnit) {
        if (amountOfUnit > this.maxAmountOf) {
            return this.maxAmountOf * this.milliseconds;
        }
        return amountOfUnit * this.milliseconds;
    }

    public long getMilliseconds() {
        return this.milliseconds;
    }
}
