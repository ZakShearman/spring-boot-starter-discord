package pink.zak.discord.utils.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.annotations.BotSubCommandComponent;
import pink.zak.discord.utils.discord.command.AutoCompletable;
import pink.zak.discord.utils.discord.command.BotCommand;
import pink.zak.discord.utils.discord.command.BotCommandExecutor;
import pink.zak.discord.utils.discord.command.BotSubCommand;
import pink.zak.discord.utils.discord.command.data.BotCommandData;
import pink.zak.discord.utils.discord.command.data.BotSubCommandData;
import pink.zak.discord.utils.discord.command.data.stored.SlashCommandInfo;
import pink.zak.discord.utils.discord.command.data.stored.SlashCommandInfoImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class DiscordCommandBackend {
    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(DiscordCommandBackend.class);

    private final @NotNull JDA jda;
    private final @NotNull SlashCommandDetailsService slashCommandDetailsService;
    private final @NotNull ExecutorService executor = ForkJoinPool.commonPool();

    private final @NotNull Map<String, BotCommandData> commandsByName = new HashMap<>();
    private final @NotNull Map<Long, BotCommandData> commandsById = new HashMap<>();

    public DiscordCommandBackend(@NotNull ApplicationContext applicationContext, @NotNull JDA jda, @Nullable Guild guild, @NotNull SlashCommandDetailsService slashCommandDetailsService) {
        this.jda = jda;
        this.slashCommandDetailsService = slashCommandDetailsService;

        Map<Class<? extends BotCommand>, Set<BotSubCommandData>> subCommandReferences = new HashMap<>();

        for (BotSubCommand subCommand : applicationContext.getBeansOfType(BotSubCommand.class).values()) {
            BotSubCommandComponent subCommandComponent = subCommand.getClass().getAnnotation(BotSubCommandComponent.class);
            BotSubCommandData commandData = new BotSubCommandData(subCommand, subCommandComponent.subCommandId(), subCommandComponent.subCommandGroupId());

            Set<BotSubCommandData> commandSet = subCommandReferences.computeIfAbsent(subCommandComponent.parent(), k -> new HashSet<>());
            commandSet.add(commandData);
        }

        for (BotCommand command : applicationContext.getBeansOfType(BotCommand.class).values()) {
            BotCommandComponent commandComponent = command.getClass().getAnnotation(BotCommandComponent.class);
            Map<String, BotSubCommandData> mappedSubCommands = BotCommandData.computeSubCommands(subCommandReferences.getOrDefault(command.getClass(), new HashSet<>()));

            BotCommandData commandData = new BotCommandData(command, mappedSubCommands, commandComponent.name());
            this.commandsByName.put(commandData.name(), commandData);
        }

        LOGGER.info("Registered all in-memory commands: {}", this.commandsByName.values());

        this.init(guild);
    }

    public void init(@Nullable Guild guild) {
        // load existing commands so we don't have to update every time
        Set<? extends SlashCommandInfo> loadedCommands = this.slashCommandDetailsService.loadCommands();

        if (loadedCommands == null) {
            List<Command> createdCommands = this.createNewCommands(guild);

            createdCommands.forEach(command -> {
                BotCommandData matchedCommand = this.commandsByName.get(command.getName());
                this.commandsById.put(command.getIdLong(), matchedCommand);
                LOGGER.info("Bound created command {} to ID {}", matchedCommand.name(), command.getIdLong());
            });

            Set<SlashCommandInfo> simplifiedCommandData = createdCommands.stream().map(command -> new SlashCommandInfoImpl(command.getName(), command.getIdLong())).collect(Collectors.toUnmodifiableSet());

            this.slashCommandDetailsService.saveCommands(simplifiedCommandData);
        } else {
            LOGGER.info("Loaded Commands {}", loadedCommands);
            for (SlashCommandInfo commandInfo : loadedCommands) {
                BotCommandData command = this.commandsByName.get(commandInfo.getName());
                this.commandsById.put(commandInfo.getId(), command);
                LOGGER.debug("Bound loaded command {} to ID {}", commandInfo.getName(), commandInfo.getId());
            }
        }
    }

    private List<Command> createNewCommands(@Nullable Guild guild) {
        Set<CommandData> createdData = this.commandsByName.values().stream().map(commandData -> commandData.command().createCommandData()).collect(Collectors.toSet());
        LOGGER.info("Created data {}", createdData);

        if (createdData.isEmpty()) {
            LOGGER.warn("No commands were created, this is likely due to no commands being registered");
            return List.of();
        }

        List<Command> createdCommands;
        if (guild == null) createdCommands = this.jda.updateCommands().addCommands(createdData).complete();
        else createdCommands = guild.updateCommands().addCommands(createdData).complete();

        LOGGER.info("Created Commands {}", createdCommands);

        return createdCommands;
    }

    @EventListener(SlashCommandInteractionEvent.class)
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            BotCommandExecutor commandExecutor = this.determineExecutor(event.getCommandIdLong(), event.getSubcommandName(), event.getSubcommandGroup());
            if (commandExecutor == null) return;

            commandExecutor.onExecute(event.getMember(), event);
        }).exceptionally(throwable -> {
            LOGGER.error("Error occurred whilst handling slash command event", throwable);
            return null;
        });
    }

    @EventListener(CommandAutoCompleteInteractionEvent.class)
    public void onSlashCommandAutoComplete(CommandAutoCompleteInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            BotCommandExecutor commandExecutor = this.determineExecutor(event.getCommandIdLong(), event.getSubcommandName(), event.getSubcommandGroup());
            if (commandExecutor == null) return;

            if (!(commandExecutor instanceof AutoCompletable autoCompletable)) {
                LOGGER.info("Received autocomplete event for non-autocompletable command {}", event.getCommandString());
                return;
            }

            autoCompletable.onAutoComplete(event);
        }).exceptionally(throwable -> {
            LOGGER.error("Error occurred whilst handling autocomplete event", throwable);
            return null;
        });
    }

    private @Nullable BotCommandExecutor determineExecutor(long commandId, @Nullable String subCommandName, @Nullable String subCommandGroup) {
        String commandPath = subCommandGroup == null ? subCommandName : subCommandGroup + "/" + subCommandName;

        BotCommandData command = this.commandsById.get(commandId);
        if (command == null) {
            LOGGER.error("Command not found with ID {} and path {}", commandId, commandPath);
            return null;
        }
        if (subCommandName == null) return command.command();

        if (subCommandGroup != null) subCommandName = subCommandGroup + "/" + subCommandName;

        BotSubCommandData subCommand = command.subCommands().get(subCommandName);
        if (subCommand == null) {
            LOGGER.error("SubCommand not found with ID {} and path {}", subCommandName, commandPath);
            return null;
        }
        return subCommand.command();
    }
}