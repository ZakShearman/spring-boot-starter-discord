package pink.zak.discord.utils.types;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class NumberUtils {

    public static boolean isNumerical(String input) {
        return isNumerical(input, Integer.MAX_VALUE);
    }

    public static boolean isLikelyLong(String input) {
        return isNumerical(input, 20);
    }

    public static boolean isLikelyInteger(String input) {
        return isNumerical(input, 10);
    }

    public static boolean isNumerical(String input, int maxLength) {
        if (input == null || input.isEmpty() || input.length() > maxLength) {
            return false;
        }
        for (Character character : input.toCharArray()) {
            if (!Character.isDigit(character)) {
                return false;
            }
        }
        return true;
    }

    public static int parseInt(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static double parseDouble(String input, double defaultValue) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static long parseLong(String input, long defaultValue) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static @NotNull BigInteger getRandomBigInteger(@NotNull BigInteger max) {
        BigInteger randomNumber;
        do {
            randomNumber = new BigInteger(max.bitLength(), ThreadLocalRandom.current());
        } while (randomNumber.compareTo(max) >= 0);
        return randomNumber.add(BigInteger.ONE); // Added to make inclusive
    }

    public static int getPercentage(int current, int max) {
        return (int) ((((float) current) / max) * 100);
    }
}
