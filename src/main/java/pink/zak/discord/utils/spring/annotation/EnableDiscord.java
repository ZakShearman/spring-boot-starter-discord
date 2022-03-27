package pink.zak.discord.utils.spring.annotation;

import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Import;
import pink.zak.discord.utils.spring.config.JdaConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

@EnableDiscordCommands
@EnableDiscordListeners

@Import({JdaConfiguration.class})
public @interface EnableDiscord {

    GatewayIntent[] requiredIntents() default {};
}
