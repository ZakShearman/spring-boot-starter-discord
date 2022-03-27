package pink.zak.discord.utils.spring.config;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Configuration
@ConfigurationProperties("discord.jda")
public class JdaConfiguration {
    private String token;
    private OnlineStatus onlineStatus = OnlineStatus.ONLINE;
    private Set<CacheFlag> cacheFlags = new HashSet<>();
    private long guildId = -1L;

    @Bean
    @SneakyThrows
    @ConditionalOnMissingBean(JDA.class)
    public JDA jda() {
        Set<GatewayIntent> requiredIntents = this.cacheFlags
            .stream()
            .map(CacheFlag::getRequiredIntent)
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableSet());

        return JDABuilder.createDefault(this.token)
            .setStatus(this.onlineStatus)
            .enableCache(this.cacheFlags)
            .enableIntents(requiredIntents)
            .build()
            .awaitReady();
    }
}
