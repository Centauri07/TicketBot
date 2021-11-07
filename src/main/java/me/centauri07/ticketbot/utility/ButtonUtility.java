package me.centauri07.ticketbot.utility;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ButtonUtility {
    public List<ActionRow> disableButton(Message message) {
        List<ActionRow> components = new ArrayList<>(message.getActionRows());
        for (Button button : message.getButtons()) {
            ComponentLayout.updateComponent(components, button.getId(), button.asDisabled());
        }

        return components;
    }
}
