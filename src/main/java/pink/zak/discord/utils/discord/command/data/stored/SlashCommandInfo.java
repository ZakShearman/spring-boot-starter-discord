package pink.zak.discord.utils.discord.command.data.stored;

import org.jetbrains.annotations.NotNull;

public interface SlashCommandInfo {

    long getId();

    @NotNull String getName();
}
