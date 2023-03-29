package pink.zak.discord.utils.autoconfig;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;

@AutoConfiguration
@AutoConfigureAfter(JdaAutoConfiguration.class)

public class JdaEventPublisherAutoConfiguration {

    public JdaEventPublisherAutoConfiguration(JDA jda, ApplicationEventPublisher publisher) {
        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onGenericEvent(@NotNull GenericEvent event) {
                publisher.publishEvent(new PayloadApplicationEvent<>(jda, event)); // will get auto converted to PayloadApplicationEvent
            }
        });
    }
}
