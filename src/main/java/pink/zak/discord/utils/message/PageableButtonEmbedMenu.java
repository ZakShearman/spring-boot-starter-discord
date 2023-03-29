package pink.zak.discord.utils.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import pink.zak.discord.utils.BotConstants;
import pink.zak.discord.utils.listener.ButtonRegistry;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public abstract class PageableButtonEmbedMenu extends PageableMenu {
    private final @Nullable ScheduledExecutorService scheduler;
    private final ButtonRegistry buttonRegistry;

    private ScheduledFuture<?> scheduledFuture;

    private Button forwardButton;
    private Button backButton;

    private InteractionHook interactionHook;

    protected PageableButtonEmbedMenu(@Nullable ScheduledExecutorService scheduler, ButtonRegistry buttonRegistry) {
        this.scheduler = scheduler;
        this.buttonRegistry = buttonRegistry;
    }

    public void editInitialInteraction(InteractionHook interactionHook) {
        MessageEmbed embed = this.createPage(super.currentPage.get());

        this.backButton = Button.primary(UUID.randomUUID().toString(), BotConstants.BACK_EMOJI).asDisabled();
        this.forwardButton = Button.primary(UUID.randomUUID().toString(), BotConstants.FORWARD_EMOJI);

        this.buttonRegistry.registerButton(this.backButton, this::previousPage)
                .registerButton(this.forwardButton, this::nextPage);

        WebhookMessageEditAction<Message> replyAction = interactionHook.editOriginal("")
                .setEmbeds(embed);

        if (this.forwardButton != null) {
            replyAction.setActionRow(this.backButton, this.forwardButton);
        }

        if (super.maxPage > 1)
            this.scheduleDeletion();

        replyAction.queue();

        this.interactionHook = interactionHook;
    }

    public void sendInitialMessage(GenericCommandInteractionEvent event, boolean ephemeral) {
        MessageEmbed embed = this.createPage(super.currentPage.get());

        this.backButton = Button.primary(UUID.randomUUID().toString(), BotConstants.BACK_EMOJI).asDisabled();
        this.forwardButton = Button.primary(UUID.randomUUID().toString(), BotConstants.FORWARD_EMOJI);

        this.buttonRegistry.registerButton(this.backButton, this::previousPage)
                .registerButton(this.forwardButton, this::nextPage);

        ReplyCallbackAction replyAction = event.replyEmbeds(embed)
                .setEphemeral(ephemeral);

        if (this.forwardButton != null) {
            replyAction.addActionRow(this.backButton, this.forwardButton);
        }

        if (super.maxPage > 1)
            this.scheduleDeletion();

        replyAction.queue();

        this.interactionHook = event.getHook();
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

    @Override
    public void drawPage(int page) {
        if (page > this.maxPage) {
            return;
        }
        MessageEmbed embed = this.createPage(page);

        if (this.maxPage > 1) this.updateButtonStates(page);

        this.interactionHook.editOriginalEmbeds(embed)
                .setActionRow(this.backButton, this.forwardButton)
                .queue();
    }

    public void drawPage(int page, ButtonInteractionEvent event) {
        if (page > this.maxPage) {
            return;
        }
        MessageEmbed embed = this.createPage(page);

        if (this.maxPage > 1) this.updateButtonStates(page);

        event.editMessageEmbeds(embed)
                .setActionRow(this.backButton, this.forwardButton)
                .queue();
    }

    public void scheduleDeletion() {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
        }
        this.scheduledFuture = this.scheduler.schedule(() -> this.buttonRegistry.unregisterButtons(this.forwardButton, this.backButton), 1, TimeUnit.HOURS);
    }

    protected void updateButtonStates(int page) {
        if (page == 1) { // If page is 1, lock back button, unlock other
            if (!this.backButton.isDisabled()) this.backButton = this.backButton.asDisabled();
            if (this.forwardButton.isDisabled()) this.forwardButton = this.forwardButton.asEnabled();
        } else if (page >= this.maxPage) { // If page is max, lock forward button, unlock other
            if (!this.forwardButton.isDisabled()) this.forwardButton = this.forwardButton.asDisabled();
            if (this.backButton.isDisabled()) this.backButton = this.backButton.asEnabled();
        } else { // Unlock both buttons
            if (this.forwardButton.isDisabled()) this.forwardButton = this.forwardButton.asEnabled();
            if (this.backButton.isDisabled()) this.backButton = this.backButton.asEnabled();
        }
    }
}
