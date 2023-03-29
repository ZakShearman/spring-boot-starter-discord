package pink.zak.discord.utils.discord.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public interface AutoCompletable {

    void onAutoComplete(CommandAutoCompleteInteractionEvent event);
}
