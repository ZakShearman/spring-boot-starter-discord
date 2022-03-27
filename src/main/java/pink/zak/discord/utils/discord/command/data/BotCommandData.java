package pink.zak.discord.utils.discord.command.data;

import org.jetbrains.annotations.NotNull;
import pink.zak.discord.utils.discord.command.BotCommand;
import pink.zak.discord.utils.discord.command.RestrictableCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public record BotCommandData(@NotNull BotCommand command,
                             Map<String, BotSubCommandData> subCommands,
                             String name, boolean admin) implements RestrictableCommand {

    public static @NotNull Map<String, BotSubCommandData> computeSubCommands(@NotNull Collection<BotSubCommandData> subCommands) {
        Map<String, BotSubCommandData> map = new HashMap<>();

        for (BotSubCommandData subCommand : subCommands) {
            if (!subCommand.subCommandGroup().isEmpty())
                map.put(subCommand.subCommandGroup() + "/" + subCommand.subCommandId(), subCommand);
            else
                map.put(subCommand.subCommandId(), subCommand);
        }

        return map;
    }

}
