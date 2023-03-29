package pink.zak.discord.utils;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.emoji.Emoji;

@UtilityClass
public class BotConstants {
    public static final String BACK_ARROW = "⬅";
    public static final String FORWARD_ARROW = "➡";

    public static final Emoji BACK_EMOJI = Emoji.fromUnicode(BotConstants.BACK_ARROW);
    public static final Emoji FORWARD_EMOJI = Emoji.fromUnicode(BotConstants.FORWARD_ARROW);
}
