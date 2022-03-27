package pink.zak.discord.utils.discord.command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public interface BotCommand extends BotCommandExecutor {

    @NotNull CommandData createCommandData();
}
