package me.centauri07.ticketbot.listeners;

import me.centauri07.ticketbot.form.Form;
import me.centauri07.ticketbot.form.models.TicketForm;
import me.centauri07.ticketbot.utility.ButtonUtility;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EntryButtonClickListener extends ListenerAdapter {
    private static Map<Long, String> entryMap = new HashMap<>();
    public static String getEntry(long id) {
        return entryMap.get(id);
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getButton().getId().startsWith("TE")) {
            event.deferEdit().setActionRows(ButtonUtility.disableButton(event.getMessage())).queue();
            Form.createSession(
                    new TicketForm(),
                    event.getMember().getUser().openPrivateChannel().complete(), event.getMember(),
                    TicketForm.fieldsOf(event.getButton().getId().split("-")[1])
            );
            entryMap.put(event.getMember().getIdLong(),
                    event.getButton().getId().split("-")[1]);
        }
    }
}
