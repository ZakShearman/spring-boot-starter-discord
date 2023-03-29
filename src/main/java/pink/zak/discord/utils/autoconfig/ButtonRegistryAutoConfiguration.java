package pink.zak.discord.utils.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pink.zak.discord.utils.listener.ButtonRegistry;

@Configuration
public class ButtonRegistryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ButtonRegistry buttonRegistry() {
        return new ButtonRegistry();
    }
}
