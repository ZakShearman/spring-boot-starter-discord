package pink.zak.discord.utils.message;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.springframework.stereotype.Component;
import pink.zak.discord.utils.BotConstants;
import pink.zak.discord.utils.listener.ButtonRegistry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public abstract class PageableButtonEmbedMenu extends PageableMenu {
    private final ButtonRegistry buttonRegistry;
    private final ScheduledExecutorService scheduler;

    private final Map<Integer, MessageEmbed> cachedPages = new ConcurrentHashMap<>();

    private ScheduledFuture<?> scheduledFuture;

    private Button forwardButton;
    private Button backButton;

    public void sendInitialMessage(SlashCommandInteractionEvent event, boolean ephemeral) {
        MessageEmbed embed = this.createPage(super.currentPage.get());
        this.cachedPages.put(super.currentPage.get(), embed);

        if (super.maxPage > 1) {
            this.backButton = Button.primary(UUID.randomUUID().toString(), BotConstants.BACK_EMOJI);
            this.forwardButton = Button.primary(UUID.randomUUID().toString(), BotConstants.FORWARD_EMOJI);

            this.buttonRegistry.registerButton(this.backButton, this::previousPage)
                .registerButton(this.forwardButton, this::nextPage);
        }

        ReplyCallbackAction replyAction = event.replyEmbeds(embed)
            .setEphemeral(ephemeral);

        if (this.forwardButton != null)
            replyAction.addActionRow(this.forwardButton);

        replyAction.queue(sentMessage -> {
            if (super.maxPage > 1)
                this.scheduleDeletion();
        });
    }

    public abstract MessageEmbed createPage(int page);

    public void nextPage(ButtonInteractionEvent event) {
        int initialPage = this.currentPage.get();
        int newPage = super.nextPage();

        if (initialPage != newPage)
            this.drawPage(this.currentPage.get(), event);
    }

    public void previousPage(ButtonInteractionEvent event) {
        int initialPage = this.currentPage.get();
        int newPage = super.previousPage();

        if (initialPage != newPage)
            this.drawPage(this.currentPage.get(), event);
    }

    public void drawPage(int page, ButtonInteractionEvent event) {
        if (page > this.maxPage) {
            return;
        }
        MessageEmbed embed = this.cachedPages.computeIfAbsent(page, this::createPage);

        Set<Button> buttons = new HashSet<>();
        if (page < this.maxPage)
            buttons.add(this.forwardButton);
        if (page > 1)
            buttons.add(this.backButton);
        event.editMessageEmbeds(embed)
            .setActionRow(buttons).queue();
    }

    @Override
    public void drawPage(int page) {

    }

    public void scheduleDeletion() {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
        }
        this.scheduledFuture = this.scheduler.schedule(() -> this.buttonRegistry.unregisterButtons(this.forwardButton, this.backButton), 1, TimeUnit.MINUTES);
    }
}
