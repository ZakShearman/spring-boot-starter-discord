package pink.zak.discord.utils.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

// todo this is not currently implemented
@Component
public class ButtonRegistry extends ListenerAdapter {
    private final Map<String, Consumer<ButtonInteractionEvent>> buttonMap = new ConcurrentHashMap<>();

    @Override
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
