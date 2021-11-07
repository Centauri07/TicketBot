package me.centauri07.ticketbot.listeners.form;

import me.centauri07.ticketbot.form.Form;
import me.centauri07.ticketbot.form.FormField;
import me.centauri07.ticketbot.utility.ButtonUtility;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OptionalFieldListener extends ListenerAdapter {
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        Form<?> form = Form.getSession(event.getUser().getIdLong());

        if (form == null) return;

        FormField<?> formField = form.getUnacknowledgedField();

        if (formField == null) return;

        if (form.idle) {
            if (!formField.required && !formField.chosen && !formField.isAcknowledged) {
                if (event.getButton().equals(formField.yes)) {
                    event.deferEdit().setActionRows(ButtonUtility.disableButton(event.getMessage())).queue();
                    form.resetTimer();
                    formField.chosen = true;
                    form.idle = false;
                    form.startSession();
                } else if (event.getButton().equals(formField.no)) {
                    event.deferEdit().setActionRows(ButtonUtility.disableButton(event.getMessage())).queue();
                    form.resetTimer();
                    formField.isAcknowledged = true;
                    form.startSession();
                }
            }
        }
    }
}
