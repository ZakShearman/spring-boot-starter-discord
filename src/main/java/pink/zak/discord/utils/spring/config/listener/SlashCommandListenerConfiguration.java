package pink.zak.discord.utils.spring.config.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import pink.zak.discord.utils.listener.SlashCommandListener;

@Configuration
public class SlashCommandListenerConfiguration {

    public SlashCommandListenerConfiguration(SlashCommandListener[] listeners, JDA jda) {
        for (SlashCommandListener listener : listeners) {
            jda.addEventListener(new ListenerAdapter() {
                @Override
                public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
                    listener.onSlashCommandInteraction(event);
                }
            });
        }
    }
}
