package pink.zak.discord.utils.discord;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pink.zak.discord.utils.discord.command.data.stored.SlashCommandInfo;

import java.util.Set;

public interface SlashCommandDetailsService {

    @Nullable Set<? extends SlashCommandInfo> loadCommands();

    void saveCommands(@NotNull Set<SlashCommandInfo> slashCommandInfos);
}
