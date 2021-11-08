package me.centauri07.ticketbot.form.models;

import me.centauri07.ticketbot.TicketBot;
import me.centauri07.ticketbot.form.Form;
import me.centauri07.ticketbot.form.FormField;
import me.centauri07.ticketbot.form.FormModel;
import me.centauri07.ticketbot.ticket.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketForm extends FormModel {
    @Override
    public <T> void onSessionFinish(Form<T> form) {
        Ticket.createTicket(form.member, form.sessionFields);
    }

    public static List<FormField<?>> fieldsOf(String entry) {
        List<FormField<?>> fields = new ArrayList<>();

        TicketBot.getInstance().getConfiguration().model.entries.stream()
                .filter(ticketModel -> ticketModel.name.equalsIgnoreCase(entry)).findFirst().ifPresent(ticket -> ticket.fields.forEach(field -> fields.add(new FormField<>(field.name, field.required, null, null))));

        return fields;
    }
}
