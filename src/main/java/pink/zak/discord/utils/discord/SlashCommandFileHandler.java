package pink.zak.discord.utils.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pink.zak.discord.utils.discord.command.data.stored.SlashCommandInfo;
import pink.zak.discord.utils.discord.command.data.stored.SlashCommandInfoImpl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class SlashCommandFileHandler implements SlashCommandDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandFileHandler.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path DATA_PATH = Path.of("data/command-data.json");

    @Override
    public @Nullable Set<? extends SlashCommandInfo> loadCommands() {
        FileReader reader;
        try {
            reader = new FileReader(DATA_PATH.toFile());
        } catch (FileNotFoundException ex) {
            return null;
        }
        JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

        Set<SlashCommandInfoImpl> commands = new HashSet<>();
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            commands.add(new SlashCommandInfoImpl(jsonObject.get("name").getAsString(), jsonObject.get("id").getAsLong()));
        }
        return commands;
    }

    @Override
    public void saveCommands(@NotNull Set<SlashCommandInfo> commands) {
        if (Files.notExists(DATA_PATH)) {
            try {
                Path parent = DATA_PATH.getParent();
                if (Files.notExists(parent)) Files.createDirectories(parent);
                Files.createFile(DATA_PATH);
            } catch (IOException ex) {
                LOGGER.error("Error creating slash command file", ex);
            }
        }

        try (Writer writer = Files.newBufferedWriter(DATA_PATH)) {
            JsonArray jsonArray = new JsonArray();

            for (SlashCommandInfo command : commands)
                jsonArray.add(this.createCommandObject(command));

            GSON.toJson(jsonArray, writer);
        } catch (IOException ex) {
            LOGGER.error("Error writing slash commands", ex);
        }
    }

    private @NotNull JsonObject createCommandObject(@NotNull SlashCommandInfo command) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", command.getName());
        jsonObject.addProperty("id", command.getId());

        return jsonObject;
    }
}
