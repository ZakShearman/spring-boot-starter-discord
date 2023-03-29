package pink.zak.discord.utils.autoconfig;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pink.zak.discord.utils.configuration.JdaConfiguration;

import javax.security.auth.login.LoginException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AutoConfiguration
@EnableConfigurationProperties(JdaConfiguration.class)

@RequiredArgsConstructor
public class JdaAutoConfiguration {

    private final JdaConfiguration jdaConfiguration;

    @Bean
    @ConditionalOnMissingBean
    public JDA jda() throws LoginException, InterruptedException {
        Set<GatewayIntent> requiredIntents = jdaConfiguration.getCacheFlags()
                .stream()
                .map(CacheFlag::getRequiredIntent)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());

        return JDABuilder.createDefault(jdaConfiguration.getToken())
                .setStatus(jdaConfiguration.getOnlineStatus())
                .enableCache(jdaConfiguration.getCacheFlags())
                .enableIntents(requiredIntents)
                .build()
                .awaitReady();

    }
}
