package pink.zak.discord.utils.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.annotations.BotSubCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;
import pink.zak.discord.utils.discord.command.BotSubCommand;
import pink.zak.discord.utils.discord.command.RestrictableCommand;
import pink.zak.discord.utils.discord.command.data.BotCommandData;
import pink.zak.discord.utils.discord.command.data.BotSubCommandData;
import pink.zak.discord.utils.listener.SlashCommandListener;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class DiscordCommandBackend implements SlashCommandListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordCommandBackend.class);
    private static final Path BASE_PATH = Path.of("");

    private final @NotNull JDA jda;
    private final ExecutorService executor = ForkJoinPool.commonPool();

    private final Map<String, BotCommandData> loadedCommands = new HashMap<>();
    private final Map<Long, BotCommandData> slashCommands = new HashMap<>(); // todo better name? tf is this

    public DiscordCommandBackend(@NotNull ApplicationContext applicationContext, @NotNull JDA jda, @Nullable Guild guild) {
        this.jda = jda;

        Map<Class<? extends BotCommand>, Set<BotSubCommandData>> subCommandReferences = new HashMap<>();

        for (BotSubCommand subCommand : applicationContext.getBeansOfType(BotSubCommand.class).values()) {
            BotSubCommandComponent subCommandComponent = subCommand.getClass().getAnnotation(BotSubCommandComponent.class);
            BotSubCommandData commandData = new BotSubCommandData(subCommand, subCommandComponent.admin(), subCommandComponent.subCommandId(), subCommandComponent.subCommandGroupId());

            Set<BotSubCommandData> commandSet = subCommandReferences.computeIfAbsent(subCommandComponent.parent(), k -> new HashSet<>());
            commandSet.add(commandData);
        }

        for (BotCommand command : applicationContext.getBeansOfType(BotCommand.class).values()) {
            BotCommandComponent commandComponent = command.getClass().getAnnotation(BotCommandComponent.class);
            Map<String, BotSubCommandData> mappedSubCommands = BotCommandData.computeSubCommands(subCommandReferences.getOrDefault(command.getClass(), new HashSet<>()));

            BotCommandData commandData = new BotCommandData(command, mappedSubCommands, commandComponent.name(), commandComponent.admin());
            this.loadedCommands.put(commandData.name(), commandData);
        }

        LOGGER.info("Registered all in-memory commands: {}", this.loadedCommands.values());

        this.init(guild);
    }

    public void init(@Nullable Guild guild) {
        // load existing commands so we don't have to update every time
        Set<SlashCommandFileHandler.SlashCommandInfo> loadedCommands = SlashCommandFileHandler.loadSlashCommands(BASE_PATH);

        if (loadedCommands == null) {
            List<Command> createdCommands = this.createNewCommands(guild);

            createdCommands.forEach(command -> {
                BotCommandData matchedCommand = this.loadedCommands.get(command.getName());
                this.slashCommands.put(command.getIdLong(), matchedCommand);
                LOGGER.info("Bound created command {} to ID {}", matchedCommand.name(), command.getIdLong());
            });

            SlashCommandFileHandler.saveSlashCommands(BASE_PATH, createdCommands);
        } else {
            LOGGER.info("Loaded Commands {}", loadedCommands);
            for (SlashCommandFileHandler.SlashCommandInfo commandInfo : loadedCommands) {
                BotCommandData command = this.loadedCommands.get(commandInfo.name());
                this.slashCommands.put(commandInfo.id(), command);
                LOGGER.debug("Bound loaded command {} to ID {}", commandInfo.name(), commandInfo.id());
            }
        }

        // permissions - could be improved to only do when necessary but whatever
//        Map<String, Set<CommandPrivilege>> privileges = new HashMap<>();
//        for (BotCommand command : this.commands.values()) {
//            if (!command.isAdmin()) {
//                for (BotSubCommand genericCommand : command.getSubCommands().values()) {
//                    if (genericCommand.isAdmin()) {
//                        privileges.put(command.getCommand().getId(), Set.of(CommandPrivilege.enableUser(240721111174610945L))); // todo especially this in prod needs to be changed
//                    }
//                }
//            } else {
//                privileges.put(command.getCommand().getId(), Set.of(CommandPrivilege.enableUser(240721111174610945L)));
//            }
//        }
//        if (!privileges.isEmpty())
//            guild.updateCommandPrivileges(privileges).queue(success -> {
//                LOGGER.info("Set restricted permissions for " + privileges.size() + " commands");
//            });
    }

    private List<net.dv8tion.jda.api.interactions.commands.Command> createNewCommands(@Nullable Guild guild) {
        Set<CommandData> createdData = this.loadedCommands.values()
            .stream()
            .map(commandData -> commandData.command().createCommandData())
            .collect(Collectors.toSet());
        LOGGER.info("Created data {}", createdData);

        List<Command> createdCommands;
        if (guild == null)
            createdCommands = this.jda.updateCommands().addCommands(createdData).complete();
        else
            createdCommands = guild.updateCommands().addCommands(createdData).complete();

        LOGGER.info("Created Commands {}", createdCommands);

        return createdCommands;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Member sender = event.getMember();
        CompletableFuture.runAsync(() -> {
            long commandId = event.getCommandIdLong();
            BotCommandData command = this.slashCommands.get(commandId);
            if (command == null) {
                LOGGER.error("Command not found with ID {} and path {}", commandId, event.getCommandPath());
                return;
            }
            if (!this.memberHasAccess(command, event, sender)) {
                return;
            }
            String subCommandName = event.getSubcommandName();
            String subCommandGroup = event.getSubcommandGroup();
            if (subCommandName == null) {
                this.executeCommand(command, sender, event);
                return;
            }

            if (subCommandGroup != null)
                subCommandName = subCommandGroup + "/" + subCommandName;

            BotSubCommandData subCommand = command.subCommands().get(subCommandName);
            if (subCommand == null) {
                LOGGER.error("SubCommand not found with ID {} and path {}", subCommandName, event.getCommandPath());
                return;
            }
            if (this.memberHasAccess(subCommand, event, sender)) {
                this.executeSlashCommand(subCommand, sender, event);
            }
        }, this.executor).exceptionally(ex -> {
            LOGGER.error("Error from CommandBase input \"{}\"", event.getCommandString(), ex);
            return null;
        });
    }

    private void executeCommand(@NotNull BotCommandData commandData, @NotNull Member sender, @NotNull SlashCommandInteractionEvent event) {
        commandData.command().onExecute(sender, event);
    }

    private void executeSlashCommand(@NotNull BotSubCommandData commandData, @NotNull Member sender, @NotNull SlashCommandInteractionEvent event) {
        commandData.command().onExecute(sender, event);
    }

    private boolean memberHasAccess(RestrictableCommand command, SlashCommandInteractionEvent event, Member member) {
        if (command.admin() && member.getRoles().stream().anyMatch(role -> role.getIdLong() == 914245973759393822L)) { // todo DONT DO STATIC ROLE role
            event.reply("no permission dud :|").queue();
            return false;
        }
        return true;
    }
}