package pink.zak.discord.utils.discord.command.data;

import org.jetbrains.annotations.NotNull;
import pink.zak.discord.utils.discord.command.BotSubCommand;

public record BotSubCommandData(@NotNull BotSubCommand command, @NotNull String subCommandId,
                                @NotNull String subCommandGroup) {
}
