package me.centauri07.ticketbot.listeners;

import me.centauri07.ticketbot.ticket.Ticket;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ShutdownListener extends ListenerAdapter {
    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        Ticket.getTickets().forEach(ticket -> ticket.closeTicket());
    }
}
