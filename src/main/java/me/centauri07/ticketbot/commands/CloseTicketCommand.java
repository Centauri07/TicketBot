package me.centauri07.ticketbot.commands;

import com.github.stefan9110.dcm.builder.CommandBuilder;
import com.github.stefan9110.dcm.command.ParentCommand;
import com.github.stefan9110.dcm.manager.executor.SlashExecutor;
import com.github.stefan9110.dcm.manager.executor.reply.InteractionResponse;
import me.centauri07.ticketbot.ticket.Ticket;
import me.centauri07.ticketbot.utility.EmbedUtility;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

public class CloseTicketCommand extends SlashExecutor {
    @Override
    public @NotNull InteractionResponse reply(Member member, String[] strings, SlashCommandEvent slashCommandEvent) {
        if (Ticket.of(slashCommandEvent.getTextChannel()) != null) {
            Ticket ticket = Ticket.of(member);

            if (ticket.member.getIdLong() == member.getIdLong() || member.hasPermission(Permission.MESSAGE_MANAGE)) {
                Ticket.closeTicket(member);
                return InteractionResponse.of(
                        EmbedUtility
                                .success("Ticket Closed", member.getAsMention() + "'s Ticket has been closed")
                                .build()
                );
            }
        }
        return InteractionResponse.of(EmbedUtility.error(
                null,
                "You cannot close that ticket!"
        ).build());
    }

    public ParentCommand command() {
        return (ParentCommand) CommandBuilder
                .create("closeticket")
                .setCommandExecutor(this)
                .setUsage("/closeticket")
                .setDescription("Close a ticket.")
                .build(true);
    }
}
