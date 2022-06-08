package pink.zak.discord.utils.time;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Time {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss");

    public static String formatAsDateTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.UTC).format(Time.DATE_FORMAT);
    }

    public static String format(Duration duration) {
        return format(duration.toMillis());
    }

    public static String format(long millis) {
        int seconds = (int) Math.floor((double) millis / 1000);
        if (millis >= TimeIdentifier.MONTH.getMilliseconds()) {
            return formatMonthsWeeks(seconds);
        }
        if (millis >= TimeIdentifier.WEEK.getMilliseconds()) {
            return formatWeeksDays(seconds);
        }
        if (millis >= TimeIdentifier.DAY.getMilliseconds()) {
            return formatDaysHours(seconds);
        }
        if (millis >= TimeIdentifier.HOUR.getMilliseconds()) {
            return formatHoursMinutes(seconds);
        }
        if (millis >= TimeIdentifier.MINUTE.getMilliseconds()) {
            return formatMinutesSeconds(seconds);
        }
        if (seconds != 1) {
            return seconds + " seconds";
        }
        return seconds + " second";
    }

    private static String formatMonthsWeeks(int seconds) {
        int months = getMonths(seconds);
        int remainderSeconds = seconds - months * 2592000;
        int weeks = getWeeks(remainderSeconds);
        StringBuilder builder = new StringBuilder(months + " month");
        if (months != 1) {
            builder.append("s");
        }
        if (weeks == 0) {
            return builder.toString();
        }
        builder.append(" ").append(weeks).append(" week");
        if (weeks != 1) {
            builder.append("s");
        }
        return builder.toString();
    }

    private static String formatWeeksDays(int seconds) {
        int weeks = getWeeks(seconds);
        int remainderSeconds = seconds - weeks * 604800;
        int days = getDays(remainderSeconds);
        StringBuilder builder = new StringBuilder(weeks + " week");
        if (weeks != 1) {
            builder.append("s");
        }
        if (days == 0) {
            return builder.toString();
        }
        builder.append(" ").append(days).append(" day");
        if (days != 1) {
            builder.append("s");
        }
        return builder.toString();
    }

    private static String formatDaysHours(int seconds) {
        int days = getDays(seconds);
        int remainderSeconds = seconds - days * 86400;
        int hours = getHours(remainderSeconds);
        StringBuilder builder = new StringBuilder(days + " day");
        if (days != 1) {
            builder.append("s");
        }
        if (hours == 0) {
            return builder.toString();
        }
        builder.append(" ").append(hours).append(" hour");
        if (hours != 1) {
            builder.append("s");
        }
        return builder.toString();
    }

    private static String formatHoursMinutes(int seconds) {
        int hours = getHours(seconds);
        int remainderSeconds = seconds - hours * 3600;
        int minutes = getMinutes(remainderSeconds);
        StringBuilder builder = new StringBuilder(hours + " hour");
        if (hours != 1) {
            builder.append("s");
        }
        if (minutes == 0) {
            return builder.toString();
        }
        builder.append(" ").append(minutes).append(" minute");
        if (minutes != 1) {
            builder.append("s");
        }
        return builder.toString();
    }

    private static String formatMinutesSeconds(int seconds) {
        int minutes = getMinutes(seconds);
        int remainderSeconds = seconds - minutes * 60;
        StringBuilder builder = new StringBuilder(minutes + " minute");
        if (minutes != 1) {
            builder.append("s");
        }
        if (remainderSeconds == 0) {
            return builder.toString();
        }
        builder.append(" ").append(remainderSeconds).append(" second");
        if (remainderSeconds != 1) {
            builder.append("s");
        }
        return builder.toString();
    }

    public static long parse(String input) {
        if (!input.contains(" ")) {
            return parseSingle(input);
        }
        long total = 0;
        for (String single : input.split(" ")) {
            total += parseSingle(single);
        }
        return total;
    }

    public static long parseSingle(String input) {
        if (input.startsWith("-")) {
            return -parseSingle(input.substring(1));
        }
        StringBuilder identifierBuilder = new StringBuilder();
        StringBuilder amountBuilder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            Character charAt = input.charAt(i);
            if (!Character.isDigit((charAt))) {
                identifierBuilder.append(charAt);
            } else {
                amountBuilder.append(charAt);
            }
        }
        TimeIdentifier identifier = TimeIdentifier.match(identifierBuilder.toString());
        if (identifier == null) {
            return -1;
        }
        long amount;
        try {
            amount = amountBuilder.toString().isEmpty() ? 1 : Long.parseLong(amountBuilder.toString());
        } catch (NumberFormatException ignored) {
            return -1;
        }
        return identifier.getMilliseconds(amount);
    }

    private static int getMonths(double seconds) {
        return (int) Math.floor(seconds / 2592000);
    }

    private static int getWeeks(double seconds) {
        return (int) Math.floor(seconds / 604800);
    }

    private static int getDays(double seconds) {
        return (int) Math.floor(seconds / 86400);
    }

    private static int getHours(double seconds) {
        return (int) Math.floor(seconds / 3600);
    }

    private static int getMinutes(double seconds) {
        return (int) Math.floor(seconds / 60);
    }
}
