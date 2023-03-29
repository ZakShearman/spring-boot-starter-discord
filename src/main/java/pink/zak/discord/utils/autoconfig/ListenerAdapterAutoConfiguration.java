package pink.zak.discord.utils.autoconfig;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

// keeping this as some people will still use ListenerAdapter over Spring's event system
@AutoConfiguration
@AutoConfigureAfter(JdaAutoConfiguration.class)
public class ListenerAdapterAutoConfiguration {

    public ListenerAdapterAutoConfiguration(@NotNull ListenerAdapter[] listenerAdapters, JDA jda) {
        for (ListenerAdapter listenerAdapter : listenerAdapters) {
            jda.addEventListener(listenerAdapter);
        }
    }
}
