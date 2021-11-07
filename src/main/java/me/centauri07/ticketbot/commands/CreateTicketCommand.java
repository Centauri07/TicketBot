package me.centauri07.ticketbot.commands;

import com.github.stefan9110.dcm.builder.CommandBuilder;
import com.github.stefan9110.dcm.command.ParentCommand;
import com.github.stefan9110.dcm.manager.executor.SlashExecutor;
import com.github.stefan9110.dcm.manager.executor.reply.InteractionResponse;
import me.centauri07.ticketbot.TicketBot;
import me.centauri07.ticketbot.form.Form;
import me.centauri07.ticketbot.ticket.Ticket;
import me.centauri07.ticketbot.utility.EmbedUtility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CreateTicketCommand extends SlashExecutor {
    @Override
    public @NotNull InteractionResponse reply(Member member, String[] strings, SlashCommandEvent slashCommandEvent) {
        if (Ticket.of(member) == null &&
                !Form.hasSession(member.getIdLong()) &&
                !Form.hasSession(member.getIdLong()))
            return InteractionResponse.of(EmbedUtility.success(
                    null,
                    "Retrieving entries..."
            ).build()).setEphemeral();

        return InteractionResponse.of(
                EmbedUtility.error("Interaction Failed",
                        "Please try again when you are not in another session or do not have an existing ticket."
                ).build()).setEphemeral();
    }

    @Override
    public void execute(Member member, String[] args, SlashCommandEvent event, InteractionHook hook) {
        if (Ticket.of(member) == null &&
                !Form.hasSession(member.getIdLong()) &&
                !Form.hasSession(member.getIdLong())) {
            List<Button> buttons = new ArrayList<>();
            TicketBot.getInstance().getConfiguration().model.entries.forEach(ticketModel ->
                    buttons.add(Button.primary("TE-" + ticketModel.name, ticketModel.name)));
            hook.editOriginalEmbeds(EmbedUtility.success("Ticket Entries", "Please select an entry").build())
                    .setActionRow(buttons).queue();
        }
    }

    public ParentCommand command() {
        return (ParentCommand) CommandBuilder
                .create("createticket")
                .setDescription("Create a ticket.")
                .setUsage("/createticket")
                .setCommandExecutor(this)
                .build(true);
    }
}
