package pink.zak.discord.utils.spring.annotation;

import org.springframework.context.annotation.Import;
import pink.zak.discord.utils.spring.config.DiscordCommandConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

@Import(DiscordCommandConfiguration.class)
public @interface EnableDiscordCommands {
}
