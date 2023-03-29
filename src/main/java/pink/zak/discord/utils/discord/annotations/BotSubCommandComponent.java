package pink.zak.discord.utils.discord.annotations;

import org.springframework.stereotype.Component;
import pink.zak.discord.utils.discord.command.BotCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface BotSubCommandComponent {

    Class<? extends BotCommand> parent();

    String subCommandId();

    String subCommandGroupId() default "";
}
