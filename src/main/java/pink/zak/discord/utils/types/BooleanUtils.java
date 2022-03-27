package pink.zak.discord.utils.types;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class BooleanUtils {

    public static boolean parseBoolean(@NotNull String input) {
        String inputLower = input.toLowerCase();
        return !input.isEmpty() && (inputLower.equals("true") || inputLower.equals("yes"));
    }

    public static boolean isBoolean(@NotNull String input) {
        String inputLower = input.toLowerCase();
        return inputLower.equals("false") || inputLower.equals("true") || inputLower.equals("yes") || input.equals("no");
    }
}
