package pink.zak.discord.utils.autoconfig;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import pink.zak.discord.utils.configuration.JdaConfiguration;
import pink.zak.discord.utils.discord.DiscordCommandBackend;
import pink.zak.discord.utils.discord.SlashCommandDetailsService;

@AutoConfiguration
@AutoConfigureAfter(JdaAutoConfiguration.class)

@RequiredArgsConstructor
public class DiscordCommandAutoConfiguration {
    private final ApplicationContext applicationContext;
    private final JdaConfiguration jdaConfiguration;
    private final SlashCommandDetailsService slashCommandDetailsService;
    private final JDA jda;

    @Bean
    @ConditionalOnMissingBean
    public DiscordCommandBackend discordCommandBackend() {
        Guild guild = this.jda.getGuildById(this.jdaConfiguration.getGuildId());
        return new DiscordCommandBackend(this.applicationContext, this.jda, guild, this.slashCommandDetailsService);
    }
}
