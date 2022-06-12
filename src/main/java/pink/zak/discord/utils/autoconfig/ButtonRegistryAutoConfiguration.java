package pink.zak.discord.utils.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import pink.zak.discord.utils.listener.ButtonRegistry;

@AutoConfiguration
public class ButtonRegistryAutoConfiguration {

    @Bean
    public ButtonRegistry buttonRegistry() {
        return new ButtonRegistry();
    }
}
