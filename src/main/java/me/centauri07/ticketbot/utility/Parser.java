package me.centauri07.ticketbot.utility;

import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

@UtilityClass
public class Parser {
    public Image parseImage(String url) {
        Image image;
        try {
            image = ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }

        return image;
    }

    public Color parseColor(String input) {
        input = input.replace(" ", "");

        for (Field field : Color.class.getDeclaredFields()) {
            if (field.getName().equals(input)) {
                if (field.getType().isAssignableFrom(Color.class)) {
                    try {
                        return (Color) field.get(Color.class);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                }
            }
        }

        return null;
    }
}
