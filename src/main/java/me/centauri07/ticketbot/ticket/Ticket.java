package me.centauri07.ticketbot.ticket;

import me.centauri07.ticketbot.form.FormField;
import me.centauri07.ticketbot.utility.EmbedUtility;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Ticket {
    private static final List<Ticket> TICKETS = new ArrayList<>();

    public static boolean createTicket(Member member, List<FormField<?>> formFields) {
        if (of(member) == null) {
            TICKETS.add(
                    new Ticket(member, member.getGuild().createTextChannel("ticket-" + member.getUser().getName()
                                    .toLowerCase()).complete(), formFields));
        }

        return false;
    }

    public static boolean closeTicket(Member member) {
        if (of(member) != null) {
            Ticket ticket = of(member);
            TICKETS.remove(ticket);
            ticket.textChannel.delete().queue();
        }
        return false;
    }

    public static Ticket of(Member member) {
        return TICKETS.stream().filter(ticket -> ticket.member.getIdLong() == member.getIdLong()).findFirst().orElse(null);
    }

    public static Ticket of(TextChannel textChannel) {
        return TICKETS.stream().filter(ticket -> ticket.textChannel.getIdLong() == textChannel.getIdLong()).findFirst().orElse(null);
    }

    public static Collection<Ticket> getTickets() {
        return TICKETS;
    }

    public final Member member;
    public final TextChannel textChannel;

    public Ticket(Member member, TextChannel textChannel, List<FormField<?>> formFields) {
        this.member = member;
        this.textChannel = textChannel;

        EmbedBuilder embedBuilder = EmbedUtility.success(
                member.getEffectiveName() + "'s Ticket",
                null
        );

        formFields.forEach(formField -> embedBuilder.addField(formField.name, formField.get().toString(), false));

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void closeTicket() {
        closeTicket(member);
    }
}