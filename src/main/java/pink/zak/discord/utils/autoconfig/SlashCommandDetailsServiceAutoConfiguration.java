package pink.zak.discord.utils.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import pink.zak.discord.utils.discord.SlashCommandDetailsService;
import pink.zak.discord.utils.discord.SlashCommandFileHandler;

@AutoConfiguration
public class SlashCommandDetailsServiceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SlashCommandDetailsService slashCommandDetailsService() {
        return new SlashCommandFileHandler();
    }
}
