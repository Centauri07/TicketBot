package me.centauri07.ticketbot.form;

import me.centauri07.ticketbot.utility.EmbedUtility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Form<T> {
    private static final List<Form<?>> FORMS = new ArrayList<>();
    private static final List<Form<?>> ACKNOWLEDGED_FORM = new ArrayList<>();

    public static Form<?> getSession(Long id) {
        return FORMS.stream().filter(session -> session.member.getIdLong() == id).findFirst().orElse(null);
    }

    public static boolean hasSession(Long id) {
        return getSession(id) != null;
    }

    public static void removeSession(Long id) {
        FORMS.removeIf(session -> session.member.getIdLong() == id);
    }

    public static <T> Form<T> createSession(T sessionModel, MessageChannel sessionChannel, Member member) {
        if (!hasSession(member.getIdLong())) {
            FORMS.add(new Form<>(sessionModel, sessionChannel, member));
            getSession(member.getIdLong()).startSession();
            return (Form<T>) getSession(member.getIdLong());
        }

        return null;
    }

    public static <T> Form<T> createSession(T sessionModel, MessageChannel sessionChannel, Member member, List<FormField<?>> fields) {
        if (!hasSession(member.getIdLong())) {
            FORMS.add(new Form<T>(sessionModel, fields, sessionChannel, member));
            getSession(member.getIdLong()).startSession();
            return (Form<T>) getSession(member.getIdLong());
        }

        return null;
    }

    public static Form<?> getAcknowledgedSession(Long id) {
        return ACKNOWLEDGED_FORM.stream().filter(session -> session.member.getIdLong() == id).findFirst().orElse(null);
    }

    public static boolean hasAcknowledgedSession(Long id) {
        return getAcknowledgedSession(id) != null;
    }

    public static void removeAcknowledgedSession(Long id) {
        ACKNOWLEDGED_FORM.removeIf(session -> session.member.getIdLong() == id);
    }

    public List<FormField<?>> sessionFields = new ArrayList<>();

    public boolean idle;
    
    public final T src;
    public final MessageChannel sessionChannel;
    public final Member member;

    public final Button confirmButton = Button.success("session-confirm", "✅ Confirm");
    public final Button cancelButton = Button.danger("session-cancel", "❌ Cancel");

    private ScheduledFuture<?> timer;

    private Form(T src, MessageChannel sessionChannel, Member member) {
        if (!(src instanceof FormModel))
            throw new IllegalArgumentException("Type should extend " + FormModel.class.getName());

        this.src = src;
        this.sessionChannel = sessionChannel;
        this.member = member;

        for (Field field : src.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(me.centauri07.ticketbot.form.Field.class)) {
                sessionFields.add(
                        new FormField<>(field.getAnnotation(me.centauri07.ticketbot.form.Field.class), field, src)
                );
            }
        }

        FORMS.add(this);

        startTimer();
    }

    private Form(T src, List<FormField<?>> fields, MessageChannel sessionChannel, Member member) {
        this.src = src;
        this.sessionChannel = sessionChannel;
        this.member = member;

        sessionFields = fields;

        FORMS.add(this);

        startTimer();
    }

    public FormField<?> getUnacknowledgedField() {
        FormField<?> formField =
                sessionFields.stream().filter(field -> !field.isAcknowledged).findFirst().orElse(null);

        while (formField != null &&
                !formField.field.getType().isAssignableFrom(String.class) &&
                        !formField.field.getType().isPrimitive() &&
                        !formField.field.getType().isAssignableFrom(Collection.class) &&
                        !formField.field.getType().isAssignableFrom(Color.class)
        ) {
            List<FormField<?>> childFormFields =
                    formField.subFormFields.stream()
                            .filter(typeField -> !typeField.isAcknowledged)
                            .collect(Collectors.toList());

            if (childFormFields.isEmpty()) {
                formField.isAcknowledged = true;

                formField = sessionFields.stream().filter(field -> !field.isAcknowledged).findFirst()
                        .orElse(null);
            } else {
                formField = childFormFields.get(0);
            }
        }

        if (formField != null) {
            if (!formField.required && !formField.chosen && !formField.isAcknowledged) {
                if (!idle) {
                    sessionChannel.sendMessageEmbeds(EmbedUtility.main(
                            null,
                            "Do you want to add " + formField.name
                    ).setColor(Color.GREEN).build()).setActionRow(formField.yes, formField.no).queue();
                    idle = true;
                }
            }
        }

        return formField;
    }

    public void fireSession(Message message) {
        if (!idle) {
            FormField<?> formField = getUnacknowledgedField();

            if (!formField.required && !formField.chosen && !formField.isAcknowledged)
                return;

            if (formField.set(message.getContentRaw()) == null) {
                message.getChannel().sendMessageEmbeds(
                        EmbedUtility.error(null, "Invalid input!").build()
                ).queue();
                return;
            }

            formField.isAcknowledged = true;

            resetTimer();

            formField = getUnacknowledgedField();

            if (formField == null) {
                sessionChannel.sendMessageEmbeds(EmbedUtility.success(
                        "Session Finished",
                        "Please click ✅ to submit the session, and react with ❌ to cancel."
                ).build()).setActionRow(confirmButton, cancelButton).queue();

                removeSession(member.getIdLong());
                ACKNOWLEDGED_FORM.add(this);

                return;
            }

            if (!formField.required && !formField.chosen && !formField.isAcknowledged)
                return;

            sessionChannel.sendMessageEmbeds(
                            EmbedUtility.success(null, "Enter " + formField.name).build()
                    )
                    .queue();
        }
    }

    public void startSession() {
        FormField<?> formField = getUnacknowledgedField();

        if (formField == null) {
            sessionChannel.sendMessageEmbeds(EmbedUtility.success(
                    "Session Finished",
                    "Please click ✅ to submit the session, and react with ❌ to cancel."
            ).build()).setActionRow(confirmButton, cancelButton).queue();

            removeSession(member.getIdLong());
            ACKNOWLEDGED_FORM.add(this);

            return;
        }

        if (!formField.required && !formField.chosen && !formField.isAcknowledged)
            return;

        sessionChannel.sendMessageEmbeds(EmbedUtility.success(null, "Enter " + formField.name)
                .build()).queue();
    }

    public void startTimer() {
        timer = Executors.newSingleThreadScheduledExecutor().schedule(
                () -> {
                    removeSession(member.getIdLong());
                    ((FormModel) src).onSessionExpire(this);
                }, 3, TimeUnit.MINUTES
        );
    }

    public void stopTimer() {
        timer.cancel(true);
    }

    public void resetTimer() {
        stopTimer();
        startTimer();
    }

}
