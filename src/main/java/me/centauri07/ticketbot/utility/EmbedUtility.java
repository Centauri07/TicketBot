package me.centauri07.ticketbot.utility;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Date;

@UtilityClass
public class EmbedUtility {
    public EmbedBuilder main(String title, String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(message);
        embedBuilder.setFooter("Proxima Centauri");
        embedBuilder.setTimestamp(new Date().toInstant());
        return embedBuilder;
    }

    public EmbedBuilder error(String title, String message) {
        return main(title, message).setColor(Color.RED);
    }

    public EmbedBuilder success(String title, String message) {
        return main(title, message).setColor(Color.GREEN);
    }
}
