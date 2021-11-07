package me.centauri07.ticketbot.listeners.form;

import me.centauri07.ticketbot.form.Form;
import me.centauri07.ticketbot.form.FormModel;
import me.centauri07.ticketbot.utility.ButtonUtility;
import me.centauri07.ticketbot.utility.EmbedUtility;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class FormListener extends ListenerAdapter {
    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        Form<?> form = Form.getSession(event.getAuthor().getIdLong());
        if (form == null) return;
        if (!form.sessionChannel.getId().equals(event.getChannel().getId())) return;

        form.fireSession(event.getMessage());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Form<?> form = Form.getSession(event.getAuthor().getIdLong());
        if (form == null) return;
        if (!form.sessionChannel.getId().equals(event.getChannel().getId())) return;
        form.fireSession(event.getMessage());
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        Form<?> acknowledgedForm = Form.getAcknowledgedSession(event.getUser().getIdLong());

        if (acknowledgedForm == null) return;

        if (event.getButton().equals(acknowledgedForm.confirmButton)) {
            event.deferEdit().setActionRows(ButtonUtility.disableButton(event.getMessage())).queue();

            ((FormModel) acknowledgedForm.src).onSessionFinish(acknowledgedForm);
            acknowledgedForm.sessionChannel.sendMessageEmbeds(EmbedUtility.success(
                    "Session Success",
                    "Session has been finished successfully."
            ).build()).queue();

            acknowledgedForm.stopTimer();
            Form.removeAcknowledgedSession(acknowledgedForm.member.getIdLong());
        } else if (event.getButton().equals(acknowledgedForm.cancelButton)) {
            event.deferEdit().setActionRows(ButtonUtility.disableButton(event.getMessage())).queue();

            acknowledgedForm.sessionChannel.sendMessageEmbeds(EmbedUtility.error(
                    "Successfully Canceled",
                    "Session has been canceled successfully"
            ).build()).queue();

            acknowledgedForm.stopTimer();
            Form.removeAcknowledgedSession(acknowledgedForm.member.getIdLong());
        }
    }
}
