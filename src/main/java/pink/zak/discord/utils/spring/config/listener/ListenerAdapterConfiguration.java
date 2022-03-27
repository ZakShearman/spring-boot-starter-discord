package pink.zak.discord.utils.spring.config.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerAdapterConfiguration {

    public ListenerAdapterConfiguration(@NotNull ListenerAdapter[] listenerAdapters, JDA jda) {
        for (ListenerAdapter listenerAdapter : listenerAdapters) {
            jda.addEventListener(listenerAdapter);
        }
    }
}
