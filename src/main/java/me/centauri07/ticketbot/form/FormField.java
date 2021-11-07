package me.centauri07.ticketbot.form;

import me.centauri07.ticketbot.utility.Parser;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FormField<T> {
    public T value;

    public boolean chosen;
    public String name;
    public boolean required;

    public Button yes = Button.success("yes", "✅ Yes");
    public Button no = Button.danger("no", "❌ No");

    public FormField(Field annotationField, java.lang.reflect.Field field, Object parent) {
        this(annotationField.name(), annotationField.required(), field, parent);
    }

    public FormField(String name, boolean required, java.lang.reflect.Field field, Object parent) {
        if (field == null) {
            try {
                this.field = this.getClass().getField("value");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        if (parent == null) {
            this.parent = this;
        }

        chosen = required;
        this.name = name;
        this.required = required;

        this.field.setAccessible(true);

        if (
                !this.field.getType().isAssignableFrom(String.class) &&
                        !this.field.getType().isPrimitive() &&
                        !this.field.getType().isAssignableFrom(Collection.class) &&
                        !this.field.getType().isAssignableFrom(Color.class)
        ) {

            if (Arrays.stream(this.field.getType().getDeclaredFields()).noneMatch(typeField -> typeField.isAnnotationPresent(Field.class)))
                throw new NullPointerException("Class doesn't have a field that is annotated with" + Field.class.getName());

            for (java.lang.reflect.Field typeField : this.field.getType().getDeclaredFields()) {
                if (typeField.isAnnotationPresent(Field.class)) {
                    subFormFields.add(
                            new FormField<>(
                                    typeField.getAnnotation(Field.class),
                                    typeField,
                                    get()
                            )
                    );
                }
            }
        }
    }

    public Object parent;
    public java.lang.reflect.Field field;
    public List<FormField<?>> subFormFields = new ArrayList<>();
    public boolean isAcknowledged = false;

    public Object get() {
        try {
            return field.get(parent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(Object object) {
        if (field.getType().isPrimitive() || field.getType().isAssignableFrom(String.class)) {
            try {
                if (field.getType().isAssignableFrom(byte.class)) {
                    field.set(parent, Byte.parseByte((String) object));
                } else if (field.getType().isAssignableFrom(short.class)) {
                    field.set(parent, Short.parseShort((String) object));
                } else if (field.getType().isAssignableFrom(int.class)) {
                    field.set(parent, Integer.parseInt((String) object));
                } else if (field.getType().isAssignableFrom(long.class)) {
                    field.set(parent, Long.parseLong((String) object));
                } else if (field.getType().isAssignableFrom(float.class)) {
                    field.set(parent, Float.parseFloat((String) object));
                } else if (field.getType().isAssignableFrom(double.class)) {
                    field.set(parent, Double.parseDouble((String) object));
                } else if (field.getType().isAssignableFrom(boolean.class)) {
                    field.set(parent, Boolean.parseBoolean((String) object));
                } else if (field.getType().isAssignableFrom(char.class)) {
                    field.set(parent, ((String) object).charAt(0));
                } else if (field.getType().isAssignableFrom(String.class)) {
                    field.set(parent, object);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (field.getType().isAssignableFrom(Color.class)) {
            try {
                field.set(parent, Parser.parseColor((String) object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (field.getType().isAssignableFrom(Image.class)) {
            try {
                field.set(parent, Parser.parseImage((String) object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                field.set(parent, object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}