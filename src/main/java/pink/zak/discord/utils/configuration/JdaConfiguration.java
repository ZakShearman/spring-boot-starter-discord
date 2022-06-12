package pink.zak.discord.utils.configuration;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties("spring.discord")
public class JdaConfiguration {
    private String token;
    private OnlineStatus onlineStatus = OnlineStatus.ONLINE;
    private Set<CacheFlag> cacheFlags = new HashSet<>();
    private long guildId = -1L;
}
