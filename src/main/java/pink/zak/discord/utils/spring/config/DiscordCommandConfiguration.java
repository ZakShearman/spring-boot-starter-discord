package pink.zak.discord.utils.spring.config;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pink.zak.discord.utils.discord.DiscordCommandBackend;

@Configuration
@RequiredArgsConstructor
public class DiscordCommandConfiguration {
    private final ApplicationContext applicationContext;
    private final JdaConfiguration jdaConfiguration;
    private final JDA jda;

    @Bean
    public DiscordCommandBackend discordCommandBackend() {
        Guild guild = this.jda.getGuildById(this.jdaConfiguration.getGuildId());
        return new DiscordCommandBackend(this.applicationContext, this.jda, guild);
    }
}
