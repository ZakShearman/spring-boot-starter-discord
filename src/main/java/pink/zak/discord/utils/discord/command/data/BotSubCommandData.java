package pink.zak.discord.utils.discord.command.data;

import org.jetbrains.annotations.NotNull;
import pink.zak.discord.utils.discord.command.BotSubCommand;
import pink.zak.discord.utils.discord.command.RestrictableCommand;

public record BotSubCommandData(@NotNull BotSubCommand command,
                                boolean admin, @NotNull String subCommandId,
                                @NotNull String subCommandGroup) implements RestrictableCommand {
}
