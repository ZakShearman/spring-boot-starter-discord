package pink.zak.discord.utils.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommandListener {

    void onSlashCommandInteraction(SlashCommandInteractionEvent event);
}
