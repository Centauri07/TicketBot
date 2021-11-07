package me.centauri07.ticketbot.form;

import me.centauri07.ticketbot.utility.EmbedUtility;

public abstract class FormModel {
    public abstract <T> void onSessionFinish(Form<T> form);
    public <T> void onSessionExpire(Form<T> form) {
        form.sessionChannel.sendMessageEmbeds(
                EmbedUtility.error("Session Expired", "Took too long to respond. Aborting session").build()
        ).queue();
    }
}
