package pink.zak.discord.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileReader;

@UtilityClass
public class JsonUtils {

    @SneakyThrows
    public static JsonElement fileToJson(File file) {
        return JsonParser.parseReader(new FileReader(file));
    }
}
