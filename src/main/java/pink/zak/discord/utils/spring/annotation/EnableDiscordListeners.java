package pink.zak.discord.utils.spring.annotation;

import org.springframework.context.annotation.Import;
import pink.zak.discord.utils.spring.config.listener.ListenerAdapterConfiguration;
import pink.zak.discord.utils.spring.config.listener.SlashCommandListenerConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

@Import({ListenerAdapterConfiguration.class, SlashCommandListenerConfiguration.class})
public @interface EnableDiscordListeners {
}
