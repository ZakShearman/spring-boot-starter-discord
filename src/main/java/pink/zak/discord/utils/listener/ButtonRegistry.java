package pink.zak.discord.utils.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ButtonRegistry {
    private final Map<String, Consumer<ButtonInteractionEvent>> buttonMap = new ConcurrentHashMap<>();

    @EventListener(ButtonInteractionEvent.class)
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Consumer<ButtonInteractionEvent> consumer = this.buttonMap.get(event.getComponentId());
        if (consumer != null)
            consumer.accept(event);
    }

    public ButtonRegistry registerButton(Button button, Consumer<ButtonInteractionEvent> consumer) {
        return this.registerButton(button.getId(), consumer);
    }

    public ButtonRegistry registerButton(String buttonId, Consumer<ButtonInteractionEvent> consumer) {
        this.buttonMap.put(buttonId, consumer);

        return this;
    }

    public void unregisterButtons(Button... buttons) {
        for (Button button : buttons)
            this.buttonMap.remove(button.getId());
    }
}
