package me.centauri07.ticketbot;

import com.github.stefan9110.dcm.CommandManagerAPI;
import com.github.stefan9110.dcm.command.ParentCommand;
import lombok.Getter;
import lombok.SneakyThrows;
import me.centauri07.ticketbot.commands.CloseTicketCommand;
import me.centauri07.ticketbot.commands.CreateTicketCommand;
import me.centauri07.ticketbot.configuration.Configuration;
import me.centauri07.ticketbot.configuration.models.ConfigModel;
import me.centauri07.ticketbot.configuration.models.FieldModel;
import me.centauri07.ticketbot.configuration.models.TicketModel;
import me.centauri07.ticketbot.listeners.EntryButtonClickListener;
import me.centauri07.ticketbot.listeners.form.FormListener;
import me.centauri07.ticketbot.listeners.form.OptionalFieldListener;
import me.centauri07.ticketbot.ticket.Ticket;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.io.File;

public class TicketBot {
    @Getter
    private static TicketBot instance;
    @Getter
    private JDA jda;
    @Getter
    private final File dataFolder = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
    @Getter
    private final Configuration<ConfigModel> configuration = new Configuration<>(dataFolder, "configuration", new ConfigModel());

    @SneakyThrows
    public void enable() {
        instance = this;

        if (configuration.model.entries.stream().noneMatch(ticketModel -> ticketModel.name.equals("Support Ticket"))) {
            TicketModel ticketModel = new TicketModel();
            ticketModel.name = "Support Ticket";

            FieldModel fieldModel = new FieldModel();
            fieldModel.name = "`Why did you open this ticket?`";
            fieldModel.required = true;

            ticketModel.fields.add(fieldModel);

            configuration.model.entries.add(ticketModel);
            configuration.save();
        }
        
        jda = JDABuilder.createDefault(configuration.model.token)
                .build().awaitReady();

        CommandManagerAPI.registerAPI(jda, "tb!");

        jda.addEventListener(
                new OptionalFieldListener(),
                new FormListener(),
                new EntryButtonClickListener()
        );

        registerCommand(new CreateTicketCommand().command());
        registerCommand(new CloseTicketCommand().command());

        for (Guild guild : jda.getGuildCache()) {
            CommandManagerAPI.getAPI().updateSlashCommands(guild);
        }

        jda.getPresence().setActivity(Activity.watching(Ticket.getTickets().size() + " tickets"));
    }

    private void registerCommand(ParentCommand command) {
        CommandManagerAPI.getAPI().registerCommand(command);
    }
}
