package pink.zak.discord.utils.types;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@UtilityClass
public class MapUtils {
    public static <K, V extends Comparable<? super V>> @NotNull List<Map.Entry<K, V>> sortByValue(@NotNull Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Collections.reverseOrder()));

        return list;
    }
}